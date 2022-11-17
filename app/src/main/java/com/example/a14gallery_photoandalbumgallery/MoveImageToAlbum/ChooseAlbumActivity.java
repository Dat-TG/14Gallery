package com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityChooseAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChooseAlbumActivity extends AppCompatActivity {
    ChooseAlbumAdapter adapter;
    ActivityChooseAlbumBinding binding;
    List<Album> albums;
    public static ActivityResultLauncher<Intent> activityMoveLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseAlbumBinding.inflate(getLayoutInflater());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.ChooseAlbumRecycleView.setHasFixedSize(true);
        binding.ChooseAlbumRecycleView.setLayoutManager(layoutManager);
        binding.ChooseAlbumRecycleView.setNestedScrollingEnabled(false);
        String folderFrom = getIntent().getStringExtra("folder");
        if (Objects.equals(folderFrom, AlbumGallery.recycleBinFolderName)) {
            binding.ChooseAlbumText.setText("Chọn nơi lưu ảnh sau khi khôi phục");
        }
        activityMoveLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.e("result code choose album", Integer.toString(result.getResultCode()));
                        if (result.getResultCode() == 123) {
                            Intent data = result.getData();
                            String dest = data.getStringExtra("DEST");
                            Log.e("ChooseAlbumActivity", dest);
                            Intent intent = new Intent();
                            intent.putExtra("DEST", dest);
                            setResult(123, intent);
                            finish();
                        }
                    }
                });

        AlbumGallery.getInstance().update(getApplicationContext());
        albums = AlbumGallery.getInstance().albums;
        if (albums==null || albums.size()==0) {
            Album Pictures=new Album();
            Pictures.setName("Pictures");
            String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
            File Pic=new File(dest);
            if (!Pic.exists()) {
                Pic.mkdirs();
            }
            Pictures.setPath(dest);
            List<Image> images= new ArrayList<>();
            Pictures.setAlbumImages(images);
            albums.add(Pictures);
        }
        adapter = new ChooseAlbumAdapter(getApplicationContext(), albums);
        binding.ChooseAlbumRecycleView.setAdapter(adapter);
        setContentView(binding.getRoot());
    }

}
