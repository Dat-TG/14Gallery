package com.example.a14gallery_photoandalbumgallery.addImage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;

import java.io.File;
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
        viewList.forEach(image -> image.imageData.setChecked(false));
        AddItemActivity.selectedAlbum = new ArrayList<>();

        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);

        onItemClick = (position, view1) -> {
            String selectedImageName = new File(viewList.get(position).imageData.getPath()).getName();
            if (!viewList.get(position).imageData.isChecked()) {
                viewList.get(position).imageData.setChecked(true);
                AddItemActivity.selectedAlbum.add(viewList.get(position).imageData);
                AddItemActivity.selectedAlbumName.add(selectedImageName);
            } else {
                viewList.get(position).imageData.setChecked(false);
                AddItemActivity.selectedAlbum.remove(viewList.get(position).imageData);
                AddItemActivity.selectedAlbumName.remove(selectedImageName);
            }
            imageFragmentAdapter.notifyItemChanged(position);
        };

        onItemLongClick = (position, view1) -> {};
        imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
        imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
        imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
        binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewList != null) {
            for (int i = 0; i < viewList.size(); i++) {
                String nameFile = new File(viewList.get(i).imageData.getPath()).getName();
                if (AddItemActivity.selectedAlbumName.contains(nameFile)) {
                    viewList.get(i).imageData.setChecked(true);
                    imageFragmentAdapter.notifyItemChanged(i);
                }
            }
        }
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
        List<String> imagesName = new ArrayList<>();
        List<Image> temp = new ArrayList<>();
        for (int i = 0; i < imagesIncluded.size(); i++) {
            File nameImages = new File(imagesIncluded.get(i).getPath());
            imagesName.add(nameImages.getName());
        }
        if (images.size() > 0) {
            for (int i = 0; i < images.size(); i++) {
                File file = new File(images.get(i).getPath());
                if (!imagesName.contains(file.getName())) {
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
