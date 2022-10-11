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
import android.widget.Toast;
import android.widget.Toolbar;

public class AlbumFragment extends Fragment {
    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.top_bar_menu_album, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alb_add:
                // Click add ablum
                return true;
            case R.id.alb_camera:
                // Click camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
                return true;
            case R.id.alb_choose:
                // Click choose(Lựa chọn)
                return true;
            case R.id.alb_grid_col_2:
                // Click grid_col_2
                return true;
            case R.id.alb_grid_col_3:
                // Click grid_col_3
                return true;
            case R.id.alb_grid_col_4:
                // Click grid_col_4
                return true;
            case R.id.alb_grid_col_5:
                // Click cgrid_col_5
                return true;
            case R.id.alb_view_mode_normal:
                // Click Lên rồi xuống
                return true;
            case R.id.alb_view_mode_convert:
                // Click Đảo ngược
                return true;
            case R.id.alb_view_mode_day:
                // Click Xếp theo ngày
                return true;
            case R.id.alb_view_mode_month:
                // Click Xếp theo tháng
                return true;
            case R.id.alb_setting:
                // Click Setting
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}