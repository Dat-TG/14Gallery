package com.example.a14gallery_photoandalbumgallery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
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
    public int typeView = 4;

    ImageFragmentAdapter imageFragmentAdapter;
    RecyclerView.LayoutManager layoutManager;
    boolean upToDown = true;
    boolean sortByDate = true;


    private Uri imageUri;
    ActivityResultLauncher<String> requestPermissionLauncher;
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

        binding.imageFragmentRecycleView.setNestedScrollingEnabled(false);
        imageFragmentAdapter = new ImageFragmentAdapter(getContext(), classifyDateList, typeView);
        binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);

        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // permission camera
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = getActivity().getApplicationContext().getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activityResultLauncher.launch(intent);
                    } else {
                        Toast.makeText(getContext(), "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                });
        //
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
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
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {

            return true;
        }
        if (menuItem.getItemId() == R.id.clear_choose) {
            binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            typeView = 2;
            binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            typeView = 3;
            binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            typeView = 4;
            binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            typeView = 5;
            binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, typeView));
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            if (!upToDown) {
                ((LinearLayoutManager) layoutManager).setReverseLayout(false);
                upToDown = true;
            }else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            if (upToDown) {
                ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                upToDown = false;
            }else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            if (!sortByDate) {
                classifyDateList = ImageGallery.getListClassifyDate(images);
                if (!upToDown) {
                    ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                }
                imageFragmentAdapter.setData(classifyDateList);
                sortByDate = true;
            }else{
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            if(sortByDate){
                classifyDateList = ImageGallery.getListClassifyMonth(images);
                if(!upToDown){
                    ((LinearLayoutManager) layoutManager).setReverseLayout(true);
                }
                imageFragmentAdapter.setData(classifyDateList);
                sortByDate = false;
            } else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
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
        }
    }
}
