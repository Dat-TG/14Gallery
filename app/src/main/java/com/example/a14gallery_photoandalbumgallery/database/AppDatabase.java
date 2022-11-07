package com.example.a14gallery_photoandalbumgallery.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.Hashtag;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.HashtagDao;

@Database(entities = {Hashtag.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DBName = "14Gallery";
    private static AppDatabase instance;
    public abstract HashtagDao hashtagDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DBName)
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

}
