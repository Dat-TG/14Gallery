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
    AlbumData getAlbumCover(String name);

    @Insert
    void insertAll(List<AlbumData> albumDataList);

    @Insert
    void insertAlbumCover(AlbumData albumData);

    @Update
    void updateAlbum(AlbumData albumData);

//    @Query("Delete from albumCover Where album_name = :name")
//    void deleteAlbumCover(String name);
//    @Delete
//    void deleteAlbumCover(AlbumData albumData);
}
