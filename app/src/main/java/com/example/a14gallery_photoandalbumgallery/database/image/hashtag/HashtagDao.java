package com.example.a14gallery_photoandalbumgallery.database.image.hashtag;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HashtagDao {
    @Query("SELECT * FROM hashtag")
    List<Hashtag> getAll();

    @Query("SELECT * FROM hashtag WHERE id IN (:ids)")
    List<Hashtag> loadAllByIds(int[] ids);

    @Query("SELECT * FROM hashtag WHERE hashtag_name = :hName")
    Hashtag findByName(String hName);

    @Insert
    void insertAll(Hashtag... hashtags);

    @Delete
    void delete(Hashtag hashtag);
}
