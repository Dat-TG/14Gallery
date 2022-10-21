package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumBinding;

import java.util.List;
import java.util.Objects;


public class DetailAlbumActivity extends AppCompatActivity implements MenuProvider {
    ActivityDetailAlbumBinding binding;
    List<Album> albums;
    String name;
    Album album;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        name = intent.getStringExtra("ALBUM");

        albums = AlbumGallery.getInstance().albums;
        Log.d("Activity", "onCreate: " + name);
        Glide.with(this)
                .load(name)
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imageView);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.top_bar_menu_image, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.img_camera) {
            // Click camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {
            // Click choose
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            // Click grid_col_2
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            // Click grid_col_3
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            // Click grid_col_4
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            // Click grid_col_5
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            return true;
        }
        if (menuItem.getItemId() == R.id.img_setting) {
            // Click Setting
            return true;
        }
        return false;
    }

}