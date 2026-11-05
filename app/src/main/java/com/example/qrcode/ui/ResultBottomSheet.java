package com.example.qrcode.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcode.R;
import com.example.qrcode.databinding.BottomSheetResultBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ResultBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetResultBinding binding;
    private String content;
    private int format;
    private Runnable onDismissListener;

    public void setOnDismissListener(Runnable listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.run();
        }
    }

    public static ResultBottomSheet newInstance(String content, int format) {
        ResultBottomSheet fragment = new ResultBottomSheet();
        Bundle args = new Bundle();
        args.putString("content", content);
        args.putInt("format", format);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            content = getArguments().getString("content");
            format = getArguments().getInt("format");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetResultBinding.inflate(inflater, container, false);

        binding.tvResultContent.setText(content);
        
        boolean isUrl = content != null && (content.startsWith("http") || content.startsWith("www"));
        binding.tvResultType.setText(isUrl ? "QR Code - URL" : "QR Code - Text");
        
        if (isUrl) {
            binding.btnPrimaryAction.setVisibility(View.VISIBLE);
            binding.btnPrimaryAction.setOnClickListener(v -> {
                try {
                    String url = content;
                    if (!url.startsWith("http")) url = "http://" + url;
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.btnPrimaryAction.setVisibility(View.GONE);
        }

        binding.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Scanned Result", content);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        binding.btnShare.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, content);
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        return binding.getRoot();
    }
}
