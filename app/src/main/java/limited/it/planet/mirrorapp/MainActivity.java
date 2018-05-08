package limited.it.planet.mirrorapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private Camera mCam;
    private MirrorView mCamPreview;
    private int mCameraId = 0;
    private FrameLayout mPreviewLayout;
    final private int REQUEST_CODE_CAMERA = 1;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCamPreview = new MirrorView(this,mCam);
        mCameraId = findFirstFrontFacingCamera();

        mPreviewLayout = (FrameLayout) findViewById(R.id.camPreview);
        mPreviewLayout.removeAllViews();

        int hasWriteStoragePermission = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteStoragePermission = checkSelfPermission(Manifest.permission.CAMERA);
        }

        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_CAMERA);
            }
            //return;
        }


        startCameraInLayout(mPreviewLayout, mCameraId);
        mCamPreview.setCameraDisplayOrientationAndSize(mCameraId);

    }


    private int findFirstFrontFacingCamera() {
        int foundId = -1;
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                foundId = camId;
                break;
            }
        }
        return foundId;
    }

    private void startCameraInLayout(FrameLayout layout, int cameraId) {
        mCam = Camera.open(cameraId);
        if (mCam != null) {
            mCamPreview = new MirrorView(this, mCam);
            layout.addView(mCamPreview);
        }
    }





    @Override
    protected void onResume() {
        super.onResume();
        if (mCam == null && mPreviewLayout != null) {
            mPreviewLayout.removeAllViews();
            startCameraInLayout(mPreviewLayout, mCameraId);
        }
    }

    @Override
    protected void onPause() {
        if (mCam != null) {
            mCam.release();
            mCam = null;
        }
        super.onPause();
    }
}
