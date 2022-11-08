package com.example.a14gallery_photoandalbumgallery.database.albumCover;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albumCover")
public class AlbumData {
    @PrimaryKey
    @ColumnInfo(name="album_name")
    @NonNull
    public String name;

    @ColumnInfo(name="album_cover")
    public String albumCover;

    public AlbumData(String name, String albumCover) {
        this.name = name;
        this.albumCover = albumCover;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public String getName() {
        return name;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public void setName(String name) {
        this.name = name;
    }
}
