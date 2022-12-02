package com.example.a14gallery_photoandalbumgallery.database.albumFavorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface AlbumFavoriteDataDAO {
    @Query("SELECT * FROM FavImg")
    List<AlbumFavoriteData> getAllFavImg();

    @Query("SELECT * FROM FavImg where imagePath=:path")
    AlbumFavoriteData getFavImgByPath(String path);

    @Insert
    void insert(AlbumFavoriteData data);

    @Update
    void update(AlbumFavoriteData data);

    @Delete
    void delete(AlbumFavoriteData data);
}
