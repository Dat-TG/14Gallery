package com.example.a14gallery_photoandalbumgallery;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityFullscreenImageBinding;
import com.example.a14gallery_photoandalbumgallery.password.InputPasswordActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FullscreenImageActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ActivityFullscreenImageBinding binding;
    String imagePath;
    boolean isWritePermissionGranted = false;
    ActivityResultLauncher<String[]> permissionResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        imagePath = intent.getExtras().getString("path");

        // Set on click for back navigator
        binding.topAppBar.setNavigationOnClickListener(v -> finish());
        binding.topAppBar.setElevation(3);
        binding.bottomAppBar.setElevation(3);
        // Set on click for image
        binding.imageView.setOnClickListener(this);
        // Set on click for buttons
        binding.btnShare.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnHide.setOnClickListener(this);
        binding.btnMore.setOnClickListener(this);

        Glide.with(this)
                .load(imagePath)
                .fitCenter()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imageView);

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                isWritePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            }
        });
    }

    /* Request write permission if not granted */
    private void requestPermission() {
        boolean minSDK = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        isWritePermissionGranted = isWritePermissionGranted || minSDK;

        List<String> permissionRequest = new ArrayList<>();

        if (!isWritePermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionRequest.isEmpty()) {
            permissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        binding.btnShare.setEnabled(enabled);
        binding.btnHide.setEnabled(enabled);
        binding.btnDelete.setEnabled(enabled);
        binding.btnEdit.setEnabled(enabled);
        binding.btnMore.setEnabled(enabled);
    }

    @Override
    public void onClick(View view) {
        // Click on image
        if (view.getId() == R.id.imageView) {
            if (binding.topBarLayout.getVisibility() == View.VISIBLE) {
                binding.topBarLayout.animate()
                        .alpha(0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                binding.topBarLayout.setVisibility(View.GONE);
                            }
                        });
                binding.bottomBarLayout.animate()
                        .alpha(0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                binding.topBarLayout.setVisibility(View.GONE);
                                // Disable all buttons
                                setButtonsEnabled(false);
                            }
                        });
            }
            else {
                binding.topBarLayout.setAlpha(0f);
                binding.bottomBarLayout.setAlpha(0f);
                binding.topBarLayout.setVisibility(View.VISIBLE);
                binding.bottomBarLayout.setVisibility(View.VISIBLE);

                binding.topBarLayout.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .setListener(null);
                binding.bottomBarLayout.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Enable all buttons
                                setButtonsEnabled(true);
                            }
                        });
            }


        }
        // Share button
        if (view.getId() == R.id.btnShare) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, null));
        }

        //  Edit button
        if (view.getId() == R.id.btnEdit) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }

        //  Hide button
        if (view.getId() == R.id.btnHide) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
        }

        //  Delete button
        if (view.getId() == R.id.btnDelete) {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
            File file = new File(imagePath);
            if (file.delete()) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                ImageGallery.getInstance().update(this);
            } else {
                Toast.makeText(this, "Delete unsuccessfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

        //  More button
        if (view.getId() == R.id.btnMore) {
            // Initialize the popup menu
            PopupMenu popupMenu = new PopupMenu(FullscreenImageActivity.this, binding.btnMore);

            // Inflate the popup menu
            popupMenu.getMenuInflater().inflate(R.menu.menu_fullscreen, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            // Show the popup menu
            popupMenu.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        // Rename button
        if (menuItem.getItemId() == R.id.btnRename) {
            // Request write permission
            requestPermission();

            File file = new File(imagePath);
            String fileName = file.getName();
            String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");

            // Get file extension (includes '.')
            String extension = "";
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                extension = fileName.substring(index);
            }

            // Text field
            final EditText editText = new EditText(this);

            String finalExtension = extension;
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this)
                    .setTitle("Đổi tên")
                    .setMessage("Nhập tên mới")
                    .setView(editText)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        // Without extension
                        String newFileNameWithoutExt = editText.getText().toString();
                        // With extension
                        String newFileName = newFileNameWithoutExt + finalExtension;

                        String newPath = imagePath.replace(fileName, newFileName);
                        Toast.makeText(this, newPath, Toast.LENGTH_SHORT).show();
                        editText.setText(newFileNameWithoutExt);
                        File newFile = new File(newPath);

                        // Check if name exists
                        if (newFile.exists()) {
                            Toast.makeText(this, "Tên đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            // Rename file, if succeed then update MediaStore content
                            if (file.renameTo(newFile)) {
                                imagePath = newPath;
                                Uri ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

                                // Query to get file details
                                Cursor cursor = getContentResolver().query(ImageCollection,
                                        new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.BUCKET_DISPLAY_NAME},
                                        MediaStore.Images.Media.DATA + "= ?", new String[]{file.getAbsolutePath()}, null);
                                cursor.moveToPosition(0);
                                int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
                                long dateTaken = cursor.getLong(dateTakenIndex);
                                int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                                int bucketDisplayName = cursor.getInt(bucketDisplayNameIndex);
                                cursor.close();

                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, newFileNameWithoutExt);
                                values.put(MediaStore.Images.Media.DISPLAY_NAME, newFileName);
                                values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
                                values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, bucketDisplayName);
                                values.put(MediaStore.Images.Media.DATA, newPath);
                                // Insert image record with new name
                                getContentResolver().insert(ImageCollection, values);
                                // Delete old one
                                getContentResolver().delete(ImageCollection, MediaStore.Images.Media.DATA + "=?",
                                        new String[]{file.getAbsolutePath()});
                            } else {
                                Toast.makeText(this, "Đổi tên thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNeutralButton("HỦY", (dialogInterface, i) -> {/* Cancel */});

            final AlertDialog dialog = materialAlertDialogBuilder.create();
            editText.setText(fileNameWithoutExtension);
            // Check EditText input as text changed
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    // Disable OK button if name is empty
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(!TextUtils.isEmpty(editText.getText().toString()));
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            dialog.show();

            return true;
        }

        // Add image to private album
        if (menuItem.getItemId() == R.id.btnAddPrivate) {
            Intent intent = new Intent(getApplicationContext(), InputPasswordActivity.class);
            intent.putExtra("message", "AddPrivate");
            intent.putExtra("imagePath", imagePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        // Add to album button
        if (menuItem.getItemId() == R.id.btnAddToAlbum) {
            return true;
        }

        // Details button
        if (menuItem.getItemId() == R.id.btnDetails) {
            return true;
        }

        // Set image as wallpaper
        if (menuItem.getItemId() == R.id.btnHomeWallpaper) {
            File image = new File(imagePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath(), bmOptions);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // get the height and width of screen
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            WallpaperManager wallpaperManager = WallpaperManager
                    .getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                Toast.makeText(this, "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        // Set image as lockscreen wallpaper
        if (menuItem.getItemId() == R.id.btnLockWallpaper) {
            File image = new File(imagePath);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath(), bmOptions);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // get the height and width of screen
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            WallpaperManager wallpaperManager = WallpaperManager
                    .getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                Toast.makeText(this, "Set wallpaper successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }

        return false;
    }
}