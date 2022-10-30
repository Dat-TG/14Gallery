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

import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentRecyclerAlbumViewBinding;

import java.util.List;

public class RecyclerAlbumViewFragment extends Fragment implements RecyclerAlbumViewAdapter.ItemClickListener {
    FragmentRecyclerAlbumViewBinding binding;
    List<Album> albumsCanAdd;
    Album albumIncluded, albumClickDetail;
    RecyclerAlbumViewAdapter recyclerAlbumViewAdapter;
    RecyclerImageViewAdapter imageFragmentAdapter;

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

        albumsCanAdd = AlbumGallery.getAlbumAddImage(getContext(), albumIncluded.getAlbumImages());
        recyclerAlbumViewAdapter = new RecyclerAlbumViewAdapter(getContext(), albumsCanAdd);
        recyclerAlbumViewAdapter.setClickListener(this);
        binding.btnBack.setVisibility(View.GONE);
        binding.albumFragmentRecycleView.setAdapter(recyclerAlbumViewAdapter);

        return binding.getRoot();
    }

    @Override
    public void onItemClick(View view, int position) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);
        imageFragmentAdapter = new RecyclerImageViewAdapter(getContext(), ImageGallery.getListClassifyDate(recyclerAlbumViewAdapter.getItem(position).getAlbumImages()), 4);
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

}
