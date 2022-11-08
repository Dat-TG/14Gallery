package com.example.a14gallery_photoandalbumgallery.addImage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityAddItemBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

public class AddItemActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActivityAddItemBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    Album album, albumClickDetail;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());

        tabLayout = binding.tabAddImageLayout;
        viewPager = binding.viewPager;

        Gson gson = new Gson();
        album = gson.fromJson(getIntent().getStringExtra("album"), Album.class);
        viewPager.setAdapter(new ViewPagerAdapter(this, album, albumClickDetail));

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText(R.string.image);
                    if (position == 1) tab.setText(R.string.album);
                });
        tabLayoutMediator.attach();

        toolbar = binding.appBarAddImage;
        setSupportActionBar(toolbar);

        binding.appBarAddImage.setTitle(getString(R.string.add_image, album.getName()));

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_bar_menu_add_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) { // Click back button
            finish();
            return true;
        }
        if (item.getItemId() == R.id.add_button) {
            Toast.makeText(this, "Click Submit", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}