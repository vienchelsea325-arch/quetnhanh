package com.example.qrcode.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrcode.database.DatabaseHelper;
import com.example.qrcode.databinding.FragmentHistoryBinding;
import com.example.qrcode.model.QRCodeModel;
import com.example.qrcode.ui.adapter.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnItemClickListener {
    private FragmentHistoryBinding binding;
    private HistoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<QRCodeModel> historyList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHelper(requireContext());

        adapter = new HistoryAdapter(historyList, this);
        binding.recyclerViewHistory.setAdapter(adapter);

        loadHistory();

        return binding.getRoot();
    }

    private void loadHistory() {
        historyList = dbHelper.getAllHistory();
        adapter.updateData(historyList);
        binding.layoutEmpty.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(QRCodeModel model) {
        ResultBottomSheet.newInstance(model.getContent(), 0).show(getParentFragmentManager(), "result");
    }

    @Override
    public void onFavoriteClick(QRCodeModel model) {
        dbHelper.updateFavorite(model.getId(), !model.isFavorite());
        loadHistory();
    }
}
