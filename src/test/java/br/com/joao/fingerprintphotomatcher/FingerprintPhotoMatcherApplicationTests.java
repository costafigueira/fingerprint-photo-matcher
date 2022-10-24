package br.com.joao.fingerprintphotomatcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;
import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.service.ExtractorService;
import br.com.joao.fingerprintphotomatcher.service.MatcherService;
import br.com.joao.fingerprintphotomatcher.service.PhotoService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FingerprintPhotoMatcherApplication.class)
@Slf4j
class FingerprintPhotoMatcherApplicationTests {

	private static String RESOURCES_PATH = ".." + File.separator + "resources";

	private static String PROCESSED_IMAGES_PATH = ".." + File.separator + "target" + File.separator + "processed"
			+ File.separator + "images";

	private static String PROCESSED_WSQS_PATH = ".." + File.separator + "target" + File.separator + "processed"
			+ File.separator + "wsqs";

	private static String RESULT_PATH = ".." + File.separator + "target" + File.separator + "result";

	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private MatcherService matcherService;

	@Test
	void contextLoads() {
		System.out.println("Teste");
	}

	@Test
	void processImages() {
		// Get all images on /test/resources/images/ process it then put the result in
		// /test/target/processed/images/ on 2 files: 1 .png and 1 .json containing
		// Base64, NFIQ and number of minutiae
		File imageDirectory = new File(RESOURCES_PATH + File.separator + "images");
		if (imageDirectory.isDirectory()) {
			for (File image : imageDirectory.listFiles()) {
				try {
					// Process image and save it with the same name
					byte[] processedImage = photoService.processImage(FileUtils.readFileToByteArray(image));
					File processed = new File(PROCESSED_IMAGES_PATH + File.separator + image.getName());
					FileUtils.writeByteArrayToFile(processed, processedImage);

					// Extract template from finger and write .json containing Base64, NFIQ and
					// number of minutiae
					ExtractRequestVO extractRequestVO = getExtractRequestFromProcessedImage(image, processedImage);
					ExtractResponseVO externalExtraction = extractorService
							.externalExtraction(extractRequestVO);
					String json = objectMapper.writeValueAsString(externalExtraction);
					File jsonFile = new File(
							PROCESSED_IMAGES_PATH + File.separator + image.getName().split(".")[0] + ".json");
					FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);

					// Get image with minutiaes and write it to a .png with same name format
					byte[] imageWithMinutiae = extractorService.requestImageConversion(
							externalExtraction.getBiometrics().get(0).getData().getBytes(), true, true, true, json);
					File minutiaeFile = new File(
							PROCESSED_IMAGES_PATH + File.separator + image.getName().split(".")[0] + "_Minutiae"
									+ image.getName().split(".")[1]);
					FileUtils.writeByteArrayToFile(minutiaeFile, imageWithMinutiae);
				} catch (IOException e) {
					log.error("Can not read or write image {} from {} - {}", image, imageDirectory, e.getMessage());
				} catch (Exception e) {
					log.error("Can not read or write image {} from {} - {}", image, imageDirectory, e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private ExtractRequestVO getExtractRequestFromProcessedImage(File image, byte[] processedImage) {
		List<BiometricVO> biometricVOs = new ArrayList<>();
		biometricVOs.add(new BiometricVO(translateFingerNames(image.getName()),
				Base64.getEncoder().encodeToString(processedImage), false));
		ExtractRequestVO extractRequestVO = new ExtractRequestVO(biometricVOs, true);
		return extractRequestVO;
	}

	@Test
	void processWsqs() {
		// Get all wsqs on /test/resources/wsqs/ process it then put the result in
		// /test/target/processed/images/ on 2 files: 1 .png/.wsq and 1 .json containing
		// Base64, NFIQ and number of minutiae
	}

	@Test
	void verifyImagesWithWsqs() {
		// Get all images on /test/target/processed/images/ and match it with all WSQs
		// on /test/target/processed/wsqs/ then put the result in
		// /test/target/result/image-wsq/ on a .json containing score and match result
	}

	@Test
	void verifyImagesWithImages() {
		// Get all images on /test/target/processed/images/ and match it with all images
		// on /test/target/processed/images/ then put the result in
		// /test/target/result/image-image/ on a .json containing score and match result
	}

	@Test
	void verifyWsqsWithWsqs() {
		// Get all WSQs on /test/target/processed/wsqs/ and match it with all WSQs
		// on /test/target/processed/wsqs/ then put the result in
		// /test/target/result/wsq-wsq/ on a .json containing score and match result
	}

	private BodyPartEnum translateFingerNames(String fingerNameOnPhoto) {
		String[] split = fingerNameOnPhoto.split("_"); // A_1, A_2, B_1, B_2
		switch (split[0]) {
			case "A":
				return BodyPartEnum.LEFT_HAND_PINKY;
			case "B":
				return BodyPartEnum.LEFT_HAND_RING;
			case "C":
				return BodyPartEnum.LEFT_HAND_MIDDLE;
			case "D":
				return BodyPartEnum.LEFT_HAND_INDEX;
			case "E":
				return BodyPartEnum.LEFT_HAND_THUMB;
			case "F":
				return BodyPartEnum.RIGHT_HAND_THUMB;
			case "G":
				return BodyPartEnum.RIGHT_HAND_INDEX;
			case "H":
				return BodyPartEnum.RIGHT_HAND_MIDDLE;
			case "I":
				return BodyPartEnum.RIGHT_HAND_RING;
			case "J":
				return BodyPartEnum.RIGHT_HAND_PINKY;
			default:
				return null;
		}
	}

}
