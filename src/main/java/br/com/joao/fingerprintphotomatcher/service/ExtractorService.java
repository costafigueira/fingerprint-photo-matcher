package br.com.joao.fingerprintphotomatcher.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricDetailsVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExternalExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExternalExtractionRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractorBiometricVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExtractorService {

	@Value("${neurotec.api.extractor:http://neurotec-services.hom.bry.com.br/api/extract}")
	private String apiExtraction;

	@Value("${neurotec.api.convert:http://neurotec-services.hom.bry.com.br/api/convert-image}")
	private String apiConversion;

	@Value("${neurotec.api.details:http://neurotec-services.hom.bry.com.br/api/content-details}")
	private String apiContentDetails;

	private static RestTemplate restTemplate = new RestTemplate();
	private static ObjectMapper objectMapper = new ObjectMapper();

	public ExtractResponseVO externalExtraction(ExtractRequestVO extractRequestVO) throws Exception {
		List<ExtractorBiometricVO> biometrics = new ArrayList<>();

		extractRequestVO.getBiometrics().stream().forEach(biometry -> {
			biometrics.add(new ExtractorBiometricVO(biometry.getBodyPart(), biometry.getData()));
		});
		ExternalExtractionRequestVO externalExtractionRequestVO = new ExternalExtractionRequestVO(biometrics);
		ExternalExtractResponseVO requestExtraction = requestExtraction(externalExtractionRequestVO,
				extractRequestVO.isEvaluateQuality());
		return convertExternalExtractionResponseToExtractionResponse(requestExtraction);
	}

	private ExternalExtractResponseVO requestExtraction(ExternalExtractionRequestVO externalExtractionRequestVO,
			boolean quality)
			throws Exception {
		try {
			HttpHeaders headers = new HttpHeaders();
			String ctxId = UUID.randomUUID().toString().substring(0, 7);
			headers.set("ctxId", ctxId);
			headers.set("quality", String.valueOf(quality));
			HttpEntity<ExternalExtractionRequestVO> request = new HttpEntity<>(externalExtractionRequestVO, headers);
			ResponseEntity<byte[]> response = restTemplate.exchange(apiExtraction,
					HttpMethod.POST, request, byte[].class);
			ExternalExtractResponseVO extractTemplate = objectMapper.readValue(response.getBody(),
					ExternalExtractResponseVO.class);
			return extractTemplate;
		} catch (Exception e) {
			log.error("Error consuming template extractor service > url: {} | message: {}",
					apiExtraction, e.getMessage(), e);
			throw e;
		}
	}

	private ExtractResponseVO convertExternalExtractionResponseToExtractionResponse(
			ExternalExtractResponseVO externalExtraction) {
		ExtractResponseVO extractResponseVO = new ExtractResponseVO(externalExtraction.getTemplate());
		externalExtraction.getFingers()
				.forEach((bodyPartName, data) -> externalExtraction.getQuality()
						.forEach((bodyPart, quality) -> {
							if (bodyPartName.toString().equals(bodyPart)) {
								List<BiometricDetailsVO> biometricDetailsVOs = new ArrayList<>();
								biometricDetailsVOs.add(new BiometricDetailsVO(bodyPartName,
										quality, Base64.getEncoder().encodeToString(data)));
								extractResponseVO.setBiometrics(biometricDetailsVOs);
							}
						}));
		return extractResponseVO;
	}

	public byte[] requestImageConversion(byte[] image, Boolean convertToPng, Boolean detail, Boolean showMinutiae,
			String imageFormat) throws Exception {
		String ctxId = UUID.randomUUID().toString().substring(0, 7);
		HttpHeaders headers = new HttpHeaders();
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConversion)
				.queryParam("convertToPng", convertToPng).queryParam("detail", detail)
				.queryParam("showMinutiae", showMinutiae)
				.queryParam("contentType", imageFormat);
		try {
			headers.set("ctxId", ctxId);
			HttpEntity<byte[]> request = new HttpEntity<>(image, headers);
			ResponseEntity<byte[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, request,
					byte[].class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error consuming image conversion service > url: {} | message: {}",
					apiExtraction, e.getMessage(), e);
			throw e;
		}
	}
}
