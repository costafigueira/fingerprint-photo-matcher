package br.com.joao.fingerprintphotomatcher.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import br.com.joao.fingerprintphotomatcher.rest.vo.BiometricVO;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;

@Service
@Slf4j
public class PhotoService {

	@PostConstruct
	private void init() {
		OpenCV.loadShared();
	}

	public byte[] processImage(byte[] image) {
		Mat matImage = getMatFromByteArrayImage(image);

		matImage = flipImage(matImage);
		matImage = removeBackgroundSimple(matImage);
		// matImage = removeBackground(matImage);
		matImage = convertImageToGrayScale(matImage);
		matImage = invertImage(matImage);
		matImage = applyAdaptiveHistogramEqualization(matImage);
		matImage = binarizeImage(matImage);
		// matImage = applyGaborFilter(matImage);
		// matImage = applyEdgeDetector(matImage);

		return getByteArrayImageFromMat(matImage);
	}

	public BiometricVO processBiometric(BiometricVO biometry) {
		if (biometry.isProcessImage()) {
			byte[] processImage = processImage(Base64.getDecoder().decode(biometry.getData()));
			biometry.setData(Base64.getEncoder().encodeToString(processImage));
		}
		return biometry;
	}

	private Mat getMatFromByteArrayImage(byte[] image) {
		return Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}

	private byte[] getByteArrayImageFromMat(Mat image) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".png", image, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		return byteArray;
	}

	private Mat flipImage(Mat image) {
		log.info("Flipping image");
		Mat flippedImage = new Mat();
		Core.flip(image, flippedImage, 1);
		return flippedImage;
	}

	private Mat convertImageToGrayScale(Mat image) {
		log.info("Converting image to gray scale");
		Mat grayScaleImage = new Mat();
		Imgproc.cvtColor(image, grayScaleImage, Imgproc.COLOR_RGB2GRAY);
		return grayScaleImage;
	}

	private Mat normalizeImage(Mat image) {
		log.info("Normalizing image");
		Mat normalizedImage = new Mat();
		Core.normalize(image, normalizedImage, 0, 128, Core.NORM_MINMAX);
		return normalizedImage;
	}

	private Mat binarizeImage(Mat image) {
		log.info("Binarize image");
		Mat binaryImage = new Mat();
		Imgproc.threshold(image, binaryImage, 128, 255, Imgproc.THRESH_BINARY);
		return binaryImage;
	}

	private Mat invertImage(Mat image) {
		log.info("invert image");
		Mat invertImage = new Mat();
		Core.bitwise_not(image, invertImage);
		return invertImage;
	}

	private Mat applyAdaptiveHistogramEqualization(Mat image) {
		log.info("Applying Adaptive Histogram Equalization");
		Mat claheImage = new Mat();
		Size tileGridSize = new Size(60, 60);
		Imgproc.createCLAHE(128, tileGridSize).apply(image, claheImage);
		return claheImage;
	}

	private Mat applyEqualization(Mat image) {
		log.info("Applying Equalization");
		Mat equalizedImage = new Mat();
		Imgproc.equalizeHist(image, equalizedImage);
		return equalizedImage;
	}

	private Mat removeBackground(Mat image) {
		log.info("Removing background of image");
		Mat mask = removeBackgroundSimple(image);
		Core.bitwise_not(mask, mask);
		Core.inRange(mask, new Scalar(0, 0, 0), new Scalar(10, 10, 10), mask);
		Core.bitwise_not(mask, mask);
		return removeBackgroundFromMask(image, mask);
	}

	private Mat removeBackgroundSimple(Mat image) {
		log.info("Removing simple background of image");

		int r = image.rows();
		int c = image.cols();
		Point p1 = new Point(c / 100, r / 100);
		Point p2 = new Point(c - c / 100, r - r / 100);
		Rect rect = new Rect(p1, p2);
		Mat mask = new Mat();
		Mat fgdModel = new Mat();
		Mat bgdModel = new Mat();

		Imgproc.grabCut(image, mask, rect, bgdModel, fgdModel, 5, Imgproc.GC_INIT_WITH_RECT);

		Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
		Core.compare(mask, source, mask, Core.CMP_EQ);

		Mat foreground = new Mat(image.size(), CvType.CV_8UC3, new Scalar(255,
				255, 255, 255));
		image.copyTo(foreground, mask);
		return foreground;
	}

	private Mat removeBackgroundFromMask(Mat image, Mat mask) {
		log.info("Removing background from mask");
		int r = image.rows();
		int c = image.cols();
		Point p1 = new Point(c / 100, r / 100);
		Point p2 = new Point(c - c / 100, r - r / 100);
		Rect rect = new Rect(p1, p2);
		Mat fgdModel = new Mat();
		Mat bgdModel = new Mat();

		convertToOpencvValues(mask); // from human readable values to OpenCV values
		Imgproc.grabCut(image, mask, rect, bgdModel, fgdModel, 5, Imgproc.GC_INIT_WITH_MASK);
		convertToHumanValues(mask); // back to human readable values
		Imgproc.threshold(mask, mask, 128, 255, Imgproc.THRESH_TOZERO);

		Mat foreground = new Mat(image.size(), CvType.CV_8UC1, new Scalar(0, 0, 0));
		image.copyTo(foreground, mask);
		return foreground;
	}

	private Mat removeBackgroundAdvanced(Mat image) {
		log.info("Removing advanced background of image");
		Mat mask = new Mat();
		Imgproc.threshold(image, mask, 128, 255, Imgproc.THRESH_BINARY);

		Core.inRange(mask, new Scalar(0, 0, 0), new Scalar(10, 10, 10), mask);
		Core.bitwise_not(mask, mask);

		return removeBackgroundFromMask(image, mask);
	}

	private Mat applyGaborFilter(Mat image) {
		log.info("Applying Gabor filter");
		image.convertTo(image, CvType.CV_32F);

		// predefined parameters for Gabor kernel
		Size kSize = new Size(31, 31);
		double sigma = 30;
		double lambda = 30;
		double gamma = 0.25;
		double psi = 0;

		// double sigma = 24;
		// double lambda = 30;
		// double gamma = 1;
		// double psi = 0;

		int numberOfGabors = 16;
		List<Mat> gabors = new ArrayList<>(numberOfGabors);
		List<Double> thetas = new ArrayList<>(numberOfGabors);
		List<Mat> kernels = new ArrayList<>(numberOfGabors);

		double thetaIncrement = 180 / numberOfGabors;

		Mat enhanced = new Mat(image.width(), image.height(), CvType.CV_32F);

		for (int i = 0; i < numberOfGabors; i++) {
			gabors.add(new Mat(image.width(), image.height(), CvType.CV_32F));
			thetas.add(i * thetaIncrement);
			kernels.add(Imgproc.getGaborKernel(kSize, sigma, thetas.get(i), lambda, gamma, psi, CvType.CV_32F));
			Imgproc.filter2D(image, gabors.get(i), -1, kernels.get(i));
		}

		for (int i = 0; i < numberOfGabors - 1; i++) {
			if (i == 0) {
				Core.addWeighted(gabors.get(i), 0, gabors.get(i + 1), 1, 0, enhanced);
			} else {
				Core.addWeighted(enhanced, 1, gabors.get(i + 1), 1, 0, enhanced);
			}
		}
		return enhanced;
	}

	private Mat applyEdgeDetector(Mat image) {
		log.info("Applying Canny edge detector");
		Mat detectedEdges = new Mat(image.height(), image.width(), CvType.CV_8UC1);
		Imgproc.blur(image, detectedEdges, new Size(3, 3));
		Imgproc.Canny(detectedEdges, detectedEdges, 10, 30);

		Mat dest = new Mat(image.height(), image.width(), CvType.CV_8UC1);
		image.copyTo(dest, detectedEdges);
		return dest;
	}

	private void convertToHumanValues(Mat mask) {
		byte[] buffer = new byte[3];
		for (int x = 0; x < mask.rows(); x++) {
			for (int y = 0; y < mask.cols(); y++) {
				mask.get(x, y, buffer);
				int value = buffer[0];
				if (value == Imgproc.GC_BGD) {
					buffer[0] = 0; // for sure background
				} else if (value == Imgproc.GC_PR_BGD) {
					buffer[0] = 85; // probably background
				} else if (value == Imgproc.GC_PR_FGD) {
					buffer[0] = (byte) 170; // probably foreground
				} else {
					buffer[0] = (byte) 255; // for sure foreground

				}
				mask.put(x, y, buffer);
			}
		}
	}

	private void convertToOpencvValues(Mat mask) {
		byte[] buffer = new byte[3];
		for (int x = 0; x < mask.rows(); x++) {
			for (int y = 0; y < mask.cols(); y++) {
				mask.get(x, y, buffer);
				int value = buffer[0];
				if (value >= 0 && value < 64) {
					buffer[0] = Imgproc.GC_BGD; // for sure background
				} else if (value >= 64 && value < 128) {
					buffer[0] = Imgproc.GC_PR_BGD; // probably background
				} else if (value >= 128 && value < 192) {
					buffer[0] = Imgproc.GC_PR_FGD; // probably foreground
				} else {
					buffer[0] = Imgproc.GC_FGD; // for sure foreground

				}
				mask.put(x, y, buffer);
			}
		}

	}

}
