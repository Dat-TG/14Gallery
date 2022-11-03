package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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
                MediaStore.Images.Media.DATE_TAKEN
        };

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection,
                null, null, orderBy + " DESC");
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
        Calendar myCal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.UK);
        // Get folder name
        // column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            if (absolutePathOfImage.contains("/PrivateAlbum/") || absolutePathOfImage.contains("/RecycleBin/")) {
                continue;
            }
            dateTaken = cursor.getLong(dateIndex);
            myCal.setTimeInMillis(dateTaken);
            String dateText = formatter.format(myCal.getTime());

            Image image = new Image();
            image.setPath(absolutePathOfImage);
            image.setDateTaken(dateText);
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
}
