package com.example.a14gallery_photoandalbumgallery.searchAlbum;

import android.annotation.SuppressLint;
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
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

public class AlbumSearchFragment extends Fragment implements MenuProvider {

    List<Image> images;
    RecyclerView searchRecyclerView;
    GridLayoutManager gridLayoutManager;
    TextView textNotImageFound;
    private SearchView searchView;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    public static AlbumFragmentAdapterSearch albumFragmentAdapterSearch;
    Calendar calendar = null;
    SimpleDateFormat formatter;
    String dateText;
    public int spanCount=0;

    public AlbumSearchFragment() {

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
        searchRecyclerView.setNestedScrollingEnabled(false);

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

//        onItemClick = (position, view1) -> {
//            if (imageFragmentAdapterSearch.getState() == ImageFragmentAdapterSearch.State.MultipleSelect) {
//                if (!viewList.get(position).imageData.isChecked()) {
//                    viewList.get(position).imageData.setChecked(true);
//                    images.get(viewList.get(position).index).setChecked(true);
//                } else {
//                    viewList.get(position).imageData.setChecked(false);
//                    images.get(viewList.get(position).index).setChecked(false);
//                }
//                imageFragmentAdapterSearch.notifyItemChanged(position);
//            } else {
//                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
//                intent.putExtra("path", viewList.get(position).imageData.getPath());
//                getContext().startActivity(intent);
//            }
//        };

//        onItemLongClick = (position, view1) -> {
//            imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.MultipleSelect);
//            viewList.get(position).imageData.setChecked(true);
//            images.get(viewList.get(position).index).setChecked(true);
//            imageFragmentAdapterSearch.notifyItemRangeChanged(0, imageFragmentAdapterSearch.getItemCount());
//            activity.invalidateOptionsMenu();
//        };
        AlbumGallery.getInstance().update(getContext());
        List<Album>albums = AlbumGallery.getInstance().albums;
        albumFragmentAdapterSearch = new AlbumFragmentAdapterSearch(getContext(), albums);
        spanCount=albums.size();
        searchRecyclerView.setAdapter(albumFragmentAdapterSearch);

        return view;
    }

    public void setRecyclerViewLayoutManager() {
//        gridLayoutManager = new GridLayoutManager(getContext(), 1);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return viewList.get(position).type == RecyclerData.Type.Label ? 1 : 1;
//            }
//        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),1, GridLayoutManager.VERTICAL,false);
        searchRecyclerView.setLayoutManager(mLayoutManager);
    }


    private void filterList(String text) {
        List<Album>  filteredList = new ArrayList<>();
        AlbumGallery.getInstance().update(getContext());
        List<Album>albums = AlbumGallery.getInstance().albums;
        for(Album album:albums){
            String name=album.getName();
            if(name.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(album);
            }
        }
        if(filteredList.isEmpty()){
//            Toast.makeText(getContext(), "No matching image found", Toast.LENGTH_SHORT).show();
            textNotImageFound.setVisibility(TextView.VISIBLE);
            textNotImageFound.setText("Không tìm thấy album phù hợp");
        }
        else{
            textNotImageFound.setVisibility(TextView.INVISIBLE);
        }
        albumFragmentAdapterSearch.setFilteredList(filteredList);
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