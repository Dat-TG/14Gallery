package com.example.a14gallery_photoandalbumgallery.addImage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.a14gallery_photoandalbumgallery.ClassifyDate;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

import java.util.List;

public class RecyclerImageViewFragment extends Fragment {
    FragmentImageBinding binding;
    LayoutManager layoutManager;
    List<ClassifyDate> classifyDateList;
    Album album;
    List<Image> imagesInAlbum;

    public RecyclerImageViewFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentImageBinding.inflate(inflater, container, false);

        imagesInAlbum = album.getAlbumImages();
        classifyDateList = ImageGallery.getListAddImage(ImageGallery.getInstance().images, imagesInAlbum);

        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);

        binding.imageFragmentRecycleView.setNestedScrollingEnabled(false);
//        binding.imageFragmentRecycleView.setAdapter(new ImageFragmentAdapter(getContext(), classifyDateList, 4));

        return binding.getRoot();
    }

}
