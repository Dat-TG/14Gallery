package com.example.a14gallery_photoandalbumgallery;

public class Photo {
    private String imgName;
    private String imgPath;
    public boolean checked;

    public Photo(String imgPath,String imgName) {
        this.imgPath = imgPath;
        this.imgName  = imgName;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}