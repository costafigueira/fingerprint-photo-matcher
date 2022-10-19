package br.com.joao.fingerprintphotomatcher.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.joao.fingerprintphotomatcher.rest.vo.ExternalMatchRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.MatchResponseVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MatcherService {

	@Value("${neurotec.api.verify:http://neurotec-services.hom.bry.com.br/api/verify}")
	private String apiVerify;

	public MatchResponseVO verifyPhotos(ExtractResponseVO template1Extraction, ExtractResponseVO template2Extraction)
			throws Exception {
		ExternalMatchRequestVO externalMatchRequestVO = new ExternalMatchRequestVO(template1Extraction.getTemplate(),
				template2Extraction.getTemplate());
		return verifyTemplates(externalMatchRequestVO);
	}

	public MatchResponseVO verifyTemplates(ExternalMatchRequestVO externalMatchRequestVO) throws Exception {
		log.info("Request to perform a verify in neurotec service");
		try {
			RestTemplate restTemplate = new RestTemplate();
			// ObjectMapper objectMapper = new ObjectMapper();
			HttpHeaders headers = new HttpHeaders();
			String ctxId = UUID.randomUUID().toString().substring(0, 7);
			headers.set("ctxId", ctxId);
			HttpEntity<ExternalMatchRequestVO> request = new HttpEntity<>(externalMatchRequestVO, headers);
			ResponseEntity<MatchResponseVO> response = restTemplate.exchange(apiVerify,
					HttpMethod.POST, request, MatchResponseVO.class);
			// NServerResponse nServerResponse = objectMapper.readValue(response.getBody(),
			// NServerResponse.class);
			return response.getBody();
		} catch (Exception e) {
			log.error("Error consuming template matcher service > url: {} | message: {}",
					apiVerify, e.getMessage(), e);
			throw e;
		}
	}

}
