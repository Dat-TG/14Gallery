package com.example.a14gallery_photoandalbumgallery.database.albumCover;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlbumDataDao {
    @Query("SELECT * FROM albumCover")
    List<AlbumData> getAllAlbumCover();

    @Query("Select * FROM albumCover where album_name=:name")
    AlbumData getAlbumCoverByName(String name);

    @Insert
    void insertAlbumCover(AlbumData albumData);

    @Update
    void updateAlbum(AlbumData albumData);

    @Delete
    void deleteAlbum(AlbumData albumData);
}
