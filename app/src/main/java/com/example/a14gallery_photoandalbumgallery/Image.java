package com.example.a14gallery_photoandalbumgallery;

import android.net.Uri;

public class Image {
    private int id;
    private String albumName;
    private String path;
    private String dateTaken;
    public long dateAdded;
    private boolean checked;
    public  Uri uri;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String name) {
        this.albumName = name;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Uri getUri(){return uri;}

    public void setUri(Uri uri){this.uri=uri;}

}
