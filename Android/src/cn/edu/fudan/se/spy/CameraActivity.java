package cn.edu.fudan.se.spy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class CameraActivity extends Activity {
    private CommunicationService service;
    private EditText ipText, portText;
    private MyCamera mCamera;
    private Camera.PreviewCallback callback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            try {
                service.setFrame(new Frame(size.width, size.height, data));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };
    private SurfaceHolder surfaceHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initPreview();
        initConnect();
        initService();

        Log.i(getClass().getName(), "onCreate");
    }

    private void initService() {
        service = new CommunicationService();
        SharedPreferences preferences = getSharedPreferences("server-address", MODE_PRIVATE);
        String ip = preferences.getString("ip", "10.131.253.211");
        int port = preferences.getInt("port", 6000);
        ipText.setText(ip);
        portText.setText(String.valueOf(port));
    }

    private void initConnect() {
        Button connect = (Button) findViewById(R.id.connect);
        ipText = (EditText) findViewById(R.id.ip);
        portText = (EditText) findViewById(R.id.port);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable ipEditable = ipText.getText(), portEditable = portText.getText();
                if (ipEditable != null && ipEditable.length() > 0 && portEditable != null && portEditable.length() > 0) {
                    String ip = ipEditable.toString();
                    int port = Integer.parseInt(portEditable.toString());
                    save(ip, port);
                    service.setServerAddress(ip, port).resumeService();
                }
            }
        });
    }

    private void initPreview() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mCamera = new MyCamera(dm.widthPixels, dm.heightPixels);

        SurfaceView sView = (SurfaceView) findViewById(R.id.sView);
        surfaceHolder = sView.getHolder();
        surfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCamera.openCamera();
                mCamera.startCamera(holder, callback);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopCamera();
            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void save(String ip, int port) {
        SharedPreferences preferences = getSharedPreferences("server-address", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("ip", ip);
        edit.putInt("port", port);
        edit.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (service != null) {
            service.resumeService();
        }
        mCamera.startCamera(surfaceHolder, callback);
        Log.i(getClass().getName(), "onResume");
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (service != null) {
            service.pauseService();
        }
        mCamera.stopCamera();
        super.onPause();
        Log.i(getClass().getName(), "onPause");
    }

    @Override
    protected void onDestroy() {
        mCamera.releaseCamera();
        if (service != null) {
            service.destroyService();
        }
        super.onDestroy();
        Log.i(getClass().getName(), "onDestroy");
    }
}

