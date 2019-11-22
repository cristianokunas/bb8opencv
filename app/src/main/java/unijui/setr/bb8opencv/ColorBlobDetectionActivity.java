package unijui.setr.bb8opencv;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Window;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import org.opencv.android.BaseLoaderCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColorBlobDetectionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG = "OCVSample::Activity";
    static {
        if (!OpenCVLoader.initDebug()){
            Log.d("TAG", "OpenCV not loaded");
        } else {
            Log.d("TAG", "OpenCV loaded");
        }
    }

        /* Colors */

    Scalar Color_Black = new Scalar(0.0d, 0.0d, 0.0d, 255.0d);
    Scalar Color_Blue = new Scalar(0.0d, 0.0d, 255.0d, 255.0d);
    Scalar Color_Cyan = new Scalar(0.0d, 255.0d, 255.0d, 255.0d);
    Scalar Color_DarkGray = new Scalar(150.0d, 150.0d, 150.0d, 255.0d);
    Scalar Color_DarkGreen = new Scalar(0.0d, 128.0d, 0.0d, 255.0d);
    Scalar Color_GreenBlue = new Scalar(10.0d, 160.0d, 190.0d, 255.0d);
    Scalar Color_LightGreen = new Scalar(0.0d, 255.0d, 0.0d, 255.0d);
    Scalar Color_Magenta = new Scalar(255.0d, 0.0d, 255.0d, 255.0d);
    Scalar Color_NiceGreen = new Scalar(100.0d, 200.0d, 100.0d, 255.0d);
    Scalar Color_Orange = new Scalar(255.0d, 128.0d, 64.0d, 255.0d);
    Scalar Color_PaleBlue = new Scalar(180.0d, 180.0d, 255.0d, 255.0d);
    Scalar Color_PalePink = new Scalar(255.0d, 190.0d, 225.0d, 255.0d);
    Scalar Color_PaleYellow = new Scalar(255.0d, 255.0d, 180.0d, 255.0d);
    Scalar Color_Purple = new Scalar(128.0d, 0.0d, 255.0d, 255.0d);
    Scalar Color_Red = new Scalar(255.0d, 0.0d, 0.0d, 255.0d);
    Scalar Color_White = new Scalar(255.0d, 255.0d, 255.0d, 255.0d);
    Scalar Color_Yellow = new Scalar(255.0d, 255.0d, 0.0d, 255.0d);

////   HSV   Green
//    int iLowH  = 45;
//    int iHighH  = 75;
//    int iLowS  = 20;
//    int iHighS  = 255;
//    int iLowV  = 10;
//    int iHighV  = 255;

    //   HSV   Green
    int iLowH  = 29;
    int iHighH  = 64;
    int iLowS  = 86;
    int iHighS  = 255;
    int iLowV  = 6;
    int iHighV  = 255;

    Mat  imgHSV, imgThresholded,imgThresholded2, mataux, circles;
    Scalar sc1, sc2;
    private Mat mRgba;
    float center_horz = 0.0f;
    float center_vert = 0.0f;
    float cols;
    float rows;
    double diameter = 0;
    int maxHeight;
    int maxWidth;
    float font_size;
    int border = 15;

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        new Intent("android.intent.action.MAIN").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setContentView(R.layout.color_blob_detection_surface_view);
        sc1 = new Scalar(iLowH, iLowS, iLowV);
        sc2 = new Scalar(iHighH, iHighS, iHighV);

        cameraBridgeViewBase = findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setCameraIndex(0); //0 for rear and 1 for front
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

//        cameraBridgeViewBase.setMaxFrameSize(1280,720);
        cameraBridgeViewBase.setMaxFrameSize(960,540);
//        cameraBridgeViewBase.setMaxFrameSize(720,480);
//        cameraBridgeViewBase.setMaxFrameSize(352,288);
//        cameraBridgeViewBase.setMaxFrameSize(320,180);
        this.maxHeight = cameraBridgeViewBase.getHeight();
        this.maxWidth = cameraBridgeViewBase.getWidth();

        ActivityCompat.requestPermissions(
                this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                },
                PackageManager.PERMISSION_GRANTED
        );

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        imgHSV = new Mat(this.maxHeight, this.maxWidth, CvType.CV_32FC1);
        imgThresholded = new Mat(this.maxHeight, this.maxWidth, CvType.CV_8U);
        imgThresholded2 = new Mat(this.maxHeight, this.maxWidth, CvType.CV_8U);
        mataux = new Mat(this.maxHeight, this.maxWidth, CvType.CV_32FC1);
        circles = new Mat(this.maxHeight, this.maxWidth, CvType.CV_32FC1);

        mRgba = new Mat(height,width, CvType.CV_8UC4);
        cols = (float) this.mRgba.cols();
        rows = (float) this.mRgba.rows();
        center_horz = (float) (this.mRgba.cols() / 2);
        center_vert = (float) (this.mRgba.rows() / 2);

        if (this.cols > 1500.0f) {
            this.font_size = 1.0f;
        } else {
            this.font_size = this.rows / this.cols;
        }
    }

    @Override
    public void onCameraViewStopped() {
        imgHSV.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Point center_frame = new Point(center_horz, center_vert);
        this.mRgba = inputFrame.rgba();
        int x1_horz = (int) (center_horz - (cols/2) + this.border);
        int y1_horz = (int) center_vert;
        int x2_horz = (int) (center_horz + (cols/2) - this.border);
        int y2_horz = (int) center_vert;
        int x1_vert = (int) center_horz;
        int y1_vert = (int) (center_vert - (rows/2) + this.border);
        int x2_vert = (int) center_horz;
        int y2_vert = (int) (center_vert + (rows/2) - this.border);
        Point p1_h = new Point(x1_horz, y1_horz);
        Point p2_h = new Point(x2_horz, y2_horz);
        Point p1_v = new Point(x1_vert, y1_vert);
        Point p2_v = new Point(x2_vert, y2_vert);

        // lines central
        Imgproc.line(mRgba, p1_h, p2_h, new Scalar(256,256,0),3);
        Imgproc.line(mRgba, p1_v, p2_v, new Scalar(256,256,0),3);
        // Point central_frame
        Imgproc.line(mRgba, center_frame, center_frame, this.Color_Purple,9);

        // lines border
        Imgproc.line(this.mRgba,
                new Point((double) this.border, (double) this.border),
                new Point((double) this.border, (double) (this.mRgba.rows() - this.border)),
                this.Color_Purple, 3);
        Imgproc.line(this.mRgba,
                new Point((double) (this.mRgba.cols() - this.border), (double) this.border),
                new Point((double) (this.mRgba.cols() - this.border), (double) (this.mRgba.rows() - this.border)),
                this.Color_Purple, 3);
        Imgproc.line(this.mRgba,
                new Point((double) this.border, (double) this.border),
                new Point((double) (this.mRgba.cols() - this.border), (double) this.border),
                this.Color_Purple, 3);
        Imgproc.line(this.mRgba,
                new Point((double) this.border, (double) (this.mRgba.rows() - this.border)),
                new Point((double) (this.mRgba.cols() - this.border), (double) (this.mRgba.rows() - this.border)),
                this.Color_Purple, 3);

        Log.i(TAG, "Coordinates: (rows: " + mRgba.rows() + ", cols: " + mRgba.cols() + ", c h: " + this.center_horz + ", c v: " + this.center_vert +
                ", x1: " + x1_horz + ", y1: " + y1_horz + ", x2: " + x2_horz + ", y2: " + y2_horz + ", center frame: "+ center_frame+")");

//        //! [convert_to_hsv]
//        Imgproc.cvtColor(mRgba, imgHSV, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(imgHSV, sc1, sc2, imgThresholded);
//        //! [convert_to_hsv]
//
//        //! [reduce_noise]
////        Imgproc.medianBlur(imgThresholded, imgThresholded, 9);
//        Imgproc.GaussianBlur(imgThresholded, imgThresholded, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
//        //! [reduce_noise]

        //! [reduce_noise]
//        Imgproc.medianBlur(imgThresholded, imgThresholded, 9);
        Imgproc.GaussianBlur(mRgba, mataux, new Size(11, 11), 0);
        //! [reduce_noise]

        //! [convert_to_hsv]
        Imgproc.cvtColor(mataux, imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.inRange(imgHSV, sc1, sc2, imgThresholded);
        //! [convert_to_hsv]

        final Size kernelSize = new Size(11, 11);
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, kernelSize);
        final Point anchor = new Point(-1, -1);
        final int iterations = 2;
        Imgproc.erode(imgThresholded, imgThresholded, kernel, anchor, iterations);
        Imgproc.dilate(imgThresholded, imgThresholded, kernel, anchor, iterations);

        //! [houghcircles]
        Mat circles = new Mat();
        Imgproc.HoughCircles(imgThresholded, circles, Imgproc.HOUGH_GRADIENT, 2.0,
                (double)imgThresholded.rows()/4, // change this value to detect circles with different distances to each other
                100.0, 30.0, 5, 300); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        //! [houghcircles]

        double a = 0.0;
        double b = 0.0;
        int r = 0;
        //! [draw]
        for (int x = 0; x < circles.rows(); x++) {
            double[] c = circles.get(x, 0);
            for(int j = 0 ; j < c.length ; j++){
                a = c[0];
                b = c[1];
                r = (int) c[2];
            }
            Point center = new Point(a, b);
            // circle center
            Imgproc.circle(mRgba, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            Imgproc.circle(mRgba, center, r, new Scalar(255,0,255), 7, 8, 0 );
            // line center_frame with center_circle
            Imgproc.line(mRgba, center_frame, center, new Scalar(0,0,256),7);
            // diameter circle
            this.diameter = (r * 2);
        }
        // Diameter Display text
        Imgproc.putText(this.mRgba, "Diameter circle: ("+ this.diameter +")",
                new Point(0.0221d * ((double) this.cols), 0.0650d * ((double) this.rows)),
                4, (double) (1.0f * this.font_size), this.Color_PalePink, 1);
        //! [draw]

        return mRgba;

//#1 ------------------------------------------------------------------ for convert_to_gray
//
//        //! [convert_to_gray]
////        Imgproc.cvtColor(mRgba, mataux, Imgproc.COLOR_BGR2GRAY);
//        //! [convert_to_gray]
//
//        //![reduce_noise]
////        Imgproc.medianBlur(imgThresholded, imgThresholded, 9);
//        //![reduce_noise]
//1# ------------------------------------------------------------------

////#2 ------------------------------------------------------------------ LaplaceDemoRun
//        //! [variables]
//        // Declare the variables we are going to use
//        Mat src_gray = new Mat(), dst = new Mat();
//        int kernel_size = 3;
//        int scale = 1;
//        int delta = 0;
//        int ddepth = CvType.CV_16S;
//        String window_name = "Laplace Demo";
//        //! [variables]
//
//        //! [reduce_noise]
//        // Reduce noise by blurring with a Gaussian filter ( kernel size = 3 )
//        Imgproc.GaussianBlur( src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT );
//        //! [reduce_noise]
//
//        //! [convert_to_gray]
//        // Convert the image to grayscale
//        Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_RGB2GRAY );
//        //! [convert_to_gray]
//
//        /// Apply Laplace function
//        Mat abs_dst = new Mat();
//        //! [laplacian]
//        Imgproc.Laplacian( src_gray, dst, ddepth, kernel_size, scale, delta, Core.BORDER_DEFAULT );
//        //! [laplacian]
//
//        //! [convert]
//        // converting back to CV_8U
//        Core.convertScaleAbs( dst, abs_dst );
//        //! [convert]
//
//        return abs_dst;
//
////2# ------------------------------------------------------------------
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"Ha um problema no OpenCV", Toast.LENGTH_SHORT).show();
        }
        else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

   /*
    final SensorEventListener myListener = new SensorEventListener() {
        private SensorManager mSensorManager;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == 1) {
                ColorBlobDetectionActivity.this.accelerometerValues = event.values;
            }
            if (event.sensor.getType() == 2) {
                ColorBlobDetectionActivity.this.magneticFieldValues = event.values;
            }
            SensorManager.getRotationMatrix(ColorBlobDetectionActivity.this.rotate, null, ColorBlobDetectionActivity.this.accelerometerValues, ColorBlobDetectionActivity.this.magneticFieldValues);
            SensorManager.getOrientation(ColorBlobDetectionActivity.this.rotate, ColorBlobDetectionActivity.this.values);
            ColorBlobDetectionActivity.this.values[0] = (float) Math.toDegrees((double) ColorBlobDetectionActivity.this.values[0]);
            if (ColorBlobDetectionActivity.this.values[0] < 0.0f) {
                ColorBlobDetectionActivity.this.values[0] = ColorBlobDetectionActivity.this.values[0] + 360.0f;
            }
            ColorBlobDetectionActivity.this.rotation_compassMinus = ColorBlobDetectionActivity.this.values[0] - ColorBlobDetectionActivity.this.captured_dir;
            ColorBlobDetectionActivity.this.rotation_compassPlus = (ColorBlobDetectionActivity.this.captured_dir + 360.0f) - ColorBlobDetectionActivity.this.values[0];
            ColorBlobDetectionActivity.this.rotation_compassAnother = (360.0f - ColorBlobDetectionActivity.this.captured_dir) + ColorBlobDetectionActivity.this.values[0];
            ColorBlobDetectionActivity.this.smallest = ColorBlobDetectionActivity.this.rotation_compassMinus;
            if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus) < Math.abs(ColorBlobDetectionActivity.this.smallest)) {
                ColorBlobDetectionActivity.this.smallest = -ColorBlobDetectionActivity.this.rotation_compassPlus;
            }
            if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother) < Math.abs(ColorBlobDetectionActivity.this.smallest)) {
                ColorBlobDetectionActivity.this.smallest = ColorBlobDetectionActivity.this.rotation_compassAnother;
            }
            if (ColorBlobDetectionActivity.this.dir2 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus1 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir2);
                ColorBlobDetectionActivity.this.rotation_compassPlus1 = ((float) (ColorBlobDetectionActivity.this.dir2 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother1 = ((float) (360 - ColorBlobDetectionActivity.this.dir2)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest1 = ColorBlobDetectionActivity.this.rotation_compassMinus1;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus1) < Math.abs(ColorBlobDetectionActivity.this.smallest1)) {
                    ColorBlobDetectionActivity.this.smallest1 = -ColorBlobDetectionActivity.this.rotation_compassPlus1;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother1) < Math.abs(ColorBlobDetectionActivity.this.smallest1)) {
                    ColorBlobDetectionActivity.this.smallest1 = ColorBlobDetectionActivity.this.rotation_compassAnother1;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir1 = ColorBlobDetectionActivity.this.smallest1;
            }
            if (ColorBlobDetectionActivity.this.dir3 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus2 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir3);
                ColorBlobDetectionActivity.this.rotation_compassPlus2 = ((float) (ColorBlobDetectionActivity.this.dir3 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother2 = ((float) (360 - ColorBlobDetectionActivity.this.dir3)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest2 = ColorBlobDetectionActivity.this.rotation_compassMinus2;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus2) < Math.abs(ColorBlobDetectionActivity.this.smallest2)) {
                    ColorBlobDetectionActivity.this.smallest2 = -ColorBlobDetectionActivity.this.rotation_compassPlus2;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother2) < Math.abs(ColorBlobDetectionActivity.this.smallest2)) {
                    ColorBlobDetectionActivity.this.smallest2 = ColorBlobDetectionActivity.this.rotation_compassAnother2;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir2 = ColorBlobDetectionActivity.this.smallest2;
            }
            if (ColorBlobDetectionActivity.this.dir4 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus3 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir4);
                ColorBlobDetectionActivity.this.rotation_compassPlus3 = ((float) (ColorBlobDetectionActivity.this.dir4 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother3 = ((float) (360 - ColorBlobDetectionActivity.this.dir4)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest3 = ColorBlobDetectionActivity.this.rotation_compassMinus3;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus3) < Math.abs(ColorBlobDetectionActivity.this.smallest3)) {
                    ColorBlobDetectionActivity.this.smallest3 = -ColorBlobDetectionActivity.this.rotation_compassPlus3;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother3) < Math.abs(ColorBlobDetectionActivity.this.smallest3)) {
                    ColorBlobDetectionActivity.this.smallest3 = ColorBlobDetectionActivity.this.rotation_compassAnother3;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir3 = ColorBlobDetectionActivity.this.smallest3;
            }
            if (ColorBlobDetectionActivity.this.dir5 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus4 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir5);
                ColorBlobDetectionActivity.this.rotation_compassPlus4 = ((float) (ColorBlobDetectionActivity.this.dir5 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother4 = ((float) (360 - ColorBlobDetectionActivity.this.dir5)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest4 = ColorBlobDetectionActivity.this.rotation_compassMinus4;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus4) < Math.abs(ColorBlobDetectionActivity.this.smallest4)) {
                    ColorBlobDetectionActivity.this.smallest4 = -ColorBlobDetectionActivity.this.rotation_compassPlus4;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother4) < Math.abs(ColorBlobDetectionActivity.this.smallest4)) {
                    ColorBlobDetectionActivity.this.smallest4 = ColorBlobDetectionActivity.this.rotation_compassAnother4;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir4 = ColorBlobDetectionActivity.this.smallest4;
            }
            if (ColorBlobDetectionActivity.this.dir6 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus5 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir6);
                ColorBlobDetectionActivity.this.rotation_compassPlus5 = ((float) (ColorBlobDetectionActivity.this.dir6 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother5 = ((float) (360 - ColorBlobDetectionActivity.this.dir6)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest5 = ColorBlobDetectionActivity.this.rotation_compassMinus5;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus5) < Math.abs(ColorBlobDetectionActivity.this.smallest5)) {
                    ColorBlobDetectionActivity.this.smallest5 = -ColorBlobDetectionActivity.this.rotation_compassPlus5;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother5) < Math.abs(ColorBlobDetectionActivity.this.smallest5)) {
                    ColorBlobDetectionActivity.this.smallest5 = ColorBlobDetectionActivity.this.rotation_compassAnother5;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir5 = ColorBlobDetectionActivity.this.smallest5;
            }
            if (ColorBlobDetectionActivity.this.dir7 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus6 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir7);
                ColorBlobDetectionActivity.this.rotation_compassPlus6 = ((float) (ColorBlobDetectionActivity.this.dir7 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother6 = ((float) (360 - ColorBlobDetectionActivity.this.dir7)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest6 = ColorBlobDetectionActivity.this.rotation_compassMinus6;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus6) < Math.abs(ColorBlobDetectionActivity.this.smallest6)) {
                    ColorBlobDetectionActivity.this.smallest6 = -ColorBlobDetectionActivity.this.rotation_compassPlus6;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother6) < Math.abs(ColorBlobDetectionActivity.this.smallest6)) {
                    ColorBlobDetectionActivity.this.smallest6 = ColorBlobDetectionActivity.this.rotation_compassAnother6;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir6 = ColorBlobDetectionActivity.this.smallest6;
            }
            if (ColorBlobDetectionActivity.this.dir8 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus7 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir8);
                ColorBlobDetectionActivity.this.rotation_compassPlus7 = ((float) (ColorBlobDetectionActivity.this.dir8 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother7 = ((float) (360 - ColorBlobDetectionActivity.this.dir8)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest7 = ColorBlobDetectionActivity.this.rotation_compassMinus7;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus7) < Math.abs(ColorBlobDetectionActivity.this.smallest7)) {
                    ColorBlobDetectionActivity.this.smallest7 = -ColorBlobDetectionActivity.this.rotation_compassPlus7;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother7) < Math.abs(ColorBlobDetectionActivity.this.smallest7)) {
                    ColorBlobDetectionActivity.this.smallest7 = ColorBlobDetectionActivity.this.rotation_compassAnother7;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir7 = ColorBlobDetectionActivity.this.smallest7;
            }
            if (ColorBlobDetectionActivity.this.dir9 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus8 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir9);
                ColorBlobDetectionActivity.this.rotation_compassPlus8 = ((float) (ColorBlobDetectionActivity.this.dir9 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother8 = ((float) (360 - ColorBlobDetectionActivity.this.dir9)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest8 = ColorBlobDetectionActivity.this.rotation_compassMinus8;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus8) < Math.abs(ColorBlobDetectionActivity.this.smallest8)) {
                    ColorBlobDetectionActivity.this.smallest8 = -ColorBlobDetectionActivity.this.rotation_compassPlus8;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother8) < Math.abs(ColorBlobDetectionActivity.this.smallest8)) {
                    ColorBlobDetectionActivity.this.smallest8 = ColorBlobDetectionActivity.this.rotation_compassAnother8;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir8 = ColorBlobDetectionActivity.this.smallest8;
            }
            if (ColorBlobDetectionActivity.this.dir10 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus9 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir10);
                ColorBlobDetectionActivity.this.rotation_compassPlus9 = ((float) (ColorBlobDetectionActivity.this.dir10 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother9 = ((float) (360 - ColorBlobDetectionActivity.this.dir10)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest9 = ColorBlobDetectionActivity.this.rotation_compassMinus9;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus9) < Math.abs(ColorBlobDetectionActivity.this.smallest9)) {
                    ColorBlobDetectionActivity.this.smallest9 = -ColorBlobDetectionActivity.this.rotation_compassPlus9;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother9) < Math.abs(ColorBlobDetectionActivity.this.smallest9)) {
                    ColorBlobDetectionActivity.this.smallest9 = ColorBlobDetectionActivity.this.rotation_compassAnother9;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir9 = ColorBlobDetectionActivity.this.smallest9;
            }
            if (ColorBlobDetectionActivity.this.dir11 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus10 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir11);
                ColorBlobDetectionActivity.this.rotation_compassPlus10 = ((float) (ColorBlobDetectionActivity.this.dir11 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother10 = ((float) (360 - ColorBlobDetectionActivity.this.dir11)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest10 = ColorBlobDetectionActivity.this.rotation_compassMinus10;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus10) < Math.abs(ColorBlobDetectionActivity.this.smallest10)) {
                    ColorBlobDetectionActivity.this.smallest10 = -ColorBlobDetectionActivity.this.rotation_compassPlus10;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother10) < Math.abs(ColorBlobDetectionActivity.this.smallest10)) {
                    ColorBlobDetectionActivity.this.smallest10 = ColorBlobDetectionActivity.this.rotation_compassAnother10;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir10 = ColorBlobDetectionActivity.this.smallest10;
            }
            if (ColorBlobDetectionActivity.this.dir12 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus11 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir12);
                ColorBlobDetectionActivity.this.rotation_compassPlus11 = ((float) (ColorBlobDetectionActivity.this.dir12 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother11 = ((float) (360 - ColorBlobDetectionActivity.this.dir12)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest11 = ColorBlobDetectionActivity.this.rotation_compassMinus11;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus11) < Math.abs(ColorBlobDetectionActivity.this.smallest11)) {
                    ColorBlobDetectionActivity.this.smallest11 = -ColorBlobDetectionActivity.this.rotation_compassPlus11;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother11) < Math.abs(ColorBlobDetectionActivity.this.smallest11)) {
                    ColorBlobDetectionActivity.this.smallest11 = ColorBlobDetectionActivity.this.rotation_compassAnother11;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir11 = ColorBlobDetectionActivity.this.smallest11;
            }
            if (ColorBlobDetectionActivity.this.dir13 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus12 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir13);
                ColorBlobDetectionActivity.this.rotation_compassPlus12 = ((float) (ColorBlobDetectionActivity.this.dir13 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother12 = ((float) (360 - ColorBlobDetectionActivity.this.dir13)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest12 = ColorBlobDetectionActivity.this.rotation_compassMinus12;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus12) < Math.abs(ColorBlobDetectionActivity.this.smallest12)) {
                    ColorBlobDetectionActivity.this.smallest12 = -ColorBlobDetectionActivity.this.rotation_compassPlus12;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother12) < Math.abs(ColorBlobDetectionActivity.this.smallest12)) {
                    ColorBlobDetectionActivity.this.smallest12 = ColorBlobDetectionActivity.this.rotation_compassAnother12;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir12 = ColorBlobDetectionActivity.this.smallest12;
            }
            if (ColorBlobDetectionActivity.this.dir14 >= 0) {
                ColorBlobDetectionActivity.this.rotation_compassMinus13 = ColorBlobDetectionActivity.this.values[0] - ((float) ColorBlobDetectionActivity.this.dir14);
                ColorBlobDetectionActivity.this.rotation_compassPlus13 = ((float) (ColorBlobDetectionActivity.this.dir14 + 360)) - ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.rotation_compassAnother13 = ((float) (360 - ColorBlobDetectionActivity.this.dir14)) + ColorBlobDetectionActivity.this.values[0];
                ColorBlobDetectionActivity.this.smallest13 = ColorBlobDetectionActivity.this.rotation_compassMinus13;
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassPlus13) < Math.abs(ColorBlobDetectionActivity.this.smallest13)) {
                    ColorBlobDetectionActivity.this.smallest13 = -ColorBlobDetectionActivity.this.rotation_compassPlus13;
                }
                if (Math.abs(ColorBlobDetectionActivity.this.rotation_compassAnother13) < Math.abs(ColorBlobDetectionActivity.this.smallest13)) {
                    ColorBlobDetectionActivity.this.smallest13 = ColorBlobDetectionActivity.this.rotation_compassAnother13;
                }
                ColorBlobDetectionActivity.this.rotation_compassDir13 = ColorBlobDetectionActivity.this.smallest13;
            }
        }
    };
     */
}