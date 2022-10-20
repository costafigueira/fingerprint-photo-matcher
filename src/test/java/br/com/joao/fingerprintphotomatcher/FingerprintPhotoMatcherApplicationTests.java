package br.com.joao.fingerprintphotomatcher;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FingerprintPhotoMatcherApplication.class)
class FingerprintPhotoMatcherApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("Teste");
	}

	@Test
	void processImages() {
		// Get all images on /test/resources/images/ process it then put the result in
		// /test/target/processed/images/ on 2 files: 1 .png and 1 .txt containing
		// Base64, NFIQ and number of minutiae
	}

	@Test
	void processWsqs() {
		// Get all wsqs on /test/resources/wsqs/ process it then put the result in
		// /test/target/processed/images/ on 2 files: 1 .png/.wsq and 1 .txt containing
		// Base64, NFIQ and number of minutiae
	}

	@Test
	void verifyImagesWithWsqs() {
		// Get all images on /test/target/processed/images/ and match it with all WSQs
		// on /test/target/processed/wsqs/ then put the result in
		// /test/target/result/image-wsq/ on a .txt containing score and match result
	}

	@Test
	void verifyImagesWithImages() {
		// Get all images on /test/target/processed/images/ and match it with all images
		// on /test/target/processed/images/ then put the result in
		// /test/target/result/image-image/ on a .txt containing score and match result
	}

	@Test
	void verifyWsqsWithWsqs() {
		// Get all WSQs on /test/target/processed/wsqs/ and match it with all WSQs
		// on /test/target/processed/wsqs/ then put the result in
		// /test/target/result/wsq-wsq/ on a .txt containing score and match result
	}

}
