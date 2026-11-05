package com.example.qrcode.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.qrcode.database.DatabaseHelper;
import com.example.qrcode.databinding.FragmentScannerBinding;
import com.example.qrcode.model.QRCodeModel;
import com.example.qrcode.util.FeedbackUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScannerFragment extends Fragment {
    private static final String TAG = "ScannerFragment";
    private FragmentScannerBinding binding;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private DatabaseHelper dbHelper;
    private Camera camera;
    private boolean isBatchScan = false;
    private boolean isScanning = true;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) startCamera();
                else Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) scanFromUri(uri);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScannerBinding.inflate(inflater, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        cameraExecutor = Executors.newSingleThreadExecutor();
        scanner = BarcodeScanning.getClient();

        binding.btnFlash.setOnClickListener(v -> toggleFlash());
        binding.btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        binding.btnBatchScan.setOnClickListener(v -> {
            isBatchScan = !isBatchScan;
            Toast.makeText(getContext(), isBatchScan ? "Batch Scan Enabled" : "Batch Scan Disabled", Toast.LENGTH_SHORT).show();
        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

        return binding.getRoot();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            Integer state = camera.getCameraInfo().getTorchState().getValue();
            camera.getCameraControl().enableTorch(state == null || state == TorchState.OFF);
        }
    }

    private void scanFromUri(android.net.Uri uri) {
        try {
            InputImage image = InputImage.fromFilePath(requireContext(), uri);
            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            processResult(barcode.getRawValue(), barcode.getFormat());
                            break;
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Failed to scan URI", e);
        }
    }

    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (imageProxy.getImage() == null || !isScanning) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            processResult(rawValue, barcode.getFormat());
                            if (!isBatchScan) break;
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void processResult(String content, int format) {
        if (!isScanning && !isBatchScan) return;
        
        FeedbackUtil.vibrate(requireContext());
        dbHelper.insertQR(new QRCodeModel(content, String.valueOf(format), System.currentTimeMillis(), false, true));
        
        if (!isBatchScan) {
            isScanning = false;
            requireActivity().runOnUiThread(() -> {
                ResultBottomSheet bottomSheet = ResultBottomSheet.newInstance(content, format);
                bottomSheet.setOnDismissListener(() -> isScanning = true);
                bottomSheet.show(getParentFragmentManager(), "result");
            });
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Scanned: " + content, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        scanner.close();
    }
}
