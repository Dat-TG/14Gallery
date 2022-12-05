package com.example.a14gallery_photoandalbumgallery.password;


import static com.example.a14gallery_photoandalbumgallery.MainActivity.NightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.a14gallery_photoandalbumgallery.MainActivity;
import com.example.a14gallery_photoandalbumgallery.R;

import java.util.List;

public class ConfirmPasswordActivity extends AppCompatActivity {

    PatternLockView mPatternLockView;
    String password;
    TextView confirmPassWord;
    SharedPreferences sharedPreferences;
    int theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_confirm_password);

        // shared preference when user comes second time to the app
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        password = sharedPreferences.getString("password-create", "0");

        mPatternLockView = findViewById(R.id.pattern_lock_view);
        confirmPassWord = findViewById(R.id.textResult);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                // if drawn pattern is equal to created pattern you will navigate to home screen
                if (password.equals(PatternLockUtils.patternToString(mPatternLockView, pattern))) {
                    confirmPassWord.setText(R.string.set_password_success);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", PatternLockUtils.patternToString(mPatternLockView, pattern));
                    editor.apply();
                    confirmPassWord.setTextColor(getResources().getColor(R.color.green));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // other wise you will get error wrong password
                    confirmPassWord.setText(R.string.set_password_fail);
                    confirmPassWord.setTextColor(getResources().getColor(R.color.red));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", "0");
                    editor.apply();
                    Toast.makeText(ConfirmPasswordActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    mPatternLockView.clearPattern();
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

