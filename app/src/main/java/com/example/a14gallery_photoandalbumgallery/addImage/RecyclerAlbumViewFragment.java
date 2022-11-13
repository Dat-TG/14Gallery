package com.example.a14gallery_photoandalbumgallery.addImage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentRecyclerAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RecyclerAlbumViewFragment extends Fragment implements RecyclerAlbumViewAdapter.ItemClickListener {
    FragmentRecyclerAlbumViewBinding binding;
    List<Album> albumsCanAdd;
    Album albumIncluded;
    RecyclerAlbumViewAdapter recyclerAlbumViewAdapter;
    ImageFragmentAdapter imageFragmentAdapter;

    private ArrayList<RecyclerData> viewList = null;
    public static List<Image> selectedInAlbum = new ArrayList<>();
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;

    public RecyclerAlbumViewFragment(Album albumIncluded) {
        this.albumIncluded = albumIncluded;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentRecyclerAlbumViewBinding.inflate(inflater, container, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.albumFragmentRecycleView.setHasFixedSize(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(false);

        albumsCanAdd = AlbumGallery.getAlbumAddImage(getContext(), albumIncluded);
        recyclerAlbumViewAdapter = new RecyclerAlbumViewAdapter(getContext(), albumsCanAdd);
        recyclerAlbumViewAdapter.setClickListener(this);
        binding.btnBack.setVisibility(View.GONE);
        binding.albumFragmentRecycleView.setAdapter(recyclerAlbumViewAdapter);

        // fragment == ImageFragment -> handleClickItem
        onItemClick = (pos, view1) -> {
            if (!viewList.get(pos).imageData.isChecked()) {
                viewList.get(pos).imageData.setChecked(true);
                selectedInAlbum.add(viewList.get(pos).imageData);
            } else {
                viewList.get(pos).imageData.setChecked(false);
                selectedInAlbum.remove(viewList.get(pos).imageData);
            }
            imageFragmentAdapter.notifyItemChanged(pos);
        };

        onItemLongClick = (pos, view1) -> {
        };
        return binding.getRoot();
    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(true);

        binding.albumFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);
        getImageViewList(albumsCanAdd.get(position).getAlbumImages(), albumIncluded);
        imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);


        binding.btnBack.setVisibility(View.VISIBLE);
        binding.btnBack.setOnClickListener(view1 -> {
            RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getContext(), 3);
            binding.albumFragmentRecycleView.setHasFixedSize(true);
            binding.albumFragmentRecycleView.setLayoutManager(layoutManager1);
            binding.albumFragmentRecycleView.setNestedScrollingEnabled(false);
            recyclerAlbumViewAdapter = new RecyclerAlbumViewAdapter(getContext(), albumsCanAdd);
            binding.albumFragmentRecycleView.setAdapter(recyclerAlbumViewAdapter);
            binding.btnBack.setVisibility(View.GONE);
        });

        imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
        imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
        binding.albumFragmentRecycleView.setAdapter(imageFragmentAdapter);

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

    private void getImageViewList(List<Image> images, Album album) {
        List<String> imagesPath = new ArrayList<>();
        List<Image> temp = new ArrayList<>();
        List<Image> albumImages = album.getAlbumImages();
        int lengthAlbum = albumImages.size();
        for (int i = 0; i < lengthAlbum; i++)
            imagesPath.add(albumImages.get(i).getPath());
        if (images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                if (!imagesPath.contains(images.get(i).getPath())) {
                    temp.add(images.get(i));
                }
            }
        }
        toViewList(temp);
    }

    private void setRecyclerViewLayoutManager(int newTypeView) {
        typeView = newTypeView;
        gridLayoutManager = new GridLayoutManager(getContext(), typeView);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewList.get(position).type == RecyclerData.Type.Label ? typeView : 1;
            }
        });
        binding.albumFragmentRecycleView.setLayoutManager(gridLayoutManager);
    }


}