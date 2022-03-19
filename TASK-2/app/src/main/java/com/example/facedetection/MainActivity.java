package com.example.facedetection;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.facedetection.DetectionBox.GraphicOverlay;
import com.example.facedetection.DetectionBox.RectOverlay;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 121;
    public CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    private ImageButton btnCapture;
    private ImageButton btnRotate;
    private PreviewView previewViewMain;
    private ImageAnalysis imageAnalysis;
    private InputImage inputImage;
    private Canvas canvas;
    private GraphicOverlay graphicOverlay;
    private FaceDetectorOptions highAccuracyOpts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check for permissions
        checkCameraPermission();

        //initializing views
        initViews();

        //for rotating Camera
        btnRotate.setOnClickListener(view -> {
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
            else
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

            startCamera();
        });

    }

    //starting camera
    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {

                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();
                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(previewViewMain.getSurfaceProvider());


                imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                                .build();


                cameraProvider.unbindAll();


                cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, imageAnalysis, preview);



                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(MainActivity.this), image -> btnCapture.setOnClickListener(view -> {

                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                Bitmap inputBitmap = toBitmap(image);
                inputImage = InputImage.fromBitmap(inputBitmap, rotationDegrees);
                Bitmap mutableBmp = inputBitmap.copy(Bitmap.Config.ARGB_8888, true);
                canvas = new Canvas(mutableBmp);

                        startDetection(image);
                    }));

            }
    catch(InterruptedException | ExecutionException e)

        {
            Log.d(TAG, "onCreate: error" + e.toString());
            Toast.makeText(this, "error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    },ContextCompat.getMainExecutor(this));
}


    private void initViews() {
        btnCapture = findViewById(R.id.btnCapture);
        btnRotate = findViewById(R.id.btnRotate);
        previewViewMain = findViewById(R.id.previewViewMain);
        graphicOverlay = findViewById(R.id.graphicOverlay);
        previewViewMain = findViewById(R.id.previewViewMain);

        highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .build();
    }

    private void checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 121);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("Permission Error")
                            .setMessage("Cannot Proceed Without Permissions")
                            .setPositiveButton("OK", (dialogInterface, i) -> finish())
                            .setCancelable(false).create();
                    alertDialog.show();
                }
            }
            startCamera();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startDetection(ImageProxy image) {
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>> result =
                detector.process(inputImage)
                        .addOnSuccessListener(
                                faces -> {
                                    // Task completed successfully
                                    for (Face face:faces){
                                        onTaskComplete(face);
                                    }
                                    Toast.makeText(MainActivity.this, count + "faces detected", Toast.LENGTH_SHORT).show();

                                })
                        .addOnFailureListener(
                                e -> Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(task -> image.close());
    }

    private Bitmap toBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    int count = 0;

    private void onTaskComplete(Face face){

        count++;
        Rect bounds = face.getBoundingBox();
        RectOverlay rect = new RectOverlay(graphicOverlay,bounds,face);
        graphicOverlay.add(rect);
        graphicOverlay.draw(canvas);

        Thread td = new Thread() {
            public void run() {

                try {

                    sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        td.start();
    }



}