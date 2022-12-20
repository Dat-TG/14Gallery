package com.example.a14gallery_photoandalbumgallery.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.a14gallery_photoandalbumgallery.database.albumCover.AlbumData;
import com.example.a14gallery_photoandalbumgallery.database.albumCover.AlbumDataDao;
import com.example.a14gallery_photoandalbumgallery.database.albumFavorite.AlbumFavoriteData;
import com.example.a14gallery_photoandalbumgallery.database.albumFavorite.AlbumFavoriteDataDAO;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.Hashtag;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.HashtagDao;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.ImageHashtag;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.ImageHashtagDao;

@Database(entities = {Hashtag.class, ImageHashtag.class, AlbumData.class, AlbumFavoriteData.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DBName = "14Gallery";
    private static AppDatabase instance;

    public abstract HashtagDao hashtagDao();

    public abstract ImageHashtagDao imageHashtagDao();

    public abstract AlbumDataDao albumDataDao();

    public abstract AlbumFavoriteDataDAO albumFavoriteDataDAO();

    public static synchronized AppDatabase getInstance(Context context) {
        // Clear database every time migrate
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName)
                    .allowMainThreadQueries()
//                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

}
