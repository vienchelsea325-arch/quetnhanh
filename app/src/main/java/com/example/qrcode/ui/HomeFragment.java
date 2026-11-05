package com.example.qrcode.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.qrcode.R;
import com.example.qrcode.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.cardScan.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_scanner));
        binding.cardCreateQR.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_generator));
        binding.cardCreateBarcode.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_generator));
        binding.cardHistory.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_history));
        binding.cardFavorites.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.nav_favorites));

        return binding.getRoot();
    }
}
