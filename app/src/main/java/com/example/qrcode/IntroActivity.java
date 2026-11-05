package com.example.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrcode.databinding.ActivityIntroBinding;
import com.example.qrcode.databinding.ItemOnboardingBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(R.string.intro_title_1, R.string.intro_desc_1, R.drawable.ic_logo_quetnhanh));
        items.add(new OnboardingItem(R.string.intro_title_2, R.string.intro_desc_2, R.drawable.ic_generator));
        items.add(new OnboardingItem(R.string.intro_title_3, R.string.intro_desc_3, R.drawable.ic_history));
        items.add(new OnboardingItem(R.string.intro_title_4, R.string.intro_desc_4, R.drawable.ic_logo_quetnhanh));

        IntroAdapter adapter = new IntroAdapter(items);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {}).attach();

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() < items.size() - 1) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
                finish();
            }
        });

        binding.btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == items.size() - 1) {
                    binding.btnNext.setText(R.string.btn_get_started);
                    binding.btnSkip.setVisibility(View.GONE);
                } else {
                    binding.btnNext.setText(R.string.btn_next);
                    binding.btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    static class OnboardingItem {
        int title, description, image;
        OnboardingItem(int title, int description, int image) {
            this.title = title;
            this.description = description;
            this.image = image;
        }
    }

    static class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.ViewHolder> {
        private final List<OnboardingItem> items;

        IntroAdapter(List<OnboardingItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OnboardingItem item = items.get(position);
            holder.binding.tvTitle.setText(item.title);
            holder.binding.tvDescription.setText(item.description);
            holder.binding.ivIllustration.setImageResource(item.image);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ItemOnboardingBinding binding;
            ViewHolder(ItemOnboardingBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
