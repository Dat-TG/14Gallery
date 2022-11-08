package com.example.a14gallery_photoandalbumgallery.album;

import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.util.ArrayList;
import java.util.List;


public class Album {
    private int id;
    private String name;
    private String coverUri;
    private List<Image> albumImages = new ArrayList<>();
    private List<Album> albumChildren = new ArrayList<>();
    private String albumCover = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String albumCoverUri) {
        this.coverUri = albumCoverUri;
    }

    public List<Image> getAlbumImages() {
        return albumImages;
    }

    public void setAlbumImages(List<Image> albumImages) {
        this.albumImages = albumImages;
    }

    public List<Album> getAlbumChildren() {
        return albumChildren;
    }

    public void setAlbumChildren(List<Album> albumChildren) {
        this.albumChildren = albumChildren;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }
}
