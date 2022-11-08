package com.example.a14gallery_photoandalbumgallery.database.image.hashtag;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageHashtagDao {
    @Query("SELECT * FROM image_hashtag")
    List<ImageHashtag> getAll();

    @Query("SELECT * FROM image_hashtag WHERE image_path IN (:imagePaths)")
    List<ImageHashtag> loadAllByPaths(String[] imagePaths);

    @Query("SELECT hashtag_name FROM hashtag JOIN image_hashtag ON hashtag.id = image_hashtag.hashtag_id" +
            " WHERE image_path IN (:imagePaths)")
    List<String> loadAllHashtagByPaths(String[] imagePaths);

    @Query("SELECT * FROM image_hashtag WHERE hashtag_id = :hashtagId")
    ImageHashtag findByHashtagId(int hashtagId);

    @Query("SELECT * FROM image_hashtag WHERE image_path = :path AND hashtag_id = :hashtagId")
    ImageHashtag findByPathAndId(String path, int hashtagId);

    @Insert
    void insertAll(ImageHashtag... imageHashtags);

    @Delete
    void delete(ImageHashtag imageHashtag);
}
