package com.example.qrcode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcode.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Loại bỏ hoàn toàn padding dưới của root view
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            
            // Ép BottomAppBar và tất cả các thành phần nằm sát đáy vật lý của máy
            androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams lp = 
                (androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) binding.bottomAppBar.getLayoutParams();
            lp.bottomMargin = 0; 
            binding.bottomAppBar.setLayoutParams(lp);

            // Đảm bảo không có translation nào kéo nó lên
            binding.bottomAppBar.setTranslationY(0);
            binding.fabScan.setTranslationY(0);
            binding.orangeBgView.setTranslationY(0);
            
            return WindowInsetsCompat.CONSUMED;
        });

        setupNavigation();

        binding.fabScan.setOnClickListener(v -> {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                navHostFragment.getNavController().navigate(R.id.nav_scanner);
            }
        });
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }
    }
}
