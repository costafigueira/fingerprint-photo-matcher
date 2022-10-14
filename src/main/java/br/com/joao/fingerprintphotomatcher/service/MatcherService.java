package br.com.joao.fingerprintphotomatcher.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.joao.fingerprintphotomatcher.rest.vo.MatchRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.MatchResponseVO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MatcherService {

    @Value("${neurotec.api.verify:http://neurotec-services.hom.bry.com.br/api/verify}")
    private String apiVerify;

    public MatchResponseVO verifyInApi(byte[] template1, byte[] template2) throws Exception {
        log.info("Request to perform a verify in neurotec service");
        MatchRequestVO matchRequestVO = new MatchRequestVO();
        matchRequestVO.setTemplate1(template1);
        matchRequestVO.setTemplate2(template2);
        try {
            RestTemplate restTemplate = new RestTemplate();
            // ObjectMapper objectMapper = new ObjectMapper();
            HttpHeaders headers = new HttpHeaders();
            String ctxId = UUID.randomUUID().toString().substring(0, 7);
            headers.set("ctxId", ctxId);
            HttpEntity<MatchRequestVO> request = new HttpEntity<>(matchRequestVO, headers);
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
