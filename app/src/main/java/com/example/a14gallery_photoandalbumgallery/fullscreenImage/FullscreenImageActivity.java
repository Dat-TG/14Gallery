package com.example.a14gallery_photoandalbumgallery.fullscreenImage;

import static com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity.activityMoveLauncher;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.database.AppDatabase;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.Hashtag;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.ImageHashtag;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityFullscreenImageBinding;
import com.example.a14gallery_photoandalbumgallery.databinding.DialogDetailsBinding;
import com.example.a14gallery_photoandalbumgallery.databinding.DialogHashtagBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.password.InputPasswordActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class FullscreenImageActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ActivityFullscreenImageBinding binding;
    DialogHashtagBinding dialogHashtagBinding;
    DialogDetailsBinding dialogDetailsBinding;
    String imagePath;
    boolean isWritePermissionGranted = false;
    ActivityResultLauncher<String[]> permissionResultLauncher;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    View hashtagDialogView;
    View detailsDialogView;
    ActivityResultLauncher<Intent> activityEditLauncher;
    Uri imageUri;

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
        binding.btnHashtag.setOnClickListener(this);

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

//        activityEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult result) {
//                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
////                      ImageGallery.getInstance().update(getApplicationContext());
////                    ImageFragment.LoadAsyncTask loadAsyncTask = new ImageFragment.LoadAsyncTask();
////                    loadAsyncTask.execute();
//                }
//            }
//        });
        activityMoveLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.e("result code image frag", Integer.toString(result.getResultCode()));
                    if (result.getResultCode() == 123) {
                        Intent data = result.getData();
                        assert data != null;
                        String dest = data.getStringExtra("DEST");
                        Log.e("ImageFragment", dest);
                        moveToAlbum(dest);
                    }
                });

    }

    public long getFilePathToMediaID(String path, Context context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DATA;
        String[] selectionArgs = {path};
        String[] projection = {MediaStore.Images.Media._ID};

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }
        assert cursor != null;
        cursor.close();

        return id;
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

        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                getFilePathToMediaID(imagePath, this));
        List<Uri> uriList = new ArrayList<>();
        uriList.add(uri);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            PendingIntent pi = MediaStore.createWriteRequest(getContentResolver(), uriList);
            try {
                this.startIntentSender(pi.getIntentSender(), null,
                        0, 0, 0, null);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }

    }

    /* Launch hashtag dialog */
    private void launchHashtagDialog() {
        List<String> hashtagList = AppDatabase.getInstance(this).imageHashtagDao().loadAllHashtagByPaths(new String[]{imagePath});
        String[] dataset = hashtagList.toArray(new String[0]);
        HashtagDialogListAdapter dialogListAdapter = new HashtagDialogListAdapter(imagePath, dataset, dialogHashtagBinding.txtNumberOfHashtag);
        dialogHashtagBinding.listAddedHashtag.setLayoutManager(new LinearLayoutManager(this));

        // Hide hashtag recyclerview when typing
        dialogHashtagBinding.editTextHashtag.setOnClickListener(v -> dialogHashtagBinding.listAddedHashtag.setVisibility(View.GONE));
        dialogHashtagBinding.editTextHashtag.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null &&
                    (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
            ) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(dialogHashtagBinding.editTextHashtag.getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            // When it's done put the list back
            dialogHashtagBinding.listAddedHashtag.setVisibility(View.VISIBLE);
            return false;
        });

        // Count hashtag items
        final int[] nItem = {dialogListAdapter.getItemCount()};
        if (nItem[0] != 0)
            dialogHashtagBinding.txtNumberOfHashtag.setText(
                    String.format(Locale.ENGLISH, "Số lượng hashtag đã thêm: %d", nItem[0])
            );
        else {
            dialogHashtagBinding.txtNumberOfHashtag.setText(R.string.no_added_hashtag_msg);
        }

        dialogHashtagBinding.listAddedHashtag.setAdapter(dialogListAdapter);

        // Set click on listener for buttons
        dialogHashtagBinding.btnAddHashtag.setOnClickListener(v -> {
            String newHashtag = String.valueOf(dialogHashtagBinding.editTextHashtag.getText());
            if (!newHashtag.equals("null") && !newHashtag.isBlank()) {
                AppDatabase appDatabase = AppDatabase.getInstance(this);
                Hashtag foundHashtag = appDatabase.hashtagDao().findByName(newHashtag);
                dialogListAdapter.addItem(newHashtag);
                if (foundHashtag == null) {
                    appDatabase.hashtagDao().insertAll(new Hashtag(newHashtag));
                    int newId = appDatabase.hashtagDao().findByName(newHashtag).id;
                    appDatabase.imageHashtagDao().insertAll(new ImageHashtag(imagePath, newId));
                } else {
                    appDatabase.imageHashtagDao().insertAll(new ImageHashtag(imagePath, foundHashtag.id));
                }
                dialogHashtagBinding.editTextHashtag.setText("");
            }
            else {
                Toast.makeText(this, "Hashtag không được để trống", Toast.LENGTH_SHORT).show();
            }
        });

        materialAlertDialogBuilder.setView(hashtagDialogView)
                .setTitle("Thêm Hashtag")
                .setNeutralButton("THOÁT", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setButtonsEnabled(boolean enabled) {
        binding.btnShare.setEnabled(enabled);
        binding.btnHide.setEnabled(enabled);
        binding.btnDelete.setEnabled(enabled);
        binding.btnEdit.setEnabled(enabled);
        binding.btnMore.setEnabled(enabled);
        binding.btnHashtag.setEnabled(enabled);
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
            } else {
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
            imageUri = Uri.fromFile(new File(imagePath));
            // Set data
            Intent editIntent = new Intent(FullscreenImageActivity.this, DsPhotoEditorActivity.class);
            editIntent.setData(imageUri);
            // Set output directory
            editIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Gallery14Edit");
            // Set toolbar color
            editIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
            // Set background color#FF000000
            editIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#FF000000"));
            // Start activity
            startActivity(editIntent);
        }

        // Add Hashtag button
        if (view.getId() == R.id.btnHashtag) {
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogHashtagBinding = DialogHashtagBinding.inflate(getLayoutInflater(), binding.getRoot(), false);
            hashtagDialogView = dialogHashtagBinding.getRoot();
            launchHashtagDialog();
        }

        //  Hide button
        if (view.getId() == R.id.btnHide) {
            Intent intent = new Intent(getApplicationContext(), InputPasswordActivity.class);
            intent.putExtra("message", "AddPrivate");
            intent.putExtra("imagePath", imagePath);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        //  Delete button
        if (view.getId() == R.id.btnDelete) {
            moveToAlbum(Environment.getExternalStorageDirectory().getAbsolutePath() + AlbumGallery.rootFolder + AlbumGallery.recycleBinFolderName);
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
            File file = new File(imagePath);
            String fileName = file.getName();
            String fileNameWithoutExtension = fileName.replaceFirst("[.][^.]+$", "");

            // Get file extension (includes '.')
            String extension = "";
            int index = fileName.lastIndexOf('.');
            if (index > 0) {
                extension = fileName.substring(index);
            }

            // Request write permission
            requestPermission();

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
                            Uri ImageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

                            // Query to check if file exists in MediaStore
                            Cursor cursor = getContentResolver().query(ImageCollection,
                                    new String[]{MediaStore.Images.Media.DATA},
                                    MediaStore.Images.Media.DATA + "= ?", new String[]{file.getAbsolutePath()}, null);
                            cursor.moveToPosition(0);
                            if (cursor.getCount() != 0) {
                                ContentValues values = new ContentValues();
                                long id = getFilePathToMediaID(imagePath, this);
                                values.put(MediaStore.MediaColumns.IS_PENDING, true);
                                getContentResolver().update(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id), values, null);
                                values.clear();

                                // Update file data
                                values.put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName);
                                values.put(MediaStore.MediaColumns.IS_PENDING, false);
                                getContentResolver().update(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id), values, null);
                            }
                            cursor.close();
                            imagePath = newPath;
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


        // Add to album button
        if (menuItem.getItemId() == R.id.btnAddToAlbum) {
            //Show album to choose
            Intent intent = new Intent(this, ChooseAlbumActivity.class);
            activityMoveLauncher.launch(intent);
            return true;
        }

        // Details button
        if (menuItem.getItemId() == R.id.btnDetails) {
            // Query image to get data
            Image image = ImageGallery.getInstance().getImageByPath(this, imagePath);
            if (image == null) {
                Toast.makeText(this, "NULL when query image", Toast.LENGTH_SHORT).show();
                return true;
            }
            // View binding for the dialog
            dialogDetailsBinding = DialogDetailsBinding
                    .inflate(getLayoutInflater(), binding.getRoot(), false);
            detailsDialogView = dialogDetailsBinding.getRoot();

            // Set data to view
            File file = new File(imagePath);
            dialogDetailsBinding.txtFileName.setText(file.getName());
            dialogDetailsBinding.txtFilePath.setText(imagePath);
            dialogDetailsBinding.txtTakenOn.setText(image.getDateTaken());
            String fileSizeString = humanReadableByteCountBin(file.length())
                    + "  " + image.getResolution();
            dialogDetailsBinding.txtFileSize.setText(fileSizeString);

            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this)
                    .setTitle(getResources().getString(R.string.details_title))
                    .setCancelable(true)
                    .setView(detailsDialogView)
                    .setNegativeButton("ĐÓNG", (dialog, which) -> dialog.dismiss());
            materialAlertDialogBuilder.show();
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

    // Return a readable file size. For example: 1024 -> 1.0KB
    public String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format(Locale.UK, "%.1f %cB", value / 1024.0, ci.current());
    }

    private void moveToAlbum(String dest) {
        Path result = null;
        String src = imagePath;
        String[] name = src.split("/");
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = Files.move(Paths.get(src), Paths.get(dest + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (result != null) {
            ImageGallery.getInstance().update(this);
            AlbumGallery.getInstance().update(this);
            //Toast.makeText(getActivity().getApplicationContext(), "Đã di chuyển ảnh thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Di chuyển ảnh không thành công", Toast.LENGTH_SHORT).show();
        }
        name = dest.split("/");
        if (Objects.equals(name[name.length - 1], "RecycleBin")) {
            Snackbar.make(findViewById(R.id.full_screen_image_layout), "Xóa ảnh thành công", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(R.id.full_screen_image_layout), "Di chuyển ảnh thành công", Snackbar.LENGTH_SHORT).show();
        }
    }
}