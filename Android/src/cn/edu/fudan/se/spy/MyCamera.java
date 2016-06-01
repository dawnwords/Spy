package cn.edu.fudan.se.spy;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by Dawnwords on 2016/6/1.
 */
public class MyCamera {
    private Camera camera;
    private boolean isPreview = false;
    private int screenWidth, screenHeight;

    public MyCamera(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void openCamera() {
        releaseCamera();
        camera = Camera.open();

        Log.i(getClass().getName(), "openCamera");
    }

    public void stopCamera() {
        if (camera != null) {
            if (isPreview) {
                camera.stopPreview();
                isPreview = false;
            }
        }
        Log.i(getClass().getName(), "stopCamera");
    }

    public void startCamera(SurfaceHolder surfaceHolder, Camera.PreviewCallback callback) {
        if (camera != null && !isPreview) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(screenWidth, screenHeight);
                parameters.setPictureSize(screenWidth, screenHeight);
                camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(callback);
                camera.startPreview();
                camera.autoFocus(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
        Log.i(getClass().getName(), "startCamera");
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        Log.i(getClass().getName(), "releaseCamera");
    }
}
