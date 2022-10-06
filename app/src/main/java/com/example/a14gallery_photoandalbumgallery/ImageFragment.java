package com.example.a14gallery_photoandalbumgallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFragment extends Fragment {
    public ImageFragment() {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.top_bar_menu_image, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.img_camera:
                // Click camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
                return true;
            case R.id.img_choose:
                // Click choose
                return true;
            case R.id.img_grid_col_2:
                // Click grid_col_2
                return true;
            case R.id.img_grid_col_3:
                // Click grid_col_3
                return true;
            case R.id.img_grid_col_4:
                // Click grid_col_4
                return true;
            case R.id.img_grid_col_5:
                // Click cgrid_col_5
                return true;
            case R.id.img_view_mode_normal:
                // Click: sort UP-TO-DOWN
                return true;
            case R.id.img_view_mode_convert:
                // Click: sort DOWN-TO-UP
                return true;
            case R.id.img_view_mode_day:
                // Click Sort by day
                return true;
            case R.id.img_view_mode_month:
                // Click Sort by month
                return true;
            case R.id.img_setting:
                // Click Setting
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}