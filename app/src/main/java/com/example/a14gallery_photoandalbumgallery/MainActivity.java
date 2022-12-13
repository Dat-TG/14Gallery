package com.example.a14gallery_photoandalbumgallery;

import android.Manifest;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;

import com.example.a14gallery_photoandalbumgallery.album.AlbumFragment;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityMainBinding;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragment;
import com.example.a14gallery_photoandalbumgallery.searchAlbum.SearchFragment;
import com.example.a14gallery_photoandalbumgallery.searchImage.ImageSearchFragment;
import com.example.a14gallery_photoandalbumgallery.setting.SettingActivity;


public class MainActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;

    ActivityMainBinding binding;
    Toolbar toolbar;

    NavController navController;
    public static int NightMode;
    SharedPreferences sharedPreferences;
    int theme, fragmentCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 0);
        theme = sharedPreferences.getInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_purple);
        fragmentCurrent = sharedPreferences.getInt("fragCurr", 1);
        if (NightMode != 2) {
            if (theme != 0) {
                setTheme(theme);
            }
        } else {
            setTheme(R.style.Theme_14GalleryPhotoAndAlbumGallery);
            AppCompatDelegate.setDefaultNightMode(NightMode);
        }

        super.onCreate(savedInstanceState);

        if (SettingActivity.flag == 1) {
            if (fragmentCurrent == 1) replaceFragment(new ImageFragment());
            else if (fragmentCurrent == 2) replaceFragment(new AlbumFragment());
            else replaceFragment(new SearchFragment());
            SettingActivity.flag = 0;
        } else replaceFragment(new ImageFragment());


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        // Top bar menu
        toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);

        // Handle bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.image_icon)
                replaceFragment(new ImageFragment());
            else if (item.getItemId() == R.id.album_icon)
                replaceFragment(new AlbumFragment());
            else if (item.getItemId() == R.id.image_search_icon)
                replaceFragment(new SearchFragment());
            return true;
        });
    }

    @Override
    protected void onResume() {
        if (theme != sharedPreferences.getInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_purple)) {
            recreate();
        }
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if permission to read external storage is granted, if not, request permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    // Request storage permission by showing AlertDialog
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because the app needs to read your storage to show images from your phone." +
                            "If permission is not granted, the app will be forced to close.")
                    .setPositiveButton("ok", (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Clear permission if SDK >= 33
                    revokeSelfPermissionOnKill(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                finishAffinity();
            }
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Set initial menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar_menu_image, menu);
        return true;
    }
}

