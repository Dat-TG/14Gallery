package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.a14gallery_photoandalbumgallery.ClassifyDate;
import com.example.a14gallery_photoandalbumgallery.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageFragment;
import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.MainActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.addImage.AddItemActivity;
import com.example.a14gallery_photoandalbumgallery.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.albumCover.AlbumCoverActivity;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class DetailAlbumActivity extends AppCompatActivity {
    ActivityDetailAlbumBinding binding;
    Toolbar toolbar;
    Album album;
    List<Image> images;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    private ImageFragmentAdapter imageFragmentAdapter=null;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;
    boolean upToDown = true;
    boolean sortByDate = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAlbumBinding.inflate(getLayoutInflater());

        toolbar = binding.appBarDetail;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Button back
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Gson gson = new Gson();
        album = gson.fromJson(getIntent().getStringExtra("ALBUM"), Album.class);
        int size = album.getAlbumImages().size();


        binding.appBarDetail.setTitle(album.getName());
        if (size != 0) {
            images = album.getAlbumImages();
            toViewList(images);
            onItemClick = (position, view1) -> {
                if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                    if (!viewList.get(position).imageData.isChecked()) {
                        viewList.get(position).imageData.setChecked(true);
                        images.get(viewList.get(position).index).setChecked(true);

                    } else {
                        viewList.get(position).imageData.setChecked(false);
                        images.get(viewList.get(position).index).setChecked(false);

                    }

                    imageFragmentAdapter.notifyItemChanged(position);
                } else {
                    Intent intent = new Intent(this, FullscreenImageActivity.class);
                    intent.putExtra("path", viewList.get(position).imageData.getPath());
                    this.startActivity(intent);
                }
            };

            onItemLongClick = (position, view1) -> {
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
                viewList.get(position).imageData.setChecked(true);
                images.get(viewList.get(position).index).setChecked(true);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                invalidateOptionsMenu();
            };

            binding.recyclerDetailView.setHasFixedSize(true);
            binding.recyclerDetailView.setNestedScrollingEnabled(true);
            setRecyclerViewLayoutManager(4);
            imageFragmentAdapter = new ImageFragmentAdapter(viewList,onItemClick,onItemLongClick);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
            binding.textNotFound.setVisibility(View.GONE);

        }else {
            if (album.getName().equals("Thùng rác")) {
                binding.textNotFound.setText(R.string.empty_recycle_bin);
            }
            else {
                binding.textNotFound.setText(R.string.no_image_found);
            }
            binding.recyclerDetailView.setVisibility(View.GONE);
        }
        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.top_bar_menu_image, menu);
        int size = album.getAlbumImages().size();
        if(size!=0) {
            if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                menu.getItem(0).setVisible(true);
                menu.getItem(6).setVisible(false);
                menu.getItem(2).setVisible(true);
                menu.getItem(3).setVisible(true);
                menu.getItem(4).setVisible(true);
                menu.getItem(5).setVisible(true);
            } else {
                menu.getItem(0).setVisible(false);
                menu.getItem(1).setVisible(true);
                menu.getItem(6).setVisible(true);
                menu.getItem(2).setVisible(false);
                menu.getItem(3).setVisible(false);
                menu.getItem(4).setVisible(false);
                menu.getItem(5).setVisible(false);
            }
        }
        else{
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(6).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        if (menuItem.getItemId() == android.R.id.home) { // Click back button
            finish();
            return true;
        }
        if(menuItem.getItemId() == R.id.detAlb_add_image) { // add Image
            Intent intent = new Intent(this, AddItemActivity.class);
            Gson gson = new Gson();
            String imagesObj = gson.toJson(album);
            intent.putExtra("album", imagesObj);
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_camera) { // Click Camera
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {
            images.forEach(imageData -> imageData.setChecked(true));
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.clear_choose) {
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_coverAlbum) {
            Intent intent = new Intent(getApplicationContext(), AlbumCoverActivity.class);
            Gson gson = new Gson();
            String imagesObj = gson.toJson(album);
            intent.putExtra("ALBUM", imagesObj);
            startActivity(intent);
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            setRecyclerViewLayoutManager(2);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            setRecyclerViewLayoutManager(3);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            setRecyclerViewLayoutManager(4);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            setRecyclerViewLayoutManager(5);
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            if (!upToDown) {
                toViewList(images);
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                upToDown = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            if (upToDown) {
                setDownToUp();
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                upToDown = false;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_day) {
            // Click Sort by day
            if (!sortByDate) {
                toViewList(images);
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
                sortByDate = true;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_view_mode_month) {
            // Click Sort by month
            if(sortByDate){
                toViewListMonth();
                imageFragmentAdapter.setData(viewList);
                binding.recyclerDetailView.setAdapter( imageFragmentAdapter);
                sortByDate = false;
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.detAlb_setting) {
            // Click Setting
            return true;
        }
        if(menuItem.getItemId()==R.id.delete_images){
            return true;
        }
        return false;
    }

    private void toViewList(List<Image>images) {
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
                label += '.';
            for (int i = 0; i < images.size(); i++) {
                String labelCur = images.get(i).getDateTaken();
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }
    private void toViewListMonth() {
        if (images.size() > 0) {
            int beg = 3;
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label=label.substring(beg,label.length()-6);
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                String labelCur = images.get(i).getDateTaken();
                labelCur=labelCur.substring(beg,labelCur.length()-6);
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }
    private void setDownToUp(){
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label += '.';
            for (int i = images.size()-1; i >=0; i--) {
                String labelCur = images.get(i).getDateTaken();
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }
    private void setRecyclerViewLayoutManager(int newTypeView) {
        typeView = newTypeView;
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), typeView);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewList.get(position).type == RecyclerData.Type.Label ? typeView : 1;
            }
        });
        binding.recyclerDetailView.setLayoutManager(gridLayoutManager);
    }

}