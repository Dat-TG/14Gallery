package com.example.a14gallery_photoandalbumgallery.addImage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.a14gallery_photoandalbumgallery.album.Album;


public class ViewPagerAdapter extends FragmentStateAdapter {
    Album album;

    public ViewPagerAdapter(FragmentActivity fa, Album album) {
        super(fa);
        this.album = album;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new RecyclerImageViewFragment(album);
        return new RecyclerAlbumViewFragment(album);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}