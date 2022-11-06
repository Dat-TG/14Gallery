package com.example.a14gallery_photoandalbumgallery.albumCover;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.View;
import android.widget.Toast;

import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.database.AppDatabase;
import com.example.a14gallery_photoandalbumgallery.database.albumCover.AlbumData;
import com.example.a14gallery_photoandalbumgallery.databinding.ActivityDetailAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.DetailAlbumActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


public class AlbumCoverActivity extends AppCompatActivity {
    ActivityDetailAlbumBinding binding;
    Toolbar toolbar;
    Album album;
    List<Image> images;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    private ImageFragmentAdapter imageFragmentAdapter = null;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;

    String nameFolder;

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

        nameFolder = getIntent().getStringExtra("NAME");
        album = AlbumGallery.getInstance().getAlbumByName(this, nameFolder);
        int size = album.getAlbumImages().size();

        binding.appBarDetail.setTitle("Chọn ảnh bìa cần đổi");
        if (size != 0) {
            images = album.getAlbumImages();
            toViewList(images);
            onItemClick = (position, view1) -> {
                if (imageFragmentAdapter.getState() != ImageFragmentAdapter.State.MultipleSelect) {
                    Intent intent = new Intent(this, DetailAlbumActivity.class);
                    intent.putExtra("NAME", album.getName());
                    AlbumData data = AppDatabase.getInstance(this).albumDataDao().getAlbumCover(album.getName());
                    AlbumData albumData = new AlbumData(album.getName(), viewList.get(position).imageData.getPath());

                    if (data != null)
                        AppDatabase.getInstance(this).albumDataDao().updateAlbum(albumData);
                    else
                        AppDatabase.getInstance(this).albumDataDao().insertAlbumCover(albumData);
                    AlbumGallery.getInstance().update(this);
                    Toast.makeText(this, "Đổi ảnh bìa thành công", Toast.LENGTH_SHORT).show();
                    this.startActivity(intent);
                    AlbumCoverActivity.this.finish();
                }
            };
            onItemLongClick = (position, view1) -> {};

            binding.recyclerDetailView.setHasFixedSize(true);
            binding.recyclerDetailView.setNestedScrollingEnabled(true);
            setRecyclerViewLayoutManager(4);
            imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            binding.recyclerDetailView.setAdapter(imageFragmentAdapter);
            binding.textNotFound.setVisibility(View.GONE);

        }
        setContentView(binding.getRoot());
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
}