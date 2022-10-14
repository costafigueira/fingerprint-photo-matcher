package br.com.joao.fingerprintphotomatcher.controller;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.joao.fingerprintphotomatcher.rest.vo.ImageRequestVO;
import br.com.joao.fingerprintphotomatcher.service.PhotoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class FingerprintPhotoMatcherController {

    @Autowired
    private PhotoService photoService;

    // @PostMapping(value = "/process-image", produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE })
    @PostMapping("/process-image")
    public ResponseEntity<String> processImage(HttpServletRequest request,
            @RequestBody ImageRequestVO imageRequestVO) {
        log.info("Request to process image received");
        byte[] image = Base64.getDecoder().decode(imageRequestVO.getImage());
        Mat matImage = photoService.getMatFromByteArrayImage(image);

        matImage = photoService.removeBackgroundSimple(matImage);
        // matImage = photoService.removeBackground(matImage);
        matImage = photoService.convertImageToGrayScale(matImage);
        matImage = photoService.invertImage(matImage);
        matImage = photoService.applyAdaptiveHistogramEqualization(matImage);
        matImage = photoService.binarizeImage(matImage);
        // matImage = photoService.applyGaborFilter(matImage);
        // matImage = photoService.applyEdgeDetector(matImage);
        byte[] finalImage = photoService.getByteArrayImageFromMat(matImage);
        log.info("Image processed successfully");
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(finalImage));
    }

}
