package unijui.setr.bb8opencv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG = "OCVSample::Activity";
    static {
        if (!OpenCVLoader.initDebug()){
            Log.d("TAG", "OpenCV not loaded");
        } else {
            Log.d("TAG", "OpenCV loaded");
        }
    }
//      Green
    int iLowH  = 45;
    int iHighH  = 75;
    int iLowS  = 20;
    int iHighS  = 255;
    int iLowV  = 10;
    int iHighV  = 255;

    // Red
//    int iLowH  = 175;
//    int iHighH  = 179;
//    int iLowS  = 255;
//    int iHighS  = 255;
//    int iLowV  = 255;
//    int iHighV  = 255;

    Mat  imgHSV, imgThresholded, mataux, circles;
    Scalar sc1, sc2;

    CameraBridgeViewBase cameraBridgeViewBase;
//    Mat mat, mat1, mat2, mat3, grayMat;
    BaseLoaderCallback baseLoaderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        sc1 = new Scalar(iLowH, iLowS, iLowV);
        sc2 = new Scalar(iHighH, iHighS, iHighV);

        cameraBridgeViewBase = findViewById(R.id.myCameraView);
        cameraBridgeViewBase.setCameraIndex(0); //0 for rear and 1 for front
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                PackageManager.PERMISSION_GRANTED);
//
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
    public void onCameraViewStarted(int width, int height) {
        imgHSV = new Mat(height,width, CvType.CV_32FC1);
        imgThresholded = new Mat(height,width, CvType.CV_32FC1);
        mataux = new Mat(height,width, CvType.CV_32FC1);
        circles = new Mat(height,width, CvType.CV_32FC1);
    }

    @Override
    public void onCameraViewStopped() {
        imgHSV.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

//        Scalar hsvMin = new Scalar(0, 50, 50, 0);//red
//        Scalar hsvMax = new Scalar(6, 255, 255, 0);//red
//        Scalar hsvMin2 = new Scalar(175, 50, 50, 0);//red
//        Scalar hsvMax2 = new Scalar(179, 255, 255, 0);//red
        //essas 2 linhas ficam
        Imgproc.cvtColor(inputFrame.rgba(), imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.inRange(imgHSV, sc1, sc2, imgThresholded);
//        Core.inRange(imgHSV, hsvMin, hsvMax, imgThresholded);
//        Core.inRange(imgHSV, hsvMin2, hsvMax2, imgThresholded);

//        Core.bitwise_and(inputFrame.rgba(), imgThresholded, mataux);

        return imgThresholded;
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
}
