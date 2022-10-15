package com.example.a14gallery_photoandalbumgallery.album;

import com.example.a14gallery_photoandalbumgallery.photo.Photo;

import java.util.Vector;

public class Album {
    private int id;
    private String name;
    private String coverUri;
    private Vector<Photo> albumPhotos;
    private  Vector<Album> albumChildren;

    public int getId() {
        return id;
    }

    public void setId( int id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri( String albumCoverUri ) {
        this.coverUri = albumCoverUri;
    }

    public Vector< Photo > getAlbumPhotos() {
        if ( albumPhotos == null ) {
            albumPhotos = new Vector<>();
        }
        return albumPhotos;
    }

    public void setAlbumPhotos( Vector<Photo > albumPhotos ) {
        this.albumPhotos = albumPhotos;
    }

    public Vector< Album > getAlbumChildren() {
        if ( albumChildren == null ) {
            albumChildren = new Vector<>();
        }
        return albumChildren;
    }

    public void setAlbumChildren( Vector<Album > albumChildren ) {
        this.albumChildren = albumChildren;
    }
}
