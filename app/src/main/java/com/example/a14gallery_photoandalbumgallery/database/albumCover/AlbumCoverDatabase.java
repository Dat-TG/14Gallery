package com.example.a14gallery_photoandalbumgallery.database.albumCover;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {AlbumData.class}, version = 1)
public abstract class AlbumCoverDatabase extends RoomDatabase {
    private static final String DBName = "14Gallery";
    public abstract AlbumDataDao albumDataDao();
    private static AlbumCoverDatabase instance;

    public static synchronized AlbumCoverDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AlbumCoverDatabase.class, DBName)
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

}
