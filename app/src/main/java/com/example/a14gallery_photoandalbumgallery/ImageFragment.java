package com.example.a14gallery_photoandalbumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

import java.util.List;

public class ImageFragment extends Fragment {
    FragmentImageBinding binding;

    List<String> images;

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
        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(false);
        images = ImageGallery.listOfImages(requireContext());
        binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), images));

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