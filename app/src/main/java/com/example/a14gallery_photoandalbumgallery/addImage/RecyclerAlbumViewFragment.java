package com.example.a14gallery_photoandalbumgallery.addImage;

import android.content.Intent;
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

import com.example.a14gallery_photoandalbumgallery.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentRecyclerAlbumViewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RecyclerAlbumViewFragment extends Fragment implements RecyclerAlbumViewAdapter.ItemClickListener {
    FragmentRecyclerAlbumViewBinding binding;
    List<Album> albumsCanAdd;
    Album albumIncluded, albumClickDetail;
    RecyclerAlbumViewAdapter recyclerAlbumViewAdapter;
    RecyclerImageViewAdapter imageFragmentAdapter;
    private int typeView=4;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    GridLayoutManager gridLayoutManager;

    public RecyclerAlbumViewFragment(Album albumIncluded, Album albumClickDetail) {
        this.albumIncluded = albumIncluded;
        this.albumClickDetail = albumClickDetail;
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

        onItemClick = (position, view1) -> {
            if (imageFragmentAdapter.getState() == RecyclerImageViewAdapter.State.MultipleSelect) {
                if (!viewList.get(position).imageData.isChecked()) {
                    viewList.get(position).imageData.setChecked(true);
                    recyclerAlbumViewAdapter.getItem(position).getAlbumImages().get(viewList.get(position).index).setChecked(true);
                } else {
                    viewList.get(position).imageData.setChecked(false);
                    recyclerAlbumViewAdapter.getItem(position).getAlbumImages().get(viewList.get(position).index).setChecked(false);
                }
                imageFragmentAdapter.notifyItemChanged(position);
            } else {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("path", viewList.get(position).imageData.getPath());
                getContext().startActivity(intent);
            }
        };

        onItemLongClick = (position, view1) -> {
            imageFragmentAdapter.setState(RecyclerImageViewAdapter.State.MultipleSelect);
            viewList.get(position).imageData.setChecked(true);
            recyclerAlbumViewAdapter.getItem(position).getAlbumImages().get(viewList.get(position).index).setChecked(true);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            getActivity().invalidateOptionsMenu();
        };

        return binding.getRoot();
    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);

        toViewList(recyclerAlbumViewAdapter.getItem(position).getAlbumImages());

        binding.albumFragmentRecycleView.setHasFixedSize(true);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);

        imageFragmentAdapter = new RecyclerImageViewAdapter(viewList,onItemClick,onItemLongClick);
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
        binding.albumFragmentRecycleView.setAdapter(imageFragmentAdapter);
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
