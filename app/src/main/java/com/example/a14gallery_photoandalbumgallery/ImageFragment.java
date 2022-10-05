package com.example.a14gallery_photoandalbumgallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

public class ImageFragment extends Fragment {
    FragmentImageBinding binding;

    Integer[] images = { R.drawable.pic01, R.drawable.pic02, R.drawable.pic03, R.drawable.pic04, R.drawable.pic05,
            R.drawable.pic06, R.drawable.pic07, R.drawable.pic08, R.drawable.pic09, R.drawable.pic10,
            R.drawable.pic11, R.drawable.pic12, R.drawable.pic13, R.drawable.pic14, R.drawable.pic15,
            R.drawable.pic16, R.drawable.pic17, R.drawable.pic18, R.drawable.pic19, R.drawable.pic20,
            R.drawable.pic21, R.drawable.pic22, R.drawable.pic23, R.drawable.pic24, R.drawable.pic25 };

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);

        binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), images));

        return binding.getRoot();
        // return inflater.inflate(R.layout.fragment_image, container, false);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.img_camera:
                // Click camera
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
                // Click grid_col_5
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