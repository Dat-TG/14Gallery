package com.example.a14gallery_photoandalbumgallery.database.image.hashtag;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hashtag")
public class Hashtag {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "hashtag_name")
    @NonNull
    public String hashtagName;

    public Hashtag(@NonNull String hashtagName) {
        this.hashtagName = hashtagName;
    }
}
