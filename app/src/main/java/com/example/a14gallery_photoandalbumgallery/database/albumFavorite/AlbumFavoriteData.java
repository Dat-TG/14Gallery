package com.example.a14gallery_photoandalbumgallery.database.albumFavorite;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "FavImg")
public class AlbumFavoriteData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String imagePath;
    public AlbumFavoriteData(@NonNull String imagePath) {
        this.imagePath=imagePath;
    }
}
