package com.example.qrcode.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrcode.database.DatabaseHelper;
import com.example.qrcode.databinding.FragmentFavoritesBinding;
import com.example.qrcode.model.QRCodeModel;
import com.example.qrcode.ui.adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements HistoryAdapter.OnItemClickListener {
    private FragmentFavoritesBinding binding;
    private HistoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<QRCodeModel> favoritesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        adapter = new HistoryAdapter(favoritesList, this);
        binding.recyclerViewFavorites.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        favoritesList = dbHelper.getFavorites();
        adapter.updateData(favoritesList);
        binding.layoutEmpty.setVisibility(favoritesList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(QRCodeModel model) {
        ResultBottomSheet.newInstance(model.getContent(), 0).show(getParentFragmentManager(), "result");
    }

    @Override
    public void onFavoriteClick(QRCodeModel model) {
        dbHelper.updateFavorite(model.getId(), !model.isFavorite());
        loadFavorites();
    }
}
