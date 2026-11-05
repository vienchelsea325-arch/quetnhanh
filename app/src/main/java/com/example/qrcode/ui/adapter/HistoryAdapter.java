package com.example.qrcode.ui.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcode.R;
import com.example.qrcode.databinding.ItemQrBinding;
import com.example.qrcode.model.QRCodeModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<QRCodeModel> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(QRCodeModel model);
        void onFavoriteClick(QRCodeModel model);
    }

    public HistoryAdapter(List<QRCodeModel> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemQrBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QRCodeModel model = list.get(position);
        holder.binding.textContent.setText(model.getContent());
        
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(model.getTimestamp());
        String date = DateFormat.format("dd MMM yyyy, HH:mm", cal).toString();
        holder.binding.textDate.setText(date);

        holder.binding.imageIcon.setImageResource(model.isScanned() ? R.drawable.ic_scanner : R.drawable.ic_generator);
        holder.binding.btnFavorite.setImageResource(model.isFavorite() ? R.drawable.ic_favorites : R.drawable.ic_favorites);
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(model));
        holder.binding.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateData(List<QRCodeModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemQrBinding binding;
        public ViewHolder(ItemQrBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
