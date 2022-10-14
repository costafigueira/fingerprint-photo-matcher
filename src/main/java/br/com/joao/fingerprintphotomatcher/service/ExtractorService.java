package br.com.joao.fingerprintphotomatcher.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import br.com.joao.fingerprintphotomatcher.enumeration.BodyPartEnum;
import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.TransactionVO;
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

	public ExtractResponseVO externalExtraction(AdvancedRestSubjectVO restSubjectVO, boolean quality) throws Exception {
		TransactionVO transactionVO = new TransactionVO();
		List<BiometricVO> biometrics = new ArrayList<>();

		restSubjectVO.getBodyParts().stream().forEach(bodyPart -> {
			if (bodyPart.isAvailable()) {
				biometrics.add(new BiometricVO(bodyPart.getBodyPart(), bodyPart.getData(), ZonedDateTime.now()));
			}
		});
		transactionVO.setBiometrics(biometrics);
		return requestExtraction(transactionVO, quality);
	}

	private ExtractResponseVO requestExtraction(TransactionVO transactionVO, boolean faceQuality)
			throws Exception {

		try {
			HttpHeaders headers = new HttpHeaders();
			String ctxId = UUID.randomUUID().toString().substring(0, 7);
			headers.set("ctxId", ctxId);
			headers.set("quality", String.valueOf(faceQuality));
			HttpEntity<TransactionVO> request = new HttpEntity<>(transactionVO, headers);
			ResponseEntity<byte[]> response = restTemplate.exchange(apiConfigurations.getApiExtraction(),
					HttpMethod.POST, request, byte[].class);
			ExtractTemplate extractTemplate = objectMapper.readValue(response.getBody(), ExtractTemplate.class);
			NSubject nSubject = NeurotecUtils.getNSubject(extractTemplate.getTemplate());
			Map<BodyPartEnum, BodyPartDetails> bodyPartDetails = NeurotecUtils.getBodyPartDetails(nSubject);
			Map<String, Integer> templateQuality = extractTemplate.getQuality();

			bodyPartDetails.forEach((k, v) -> templateQuality.forEach((bodyPart, quality) -> {
				if (k.toString().equals(bodyPart)) {
					v.setQuality(quality);
				}
			}));
			return new ExtractResponseVO(extractTemplate.getTemplate(), bodyPartDetails, extractTemplate.getFaceToken(),
					extractTemplate.getIcaos(), extractTemplate.getFingers());
		} catch (RestClientResponseException e) {
			int statusCode = e.getRawStatusCode();

			if (statusCode > 500) {
				logger.error("Error communicating with template extractor service > url: {} | message: {}",
						apiConfigurations.getApiExtraction(), e.getMessage(), e);
				throw new RestClientOperationException(apiConfigurations.getApiExtraction(), e);
			}
			String responseBody = e.getResponseBodyAsString();
			try {
				ExtractErrorVO errorResponse = DirectoryUtils.getMapper().readValue(responseBody, ExtractErrorVO.class);
				logger.error("Error during template extraction > URL: {} | status: {} | code: {} | message: {}",
						apiConfigurations.getApiExtraction(), statusCode,
						errorResponse.getCode(), errorResponse.getMessage());
				throw new TemplateCreationException(errorResponse.getMessage());
			} catch (IOException ex) {
				logger.error("Error during template extraction. Couldn't read result error > URL {} | message: {}",
						apiConfigurations.getApiExtraction(), ex.getMessage(), ex);
				throw new TemplateCreationException(e.getMessage());
			}
		} catch (RestClientException ex) {
			logger.error("Error communicating with template extractor service > url: {} | message: {}",
					apiConfigurations.getApiExtraction(), ex.getMessage(), ex);
			throw new RestClientOperationException(apiConfigurations.getApiExtraction(), ex);
		}

		catch (IOException e) {
			logger.error("Error reading template response  > {}", e.getMessage(), e);
			throw new TemplateCreationException(e.getMessage());
		}

	}
}
