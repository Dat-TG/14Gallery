package com.example.a14gallery_photoandalbumgallery.database.image.hashtag;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "image_hashtag")
public class ImageHashtag {
    @ColumnInfo(name = "image_path")
    @NonNull
    public String imagePath;

    @ColumnInfo(name = "hashtag_id")
    public int hashtagId;

    public ImageHashtag(@NonNull String imagePath, int hashtagId) {
        this.imagePath = imagePath;
        this.hashtagId = hashtagId;
    }
}
