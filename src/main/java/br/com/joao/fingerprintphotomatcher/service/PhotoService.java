package br.com.joao.fingerprintphotomatcher.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;

@Service
@Slf4j
public class PhotoService {

    @PostConstruct
    private void init() {
        OpenCV.loadShared();
    }

    public Mat getMatFromByteArrayImage(byte[] image) {
        return Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    public byte[] getByteArrayImageFromMat(Mat image) {
        MatOfByte matOfByte = new MatOfByte();
        // Imgcodecs.imencode(".jpg", image, matOfByte);
        Imgcodecs.imencode(".png", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        return byteArray;
    }

    public Mat convertImageToGrayScale(Mat image) {
        log.info("Converting image to gray scale");
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
        return image;
    }

    public Mat normalizeImage(Mat image) {
        log.info("Normalizing image");
        Core.normalize(image, image, 0, 128, Core.NORM_MINMAX);
        return image;
    }

    public Mat binarizeImage(Mat image) {
        log.info("Binarize image");
        Imgproc.threshold(image, image, 128, 255, Imgproc.THRESH_BINARY);
        return image;
    }

    public Mat invertImage(Mat image) {
        log.info("invert image");
        Core.bitwise_not(image, image);
        return image;
    }

    public Mat applyAdaptiveHistogramEqualization(Mat image) {
        log.info("Applying Adaptive Histogram Equalization");
        Mat claheImage = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        Size tileGridSize = new Size(60, 60);
        Imgproc.createCLAHE(128, tileGridSize).apply(image, claheImage);
        return claheImage;
    }

    public Mat applyEqualization(Mat image) {
        log.info("Applying Equalization");
        Mat equalizedImage = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        Imgproc.equalizeHist(image, equalizedImage);
        return equalizedImage;
    }

    public Mat applyGaborFilter(Mat image) {
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

    public Mat applyEdgeDetector(Mat image) {
        log.info("Applying Canny edge detector");
        // Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);
        Mat detectedEdges = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        Imgproc.blur(image, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, 10, 30);

        Mat dest = new Mat(image.height(), image.width(), CvType.CV_8UC1);
        image.copyTo(dest, detectedEdges);
        return dest;
    }

    public Mat removeBackground(Mat image) {
        log.info("Removing background of image");

        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        // thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(image.size(), CvType.CV_8U);
        Imgproc.cvtColor(image, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = getHistAverage(hsvImg, hsvPlanes.get(0));

        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

        Imgproc.blur(thresholdImg, thresholdImg, new Size(5, 5));

        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 3);

        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(image.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        image.copyTo(foreground, thresholdImg);

        // Rect rectangle = new Rect(0, 0, image.width(), image.height());
        // Mat result = Mat.zeros(image.height(), image.width(), CvType.CV_8U);
        // Mat bgdModel = Mat.zeros(1, 65, CvType.CV_64F);
        // Mat fgdModel = Mat.zeros(1, 65, CvType.CV_64F);
        // Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(3));
        // Imgproc.grabCut(image, result, rectangle, bgdModel, fgdModel, 8,
        // Imgproc.GC_INIT_WITH_MASK);
        // Core.compare(result, source, result, Core.CMP_EQ);
        // Mat foreground = new Mat(image.size(), CvType.CV_8UC3, new Scalar(255, 255,
        // 255));
        // image.copyTo(foreground, result);

        return foreground;
    }

    private double getHistAverage(Mat hsvImg, Mat hueValues) {
        // init
        double average = 0.0;
        Mat hist_hue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (hist_hue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        return average = average / hsvImg.size().height / hsvImg.size().width;
    }

}
