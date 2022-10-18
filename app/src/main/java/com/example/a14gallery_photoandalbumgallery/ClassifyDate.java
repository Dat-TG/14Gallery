package com.example.a14gallery_photoandalbumgallery;

import java.util.List;

public class ClassifyDate {
    private String nameClassifyDate;
    private List<Image> listImage;

    public ClassifyDate(String nameClassifyDate, List<Image> listImage) {
        this.nameClassifyDate = nameClassifyDate;
        this.listImage = listImage;
    }

    public String getNameClassifyDate() {
        return nameClassifyDate;
    }

    public void setNameClassifyDate(String nameClassifyDate) {
        this.nameClassifyDate = nameClassifyDate;
    }

    public List<Image> getListImage() {
        return listImage;
    }

    public void setListImage(List<Image> listImage) {
        this.listImage = listImage;
    }

    public void addListImage(Image img) {
        this.listImage.add(img);
    }

}