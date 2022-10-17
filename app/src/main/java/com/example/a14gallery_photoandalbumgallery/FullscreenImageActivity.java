package com.example.a14gallery_photoandalbumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityFullscreenImageBinding;

public class FullscreenImageActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityFullscreenImageBinding binding;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ImageFragmentAdapter imageFragmentAdapter = new ImageFragmentAdapter(this, ImageGallery.getInstance().images);

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("id");
        image = imageFragmentAdapter._images.get(position);

        Glide.with(this)
                .load(image)
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imageView);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnShare) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.btnEdit) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.btnHide) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.btnDelete) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.btnMore) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }
    }
}