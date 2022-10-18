package br.com.joao.fingerprintphotomatcher.controller;

import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joao.fingerprintphotomatcher.enumeration.ResultEnum;
import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ExtractRequestVO;
import br.com.joao.fingerprintphotomatcher.rest.vo.ImageRequestVO;
import br.com.joao.fingerprintphotomatcher.service.ExtractorService;
import br.com.joao.fingerprintphotomatcher.service.MatcherService;
import br.com.joao.fingerprintphotomatcher.service.PhotoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class FingerprintPhotoMatcherController {

	// private static final Predicate<BiometricVO> TO_PROCESS_IMAGES = biometricVO
	// -> biometricVO.isProcessImage();

	@Autowired
	private PhotoService photoService;

	@Autowired
	private ExtractorService extractorService;

	@Autowired
	private MatcherService matcherService;

	// @PostMapping(value = "/process-image", produces = {
	// MediaType.APPLICATION_OCTET_STREAM_VALUE })
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
	public ResponseEntity<String> extractTemplate(HttpServletRequest request,
			@RequestBody ExtractRequestVO extractRequestVO) {
		log.info("Request to extract template received");
		List<BiometricVO> biometrics = extractRequestVO.getBiometrics();
		List<BiometricVO> toProcessBiometrics = biometrics.stream().filter(biometricVO -> biometricVO.isProcessImage())
				.collect(Collectors.toList());
		log.info("Template extracted successfully");
		return null;
	}

	@PostMapping("/verify")
	public ResponseEntity<String> verify(HttpServletRequest request,
			@RequestBody ImageRequestVO imageRequestVO) {
		log.info("Request to verify two images received");
		log.info("Verify successfully executed");
		return null;
	}

	@PostMapping("/verify-templates")
	public ResponseEntity<String> verifyTemplates(HttpServletRequest request,
			@RequestBody ImageRequestVO imageRequestVO) {
		log.info("Request to verify two templates received");
		log.info("Verify successfully executed");
		return null;
	}

}
