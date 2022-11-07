package br.com.joao.fingerprintphotomatcher.controller;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joao.fingerprintphotomatcher.rest.vo.ExternalMatchRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractResponseVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ImageRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.MatchRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.MatchResponseVO;
import br.com.joao.fingerprintphotomatcher.service.ExtractorService;
import br.com.joao.fingerprintphotomatcher.service.MatcherService;
import br.com.joao.fingerprintphotomatcher.service.PhotoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class FingerprintPhotoMatcherController {

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private MatcherService matcherService;

	@PostMapping("/process-image")
	public ResponseEntity<String> processImage(HttpServletRequest request,
			@RequestBody ImageRequestVO imageRequestVO) {
		log.info("Request to process image received");
		byte[] image = Base64.getDecoder().decode(imageRequestVO.getImage());
		byte[] finalImage = photoService.processImage(image);
		log.info("Image processed successfully");
		return ResponseEntity.ok(Base64.getEncoder().encodeToString(finalImage));
	}

	@PostMapping("/extract-template")
	public ResponseEntity<ExtractResponseVO> extractTemplate(HttpServletRequest request,
			@RequestBody ExtractRequestVO extractRequestVO) throws Exception {
		log.info("Request to extract template received");
		extractRequestVO.getBiometrics().forEach(photoService::processBiometric);
		ExtractResponseVO externalExtraction = extractorService.externalExtraction(extractRequestVO);
		log.info("Template extracted successfully");
		return ResponseEntity.ok(externalExtraction);
	}

	@PostMapping("/verify")
	public ResponseEntity<MatchResponseVO> verify(HttpServletRequest request,
			@RequestBody MatchRequestVO matchRequestVO) throws Exception {
		log.info("Request to verify two images received");
		matchRequestVO.getTemplate1().getBiometrics().forEach(photoService::processBiometric);
		matchRequestVO.getTemplate2().getBiometrics().forEach(photoService::processBiometric);
		ExtractResponseVO template1Extraction = extractorService.externalExtraction(matchRequestVO.getTemplate1());
		ExtractResponseVO template2Extraction = extractorService.externalExtraction(matchRequestVO.getTemplate2());
		MatchResponseVO match = matcherService.verifyPhotos(template1Extraction, template2Extraction);
		log.info("Verify successfully executed");
		return ResponseEntity.ok(match);
	}

	@PostMapping("/verify-templates")
	public ResponseEntity<MatchResponseVO> verifyTemplates(HttpServletRequest request,
			@RequestBody ExternalMatchRequestVO externalMatchRequestVO) throws Exception {
		log.info("Request to verify two templates received");
		MatchResponseVO match = matcherService.verifyTemplates(externalMatchRequestVO);
		log.info("Verify successfully executed");
		return ResponseEntity.ok(match);
	}

}
