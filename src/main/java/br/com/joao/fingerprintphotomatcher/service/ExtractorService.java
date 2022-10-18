package br.com.joao.fingerprintphotomatcher.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

	public ExtractResponseVO externalExtraction(TransactionVO transactionVO, boolean quality) throws Exception {
		return requestExtraction(transactionVO, quality);
	}

	private ExtractResponseVO requestExtraction(TransactionVO transactionVO, boolean quality)
			throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate();
            // ObjectMapper objectMapper = new ObjectMapper();
			HttpHeaders headers = new HttpHeaders();
			String ctxId = UUID.randomUUID().toString().substring(0, 7);
			headers.set("ctxId", ctxId);
			headers.set("quality", String.valueOf(quality));
			HttpEntity<TransactionVO> request = new HttpEntity<>(transactionVO, headers);
			ResponseEntity<ExtractResponseVO> response = restTemplate.exchange(apiExtraction,
					HttpMethod.POST, request, ExtractResponseVO.class);
			// ExtractResponseVO extractTemplate = objectMapper.readValue(response.getBody(), ExtractResponseVO.class);

			// bodyPartDetails.forEach((k, v) -> templateQuality.forEach((bodyPart, quality) -> {
			// 	if (k.toString().equals(bodyPart)) {
			// 		v.setQuality(quality);
			// 	}
			// }));
			return response.getBody();
		} catch (Exception e) {
            log.error("Error consuming template extractor service > url: {} | message: {}",
                    apiExtraction, e.getMessage(), e);
            throw e;
        }

	}
}
