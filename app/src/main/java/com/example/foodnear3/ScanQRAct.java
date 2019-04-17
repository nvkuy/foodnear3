package com.example.foodnear3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanQRAct extends AppCompatActivity {

    SurfaceView camView;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        start();
        camcallback();
        onDetected();
    }

    private void onDetected() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> sparseArray = detections.getDetectedItems();
                if (sparseArray.size() > 0) {
                    Intent i = new Intent(getApplication(), MainActivity.class);
                    try {
                        i.putExtra("street_code", Integer.valueOf(sparseArray.valueAt(0).displayValue));
                        setResult(RESULT_OK, i);
                    } catch (Exception e) {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            }
        });
    }

    private void camcallback() {
        camView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(camView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void start() {

        camView = (SurfaceView) findViewById(R.id.cameraView);
        camView.setZOrderMediaOverlay(true);
        surfaceHolder = (SurfaceHolder) camView.getHolder();

        barcodeDetector = new BarcodeDetector
                .Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!barcodeDetector.isOperational()) {
            setResult(RESULT_CANCELED);
            finish();
        }

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(680, 480)
                .build();

    }
}
