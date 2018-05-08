package limited.it.planet.mirrorapp;

import android.app.Activity;
import android.content.Context;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Tarikul on 5/8/2018.
 */

public class MirrorView extends SurfaceView implements
        SurfaceHolder.Callback {

    public static String DEBUG_TAG = "debug";

    private SurfaceHolder mHolder;
    private Camera mCamera;
    Context mContext;

    public MirrorView(Context context, Camera camera) {
        super(context);
        this.mContext = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception error) {
            Log.d(DEBUG_TAG,
                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w,
                               int h) {
        if (mHolder.getSurface() == null) {
            return;
        }

        // can't make changes while mPreviewLayout is active
        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }

        try {

            // start up the mPreviewLayout
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception error) {
            Log.d(DEBUG_TAG,
                    "Error starting mPreviewLayout: " + error.getMessage());
        }
    }

    public void setCameraDisplayOrientationAndSize(int mCameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = rotation * 90;

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);

        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        if (result == 90 || result == 270) {
            mHolder.setFixedSize(previewSize.height, previewSize.width);
        } else {
            mHolder.setFixedSize(previewSize.width, previewSize.height);

        }
    }
}
