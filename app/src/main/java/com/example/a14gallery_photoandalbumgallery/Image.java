package com.example.a14gallery_photoandalbumgallery;

public class Image {
    private int id;
    private String albumName;
    private String path;
    private String dateTaken;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
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

    public void setAlbumName( String name ) {
        this.albumName = name;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

}
