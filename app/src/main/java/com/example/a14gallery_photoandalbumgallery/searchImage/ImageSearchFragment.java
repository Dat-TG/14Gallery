package com.example.a14gallery_photoandalbumgallery.searchImage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.searchImage.ImageFragmentAdapterSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ImageSearchFragment extends Fragment implements MenuProvider {

    List<Image> images;
    RecyclerView searchRecyclerView;
    GridLayoutManager gridLayoutManager;
    RecyclerView.LayoutManager linearLayout;
    TextView textNotImageFound;
    private SearchView searchView;

    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    public static ImageFragmentAdapterSearch imageFragmentAdapterSearch;

    public ImageSearchFragment() {

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentActivity activity = requireActivity();
        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        View view = inflater.inflate(R.layout.fragment_image_search, container, false);
        searchRecyclerView = view.findViewById(R.id.searchRecycleView);
        searchView = view.findViewById(R.id.searchView);
        textNotImageFound = view.findViewById(R.id.textNotImageFound);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setNestedScrollingEnabled(true);

        // Set up Search Bar:
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        // Set up data and display
        images = ImageGallery.getInstance().getListOfImages(getContext());
        toViewList();
        setRecyclerViewLayoutManager();

        onItemClick = (position, view1) -> {
            if (imageFragmentAdapterSearch.getState() == ImageFragmentAdapterSearch.State.MultipleSelect) {
                if (!viewList.get(position).imageData.isChecked()) {
                    viewList.get(position).imageData.setChecked(true);
                    images.get(viewList.get(position).index).setChecked(true);
                } else {
                    viewList.get(position).imageData.setChecked(false);
                    images.get(viewList.get(position).index).setChecked(false);
                }
                imageFragmentAdapterSearch.notifyItemChanged(position);
            } else {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("path", viewList.get(position).imageData.getPath());
                getContext().startActivity(intent);
            }
        };

        onItemLongClick = (position, view1) -> {
            imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.MultipleSelect);
            viewList.get(position).imageData.setChecked(true);
            images.get(viewList.get(position).index).setChecked(true);
            imageFragmentAdapterSearch.notifyItemRangeChanged(0, imageFragmentAdapterSearch.getItemCount());
            activity.invalidateOptionsMenu();
        };

        imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
        imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
        searchRecyclerView.setAdapter(imageFragmentAdapterSearch);

        return view;
    }

    public void setRecyclerViewLayoutManager() {
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewList.get(position).type == RecyclerData.Type.Label ? 1 : 1;
            }
        });
        searchRecyclerView.setLayoutManager(gridLayoutManager);
    }


    private void filterList(String text) {
        List<RecyclerData> filteredList = new ArrayList<>();
        for (RecyclerData recyclerData : viewList) {
            if (recyclerData.imageData.getDateTaken().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(recyclerData);
            }
        }
        if (filteredList.isEmpty()) {
//            Toast.makeText(getContext(), "No matching image found", Toast.LENGTH_SHORT).show();
            textNotImageFound.setVisibility(TextView.VISIBLE);
            textNotImageFound.setText("Không tìm thấy ảnh phù hợp!");
        } else {
            textNotImageFound.setVisibility(TextView.INVISIBLE);
        }
        imageFragmentAdapterSearch.setFilteredList(filteredList);
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        if (!menu.hasVisibleItems()) {
            menuInflater.inflate(R.menu.top_bar_menu_search, menu);
        }
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.src_setting) {
            // TODO: Click setting
            return true;
        }
        return false;
    }

    private void toViewList() {
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }
}