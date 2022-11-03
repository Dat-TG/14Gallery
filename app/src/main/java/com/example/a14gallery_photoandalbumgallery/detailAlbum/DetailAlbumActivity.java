package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.a14gallery_photoandalbumgallery.ClassifyDate;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.MainActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.addImage.AddItemActivity;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumBinding;
import com.google.gson.Gson;

import java.util.List;


public class DetailAlbumActivity extends AppCompatActivity {
    ActivityDetailAlbumBinding binding;
    Toolbar toolbar;
    Album album;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAlbumBinding.inflate(getLayoutInflater());

        toolbar = binding.appBarDetail;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Button back
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Gson gson = new Gson();
        album = gson.fromJson(getIntent().getStringExtra("ALBUM"), Album.class);
        int size = album.getAlbumImages().size();

        binding.appBarDetail.setTitle(album.getName());
        if (size != 0) {
            List<Image> images = album.getAlbumImages();
            List<ClassifyDate> classifyDateList = ImageGallery.getListClassifyDate(images);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

            binding.recyclerDetailView.setHasFixedSize(true);
            binding.recyclerDetailView.setNestedScrollingEnabled(true);
            binding.recyclerDetailView.setLayoutManager(layoutManager);
            binding.recyclerDetailView.setNestedScrollingEnabled(false);
            binding.recyclerDetailView.setAdapter(new ImageFragmentAdapter(this, classifyDateList, 4));
            binding.textNotFound.setVisibility(View.GONE);

        }else {
            if (album.getName().equals("Thùng rác")) {
                binding.textNotFound.setText(R.string.empty_recycle_bin);
            }
            else {
                binding.textNotFound.setText(R.string.no_image_found);
            }
            binding.recyclerDetailView.setVisibility(View.GONE);
        }
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.top_bar_menu_detail_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        if (menuItem.getItemId() == android.R.id.home) { // Click back button
            finish();
            return true;
        }
        if(menuItem.getItemId() == R.id.detAlb_add_image) { // add Image
            Intent intent = new Intent(this, AddItemActivity.class);
            Gson gson = new Gson();
            String imagesObj = gson.toJson(album);
            intent.putExtra("album", imagesObj);
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_camera) { // Click Camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_choose) {
            // Click choose
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_2) {
            // Click grid_col_2
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_3) {
            // Click grid_col_3
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_4) {
            // Click grid_col_4
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_5) {
            // Click grid_col_5
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_day) {
            // Click Sort by day
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_month) {
            // Click Sort by month
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_setting) {
            // Click Setting
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}