package br.com.joao.fingerprintphotomatcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;
import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExternalMatchRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.MatchResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.PostmanEnvValueVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.PostmanEnvironmentVO;
import br.com.joao.fingerprintphotomatcher.service.ExtractorService;
import br.com.joao.fingerprintphotomatcher.service.MatcherService;
import br.com.joao.fingerprintphotomatcher.service.PhotoService;
import br.com.joao.fingerprintphotomatcher.util.ProcessedFilesReport;
import br.com.joao.fingerprintphotomatcher.util.VerifyReport;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FingerprintPhotoMatcherApplication.class)
@Slf4j
class FingerprintPhotoMatcherApplicationTests {

	private static final String TESTS_PATH = "src" + File.separator + "test";

	private static final String RESOURCES_PATH = TESTS_PATH + File.separator + "resources";

	private static final String RESOURCES_IMAGES_PATH = RESOURCES_PATH + File.separator + "images";

	private static final String RESOURCES_WSQS_PATH = RESOURCES_PATH + File.separator + "wsqs";

	private static final String TARGET_PATH = TESTS_PATH + File.separator + "target";

	private static final String TARGET_PROCESSED_PATH = TARGET_PATH + File.separator + "processed";

	private static final String PROCESSED_IMAGES_PATH = TARGET_PROCESSED_PATH + File.separator + "images";

	private static final String PROCESSED_WSQS_PATH = TARGET_PROCESSED_PATH + File.separator + "wsqs";

	private static final String RESULT_PATH = TARGET_PATH + File.separator + "result";

	private static final String VERIFY_IMAGE_IMAGE_PATH = RESULT_PATH + File.separator + "image-image";

	private static final String VERIFY_IMAGE_WSQ_PATH = RESULT_PATH + File.separator + "image-wsq";

	private static final String VERIFY_WSQ_WSQ_PATH = RESULT_PATH + File.separator + "wsq-wsq";

	private static final String REPORTS_PATH = TARGET_PATH + File.separator + "reports";

	private static final String POSTMAN_ENV_PATH = TARGET_PATH + File.separator + "postman-env";

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private MatcherService matcherService;

	@Test
	void executeTests() {
		log.info("Tests started");
		cleanPreviousTests();
		buildPostmanEnvFile();
		processImages();
		processWsqs();
		verifyImagesWithWsqs();
		verifyImagesWithImages();
		verifyWsqsWithWsqs();
		buildReports();
		log.info("Tests finished successfully!");
	}

	// Clean the target directory with previous tests
	private void cleanPreviousTests() {
		try {
			log.info("Cleaning previous tests");
			FileUtils.deleteDirectory(new File(TARGET_PATH));
		} catch (IOException e) {
			log.error("Error trying to delete target directory - {}", e.getMessage());
		}
	}

	// Execute consumer of each item of the array in parallel
	private <T> void executeInParallel(T[] array, Consumer<T> consumer) {
		ExecutorService executorService = Executors.newFixedThreadPool(15); // Adjust the thread pool size as needed
		for (T item : array) {
			executorService.submit(() -> {
				consumer.accept(item);
			});
		}
		executorService.shutdown();
		while (!executorService.isTerminated()) {
			// Wait for all tasks to complete
		}
	}

	// Get all images on /test/resources/images/ process it then put the result in
	// /test/target/processed/images/ on 2 files: 1 .png and 1 .json containing
	// Base64, NFIQ and number of minutiae
	private void processImages() {
		log.info("Init process images test");
		File imagesDirectory = new File(RESOURCES_IMAGES_PATH);
		if (imagesDirectory.isDirectory()) {
			executeInParallel(imagesDirectory.listFiles(), image -> {
				try {
					String name = image.getName();
					log.info("Processing image: {}", name);
					String[] split = name.split("\\.");

					// Process image and save it with the same name
					byte[] processedImage = photoService.processImage(FileUtils.readFileToByteArray(image));
					File processed = new File(PROCESSED_IMAGES_PATH + File.separator + name);
					FileUtils.writeByteArrayToFile(processed, processedImage);

					extractTemplateAndGetMinutiaeImage(image, processedImage, split, PROCESSED_IMAGES_PATH);
				} catch (IOException e) {
					log.error("Can not read or write image {} from {} - {}", image, imagesDirectory, e.getMessage());
				} catch (Exception e) {
					log.error("Can not read or write image {} from {} - {}", image, imagesDirectory, e.getMessage());
					e.printStackTrace();
				}
			});
		}
	}

	// Get all wsqs on /test/resources/wsqs/ process it then put the result in
	// /test/target/processed/images/ on 2 files: 1 .png/.wsq and 1 .json containing
	// Base64, NFIQ and number of minutiae
	private void processWsqs() {
		log.info("Init process wsqs test");
		File wsqsDirectory = new File(RESOURCES_WSQS_PATH);
		if (wsqsDirectory.isDirectory()) {
			executeInParallel(wsqsDirectory.listFiles(), wsq -> {
				try {
					String name = wsq.getName();
					log.info("Processing wsq: {}", name);
					String[] split = name.split("\\.");

					extractTemplateAndGetMinutiaeImage(wsq, FileUtils.readFileToByteArray(wsq), split,
							PROCESSED_WSQS_PATH);
				} catch (IOException e) {
					log.error("Can not read or write image {} from {} - {}", wsq, wsqsDirectory, e.getMessage());
				} catch (Exception e) {
					log.error("Can not read or write image {} from {} - {}", wsq, wsqsDirectory, e.getMessage());
					e.printStackTrace();
				}
			});
		}
	}

	// Extract template from finger and get minutiae image
	private void extractTemplateAndGetMinutiaeImage(File biometry, byte[] processedBiometry, String[] split,
			String resultPath) throws IOException, Exception, JsonProcessingException {
		// Extract template from finger and write .json containing Base64, NFIQ and
		// number of minutiae
		ExtractRequestVO extractRequestVO = getExtractRequestFromBiometry(biometry, processedBiometry);
		ExtractResponseVO externalExtraction = extractorService.externalExtraction(extractRequestVO);
		String json = objectMapper.writeValueAsString(externalExtraction);
		File jsonFile = new File(resultPath + File.separator + split[0] + ".json");
		FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);

		// Get image with minutiaes and write it to a .png with same name format
		byte[] imageWithMinutiae = extractorService.requestImageConversion(
				externalExtraction.getBiometrics().get(0).getData(),
				true, true, true, null);
		File minutiaeFile = new File(resultPath + File.separator + split[0] + "-Minutiae.png");
		FileUtils.writeByteArrayToFile(minutiaeFile, imageWithMinutiae);
	}

	// Get all images on /test/target/processed/images/ and match it with all WSQs
	// on /test/target/processed/wsqs/ then put the result in
	// /test/target/result/image-wsq/ on a .json containing score and match result
	private void verifyImagesWithWsqs() {
		log.info("Init verify images-wsqs test");
		Map<String, byte[]> imagesTemplates = getTemplates(PROCESSED_IMAGES_PATH);
		Map<String, byte[]> wsqsTemplates = getTemplates(PROCESSED_WSQS_PATH);
		verifyTemplates(imagesTemplates, wsqsTemplates, VERIFY_IMAGE_WSQ_PATH);
	}

	// Get all images on /test/target/processed/images/ and match it with all images
	// on /test/target/processed/images/ then put the result in
	// /test/target/result/image-image/ on a .json containing score and match result
	private void verifyImagesWithImages() {
		log.info("Init verify images-images test");
		Map<String, byte[]> imagesTemplates = getTemplates(PROCESSED_IMAGES_PATH);
		verifyTemplates(imagesTemplates, imagesTemplates, VERIFY_IMAGE_IMAGE_PATH);
	}

	// Get all WSQs on /test/target/processed/wsqs/ and match it with all WSQs
	// on /test/target/processed/wsqs/ then put the result in
	// /test/target/result/wsq-wsq/ on a .json containing score and match result
	private void verifyWsqsWithWsqs() {
		log.info("Init verify wsqs-wsqs test");
		Map<String, byte[]> wsqsTemplates = getTemplates(PROCESSED_WSQS_PATH);
		verifyTemplates(wsqsTemplates, wsqsTemplates, VERIFY_WSQ_WSQ_PATH);
	}

	private void buildReports() {
		log.info("Init test reports");

		log.info("Building processed images report");
		buildProcessedFilesReport(PROCESSED_IMAGES_PATH, "Processed_Images_Report");

		log.info("Building processed wsqs report");
		buildProcessedFilesReport(PROCESSED_WSQS_PATH, "Processed_WSQs_Report");

		log.info("Building verify image-wsq report");
		buildVerifyReport(VERIFY_IMAGE_WSQ_PATH, "Verify_Image-WSQ_Report");

		log.info("Building verify image-image report");
		buildVerifyReport(VERIFY_IMAGE_IMAGE_PATH, "Verify_Image-Image_Report");

		log.info("Building verify wsq-wsq report");
		buildVerifyReport(VERIFY_WSQ_WSQ_PATH, "Verify_WSQ-WSQ_Report");
	}

	private ExtractRequestVO getExtractRequestFromBiometry(File biometry, byte[] processedBiometry) {
		List<BiometricVO> biometricVOs = new ArrayList<>();
		biometricVOs.add(new BiometricVO(translateFingerNames(biometry.getName()),
				Base64.getEncoder().encodeToString(processedBiometry), false));
		ExtractRequestVO extractRequestVO = new ExtractRequestVO(biometricVOs, true);
		return extractRequestVO;
	}

	private BodyPartEnum translateFingerNames(String fingerNameOnPhoto) {
		String[] split = fingerNameOnPhoto.split("-");
		switch (split[0]) {
			case "LEFT_HAND_PINKY":
				return BodyPartEnum.LEFT_HAND_PINKY;
			case "LEFT_HAND_RING":
				return BodyPartEnum.LEFT_HAND_RING;
			case "LEFT_HAND_MIDDLE":
				return BodyPartEnum.LEFT_HAND_MIDDLE;
			case "LEFT_HAND_INDEX":
				return BodyPartEnum.LEFT_HAND_INDEX;
			case "LEFT_HAND_THUMB":
				return BodyPartEnum.LEFT_HAND_THUMB;
			case "RIGHT_HAND_THUMB":
				return BodyPartEnum.RIGHT_HAND_THUMB;
			case "RIGHT_HAND_INDEX":
				return BodyPartEnum.RIGHT_HAND_INDEX;
			case "RIGHT_HAND_MIDDLE":
				return BodyPartEnum.RIGHT_HAND_MIDDLE;
			case "RIGHT_HAND_RING":
				return BodyPartEnum.RIGHT_HAND_RING;
			case "RIGHT_HAND_PINKY":
				return BodyPartEnum.RIGHT_HAND_PINKY;
			default:
				return null;
		}
	}

	private Map<String, byte[]> getTemplates(String filePath) {
		Map<String, byte[]> templates = new HashMap<>();
		File directory = new File(filePath);
		if (directory.isDirectory()) {
			executeInParallel(directory.listFiles(), file -> {
				try {
					// Check if file is json
					String name = file.getName();
					String[] split = name.split("\\.");
					if (split[1].equals("json")) {
						log.info("Getting template from file: {}", name);
						// Get template from json of processed file
						ExtractResponseVO extractedFile = objectMapper.readValue(file, ExtractResponseVO.class);
						if (extractedFile.getBiometrics().get(0).getNfiq() <= 5) {
							templates.put(split[0], extractedFile.getTemplate());
						}
					}
				} catch (IOException e) {
					log.error("Can not read or write file {} from {} - {}", file, directory, e.getMessage());
				} catch (Exception e) {
					log.error("Can not read or write file {} from {} - {}", file, directory, e.getMessage());
					e.printStackTrace();
				}
			});
		}
		return templates;
	}

	private void verifyTemplates(Map<String, byte[]> templates1, Map<String, byte[]> templates2,
			String pathToStoreResult) {
		ExecutorService executorService = Executors.newFixedThreadPool(15); // Adjust the thread pool size as needed
		templates1.forEach((template1Name, template1Data) -> {
			templates2.forEach((template2Name, template2Data) -> {
				executorService.submit(() -> {
					try {
						ExternalMatchRequestVO externalMatchRequestVO = new ExternalMatchRequestVO(template1Data,
								template2Data);
						MatchResponseVO match = matcherService.verifyTemplates(externalMatchRequestVO);
						match.setExpectedResult("NO_MATCH");
						if (translateFingerNames(template1Name) == translateFingerNames(template2Name)) {
							match.setExpectedResult("SUCCESS");
						}

						String json = objectMapper.writeValueAsString(match);
						File jsonFile = new File(
								pathToStoreResult + File.separator + template1Name + "-Vs-" + template2Name + ".json");
						FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);
					} catch (Exception e) {
						log.error("Can not verify template1: {} with template2: {} - {}", template1Name, template2Name,
								e.getMessage());
						e.printStackTrace();
					}
				});
			});
		});

		executorService.shutdown();
		while (!executorService.isTerminated()) {
			// Wait for all tasks to complete
		}
	}

	private void buildProcessedFilesReport(String pathOfProcessedFiles, String reportName) {
		try {
			int numberOfFiles = 0;
			int amountOfNfiq1 = 0;
			int amountOfNfiq2 = 0;
			int amountOfNfiq3 = 0;
			int amountOfNfiq4 = 0;
			int amountOfNfiq5 = 0;
			File processedFilesDirectory = new File(pathOfProcessedFiles);
			if (processedFilesDirectory.isDirectory()) {
				for (File file : processedFilesDirectory.listFiles()) {
					// Check if file is json
					if (file.getName().split("\\.")[1].equals("json")) {
						numberOfFiles++;
						// Get data from json of processed image
						ExtractResponseVO extractResponse = objectMapper.readValue(file, ExtractResponseVO.class);
						switch (extractResponse.getBiometrics().get(0).getNfiq()) {
							case 1:
								amountOfNfiq1++;
								break;
							case 2:
								amountOfNfiq2++;
								break;
							case 3:
								amountOfNfiq3++;
								break;
							case 4:
								amountOfNfiq4++;
								break;
							case 5:
								amountOfNfiq5++;
								break;
							default:
								break;
						}
					}
				}
				ProcessedFilesReport processedFilesReport = new ProcessedFilesReport(numberOfFiles, amountOfNfiq1,
						amountOfNfiq2, amountOfNfiq3, amountOfNfiq4, amountOfNfiq5);

				String json = objectMapper.writeValueAsString(processedFilesReport);
				File jsonFile = new File(REPORTS_PATH + File.separator + reportName + ".json");
				FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);
			}
		} catch (Exception e) {
			log.error("Can not read or write json {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private void buildVerifyReport(String pathOfVerify, String reportName) {
		try {
			int numberOfVerifies = 0;
			int amountOfNoMatches = 0;
			int expectedNoMatches = 0;
			int amountOfMatches = 0;
			int expectedMatches = 0;
			double hitPercentageOfNoMatch = 0;
			double hitPercentageOfMatch = 0;
			double overallHitPercentage = 0;
			int amountOfFalseAcceptances = 0;
			int amountOfFalseRejections = 0;
			int amountOfTrueAcceptances = 0;
			int amountOfTrueRejections = 0;
			double far = 0;
			double frr = 0;
			double tar = 0;
			double trr = 0;
			double eer = 0;

			int hits = 0;
			File verifyDirectory = new File(pathOfVerify);
			if (verifyDirectory.isDirectory()) {
				for (File file : verifyDirectory.listFiles()) {
					// Check if file is json
					if (file.getName().split("\\.")[1].equals("json")) {
						numberOfVerifies++;
						// Get data from json of processed image
						MatchResponseVO matchResponse = objectMapper.readValue(file, MatchResponseVO.class);
						switch (matchResponse.getResult()) {
							case SUCCESS:
								amountOfMatches++;
								break;
							case NO_MATCH:
								amountOfNoMatches++;
								break;
							case FAILURE:
							default:
								break;
						}
						switch (matchResponse.getExpectedResult()) {
							case "SUCCESS":
								expectedMatches++;
								switch (matchResponse.getResult()) {
									case SUCCESS:
										hits++;
										amountOfTrueAcceptances++;
										break;
									case NO_MATCH:
										amountOfFalseRejections++;
										break;
									case FAILURE:
									default:
										break;
								}
								break;
							case "NO_MATCH":
								expectedNoMatches++;
								switch (matchResponse.getResult()) {
									case SUCCESS:
										amountOfFalseAcceptances++;
										break;
									case NO_MATCH:
										hits++;
										amountOfTrueRejections++;
										break;
									case FAILURE:
									default:
										break;
								}
								break;
							default:
								break;
						}
					}
				}
				hitPercentageOfNoMatch = (double) amountOfNoMatches / (double) expectedNoMatches * 100.0;
				hitPercentageOfMatch = (double) amountOfMatches / (double) expectedMatches * 100.0;
				overallHitPercentage = (double) hits / (double) numberOfVerifies * 100.0;

				far = (double) amountOfFalseAcceptances / (double) numberOfVerifies * 100.0;
				frr = (double) amountOfFalseRejections / (double) numberOfVerifies * 100.0;
				tar = (double) amountOfTrueAcceptances / (double) expectedMatches * 100.0;
				trr = (double) amountOfTrueRejections / (double) expectedNoMatches * 100.0;
				eer = (far + frr) / 2.0;

				VerifyReport verifyReport = new VerifyReport(numberOfVerifies, amountOfNoMatches, expectedNoMatches,
						hitPercentageOfNoMatch, amountOfMatches, expectedMatches, hitPercentageOfMatch,
						amountOfFalseAcceptances, amountOfFalseRejections, amountOfTrueAcceptances,
						amountOfTrueRejections, far, frr, tar, trr, eer, overallHitPercentage);

				String json = objectMapper.writeValueAsString(verifyReport);
				File jsonFile = new File(REPORTS_PATH + File.separator + reportName + ".json");
				FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);
			}

		} catch (Exception e) {
			log.error("Can not read or write json {}", e.getMessage());
			e.printStackTrace();
		}
	}

	private void buildPostmanEnvFile() {
		try {
			log.info("Building postman env json file");
			PostmanEnvironmentVO postmanEnvironmentVO = new PostmanEnvironmentVO();
			List<PostmanEnvValueVO> values = postmanEnvironmentVO.getValues();

			File imagesDirectory = new File(RESOURCES_IMAGES_PATH);
			if (imagesDirectory.isDirectory()) {
				for (File image : imagesDirectory.listFiles()) {
					String name = image.getName();
					String[] split = name.split("\\.");

					PostmanEnvValueVO postmanEnvValueVO = new PostmanEnvValueVO();
					postmanEnvValueVO.setKey(split[0]);
					postmanEnvValueVO
							.setValue(Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(image)));
					values.add(postmanEnvValueVO);
				}
			}

			File wsqsDirectory = new File(RESOURCES_WSQS_PATH);
			if (wsqsDirectory.isDirectory()) {
				for (File wsq : wsqsDirectory.listFiles()) {
					String name = wsq.getName();
					String[] split = name.split("\\.");

					PostmanEnvValueVO postmanEnvValueVO = new PostmanEnvValueVO();
					postmanEnvValueVO.setKey(split[0] + "-WSQ");
					postmanEnvValueVO
							.setValue(Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(wsq)));
					values.add(postmanEnvValueVO);
				}
			}

			String json = objectMapper.writeValueAsString(postmanEnvironmentVO);
			File jsonFile = new File(
					POSTMAN_ENV_PATH + File.separator + "Fingerprint Photo Matcher Generated.postman_environment.json");
			FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("Error building postman environment - {}", e.getMessage());
		}
	}

}
