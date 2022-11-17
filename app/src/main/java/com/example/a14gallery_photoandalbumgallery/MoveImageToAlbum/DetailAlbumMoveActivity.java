package com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumMoveBinding;
import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.addImage.AddItemActivity;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.albumCover.AlbumCoverActivity;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumMoveBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class DetailAlbumMoveActivity extends AppCompatActivity {
    ActivityDetailAlbumMoveBinding binding;
    Toolbar toolbar;
    Album album;
    List<Image> images;
    String nameFolder;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    private ImageFragmentAdapter imageFragmentAdapter = null;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;
    Button okButton;
    Button cancleButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailAlbumMoveBinding.inflate(getLayoutInflater());

        toolbar = binding.appBarDetail;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Button back
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        nameFolder = getIntent().getStringExtra("NAME");
        if (nameFolder.equals(AlbumGallery.favoriteAlbumFolderName) || nameFolder.equals(AlbumGallery.privateAlbumFolderName) || nameFolder.equals(AlbumGallery.recycleBinFolderName)) {
            Gson gson = new Gson();
            album = gson.fromJson(getIntent().getStringExtra("ALBUM"), Album.class);
        } else {
            AlbumGallery.getInstance().update(this);
            album = AlbumGallery.getInstance().getAlbumByName(this, nameFolder);
        }

        if (album==null) {
            album=new Album();
            album.setName("Pictures");
            List<Image>images=new ArrayList<>();
            album.setAlbumImages(images);
            album.setPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures");
        }
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
            imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
            binding.textNotFound.setVisibility(View.GONE);

        } else {
            if (album.getName().equals("Thùng rác")) {
                binding.textNotFound.setText(R.string.empty_recycle_bin);
            } else {
                binding.textNotFound.setText(R.string.no_image_found);
            }
            binding.recyclerDetailView.setVisibility(View.GONE);
        }
        okButton = binding.getRoot().findViewById(R.id.ok_button);
        cancleButton = binding.getRoot().findViewById(R.id.cancel_move_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("DESTPATH", album.getPath());
                Intent intent = new Intent();
                intent.putExtra("DEST", album.getPath());
                setResult(123, intent);
                finish();
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlbumGallery.getInstance().update(this);
        if (nameFolder.equals(AlbumGallery.favoriteAlbumFolderName) || nameFolder.equals(AlbumGallery.privateAlbumFolderName) || nameFolder.equals(AlbumGallery.recycleBinFolderName)) {
            Gson gson = new Gson();
            album = gson.fromJson(getIntent().getStringExtra("ALBUM"), Album.class);
        } else {
            AlbumGallery.getInstance().update(this);
            album = AlbumGallery.getInstance().getAlbumByName(this, nameFolder);
        }
        if (album==null) {
            album=new Album();
            album.setName("Pictures");
            List<Image>images=new ArrayList<>();
            album.setAlbumImages(images);
            album.setPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures");
        }
        if (album.getAlbumImages().size() != 0) {
            images = album.getAlbumImages();
            toViewList(images);
            imageFragmentAdapter.setData(viewList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        if (menuItem.getItemId() == android.R.id.home) { // Click back button
            finish();
            return true;
        }
        return false;
    }

    private void toViewList(List<Image> images) {
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