package com.example.qrcode.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrcode.database.DatabaseHelper;
import com.example.qrcode.databinding.FragmentGeneratorBinding;
import com.example.qrcode.model.QRCodeModel;
import com.example.qrcode.util.CodeGenerator;
import com.example.qrcode.util.FileUtil;

public class GeneratorFragment extends Fragment {
    private FragmentGeneratorBinding binding;
    private DatabaseHelper dbHelper;
    private Bitmap currentBitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGeneratorBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        binding.btnGenerate.setOnClickListener(v -> generateQR());
        binding.btnSave.setOnClickListener(v -> saveQR());

        binding.typeUrl.setOnClickListener(v -> binding.inputLayout.setHint("Enter URL"));
        binding.typeText.setOnClickListener(v -> binding.inputLayout.setHint("Enter Text"));
        binding.typeWifi.setOnClickListener(v -> binding.inputLayout.setHint("Enter SSID;Password"));

        return binding.getRoot();
    }

    private void generateQR() {
        String content = binding.etContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            currentBitmap = CodeGenerator.generateQRCode(content, 512);
            binding.ivQRResult.setImageBitmap(currentBitmap);
            binding.cardResult.setVisibility(View.VISIBLE);
            
            dbHelper.insertQR(new QRCodeModel(content, "QR_CODE", System.currentTimeMillis(), false, false));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error generating QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveQR() {
        if (currentBitmap != null) {
            try {
                FileUtil.saveBitmapToGallery(requireContext(), currentBitmap, "QR_" + System.currentTimeMillis());
                Toast.makeText(getContext(), "Saved to Gallery", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Save failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
