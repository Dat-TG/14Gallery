package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

public class ImageGallery {
    public static ArrayList<Photo> listOfImages(Context context) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        int column_index_folder_name;
        //ArrayList<String> listOfAllImages = new ArrayList<>();
        ArrayList<Photo> mPhotos = new ArrayList<>();
        String absolutePathOfImage;
        String name;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};

        String orderBy = MediaStore.Video.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection,
                null, null,  orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        // Get folder name
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            //listOfAllImages.add(absolutePathOfImage);

            name=cursor.getString(column_index_folder_name);
            mPhotos.add(new Photo(absolutePathOfImage,name));
        }

        cursor.close();

        return mPhotos;
    }
}
