package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static com.example.a14gallery_photoandalbumgallery.MainActivity.NightMode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.BuildConfig;
import com.example.a14gallery_photoandalbumgallery.GIF.AnimatedGIFWriter;
import com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.addImage.AddItemActivity;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.albumCover.AlbumCoverActivity;
import com.example.a14gallery_photoandalbumgallery.database.AppDatabase;
import com.example.a14gallery_photoandalbumgallery.database.albumFavorite.AlbumFavoriteData;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragment;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.setting.SettingActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class DetailAlbumActivity extends AppCompatActivity {
    ActivityDetailAlbumBinding binding;
    Toolbar toolbar;
    Album album;
    List<Image> images;
    String nameFolder;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    private ImageFragmentAdapter imageFragmentAdapter = null;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;
    boolean upToDown = true;
    boolean sortByDate = true;
    ActivityResultLauncher<Intent> activityMoveLauncher;
    String nameGIF = "animation";
    int delay = 500;

    ActivityResultLauncher<Intent> cameraResultLauncher;
    Uri imageUri;
    SharedPreferences sharedPreferences;
    int theme;

    ActivityResultLauncher<String> requestPermissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher;

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
        binding = ActivityDetailAlbumBinding.inflate(getLayoutInflater());

        toolbar = binding.appBarDetail;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Button back
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        nameFolder = getIntent().getStringExtra("NAME");
        if (nameFolder.equals(AlbumGallery.favoriteAlbumFolderName) || nameFolder.equals(AlbumGallery.privateAlbumFolderName) || nameFolder.equals(AlbumGallery.recycleBinFolderName)) {
            AlbumGallery.getInstance().update(this);
            if (nameFolder.equals(AlbumGallery.favoriteAlbumFolderName)) {
                try {
                    album = getAlbumFavorite();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Gson gson = new Gson();
                album = gson.fromJson(getIntent().getStringExtra("ALBUM"), Album.class);
            }
        } else {
            AlbumGallery.getInstance().update(this);
            album = AlbumGallery.getInstance().getAlbumByName(this, nameFolder);
        }


        activityMoveLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.e("result code image frag", Integer.toString(result.getResultCode()));
                        if (result.getResultCode() == 123) {
                            Intent data = result.getData();
                            assert data != null;
                            String dest = data.getStringExtra("DEST");
                            Log.e("ImageFragment", dest);
                            moveToAlbum(dest);
                        }
                        toViewList(images);
                        imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                        imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                        invalidateOptionsMenu();
                        onResume();
                    }
                });

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = this.getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activityResultLauncher.launch(intent);
                    } else {
                        Toast.makeText(this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                });
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    getApplicationContext();
                    if ( result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("ACTION_IMAGE_CAPTURE", "success");
                    }
                    else{
                        Log.e("ACTION_IMAGE_CAPTURE", "fail");
                        this.getContentResolver().delete(imageUri, null,null);
                    }
                });

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    String imagePath = getPathFromURI(imageUri);
                    Log.e("src", imagePath);
                    Path resultPath = null;
                    String src = imagePath;
                    String name[] = src.split("/");
                    // Log.d("Path Album ", album.getName() + " và " + album.getPath() + " NameFolder: " + nameFolder);
                    // D/Path Album: Gallery14Edit và /storage/emulated/0/Pictures/Gallery14Edit/NameFolder: Gallery14Edit
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            resultPath = Files.move(Paths.get(src), Paths.get(album.getPath() + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);

                        }
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if (resultPath != null) {
                        //Toast.makeText(getActivity().getApplicationContext(), "Đã di chuyển ảnh thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Log.e("ACTION_IMAGE_CAPTURE", "fail");
                    getApplicationContext().getContentResolver().delete(imageUri, null, null);
                }
            }
        });


        int size = album.getAlbumImages().size();

        binding.appBarDetail.setTitle(album.getName());
        if (size != 0) {
            images = album.getAlbumImages();
            toViewList(images);
            onItemClick = (position, view1) -> {
                if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                    if (!viewList.get(position).imageData.isChecked()) {
                        viewList.get(position).imageData.setChecked(true);
                        images.get(viewList.get(position).index).setChecked(true);

                    } else {
                        viewList.get(position).imageData.setChecked(false);
                        images.get(viewList.get(position).index).setChecked(false);

                    }
                    imageFragmentAdapter.notifyItemChanged(position);
                } else {
                    Intent intent = new Intent(this, FullscreenImageActivity.class);
                    intent.putExtra("albumName", album.getName());
                    if (Objects.equals(album.getName(), AlbumGallery.favoriteAlbumFolderNameVn) || Objects.equals(album.getName(), AlbumGallery.privateAlbumFolderNameVn) || Objects.equals(album.getName(), AlbumGallery.recycleBinFolderNameVn)) {
                        Gson gson = new Gson();
                        String albumObj = gson.toJson(album);
                        intent.putExtra("album", albumObj);
                    }
                    intent.putExtra("position", images.indexOf(viewList.get(position).imageData));
                    intent.putExtra("path", viewList.get(position).imageData.getPath());
                    Log.e("position selected ", position.toString());
                    Log.e("imagePath", viewList.get(position).imageData.getPath());
                    this.startActivity(intent);
                }
            };

            onItemLongClick = (position, view1) -> {
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
                viewList.get(position).imageData.setChecked(true);
                images.get(viewList.get(position).index).setChecked(true);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
            };

//            binding.recyclerDetailView.setHasFixedSize(true);
            binding.recyclerDetailView.setNestedScrollingEnabled(true);
            setRecyclerViewLayoutManager(4);
            imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
            binding.textNotFound.setVisibility(View.GONE);

        } else {
            if (album.getName().equals("Thùng rác")) {
                binding.textNotFound.setText(R.string.empty_recycle_bin);
            } else {
                binding.textNotFound.setText(R.string.no_image_found);
            }
            binding.recyclerDetailView.setVisibility(View.GONE);
        }
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        if (theme != sharedPreferences.getInt("Theme", R.style.Theme_14GalleryPhotoAndAlbumGallery_purple)) {
            recreate();
        }
        super.onResume();
        AlbumGallery.getInstance().update(this);
        nameFolder = getIntent().getStringExtra("NAME");
        if (nameFolder.equals(AlbumGallery.privateAlbumFolderName) || nameFolder.equals(AlbumGallery.favoriteAlbumFolderName) || nameFolder.equals(AlbumGallery.recycleBinFolderName)) {
            if (nameFolder.equals(AlbumGallery.favoriteAlbumFolderName)) {
                try {
                    album = getAlbumFavorite();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (nameFolder.equals(AlbumGallery.privateAlbumFolderName)) {
                album = getAlbumPrivate();
            } else {
                album = getRecycleBin();
            }
        } else {
            AlbumGallery.getInstance().update(this);
            album = AlbumGallery.getInstance().getAlbumByName(this, nameFolder);
        }
        if (album == null) {
            binding.textNotFound.setText(R.string.no_image_found);
            binding.textNotFound.setVisibility(View.VISIBLE);
            binding.recyclerDetailView.setVisibility(View.GONE);
            return;
        }
        Log.e("album này là", album.getName() + " và " + album.getPath());
        if (album.getAlbumImages().size() != 0) {
            binding.recyclerDetailView.setVisibility(View.VISIBLE);
//            binding.recyclerDetailView.setHasFixedSize(false);
            binding.recyclerDetailView.setNestedScrollingEnabled(true);
            setRecyclerViewLayoutManager(4);
            images = album.getAlbumImages();
            toViewList(images);
            imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
            binding.textNotFound.setVisibility(View.GONE);
        }

        images = album.getAlbumImages();
        Log.e("heh", Integer.toString(images.size()));
        if (images != null && images.size() > 0) {
            //images = album.getAlbumImages();
            toViewList(images);
            imageFragmentAdapter.setData(viewList);
        } else {
            if (album.getName().equals("Thùng rác") || album.getName().equals(AlbumGallery.recycleBinFolderName)) {
                binding.textNotFound.setText(R.string.empty_recycle_bin);
            } else {
                binding.textNotFound.setText(R.string.no_image_found);
            }
            binding.recyclerDetailView.setVisibility(View.GONE);
            binding.textNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.clear();
        if (album == null) {
            return false;
        }
        getMenuInflater().inflate(R.menu.top_bar_menu_detail_album, menu);
        int size = album.getAlbumImages().size();

        if (size != 0) {
            if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                menu.getItem(0).setVisible(true);
                menu.getItem(3).setVisible(true);
                menu.getItem(1).setVisible(false);
                menu.getItem(2).setVisible(false);
                menu.getItem(4).setVisible(false);
                menu.getItem(5).setVisible(false);
                menu.getItem(6).setVisible(true);
                menu.getItem(7).setVisible(true);
                menu.getItem(9).setVisible(true);
                menu.getItem(10).setVisible(false);
                menu.getItem(8).setVisible(false);
            } else {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                menu.getItem(3).setVisible(true);
                menu.getItem(4).setVisible(true);
                menu.getItem(5).setVisible(true);
                menu.getItem(6).setVisible(false);
                menu.getItem(10).setVisible(true);
                menu.getItem(7).setVisible(false);
                menu.getItem(2).setVisible(true);
                menu.getItem(9).setVisible(false);
                menu.getItem(8).setVisible(true);
            }
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(true);
            menu.getItem(6).setVisible(false);
            menu.getItem(10).setVisible(true);
            menu.getItem(7).setVisible(false);
            menu.getItem(9).setVisible(false);
            menu.getItem(8).setVisible(true);
        }
        if (Objects.equals(nameFolder, AlbumGallery.recycleBinFolderName)) {
            MenuItem item = menu.findItem(R.id.move_images);
            item.setTitle("Khôi phục");
        }
        if (Objects.equals(nameFolder, AlbumGallery.privateAlbumFolderName) || Objects.equals(nameFolder, AlbumGallery.recycleBinFolderName) || Objects.equals(nameFolder, AlbumGallery.favoriteAlbumFolderName)) {
            MenuItem item = menu.findItem(R.id.detAlb_coverAlbum);
            item.setVisible(false);
            item = menu.findItem(R.id.detAlb_deleteAlbum);
            item.setVisible(false);
            item = menu.findItem(R.id.detAlb_rename);
            item.setVisible(false);
            item = menu.findItem(R.id.detAlb_add_image);
            item.setVisible(false);
            item = menu.findItem(R.id.detAlb_camera);
            item.setVisible(false);
        }
        if (Objects.equals(nameFolder, AlbumGallery.favoriteAlbumFolderName)) {
            MenuItem item = menu.findItem(R.id.move_images);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        if (menuItem.getItemId() == android.R.id.home) { // Click back button
            finish();
            return true;
        }
        if (album == null) {
            return false;
        }
        if (menuItem.getItemId() == R.id.detAlb_add_image) { // add Image
            Intent intent = new Intent(this, AddItemActivity.class);
            intent.putExtra("NAME", album.getName());
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_rename) {//Rename album
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("PERMISSION NEEDED");
                    alert.setMessage("This app need you allow to mange your storage to be able to create, or modify images and albums");
                    alert.setPositiveButton("ALLOW", (dialog, whichButton) -> { // Set an EditText view to get user input
                        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(intent, 501);
                    });
                    alert.setNegativeButton("DENY", (dialog, whichButton) -> {/* Canceled.*/});
                    ImageView img = new ImageView(this);
                    Glide.with(this).asGif().load(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + this.getResources().getResourcePackageName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceTypeName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceEntryName(R.drawable.instruction_manage))).into(img);
                    img.setImageResource(R.drawable.instruction_manage);
                    alert.setView(img);
                    alert.show();
                    return false;
                }
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Đổi tên album");
            LinearLayout layout = new LinearLayout(this);
            final TextView textView = new TextView(this);
            final EditText input = new EditText(this);
            textView.setText("Nhập tên album mới");
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(textView);
            layout.addView(input);
            layout.setPadding(50, 50, 50, 0);
            alert.setView(layout);
            alert.setPositiveButton("ĐỒNG Ý", (dialog, whichButton) -> {
                String nameAlb = input.getText().toString();
                if (nameAlb.isEmpty()) {
                    Snackbar.make(findViewById(R.id.detail_album_layout), "Tên album không hợp lệ", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                File sourceFile = new File(album.getPath());
                String name[] = album.getPath().split("/");
                String dest = "";
                for (int i = 0; i < name.length - 1; i++) {
                    dest += name[i] + "/";
                }
                dest += nameAlb;
                File destFile = new File(dest);
                if (sourceFile.renameTo(destFile)) {
                    Snackbar.make(findViewById(R.id.detail_album_layout), "Đổi tên album thành công", Snackbar.LENGTH_SHORT).show();
                    binding.appBarDetail.setTitle(nameAlb);
                } else {
                    Snackbar.make(findViewById(R.id.detail_album_layout), "Đổi tên album không thành công", Snackbar.LENGTH_SHORT).show();
                }
                AlbumGallery.getInstance().update(this);
                setContentView(binding.getRoot());
            });
            alert.setNegativeButton("HỦY", (dialog, whichButton) -> {
            });
            alert.show();
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_camera) { // Click Camera
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_choose) {
            images.forEach(imageData -> imageData.setChecked(true));
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.clear_choose) {
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_coverAlbum) {
            Intent intent = new Intent(getApplicationContext(), AlbumCoverActivity.class);
            intent.putExtra("NAME", album.getName());
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_2) {
            setRecyclerViewLayoutManager(2);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_3) {
            setRecyclerViewLayoutManager(3);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_4) {
            setRecyclerViewLayoutManager(4);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_grid_col_5) {
            setRecyclerViewLayoutManager(5);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_normal) {
            // Click sort UP-TO-DOWN
            if (!upToDown) {
                toViewList(images);
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                upToDown = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_convert) {
            // Click sort DOWN-TO-UP
            if (upToDown) {
                setDownToUp();
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                upToDown = false;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_day) {
            // Click Sort by day
            if (!sortByDate) {
                toViewList(images);
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                sortByDate = true;
                upToDown = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_month) {
            // Click Sort by month
            if (sortByDate) {
                toViewListMonth();
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                sortByDate = false;
                upToDown = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_deleteAlbum) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("PERMISSION NEEDED");
                    alert.setMessage("This app need you allow to mange your storage to be able to create, or modify images and albums");
                    alert.setPositiveButton("ALLOW", (dialog, whichButton) -> { // Set an EditText view to get user input
                        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(intent, 501);
                    });
                    alert.setNegativeButton("DENY", (dialog, whichButton) -> {/* Canceled.*/});
                    ImageView img = new ImageView(this);
                    Glide.with(this).asGif().load(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + this.getResources().getResourcePackageName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceTypeName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceEntryName(R.drawable.instruction_manage))).into(img);
                    img.setImageResource(R.drawable.instruction_manage);
                    alert.setView(img);
                    alert.show();
                    return false;
                }
            }
            deleteAlbumAndMoveImages(album);
            onResume();
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_deleteImg) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("PERMISSION NEEDED");
                    alert.setMessage("This app need you allow to mange your storage to be able to create, or modify images and albums");
                    alert.setPositiveButton("ALLOW", (dialog, whichButton) -> { // Set an EditText view to get user input
                        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(intent, 501);
                    });
                    alert.setNegativeButton("DENY", (dialog, whichButton) -> {/* Canceled.*/});
                    ImageView img = new ImageView(this);
                    Glide.with(this).asGif().load(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + this.getResources().getResourcePackageName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceTypeName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceEntryName(R.drawable.instruction_manage))).into(img);
                    img.setImageResource(R.drawable.instruction_manage);
                    alert.setView(img);
                    alert.show();
                    return false;
                }
            }
            moveToAlbum(Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.recycleBinFolderName);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            onResume();
            return true;
        }
        if (menuItem.getItemId() == R.id.move_images) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("PERMISSION NEEDED");
                    alert.setMessage("This app need you allow to mange your storage to be able to create, or modify images and albums");
                    alert.setPositiveButton("ALLOW", (dialog, whichButton) -> { // Set an EditText view to get user input
                        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityForResult(intent, 501);
                    });
                    alert.setNegativeButton("DENY", (dialog, whichButton) -> {/* Canceled.*/});
                    ImageView img = new ImageView(this);
                    Glide.with(this).asGif().load(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                            + "://" + this.getResources().getResourcePackageName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceTypeName(R.drawable.instruction_manage)
                            + '/' + this.getResources().getResourceEntryName(R.drawable.instruction_manage))).into(img);
                    img.setImageResource(R.drawable.instruction_manage);
                    alert.setView(img);
                    alert.show();
                    return false;
                }
            }
            Intent intent = new Intent(this, ChooseAlbumActivity.class);
            if (Objects.equals(nameFolder, AlbumGallery.recycleBinFolderName))
                intent.putExtra("folder", AlbumGallery.recycleBinFolderName);
            activityMoveLauncher.launch(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.create_GIF) {
            inputGIF();
            return true;
        }
        if (menuItem.getItemId() == R.id.create_PDF) {
            if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                ArrayList<Image> selectedImages = images.stream()
                        .filter(Image::isChecked)
                        .collect(Collectors.toCollection(ArrayList::new));
                ImageFragment.createPDF(this, selectedImages);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
            } else {
                ImageFragment.createPDF(this, images);
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.setting) {   // Click Setting
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void toViewListMonth() {
        if (images.size() > 0) {
            int beg = 3;
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label = label.substring(beg, label.length() - 6);
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                String labelCur = images.get(i).getDateTaken();
                labelCur = labelCur.substring(beg, labelCur.length() - 6);
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).type == RecyclerData.Type.Label) {
                switch (viewList.get(i).labelData) {
                    case "January": {
                        viewList.get(i).labelData = "Tháng 1";
                        break;
                    }
                    case "February": {
                        viewList.get(i).labelData = "Tháng 2";
                        break;
                    }
                    case "March": {
                        viewList.get(i).labelData = "Tháng 3";
                        break;
                    }
                    case "April": {
                        viewList.get(i).labelData = "Tháng 4";
                        break;
                    }
                    case "May": {
                        viewList.get(i).labelData = "Tháng 5";
                        break;
                    }
                    case "June": {
                        viewList.get(i).labelData = "Tháng 6";
                        break;
                    }
                    case "July": {
                        viewList.get(i).labelData = "Tháng 7";
                        break;
                    }
                    case "August": {
                        viewList.get(i).labelData = "Tháng 8";
                        break;
                    }
                    case "September": {
                        viewList.get(i).labelData = "Tháng 9";
                        break;
                    }
                    case "October": {
                        viewList.get(i).labelData = "Tháng 10";
                        break;
                    }
                    case "November": {
                        viewList.get(i).labelData = "Tháng 11";
                        break;
                    }
                    case "December": {
                        viewList.get(i).labelData = "Tháng 12";
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    private void toViewList(List<Image> images) {
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                String labelCur = images.get(i).getDateTaken();
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
        int beg = 3;
        for (int i = 0; i < viewList.size(); i++) {
            if (viewList.get(i).type == RecyclerData.Type.Label) {
                switch (viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6)) {
                    case "January": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 1");
                        break;
                    }
                    case "February": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 2");
                        break;
                    }
                    case "March": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 3");
                        break;
                    }
                    case "April": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 4");
                        break;
                    }
                    case "May": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 5");
                        break;
                    }
                    case "June": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 6");
                        break;
                    }
                    case "July": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 7");
                        break;
                    }
                    case "August": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 8");
                        break;
                    }
                    case "September": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 9");
                        break;
                    }
                    case "October": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 10");
                        break;
                    }
                    case "November": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 11");
                        break;
                    }
                    case "December": {
                        viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 12");
                        break;
                    }
                    default:
                        break;
                }

            }
        }
    }

    private void setDownToUp() {
        if (images.size() > 0) {
            if (!upToDown && !sortByDate) {
                int beg = 3;
                viewList = new ArrayList<>();
                String label = images.get(0).getDateTaken();
                label = label.substring(beg, label.length() - 6);
                label += '.';
                for (int i = images.size() - 1; i >= 0; i--) {
                    String labelCur = images.get(i).getDateTaken();
                    labelCur = labelCur.substring(beg, labelCur.length() - 6);
                    if (!labelCur.equals(label)) {
                        label = labelCur;
                        viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                    }
                    viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
                }
                for (int i = 0; i < viewList.size(); i++) {
                    if (viewList.get(i).type == RecyclerData.Type.Label) {
                        switch (viewList.get(i).labelData) {
                            case "January": {
                                viewList.get(i).labelData = "Tháng 1";
                                break;
                            }
                            case "February": {
                                viewList.get(i).labelData = "Tháng 2";
                                break;
                            }
                            case "March": {
                                viewList.get(i).labelData = "Tháng 3";
                                break;
                            }
                            case "April": {
                                viewList.get(i).labelData = "Tháng 4";
                                break;
                            }
                            case "May": {
                                viewList.get(i).labelData = "Tháng 5";
                                break;
                            }
                            case "June": {
                                viewList.get(i).labelData = "Tháng 6";
                                break;
                            }
                            case "July": {
                                viewList.get(i).labelData = "Tháng 7";
                                break;
                            }
                            case "August": {
                                viewList.get(i).labelData = "Tháng 8";
                                break;
                            }
                            case "September": {
                                viewList.get(i).labelData = "Tháng 9";
                                break;
                            }
                            case "October": {
                                viewList.get(i).labelData = "Tháng 10";
                                break;
                            }
                            case "November": {
                                viewList.get(i).labelData = "Tháng 11";
                                break;
                            }
                            case "December": {
                                viewList.get(i).labelData = "Tháng 12";
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            } else {
                viewList = new ArrayList<>();
                String label = images.get(0).getDateTaken();
                label += '.';
                for (int i = images.size() - 1; i >= 0; i--) {
                    String labelCur = images.get(i).getDateTaken();
                    if (!labelCur.equals(label)) {
                        label = labelCur;
                        viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                    }
                    viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
                }
                int beg = 3;
                for (int i = 0; i < viewList.size(); i++) {
                    if (viewList.get(i).type == RecyclerData.Type.Label) {
                        switch (viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6)) {
                            case "January": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 1");
                                break;
                            }
                            case "February": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 2");
                                break;
                            }
                            case "March": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 3");
                                break;
                            }
                            case "April": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 4");
                                break;
                            }
                            case "May": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 5");
                                break;
                            }
                            case "June": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 6");
                                break;
                            }
                            case "July": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 7");
                                break;
                            }
                            case "August": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 8");
                                break;
                            }
                            case "September": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 9");
                                break;
                            }
                            case "October": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 10");
                                break;
                            }
                            case "November": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 11");
                                break;
                            }
                            case "December": {
                                viewList.get(i).labelData = viewList.get(i).labelData.replace(viewList.get(i).labelData.substring(beg, viewList.get(i).labelData.length() - 6), "Tháng 12");
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    private void setRecyclerViewLayoutManager(int newTypeView) {
        typeView = newTypeView;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), typeView);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewList.get(position).type == RecyclerData.Type.Label ? typeView : 1;
            }
        });
        binding.recyclerDetailView.setLayoutManager(gridLayoutManager);
    }

    public boolean isFavorite(String imagePath) {
        AlbumFavoriteData img = AppDatabase.getInstance(this).albumFavoriteDataDAO().getFavImgByPath(imagePath);
        if (img == null) {
            return false;
        } else {
            return true;
        }
    }

    private void moveToAlbum(String dest) {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Image image : selectedImages) {
            Log.e("src", image.getPath());
            Path result = null;
            String src = image.getPath();
            String[] name = src.split("/");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result = Files.move(Paths.get(src), Paths.get(dest + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (result != null) {
                if (isFavorite(src)) {
                    AlbumFavoriteData old = AppDatabase.getInstance(this).albumFavoriteDataDAO().getFavImgByPath(src);
                    AppDatabase.getInstance(this).albumFavoriteDataDAO().delete(old);
                    String name2[] = dest.split("/");
                    if (!Objects.equals(name2[name2.length - 1], AlbumGallery.recycleBinFolderName)) {
                        AlbumFavoriteData newImg = new AlbumFavoriteData(dest + name[name.length - 1]);
                        AppDatabase.getInstance(this).albumFavoriteDataDAO().insert(newImg);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công", Toast.LENGTH_SHORT).show();
            }
            name = dest.split("/");
            if (Objects.equals(name[name.length - 1], AlbumGallery.recycleBinFolderName)) {

                Snackbar.make(findViewById(R.id.detail_album_layout), "Xóa ảnh thành công", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(findViewById(R.id.detail_album_layout), "Di chuyển ảnh thành công", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteAlbumAndMoveImages(Album albumDelete) { //Dest: move all image to Pictures default
        List<Album> albumDefaults = AlbumGallery.getPhoneAlbums(this).stream()
                .filter(album1 -> !album1.getPath().contains(AlbumGallery.rootFolder))
                .collect(Collectors.toCollection(ArrayList::new));
        List<Image> imagesAlbum = albumDelete.getAlbumImages();
        AlertDialog.Builder alert = new AlertDialog.Builder(
                this);
        alert.setTitle("Xóa Album");
        alert.setMessage("Bạn có chắc chắn muốn xóa album này không ?");

        alert.setPositiveButton("Xóa", (dialog, which) -> {
            for (Image image : imagesAlbum) {
                Log.e("src", image.getPath());
                String src = image.getPath();
                String[] name = src.split("/");
                Path result = null;
                String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures";
                File Pictures = new File(dest);
                if (!Pictures.exists()) {
                    Pictures.mkdirs();
                }
                dest += "/" + name[name.length - 1];
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        result = Files.move(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Xóa album không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (result != null) {
                    if (isFavorite(src)) {
                        AlbumFavoriteData old = AppDatabase.getInstance(this).albumFavoriteDataDAO().getFavImgByPath(src);
                        AppDatabase.getInstance(this).albumFavoriteDataDAO().delete(old);
                    }
                    //Toast.makeText(getActivity().getApplicationContext(), "Đã di chuyển ảnh thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Xóa album thành công", Toast.LENGTH_SHORT).show();
                }
            }
            File dirName = new File(album.getPath());
            dirName.delete();
            finish();
            dialog.dismiss();
        });
        alert.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        alert.show();
    }


    private Album getAlbumPrivate() {
        Date lastModDate;
        Album Private = new Album();
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.privateAlbumFolderName);
        File[] content = folder.listFiles();
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.privateAlbumFolderName + '/';
        folderPath = folderPath + "%";
        Private.setName("Riêng tư");
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
        };
        String[] selectionArgs = new String[]{folderPath};
        String selection = MediaStore.Images.Media.DATA + " like ? ";
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_MODIFIED;
        Cursor cursor = getContentResolver().query(images, projection, selection, selectionArgs, orderBy + " DESC");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                long dateTaken;
                int bucketNameColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media._ID);

                int dateTakenColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);
                do {
                    Calendar myCal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                    // Get the field values
                    bucketName = cursor.getString(bucketNameColumn);
                    data = cursor.getString(imageUriColumn);
                    imageId = cursor.getString(imageIdColumn);
//                    dateTaken = cursor.getLong(dateTakenColumn);
//                    myCal.setTimeInMillis(dateTaken);
//                    String dateText = formatter.format(myCal.getTime());
                    File file = new File(data);
                    lastModDate = new Date(file.lastModified());
                    myCal.setTimeInMillis(lastModDate.getTime());
                    String dateText = formatter.format(myCal.getTime());

                    Image image = new Image();
                    image.setAlbumName(bucketName);
                    image.setPath(data);
                    image.setId(Integer.parseInt(imageId));
                    image.setDateTaken(dateText);

                    Private.getAlbumImages().add(image);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        return Private;
    }

    public Album getAlbumFavorite() throws ParseException {
        Album Favorite = new Album();
        Favorite.setName("Ưa thích");
        List<AlbumFavoriteData> FavList = AppDatabase.getInstance(this).albumFavoriteDataDAO().getAllFavImg();
        for (int i = 0; i < FavList.size(); i++) {
            Image img = new Image();
            img.setPath(FavList.get(i).imagePath);
            Log.e("FavAlbum",img.getPath());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                img = ImageGallery.getInstance().getImageByPath(this, FavList.get(i).imagePath);
                SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy\nEEEE HH:mm", Locale.UK);
                Date d = formatter.parse(img.getDateTaken());
                SimpleDateFormat Nformatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.UK);
                String datetext = Nformatter.format(d);
                img.setDateTaken(datetext);
                Log.e("date", img.getDateTaken());
            }
            Favorite.getAlbumImages().add(img);
        }
        return Favorite;
    }

    private Album getRecycleBin() {
        Date lastModDate;
        Album RecycleBin = new Album();
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.recycleBinFolderName);
        File[] content = folder.listFiles();
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.recycleBinFolderName + '/';
        folderPath = folderPath + "%";
        RecycleBin.setName("Thùng rác");
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN
        };
        String[] selectionArgs = new String[]{folderPath};
        String selection = MediaStore.Images.Media.DATA + " like ? ";
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Video.Media.DATE_MODIFIED;
        Cursor cursor = getContentResolver().query(images, projection, selection, selectionArgs, orderBy + " DESC");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                long dateTaken;
                int bucketNameColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media._ID);

                int dateTakenColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_TAKEN);
                do {
                    Calendar myCal = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
                    // Get the field values
                    bucketName = cursor.getString(bucketNameColumn);
                    data = cursor.getString(imageUriColumn);
                    imageId = cursor.getString(imageIdColumn);
//                    dateTaken = cursor.getLong(dateTakenColumn);
//                    myCal.setTimeInMillis(dateTaken);
//                    String dateText = formatter.format(myCal.getTime());
                    File file = new File(data);
                    lastModDate = new Date(file.lastModified());
                    myCal.setTimeInMillis(lastModDate.getTime());
                    String dateText = formatter.format(myCal.getTime());

                    Image image = new Image();
                    image.setAlbumName(bucketName);
                    image.setPath(data);
                    image.setId(Integer.parseInt(imageId));
                    image.setDateTaken(dateText);

                    RecycleBin.getAlbumImages().add(image);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return RecycleBin;
    }

    private void createGIF(String dest, int delay) {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
        writer.setDelay(delay);
        OutputStream os;
        try {
            os = new FileOutputStream(dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // Use -1 for both logical screen width and height to use the first frame dimension
        try {
            writer.prepareForWrite(os, -1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
        for (Image image : selectedImages) {
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath()); // Grab the Bitmap whatever way you can
            try {
                writer.writeFrame(os, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                return;
            }
            // Keep adding frame here
        }
        try {
            writer.finishWrite(os);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // And you are done!!!
        Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF thành công", Snackbar.LENGTH_SHORT).show();
    }

    public void inputGIF() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tạo ảnh GIF");
        LinearLayout layout = new LinearLayout(this);
        final TextView textView1 = new TextView(this);
        final EditText input1 = new EditText(this); // Set an EditText view to get user input
        final TextView textView2 = new TextView(this);
        final EditText input2 = new EditText(this);
        textView1.setText("Nhập tên ảnh (không cần .gif)");
        textView2.setText("Nhập thời gian delay giữa các frame (ms)");
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView1);
        layout.addView(input1);
        layout.addView(textView2);
        layout.addView(input2);
        layout.setPadding(50, 50, 50, 0);
        alert.setView(layout);
        String dest = album.getPath() + "/";
        File file = new File(dest);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (success) {
                Log.e("RES", "Success");
            } else {
                Log.e("RES", "Failed");
            }
        }
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            nameGIF = input1.getText().toString();
            if (nameGIF.isEmpty()) {
                Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                toViewList(images);
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
                return;
            }
            try {
                delay = Integer.parseInt(input2.getText().toString());
            } catch (Exception e) {
                Snackbar.make(findViewById(R.id.detail_album_layout), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                toViewList(images);
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
                return;
            }
            File anh = new File(dest + nameGIF + ".gif");
            if (anh.exists()) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(this);
                confirm.setTitle("Đợi một chút");
                confirm.setCancelable(true);
                confirm.setMessage("File " + nameGIF + ".gif đã tồn tại. Bạn có muốn ghi đè không?")
                        .setPositiveButton("Có", (dialog1, id) -> {
                            try {
                                createGIF(dest + nameGIF + ".gif", delay);
                                toViewList(images);
                                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                                invalidateOptionsMenu();
                                onResume();
                            } catch (Exception e) {
                                //Exception
                            }
                        })
                        .setNegativeButton("Không", (dialog12, id) -> {
                            dialog12.cancel();
                            toViewList(images);
                            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                            invalidateOptionsMenu();
                        })
                        .show();
            } else {
                createGIF(dest + nameGIF + ".gif", delay);
                toViewList(images);
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
                onResume();
            }
        });
        alert.setNegativeButton("Hủy", (dialog, whichButton) -> {
            toViewList(images);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            invalidateOptionsMenu();
        });
        alert.show();
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}