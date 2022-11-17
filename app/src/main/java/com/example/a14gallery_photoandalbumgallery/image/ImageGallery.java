package com.example.a14gallery_photoandalbumgallery.image;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Singleton Pattern
public class ImageGallery {
    boolean loaded = false;
    public List<Image> images;
    private static ImageGallery INSTANCE;

    // Constructor
    private ImageGallery() {
    }

    public static ImageGallery getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageGallery();
        }
        return INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Image getImageByPath(Context context, String path) {
        Image image = null;
        Uri imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media._ID,
                MediaStore.MediaColumns.RESOLUTION
        };
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = {path};

        Cursor cursor = context.getContentResolver().query(imageCollection, projection,
                selection, selectionArgs, null);
        cursor.moveToNext();
        int dateColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
        int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int resolutionColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RESOLUTION);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy\nEEEE HH:mm", Locale.UK);
        if (cursor.getCount() != 0) {
            long dateTaken = cursor.getLong(dateColumnIndex);
            calendar.setTimeInMillis(dateTaken);
            String dateText = formatter.format(calendar.getTime());

            int id = cursor.getInt(idColumnIndex);
            String resolution = cursor.getString(resolutionColumnIndex);

            image = new Image();
            image.setPath(path);
            image.setDateTaken(dateText);
            image.setId(id);
            image.setResolution(resolution);
        }
        cursor.close();
        return image;
    }

    public List<Image> getListOfImages(Context context) {
        if (!loaded) load(context);
        return images;
    }

    public void load(Context context) {
        if (!loaded) {
            images = listOfImages(context);
            loaded = true;
        }
    }

    public void update(Context context) {
        if (!loaded)
            load(context);
        else {
            images = listOfImages(context);
        }
    }

    public static ArrayList<Image> listOfImages(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data, dateIndex;
        ArrayList<Image> listOfAllImages = new ArrayList<>();
        String absolutePathOfImage;
        long dateTaken;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID
        };

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection,
                null, null, orderBy + " DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        Calendar myCal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.UK);
        // Get folder name
        // column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            if (absolutePathOfImage.contains("/" + AlbumGallery.privateAlbumFolderName + "/") || absolutePathOfImage.contains("/" + AlbumGallery.recycleBinFolderName + "/")) {
                continue;
            }
            dateTaken = cursor.getLong(dateIndex);
            myCal.setTimeInMillis(dateTaken);
            String dateText = formatter.format(myCal.getTime());

            Image image = new Image();
            image.setPath(absolutePathOfImage);
            image.setDateTaken(dateText);
            long dateAdded = cursor.getLong(dateAddedColumn);
            image.setDateAdded(dateAdded * 1000L);
            long id = cursor.getLong(idColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            image.setUri(contentUri);
            listOfAllImages.add(image);
        }

        cursor.close();

        return listOfAllImages;
    }

    public static List<ClassifyDate> getListClassifyDate(List<Image> images) {
        List<ClassifyDate> ClassifyDateList = new ArrayList<>();
        int ClassifyDateCount = 0;

        try {
            ClassifyDateList.add(new ClassifyDate(images.get(0).getDateTaken(), new ArrayList<>()));
            ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(0));
            for (int i = 1; i < images.size(); i++) {
                if (!images.get(i).getDateTaken().equals(images.get(i - 1).getDateTaken())) {
                    ClassifyDateList.add(new ClassifyDate(images.get(i).getDateTaken(), new ArrayList<>()));
                    ClassifyDateCount++;
                }
                ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(i));
            }
            return ClassifyDateList;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ClassifyDate> getListClassifyMonth(List<Image> images) {
        List<ClassifyDate> ClassifyDateList = new ArrayList<>();
        int ClassifyDateCount = 0;
        int beg = 3, end = 5;
        String nameClassify;
        try {
            ClassifyDateList.add(new ClassifyDate(images.get(0).getDateTaken().substring(beg), new ArrayList<>()));
            ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(0));
            for (int i = 1; i < images.size(); i++) {
                if (!images.get(i).getDateTaken().substring(beg)
                        .equals(images.get(i - 1).getDateTaken().substring(beg))) {
                    nameClassify = images.get(i).getDateTaken().substring(beg);
//                    Log.d("Name: ", nameClassify);
                    ClassifyDateList.add(new ClassifyDate(nameClassify, new ArrayList<>()));
                    ClassifyDateCount++;
                }
                ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(i));
            }
            return ClassifyDateList;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ClassifyDate> getListAddImage(List<Image> images, List<Image> imagesIncluded) {
        ArrayList<RecyclerData> viewList = new ArrayList<>();

        List<ClassifyDate> ClassifyDateList = new ArrayList<>();
        List<String> imagesPath = new ArrayList<>();
        for (int i = 0; i < imagesIncluded.size(); i++)
            imagesPath.add(imagesIncluded.get(i).getPath());
        int ClassifyDateCount = 0;
        int size = 0;
        try {
            ClassifyDateList.add(new ClassifyDate(images.get(0).getDateTaken(), new ArrayList<>()));
            ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(0));
            for (int i = 1; i < images.size(); i++) {
                if (!images.get(i).getDateTaken().equals(images.get(i - 1).getDateTaken())) {
                    ClassifyDateList.add(new ClassifyDate(images.get(i).getDateTaken(), new ArrayList<>()));
                    ClassifyDateCount++;
                }
                if (!imagesPath.contains(images.get(i).getPath()))
                    ClassifyDateList.get(ClassifyDateCount).addListImage(images.get(i));
            }
            if (imagesPath.contains(images.get(0).getPath()))
                ClassifyDateList.get(0).getListImage().remove(0);
            size = ClassifyDateList.size();
            for (int i = 0; i < size; i++) {
                if (ClassifyDateList.get(i).getListImage().size() <= 0) {
                    ClassifyDateList.remove(ClassifyDateList.get(i));
                }
            }
            return ClassifyDateList;
        } catch (Exception e) {
            return null;
        }
    }
}