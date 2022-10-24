package com.example.a14gallery_photoandalbumgallery;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

import java.util.List;

public class ImageFragment extends Fragment implements MenuProvider {
    FragmentImageBinding binding;

    List<Image> images;
    List<ClassifyDate> classifyDateList;

    ImageFragmentAdapter imageFragmentAdapter;
    RecyclerView.LayoutManager layoutManager;
    boolean upToDown = true;
    boolean sortByDate = true;

    ActivityResultLauncher<Intent> activityResultLauncher;

    public ImageFragment() {

    }

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater, container, false);

        // Get images from gallery
        ImageGallery.getInstance().update(requireContext());
        images = ImageGallery.getInstance().images;

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);

        images = ImageGallery.getInstance().getListOfImages(getContext());
        classifyDateList = ImageGallery.getListClassifyDate(images);

        imageFragmentAdapter = new ImageFragmentAdapter(getContext(),classifyDateList);
        binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);

        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        //
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == getActivity().RESULT_OK && result.getData() != null ){
                    LoadAsyncTask loadAsyncTask = new LoadAsyncTask();
                    loadAsyncTask.execute();
                }
            }
        });


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
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {
            // Click choose
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            // Click grid_col_2
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            // Click grid_col_3
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            // Click grid_col_4
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            // Click grid_col_5
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            if(!upToDown){
                ((LinearLayoutManager) layoutManager).setReverseLayout(false);
                upToDown = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            if(upToDown){
                ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                upToDown = false;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            if(!sortByDate){
                classifyDateList = ImageGallery.getListClassifyDate(images);
                if(!upToDown){
                    ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                }
                imageFragmentAdapter.setData(classifyDateList);
                binding.imageFragmentRecycleView.setAdapter( imageFragmentAdapter);

                sortByDate = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            if(sortByDate){
                classifyDateList = ImageGallery.getListClassifyMonth(images);
                if(!upToDown){
//                    Collections.reverse(classifyDateList);
                    ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                }
                imageFragmentAdapter.setData(classifyDateList);
                binding.imageFragmentRecycleView.setAdapter( imageFragmentAdapter);
                sortByDate = false;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_setting) {
            // Click Setting
            return true;
        }
        return false;
    }

    public class LoadAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ImageGallery.getInstance().update(getActivity());
            images = ImageGallery.listOfImages(requireContext());
            if (sortByDate) {
                classifyDateList = ImageGallery.getListClassifyDate(images);
            } else {
                classifyDateList = ImageGallery.getListClassifyMonth(images);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            imageFragmentAdapter.setData(classifyDateList);
            binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
        }
    }
}
