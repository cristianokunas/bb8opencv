package unijui.setr.bb8opencv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ColorBlobDetectionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCV::ColorBlobDetectionActivity";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d("TAG", "OpenCV not loaded");
        } else {
            Log.d("TAG", "OpenCV loaded");
        }
    }

    /* Colors */
    private Scalar Color_Black = new Scalar(0.0d, 0.0d, 0.0d, 255.0d);
    private Scalar Color_Blue = new Scalar(0.0d, 0.0d, 255.0d, 255.0d);
    private Scalar Color_Cyan = new Scalar(0.0d, 255.0d, 255.0d, 255.0d);
    private Scalar Color_DarkGray = new Scalar(150.0d, 150.0d, 150.0d, 255.0d);
    private Scalar Color_DarkGreen = new Scalar(0.0d, 128.0d, 0.0d, 255.0d);
    private Scalar Color_GreenBlue = new Scalar(10.0d, 160.0d, 190.0d, 255.0d);
    private Scalar Color_LightGreen = new Scalar(0.0d, 255.0d, 0.0d, 255.0d);
    private Scalar Color_Magenta = new Scalar(255.0d, 0.0d, 255.0d, 255.0d);
    private Scalar Color_NiceGreen = new Scalar(100.0d, 200.0d, 100.0d, 255.0d);
    private Scalar Color_Orange = new Scalar(255.0d, 128.0d, 64.0d, 255.0d);
    private Scalar Color_PaleBlue = new Scalar(180.0d, 180.0d, 255.0d, 255.0d);
    private Scalar Color_PalePink = new Scalar(255.0d, 190.0d, 225.0d, 255.0d);
    private Scalar Color_PaleYellow = new Scalar(255.0d, 255.0d, 180.0d, 255.0d);
    private Scalar Color_Purple = new Scalar(128.0d, 0.0d, 255.0d, 255.0d);
    private Scalar Color_Red = new Scalar(255.0d, 0.0d, 0.0d, 255.0d);
    private Scalar Color_White = new Scalar(255.0d, 255.0d, 255.0d, 255.0d);
    private Scalar Color_Yellow = new Scalar(255.0d, 255.0d, 0.0d, 255.0d);

    private Scalar sc1;
    private Scalar sc2;

    private Mat imgHSV;
    private Mat imgThresholded;
    private Mat mataux;
    private Mat mRgba;

    private float center_horz = 0.0f;
    private float center_vert = 0.0f;
    private float cols;
    private float rows;
    private float font_size;
    private double diameter = 0;

    //   HSV   Green
    private int iLowH = 29;
    private int iHighH = 64;
    private int iLowS = 86;
    private int iHighS = 255;
    private int iLowV = 6;
    private int iHighV = 255;

    private int maxHeight;
    private int maxWidth;
    private int border = 15;

////   HSV   Green test
//    private final int iLowH  = 45;
//    private final int iHighH  = 75;
//    private final int iLowS  = 20;
//    private final int iHighS  = 255;
//    private final int iLowV  = 10;
//    private final int iHighV  = 255;

    private CameraBridgeViewBase cameraBridgeViewBase;
    private BaseLoaderCallback baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        cameraBridgeViewBase.setMaxFrameSize(960, 540);
//        cameraBridgeViewBase.setMaxFrameSize(720,480);
//        cameraBridgeViewBase.setMaxFrameSize(352,288);
//        cameraBridgeViewBase.setMaxFrameSize(320,180);
        maxHeight = cameraBridgeViewBase.getHeight();
        maxWidth = cameraBridgeViewBase.getWidth();

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
                switch (status) {
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
        this.imgHSV = new Mat(maxHeight, maxWidth, CvType.CV_32FC1);
        this.imgThresholded = new Mat(maxHeight, maxWidth, CvType.CV_8U);
        this.mataux = new Mat(maxHeight, maxWidth, CvType.CV_32FC1);
        this.mRgba = new Mat(height, width, CvType.CV_8UC4);

        this.cols = (float) this.mRgba.cols();
        this.rows = (float) this.mRgba.rows();
        this.center_horz = (float) (this.mRgba.cols() / 2);
        this.center_vert = (float) (this.mRgba.rows() / 2);

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

    @SuppressLint("LongLogTag")
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Point center_frame = new Point(center_horz, center_vert);
        this.mRgba = inputFrame.rgba();
        int x1_horz = (int) (center_horz - (this.cols / 2) + this.border);
        int y1_horz = (int) center_vert;
        int x2_horz = (int) (center_horz + (this.cols / 2) - this.border);
        int y2_horz = (int) center_vert;
        int x1_vert = (int) center_horz;
        int y1_vert = (int) (center_vert - (this.rows / 2) + this.border);
        int x2_vert = (int) center_horz;
        int y2_vert = (int) (center_vert + (this.rows / 2) - this.border);

        Point p1_h = new Point(x1_horz, y1_horz);
        Point p2_h = new Point(x2_horz, y2_horz);
        Point p1_v = new Point(x1_vert, y1_vert);
        Point p2_v = new Point(x2_vert, y2_vert);

        Log.i(TAG, "Coordinates: (rows: " + this.mRgba.rows() + ", cols: " + this.mRgba.cols()
                + "," + "c h: " + this.center_horz + ", c v: " + this.center_vert
                + ", x1: " + x1_horz + ", y1: " + y1_horz + ", x2: " + x2_horz + ", y2: " + y2_horz
                + ", center frame: " + center_frame + ")");
/*
        //! [reduce_noise]
        /* Imgproc.medianBlur(imgThresholded, imgThresholded, 9); */
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
                (double) imgThresholded.rows() / 4, // change this value to detect circles with different distances to each other
                100.0, 30.0, 5, 300); // change the last two parameters
        // (min_radius & max_radius) to detect larger circles
        //! [houghcircles]

        double a = 0.0;
        double b = 0.0;
        int r = 0;

        //! [draw]
        for (int x = 0; x < circles.rows(); x++) {
            double[] c = circles.get(x, 0);
            for (int j = 0; j < c.length; j++) {
                a = c[0];
                b = c[1];
                r = (int) c[2];
            }
            Point center = new Point(a, b);
            // circle center
            Imgproc.circle(mRgba, center, 1, this.Color_DarkGreen, 3, 8, 0);
            // circle outline
            Imgproc.circle(mRgba, center, r, this.Color_Magenta, 7, 8, 0);
            // line center_frame with center_circle
            Imgproc.line(mRgba, center_frame, center, this.Color_Blue, 7);
            // diameter circle
            this.diameter = (r * 2);
        }
        // Diameter Display text
        Imgproc.putText(this.mRgba, "Diameter circle: (" + this.diameter + ")",
                new Point(0.0221d * ((double) this.cols), 0.0650d * ((double) this.rows)),
                4, (double) (1.0f * this.font_size), this.Color_PalePink, 1);
        //! [draw]

        // lines central
        Imgproc.line(mRgba, p1_h, p2_h, this.Color_Yellow, 3);
        Imgproc.line(mRgba, p1_v, p2_v, this.Color_Yellow, 3);

        // Point central_frame
        Imgproc.line(mRgba, center_frame, center_frame, this.Color_Purple, 9);

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

        return mRgba;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "Ha um problema no OpenCV", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }
}