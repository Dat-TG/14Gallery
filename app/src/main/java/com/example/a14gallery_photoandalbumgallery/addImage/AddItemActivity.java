package com.example.a14gallery_photoandalbumgallery.addImage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityAddItemBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddItemActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActivityAddItemBinding binding;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    Album album;
    public static List<Image> selectedAlbum = new ArrayList<>();
    public static List<String> selectedAlbumName = new ArrayList<>();

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemBinding.inflate(getLayoutInflater());

        tabLayout = binding.tabAddImageLayout;
        viewPager = binding.viewPager;

        AlbumGallery.getInstance().update(this);
        String nameAlbum = getIntent().getStringExtra("NAME");
        album = AlbumGallery.getInstance().getAlbumByName(this, nameAlbum);
        viewPager.setAdapter(new ViewPagerAdapter(this, album));

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
        getMenuInflater().inflate(R.menu.top_bar_menu_add_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) { // Click back button
            selectedAlbumName = new ArrayList<>();
            selectedAlbum = new ArrayList<>();
            finish();
            return true;
        }
        if (item.getItemId() == R.id.add_button) { // Click check to add image
            String message = "Bạn chắc chắn muốn thêm " + selectedAlbum.size() + " ảnh vào album này ?";
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    this);
            alert.setTitle("Xác nhận");
            alert.setMessage(message);
            alert.setPositiveButton("YES", (dialog, which) -> {
                String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/14Gallery/" + album.getName();
                moveToAlbum(getApplicationContext(), selectedAlbum, dest);
                finish();
                dialog.dismiss();

            });
            alert.setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            alert.show();
            return true;
        }
        return false;
    }

    // Move image by path image
    private void moveToAlbum(Context context, List<Image> images, String dest) {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        int count = 0;
        try {
            for (Image image : selectedImages) {
                String src = image.getPath();
                Path result;
                String[] name = src.split("/");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result = Files.move(Paths.get(src), Paths.get(dest + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);
                    if (result != null) count++;
                }
            }
            Toast.makeText(context, "Đã di chuyển thành công " + count + " ảnh vào album", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Có lỗi xảy ra!! Chỉ di chuyển thành công " + count + "/" + selectedImages.size() + " ảnh vào album", Toast.LENGTH_SHORT).show();
        }
    }
}