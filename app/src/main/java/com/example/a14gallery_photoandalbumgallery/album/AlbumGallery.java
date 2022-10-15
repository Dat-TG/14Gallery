package com.example.a14gallery_photoandalbumgallery.album;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;

import com.example.a14gallery_photoandalbumgallery.photo.Photo;

import java.io.File;
import java.util.Vector;

public class AlbumGallery {

    public static Pair<Vector<Album>, Vector<String>> getPhoneAlbums(Context context) {
        // Creating vectors to hold the final albums objects and albums names
        Vector<Album> Albums = new Vector<>();
        Vector<String> albumsNames = new Vector<>();

        // which image properties are we querying
        String[] projection = new String[]{
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };
        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cur = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if (cur != null && cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cur.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cur.getColumnIndex(
                        MediaStore.Images.Media._ID);

                do {
                    // Get the field values
                    bucketName = cur.getString(bucketNameColumn);
                    data = cur.getString(imageUriColumn);
                    imageId = cur.getString(imageIdColumn);

                    // Adding a new Photo object to Photos vector
                    Photo Photo = new Photo();
                    Photo.setAlbumName(bucketName);
                    Photo.setPhotoUri(data);
                    Photo.setId(Integer.parseInt(imageId));

                    if (albumsNames.contains(bucketName)) {
                        for (Album album : Albums) {
                            if (album.getName().equals(bucketName)) {
                                album.getAlbumPhotos().add(Photo);
                                break;
                            }
                        }
                    } else {
                        Album album = new Album();
                        album.setId(Photo.getId());
                        album.setName(bucketName);
                        album.setCoverUri(Photo.getPhotoUri());
                        album.getAlbumPhotos().add(Photo);

                        Albums.add(album);
                        albumsNames.add(bucketName);
                    }

                } while (cur.moveToNext());
            }

            cur.close();
        }
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/14Gallery/");
        File[] allFiles = folder.listFiles();
        for (int i=0;i<allFiles.length;i++) {
            File[]content=allFiles[i].listFiles();
            if (content.length==0) {
                Album album = new Album();
                //album.setId(Photo.getId());
                album.setName(allFiles[i].getName());
                //album.setCoverUri(Photo.getPhotoUri());
                //album.getAlbumPhotos().add(Photo);

                Albums.add(album);
                albumsNames.add(allFiles[i].getName());
            }
        }
        return new Pair(Albums, albumsNames);
    }
}
