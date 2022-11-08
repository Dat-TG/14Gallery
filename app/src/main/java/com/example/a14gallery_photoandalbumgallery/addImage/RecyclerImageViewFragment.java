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
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RecyclerImageViewFragment extends Fragment {
    FragmentImageBinding binding;
    LayoutManager layoutManager;
    Album album;
    List<Image> imagesInAlbum;

    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    private ImageFragmentAdapter imageFragmentAdapter;
    public int typeView = 4;
    GridLayoutManager gridLayoutManager;

    public RecyclerImageViewFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentImageBinding.inflate(inflater, container, false);
        imagesInAlbum = album.getAlbumImages();
        toViewListAdd(ImageGallery.getInstance().images, imagesInAlbum);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);

        onItemClick = (position, view1) -> {
            if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                if (!viewList.get(position).imageData.isChecked()) {
                    viewList.get(position).imageData.setChecked(true);
                    //imagesInAlbum.get(viewList.get(position).index).setChecked(true);
                } else {
                    viewList.get(position).imageData.setChecked(false);
                    //imagesInAlbum.get(viewList.get(position).index).setChecked(false);
                }
                imageFragmentAdapter.notifyItemChanged(position);
            } else {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("path", viewList.get(position).imageData.getPath());
                getContext().startActivity(intent);
            }
        };

        onItemLongClick = (position, view1) -> {
            //imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
            viewList.get(position).imageData.setChecked(true);
            //imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            getActivity().invalidateOptionsMenu();
        };

        imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
        imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
        imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
        binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);

        return binding.getRoot();
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

    private void toViewListAdd(List<Image> images, List<Image> imagesIncluded) {
        List<String> imagesPath = new ArrayList<>();
        List<Image> temp = new ArrayList<>();
        for (int i = 0; i < imagesIncluded.size(); i++)
            imagesPath.add(imagesIncluded.get(i).getPath());
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
        binding.imageFragmentRecycleView.setLayoutManager(gridLayoutManager);
    }
}
