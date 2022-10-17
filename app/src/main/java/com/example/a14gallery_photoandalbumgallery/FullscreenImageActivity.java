package com.example.a14gallery_photoandalbumgallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityFullscreenImageBinding;

public class FullscreenImageActivity extends AppCompatActivity {
    ActivityFullscreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("id");
        ImageFragmentAdapter imageFragmentAdapter = new ImageFragmentAdapter(this, ImageGallery.getInstance().images);
        Glide.with(this)
                .load(imageFragmentAdapter._images.get(position))
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imageView);
    }
}