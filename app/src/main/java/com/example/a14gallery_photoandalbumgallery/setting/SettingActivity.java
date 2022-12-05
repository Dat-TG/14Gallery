package com.example.a14gallery_photoandalbumgallery.setting;


import static com.example.a14gallery_photoandalbumgallery.MainActivity.NightMode;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.a14gallery_photoandalbumgallery.MainActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySettingBinding binding = null;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int theme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        NightMode = sharedPreferences.getInt("NightModeInt", 0);
        theme = sharedPreferences.getInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_purple);
        if (NightMode != 2) {
            if (theme != 0) setTheme(theme);
        } else {
            setTheme(R.style.Theme_14GalleryPhotoAndAlbumGallery);
            AppCompatDelegate.setDefaultNightMode(NightMode);
        }
        super.onCreate(savedInstanceState);

        binding = ActivitySettingBinding.inflate(getLayoutInflater());

        toolbar = binding.appBarSetting;
        setSupportActionBar(toolbar);
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();

        // Button back
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        editor = sharedPreferences.edit();

        binding.clickChangeTheme.setOnClickListener(this);

        binding.switchDarkMode.setChecked(NightMode == 2);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        editor.putInt("NightModeInt", 2);
                        editor.commit();
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        editor.putInt("NightModeInt", 1);
                        editor.commit();
                    }
                }
        );

        binding.themeBlue.setOnClickListener(this);
        binding.themeBrown.setOnClickListener(this);
        binding.themeGreen.setOnClickListener(this);
        binding.themeGreenDark.setOnClickListener(this);
        binding.themePink.setOnClickListener(this);
        binding.themePurple.setOnClickListener(this);
        binding.themeRedDark.setOnClickListener(this);
        binding.themeYellow.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);
        switch (v.getId()) {
            case R.id.click_change_theme:
                if (binding.allThemes.getVisibility() == View.VISIBLE)
                    binding.allThemes.setVisibility(View.GONE);
                else {
                    binding.allThemes.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.theme_blue:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_blue);
                editor.commit();
                recreate();
                break;
            case R.id.theme_brown:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_brown);
                editor.commit();
                recreate();
                break;
            case R.id.theme_green:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_green);
                editor.commit();
                recreate();
                break;
            case R.id.theme_greenDark:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_greenDark);
                editor.commit();
                recreate();
                break;
            case R.id.theme_purple:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_purple);
                editor.commit();
                recreate();
                break;
            case R.id.theme_redDark:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery);
                editor.commit();
                recreate();
                break;
            case R.id.theme_yellow:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_yellow);
                editor.commit();
                recreate();
                break;
            case R.id.theme_pink:
                editor.putInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_pink);
                editor.commit();
                recreate();
                break;
        }
    }
}
