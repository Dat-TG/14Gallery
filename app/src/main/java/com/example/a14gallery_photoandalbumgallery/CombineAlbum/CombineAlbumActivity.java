package com.example.a14gallery_photoandalbumgallery.CombineAlbum;

import static com.example.a14gallery_photoandalbumgallery.MainActivity.NightMode;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumAdapter;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.database.AppDatabase;
import com.example.a14gallery_photoandalbumgallery.database.albumFavorite.AlbumFavoriteData;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityChooseAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityCombineAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CombineAlbumActivity extends AppCompatActivity {
    CombineAlbumAdapter adapter;
    ActivityCombineAlbumBinding binding;
    List<Album> albums;
    public static ActivityResultLauncher<Intent> activityMoveLauncher;

    SharedPreferences sharedPreferences;
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
        binding = ActivityCombineAlbumBinding.inflate(getLayoutInflater());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        binding.CombineAlbumRecycleView.setHasFixedSize(true);
        binding.CombineAlbumRecycleView.setLayoutManager(layoutManager);
        binding.CombineAlbumRecycleView.setNestedScrollingEnabled(false);
        AlbumGallery.getInstance().update(getApplicationContext());
        albums = AlbumGallery.getInstance().albums;
        if (albums == null || albums.size() == 0) {
            Album Pictures = new Album();
            Pictures.setName("Pictures");
            String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
            File Pic = new File(dest);
            if (!Pic.exists()) {
                Pic.mkdirs();
            }
            Pictures.setPath(dest);
            List<Image> images = new ArrayList<>();
            Pictures.setAlbumImages(images);
            albums.add(Pictures);
        }
        binding.btnCombine.setOnClickListener(view -> {
            List<Album> selectedAlbum = CombineAlbumAdapter.getSelectedAlbum();
            if (selectedAlbum.size()<1) {
                finish();
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Nhập tên album mới");
            LinearLayout layout = new LinearLayout(this);
            final TextView textView1 = new TextView(this);
            final EditText input1 = new EditText(this); // Set an EditText view to get user input
            textView1.setText("Tên album:");
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(textView1);
            layout.addView(input1);
            layout.setPadding(50, 50, 50, 0);
            alert.setView(layout);
            alert.setPositiveButton("Ok", (dialog, whichButton) -> {
                String newName=input1.getText().toString();
                int max=0;
                int posMax=0;
                for (int i=0;i<selectedAlbum.size();i++) {
                    if (selectedAlbum.get(i).getAlbumImages().size()>max) {
                        max=selectedAlbum.get(i).getAlbumImages().size();
                        posMax=i;
                    }
                }
                String dest=selectedAlbum.get(posMax).getPath();
                for (int i=0;i<selectedAlbum.size();i++) {
                    if (i!=posMax) {
                        for (int j=0;j<selectedAlbum.get(i).getAlbumImages().size();j++) {
                            String src=selectedAlbum.get(i).getAlbumImages().get(j).getPath();
                            moveToAlbum(src,dest);
                        }
                        File dirName = new File(selectedAlbum.get(i).getPath());
                        dirName.delete();
                    }
                }
                File srcFile=new File(dest);
                File destFile=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+AlbumGallery.rootFolder+newName);
                srcFile.renameTo(destFile);
                Snackbar.make(findViewById(R.id.CombineAlbumLayout), "Đã gộp albums", Snackbar.LENGTH_SHORT).show();
                finish();
            });
            alert.setNegativeButton("Hủy", (dialog, whichButton) -> {
            });
            alert.show();
        });
        binding.btnCombineCancle.setOnClickListener(view -> {
            finish();;
        });
        adapter = new CombineAlbumAdapter(this, albums);
        binding.CombineAlbumRecycleView.setAdapter(adapter);
        setContentView(binding.getRoot());
    }

    private void moveToAlbum(String src, String dest) {
        Path result = null;
        String[] name = src.split("/");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = Files.move(Paths.get(src), Paths.get(dest + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.e("path",src+' '+dest);
        if (result != null) {
            ImageGallery.getInstance().update(this);
            AlbumGallery.getInstance().update(this);
            if (isFavorite(src)) {
                AlbumFavoriteData old = AppDatabase.getInstance(this).albumFavoriteDataDAO().getFavImgByPath(src);
                AppDatabase.getInstance(this).albumFavoriteDataDAO().delete(old);
                String name2[] = dest.split("/");
                if (!Objects.equals(name2[name2.length - 1], AlbumGallery.recycleBinFolderName)) {
                    AlbumFavoriteData newImg = new AlbumFavoriteData(dest + name[name.length - 1]);
                    AppDatabase.getInstance(this).albumFavoriteDataDAO().insert(newImg);
                }
            }
            //Toast.makeText(getActivity().getApplicationContext(), "Đã di chuyển ảnh thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isFavorite(String imagePath) {
        AlbumFavoriteData img =AppDatabase.getInstance(this).albumFavoriteDataDAO().getFavImgByPath(imagePath);
        if (img==null) {
            return false;
        }
        else {
            return true;
        }
    }

}