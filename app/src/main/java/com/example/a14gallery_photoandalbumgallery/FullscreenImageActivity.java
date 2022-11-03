package com.example.a14gallery_photoandalbumgallery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
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

    @Override
    public void onClick(View view) {
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
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
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
            requestPermission();
            File file = new File(imagePath);
            String fileName = file.getName();
            String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");

            // Get file extension (with '.')
            String extension = "";
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                extension = fileName.substring(index);
            }
            Toast.makeText(this, "extension: " + extension, Toast.LENGTH_SHORT).show();

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
                        if (newFile.exists()) {
                            Toast.makeText(this, "Tên đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {
                            if (file.renameTo(newFile)) {
                                imagePath = newPath;
                                Uri ImageCollection;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                                    // ImageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                } else {
                                    ImageCollection = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
                                }
                                // Query to get file details
                                Cursor cursor = getContentResolver().query(ImageCollection,
                                        new String[] {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.BUCKET_DISPLAY_NAME},
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
                                getContentResolver().insert(ImageCollection, values);

                                getContentResolver().delete(ImageCollection, MediaStore.Images.Media.DATA + "=?",
                                        new String[]{file.getAbsolutePath()});
                            }
                            else {
                                Toast.makeText(this, "Đổi tên thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNeutralButton("CANCEL", (dialogInterface, i) -> {/* Cancel */});

            final AlertDialog dialog = materialAlertDialogBuilder.create();
            editText.setText(fileNameWithoutExtension);
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

        // Add to album button
        if (menuItem.getItemId() == R.id.btnAddToAlbum) {
            return true;
        }

        // Details button
        if (menuItem.getItemId() == R.id.btnDetails) {
            return true;
        }
        return false;
    }
}