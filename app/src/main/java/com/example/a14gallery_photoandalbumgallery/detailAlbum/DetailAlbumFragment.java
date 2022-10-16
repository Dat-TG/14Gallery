package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import static android.content.Intent.getIntent;
import static android.content.Intent.parseIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentDetailAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.photo.Photo;

import java.util.Objects;
import java.util.Vector;


public class DetailAlbumFragment extends Fragment {
    Context _context;
    FragmentDetailAlbumBinding binding;
    Vector<Photo> albumPhotos;
    Vector<Album> albumChildren;
    Vector<String> albumUri = new Vector<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Intent intent = requireActivity().getIntent();
        Bundle bundle = intent.getExtras();
        albumPhotos = (Vector<Photo>) bundle.getSerializable("ALBUM_PHOTOS");
        for (int i = 0; i < albumPhotos.size(); i++) {
            albumUri.add(albumPhotos.get(i).getPhotoUri());
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.top_bar_menu_image, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailAlbumBinding.inflate(inflater, container, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.albumDetail.setHasFixedSize(true);
        binding.albumDetail.setLayoutManager(layoutManager);
        binding.albumDetail.setNestedScrollingEnabled(false);
//        albumChildren =  getIntent().getExtras().getSerializable("ALBUM_CHILDREN");

        binding.albumDetail.setAdapter(new ImageFragmentAdapter(getContext(), albumUri));
        return binding.getRoot();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.img_camera) {
            // Click camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.img_choose) {
            // Click choose
            return true;
        }
        if (item.getItemId() == R.id.img_grid_col_2) {
            // Click grid_col_2
            return true;
        }
        if (item.getItemId() == R.id.img_grid_col_3) {
            // Click grid_col_3
            return true;
        }
        if (item.getItemId() == R.id.img_grid_col_4) {
            // Click grid_col_4
            return true;
        }
        if (item.getItemId() == R.id.img_grid_col_5) {
            // Click grid_col_5
            return true;
        }
        if (item.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            return true;
        }
        if (item.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            return true;
        }
        if (item.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            return true;
        }
        if (item.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            return true;
        }
        if (item.getItemId() == R.id.img_setting) {
            // Click Setting
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}