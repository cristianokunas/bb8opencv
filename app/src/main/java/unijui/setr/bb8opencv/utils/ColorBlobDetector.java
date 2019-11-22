package unijui.setr.bb8opencv.utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import unijui.setr.bb8opencv.ColorBlobDetectionActivity;

public class ColorBlobDetector {
//    // Lower and Upper bounds for range checking in HSV color space
//    private Scalar mLowerBound = new Scalar(0);
//    private Scalar mUpperBound = new Scalar(0);
//    // Minimum contour area in percent for contours filtering
//    private static double mMinContourArea = 0.1;
//    // Color radius for range checking in HSV color space
//    private Scalar mColorRadius = new Scalar(25,50,50,0);
//    private Mat mSpectrum = new Mat();
//    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
//
//    // Cache
//    Mat mPyrDownMat = new Mat();
//    Mat mHsvMat = new Mat();
//    Mat mMask = new Mat();
//    Mat mDilatedMask = new Mat();
//    Mat mHierarchy = new Mat();
//
//    public void setColorRadius(Scalar radius) {
//        mColorRadius = radius;
//    }
//
//    public void setHsvColor(Scalar hsvColor) {
//        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
//        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;
//
//        mLowerBound.val[0] = minH;
//        mUpperBound.val[0] = maxH;
//
//        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
//        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];
//
//        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
//        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];
//
//        mLowerBound.val[3] = 0;
//        mUpperBound.val[3] = 255;
//
//        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);
//
//        for (int j = 0; j < maxH-minH; j++) {
//            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
//            spectrumHsv.put(0, j, tmp);
//        }
//
//        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
//    }
//
//    public Mat getSpectrum() {
//        return mSpectrum;
//    }
//
//    public void setMinContourArea(double area) {
//        mMinContourArea = area;
//    }
//
//    public void process(Mat rgbaImage) {
//        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
//        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
//
//        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
//
//        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
//        Imgproc.dilate(mMask, mDilatedMask, new Mat());
//
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//
//        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Find max contour area
//        double maxArea = 0;
//        Iterator<MatOfPoint> each = contours.iterator();
//        while (each.hasNext()) {
//            MatOfPoint wrapper = each.next();
//            double area = Imgproc.contourArea(wrapper);
//            if (area > maxArea)
//                maxArea = area;
//        }
//
//        // Filter contours by area and resize to fit the original image size
//        mContours.clear();
//        each = contours.iterator();
//        while (each.hasNext()) {
//            MatOfPoint contour = each.next();
//            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
//                Core.multiply(contour, new Scalar(4,4), contour);
//                mContours.add(contour);
//            }
//        }
//    }
//
//    public List<MatOfPoint> getContours() {
//        return mContours;
//    }






    private static double mMinContourArea = 0.1d;
    int dilate_val = 0;
    int guna1_val = 0;
    int guna2_val = 0;
    int guna3_val = 0;
    int guna4_val = 0;
    double kawasan = 0.0d;
    private ColorBlobDetectionActivity mActivity;
    private Scalar mColorRadius = new Scalar(20.0d, 50.0d, 50.0d, 0.0d);
    private Scalar mColorRadiusOPG = new Scalar(20.0d, 50.0d, 50.0d, 0.0d);
    private Scalar mColorRadiusOWG = new Scalar(20.0d, 50.0d, 50.0d, 0.0d);
    private Scalar mColorRadiusOWT = new Scalar(20.0d, 50.0d, 50.0d, 0.0d);
    private List<MatOfPoint> mContours = new ArrayList();
    private List<MatOfPoint> mContours2 = new ArrayList();
    private List<MatOfPoint> mContours3 = new ArrayList();
    private List<MatOfPoint> mContours4 = new ArrayList();
    Mat mDilatedMask = new Mat();
    Mat mDilatedMask2 = new Mat();
    Mat mDilatedMask3 = new Mat();
    Mat mDilatedMask4 = new Mat();
    Mat mHierarchy = new Mat();
    Mat mHierarchy2 = new Mat();
    Mat mHierarchy3 = new Mat();
    Mat mHierarchy4 = new Mat();
    Mat mHsvMat = new Mat();
    Mat mHsvMat2 = new Mat();
    Mat mHsvMat3 = new Mat();
    Mat mHsvMat4 = new Mat();
    private Scalar mLowerBoundBall = new Scalar(0.0d);
    private Scalar mLowerBoundOPG = new Scalar(0.0d);
    private Scalar mLowerBoundOWG = new Scalar(0.0d);
    private Scalar mLowerBoundOWT = new Scalar(0.0d);
    Mat mMask = new Mat();
    Mat mMask2 = new Mat();
    Mat mMask3 = new Mat();
    Mat mMask4 = new Mat();
    Mat mPyrDownMat = new Mat();
    Mat mPyrDownMat2 = new Mat();
    Mat mPyrDownMat3 = new Mat();
    Mat mPyrDownMat4 = new Mat();
    private Mat mSpectrum1 = new Mat();
    private Mat mSpectrum2 = new Mat();
    private Mat mSpectrum3 = new Mat();
    private Mat mSpectrum4 = new Mat();
    private Scalar mUpperBoundBall = new Scalar(0.0d);
    private Scalar mUpperBoundOPG = new Scalar(0.0d);
    private Scalar mUpperBoundOWG = new Scalar(0.0d);
    private Scalar mUpperBoundOWT = new Scalar(0.0d);

    public void setGunaVal1(int guna01) {
        this.guna1_val = guna01;
    }

    public void setGunaVal2(int guna02) {
        this.guna2_val = guna02;
    }

    public void setGunaVal3(int guna03) {
        this.guna3_val = guna03;
    }

    public void setGunaVal4(int guna04) {
        this.guna4_val = guna04;
    }

    public void clearContour1() {
        this.mContours.clear();
    }

    public void clearContour2() {
        this.mContours2.clear();
    }

    public void clearContour3() {
        this.mContours3.clear();
    }

    public void clearContour4() {
        this.mContours4.clear();
    }

    public void setDilateVal(int dailet) {
        this.dilate_val = dailet;
    }

    public void setColorRadiusBall(Scalar radius1) {
        this.mColorRadius = radius1;
    }

    public void setHsvColorBall(Scalar hsvColor) {
        double minH = hsvColor.val[0] >= this.mColorRadius.val[0] ? hsvColor.val[0] - this.mColorRadius.val[0] : 0.0d;
        double maxH = hsvColor.val[0] + this.mColorRadius.val[0] <= 255.0d ? hsvColor.val[0] + this.mColorRadius.val[0] : 255.0d;
        this.mLowerBoundBall.val[0] = minH;
        this.mUpperBoundBall.val[0] = maxH;
        this.mLowerBoundBall.val[1] = hsvColor.val[1] - this.mColorRadius.val[1];
        this.mUpperBoundBall.val[1] = hsvColor.val[1] + this.mColorRadius.val[1];
        this.mLowerBoundBall.val[2] = hsvColor.val[2] - this.mColorRadius.val[2];
        this.mUpperBoundBall.val[2] = hsvColor.val[2] + this.mColorRadius.val[2];
        this.mLowerBoundBall.val[3] = 0.0d;
        this.mUpperBoundBall.val[3] = 255.0d;
        Mat spectrumHsv = new Mat(1, (int) (maxH - minH), CvType.CV_8UC3);
        for (int j = 0; ((double) j) < maxH - minH; j++) {
            spectrumHsv.put(0, j, new byte[]{(byte) ((int) (((double) j) + minH)), -1, -1});
        }
        Imgproc.cvtColor(spectrumHsv, this.mSpectrum1, 71, 4);
    }

    public void setColorRadiusOWG(Scalar radius2) {
        this.mColorRadiusOWG = radius2;
    }

    public void setHsvColorOWG(Scalar hsvColor2) {
        double minH2 = hsvColor2.val[0] >= this.mColorRadius.val[0] ? hsvColor2.val[0] - this.mColorRadius.val[0] : 0.0d;
        double maxH2 = hsvColor2.val[0] + this.mColorRadius.val[0] <= 255.0d ? hsvColor2.val[0] + this.mColorRadius.val[0] : 255.0d;
        this.mLowerBoundOWG.val[0] = minH2;
        this.mUpperBoundOWG.val[0] = maxH2;
        this.mLowerBoundOWG.val[1] = hsvColor2.val[1] - this.mColorRadiusOWG.val[1];
        this.mUpperBoundOWG.val[1] = hsvColor2.val[1] + this.mColorRadiusOWG.val[1];
        this.mLowerBoundOWG.val[2] = hsvColor2.val[2] - this.mColorRadiusOWG.val[2];
        this.mUpperBoundOWG.val[2] = hsvColor2.val[2] + this.mColorRadiusOWG.val[2];
        this.mLowerBoundOWG.val[3] = 0.0d;
        this.mUpperBoundOWG.val[3] = 255.0d;
    }

    public void setColorRadiusOPG(Scalar radius3) {
        this.mColorRadiusOPG = radius3;
    }

    public void setHsvColorOPG(Scalar hsvColor3) {
        double minH3 = hsvColor3.val[0] >= this.mColorRadiusOPG.val[0] ? hsvColor3.val[0] - this.mColorRadiusOPG.val[0] : 0.0d;
        double maxH3 = hsvColor3.val[0] + this.mColorRadiusOPG.val[0] <= 255.0d ? hsvColor3.val[0] + this.mColorRadiusOPG.val[0] : 255.0d;
        this.mLowerBoundOPG.val[0] = minH3;
        this.mUpperBoundOPG.val[0] = maxH3;
        this.mLowerBoundOPG.val[1] = hsvColor3.val[1] - this.mColorRadiusOPG.val[1];
        this.mUpperBoundOPG.val[1] = hsvColor3.val[1] + this.mColorRadiusOPG.val[1];
        this.mLowerBoundOPG.val[2] = hsvColor3.val[2] - this.mColorRadiusOPG.val[2];
        this.mUpperBoundOPG.val[2] = hsvColor3.val[2] + this.mColorRadiusOPG.val[2];
        this.mLowerBoundOPG.val[3] = 0.0d;
        this.mUpperBoundOPG.val[3] = 255.0d;
    }

    public void setColorRadiusOWT(Scalar radius4) {
        this.mColorRadiusOWT = radius4;
    }

    public void setHsvColorOWT(Scalar hsvColor4) {
        double minH4 = hsvColor4.val[0] >= this.mColorRadiusOWT.val[0] ? hsvColor4.val[0] - this.mColorRadiusOWT.val[0] : 0.0d;
        double maxH4 = hsvColor4.val[0] + this.mColorRadiusOWT.val[0] <= 255.0d ? hsvColor4.val[0] + this.mColorRadiusOWT.val[0] : 255.0d;
        this.mLowerBoundOWT.val[0] = minH4;
        this.mUpperBoundOWT.val[0] = maxH4;
        this.mLowerBoundOWT.val[1] = hsvColor4.val[1] - this.mColorRadiusOWT.val[1];
        this.mUpperBoundOWT.val[1] = hsvColor4.val[1] + this.mColorRadiusOWT.val[1];
        this.mLowerBoundOWT.val[2] = hsvColor4.val[2] - this.mColorRadiusOWT.val[2];
        this.mUpperBoundOWT.val[2] = hsvColor4.val[2] + this.mColorRadiusOWT.val[2];
        this.mLowerBoundOWT.val[3] = 0.0d;
        this.mUpperBoundOWT.val[3] = 255.0d;
    }

    public Mat getSpectrum() {
        return this.mSpectrum1;
    }

    public Mat getSpectrum2() {
        return this.mSpectrum2;
    }

    public Mat getSpectrum3() {
        return this.mSpectrum3;
    }

    public Mat getSpectrum4() {
        return this.mSpectrum4;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, this.mPyrDownMat);
        Imgproc.pyrDown(this.mPyrDownMat, this.mPyrDownMat);
        if (this.dilate_val == 1) {
            Imgproc.dilate(this.mPyrDownMat, this.mHsvMat, new Mat());
            Imgproc.cvtColor(this.mHsvMat, this.mDilatedMask, 67);
        } else {
            Imgproc.cvtColor(this.mPyrDownMat, this.mDilatedMask, 67);
        }
        if (this.guna1_val == 1) {
            Core.inRange(this.mDilatedMask, this.mLowerBoundBall, this.mUpperBoundBall, this.mMask);
            ArrayList<MatOfPoint> arrayList = new ArrayList<>();
            Imgproc.findContours(this.mMask, arrayList, this.mHierarchy, 0, 2);
            double maxArea = 0.0d;
            for (MatOfPoint wrapper : arrayList) {
                double area = Imgproc.contourArea(wrapper);
                if (area > maxArea) {
                    maxArea = area;
                }
                this.kawasan = area;
            }
            this.mContours.clear();
            for (MatOfPoint contour : arrayList) {
                if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
                    Scalar scalar = new Scalar(4.0d, 4.0d);
                    Core.multiply((Mat) contour, scalar, (Mat) contour);
                    this.mContours.add(contour);
                }
            }
        }
        if (this.guna2_val == 1) {
            Core.inRange(this.mDilatedMask, this.mLowerBoundOWG, this.mUpperBoundOWG, this.mMask2);
            ArrayList<MatOfPoint> arrayList2 = new ArrayList<>();
            Imgproc.findContours(this.mMask2, arrayList2, this.mHierarchy, 0, 2);
            double maxArea2 = 0.0d;
            for (MatOfPoint wrapper2 : arrayList2) {
                double area2 = Imgproc.contourArea(wrapper2);
                if (area2 > maxArea2) {
                    maxArea2 = area2;
                }
            }
            this.mContours2.clear();
            for (MatOfPoint contour2 : arrayList2) {
                if (Imgproc.contourArea(contour2) > mMinContourArea * maxArea2) {
                    Scalar scalar2 = new Scalar(4.0d, 4.0d);
                    Core.multiply((Mat) contour2, scalar2, (Mat) contour2);
                    this.mContours2.add(contour2);
                }
            }
        }
        if (this.guna3_val == 1) {
            Core.inRange(this.mDilatedMask, this.mLowerBoundOPG, this.mUpperBoundOPG, this.mMask3);
            ArrayList<MatOfPoint> arrayList3 = new ArrayList<>();
            Imgproc.findContours(this.mMask3, arrayList3, this.mHierarchy, 0, 2);
            double maxArea3 = 0.0d;
            for (MatOfPoint wrapper3 : arrayList3) {
                double area3 = Imgproc.contourArea(wrapper3);
                if (area3 > maxArea3) {
                    maxArea3 = area3;
                }
            }
            this.mContours3.clear();
            for (MatOfPoint contour3 : arrayList3) {
                if (Imgproc.contourArea(contour3) > mMinContourArea * maxArea3) {
                    Scalar scalar3 = new Scalar(4.0d, 4.0d);
                    Core.multiply((Mat) contour3, scalar3, (Mat) contour3);
                    this.mContours3.add(contour3);
                }
            }
        }
        if (this.guna4_val == 1) {
            Core.inRange(this.mDilatedMask, this.mLowerBoundOWT, this.mUpperBoundOWT, this.mMask4);
            ArrayList<MatOfPoint> arrayList4 = new ArrayList<>();
            Imgproc.findContours(this.mMask4, arrayList4, this.mHierarchy, 0, 2);
            double maxArea4 = 0.0d;
            for (MatOfPoint wrapper4 : arrayList4) {
                double area4 = Imgproc.contourArea(wrapper4);
                if (area4 > maxArea4) {
                    maxArea4 = area4;
                }
            }
            this.mContours4.clear();
            for (MatOfPoint contour4 : arrayList4) {
                if (Imgproc.contourArea(contour4) > mMinContourArea * maxArea4) {
                    Scalar scalar4 = new Scalar(4.0d, 4.0d);
                    Core.multiply((Mat) contour4, scalar4, (Mat) contour4);
                    this.mContours4.add(contour4);
                }
            }
        }
    }

    public List<MatOfPoint> getContours() {
        return this.mContours;
    }

    public List<MatOfPoint> getContours2() {
        return this.mContours2;
    }

    public List<MatOfPoint> getContours3() {
        return this.mContours3;
    }

    public List<MatOfPoint> getContours4() {
        return this.mContours4;
    }

    public double pulang_kawasan() {
        return this.kawasan;
    }

}
