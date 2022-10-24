package com.example.a14gallery_photoandalbumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;
import com.example.a14gallery_photoandalbumgallery.databinding.ItemClassifyDateBinding;

import java.util.List;

public class ImageFragment extends Fragment implements MenuProvider {
    ItemClassifyDateBinding binding;

    List<Image> images;
    List<ClassifyDate> classifyDateList;
    public int typeView=4;
    public ImageFragment() {

    }

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ItemClassifyDateBinding.inflate(inflater, container, false);

        // Get images from gallery
        ImageGallery.getInstance().update(requireContext());
        images = ImageGallery.getInstance().images;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.rcvImages.setHasFixedSize(true);
        binding.rcvImages.setNestedScrollingEnabled(true);
        binding.rcvImages.setLayoutManager(layoutManager);

        images = ImageGallery.getInstance().getListOfImages(getContext());
        classifyDateList = ImageGallery.getListClassifyDate(images);

        binding.rcvImages.setNestedScrollingEnabled(false);
        binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));

        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        return binding.getRoot();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        if (!menu.hasVisibleItems()) {
            menuInflater.inflate(R.menu.top_bar_menu_image, menu);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.img_camera) {
            // Click camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {

            return true;
        }
        if (menuItem.getItemId() == R.id.clear_choose) {
            binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            typeView=2;
            binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            typeView=3;
            binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            typeView=4;
            binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            typeView=5;
            binding.rcvImages.setAdapter(new ImageFragmentAdapter(getContext(),classifyDateList,typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            return true;
        }
        if (menuItem.getItemId() == R.id.img_setting) {
            // Click Setting
            return true;
        }
        return false;
    }

}
