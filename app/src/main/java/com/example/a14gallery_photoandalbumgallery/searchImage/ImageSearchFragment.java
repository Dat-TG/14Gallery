package com.example.a14gallery_photoandalbumgallery.searchImage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageSearchBinding;
import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.image.ImageGallery;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;
import com.example.a14gallery_photoandalbumgallery.setting.SettingActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class ImageSearchFragment extends Fragment implements MenuProvider {
    FragmentImageSearchBinding binding;
    List<Image> images;
    public int typeView = 4;

    public static ImageFragmentAdapterSearch imageFragmentAdapterSearch;
    RecyclerView.LayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;
    Calendar calendar;
    SimpleDateFormat formatter;
    String dateText;
    FragmentActivity activity;
    String mode = "";
    private ArrayList<RecyclerData> viewListDate = new ArrayList<>();
    private ArrayList<RecyclerData> viewListHashtag = new ArrayList<>();

    public ImageSearchFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = requireActivity();
        binding = FragmentImageSearchBinding.inflate(inflater, container, false);
        images = ImageGallery.getInstance().getListOfImages(getContext());
        toViewList();
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.searchRecycleView.setHasFixedSize(true);
        binding.searchRecycleView.setNestedScrollingEnabled(true);
        binding.searchRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);

        setOnClick(activity, viewList);

        imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
        imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
        binding.searchRecycleView.setAdapter(imageFragmentAdapterSearch);

        // Set up Search Bar:

        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(activity,
                R.array.category_search_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.spinner1.setAdapter(adapter);
        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    mode = "hashtag";
                    if (viewListHashtag.size() == 0) {
                        viewListHashtag = getViewListHashtag("");
                    }
                    viewList = viewListHashtag;
                } else {
                    mode = "date";
                    if (viewListDate.size() == 0) {
                        viewListDate = getViewListDate();
                    }
                    viewList = viewListDate;
                }
                setOnClick(activity, viewList);
                imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
                imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
                binding.searchRecycleView.setAdapter(imageFragmentAdapterSearch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.top_bar_menu_add_image, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.src_setting) {
            Intent intent = new Intent(getContext(), SettingActivity.class);
            intent.putExtra("Fragment", 3);
            requireActivity().startActivity(intent);
            return true;
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();
        ImageGallery.getInstance().update(getActivity());
        images = ImageGallery.listOfImages(requireContext());
        if (!mode.equals("date")){
            {
                viewListHashtag = getViewListHashtag("");
                viewList = viewListHashtag;
            }
            setOnClick(activity, viewList);
            imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
            binding.searchRecycleView.setAdapter(imageFragmentAdapterSearch);
        }
    }

    private void setOnClick(FragmentActivity activity, ArrayList<RecyclerData> viewList) {
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
                intent.putExtra("position", position - 1);
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
    }

    private void filterList(String newText) {
        if (!newText.equals("")) {
            ArrayList<RecyclerData> filteredList = new ArrayList<>();
            if (mode.equals("date")) {
                for (RecyclerData recyclerData : viewList) {
                    File file = new File(recyclerData.imageData.getPath());
                    Date lastModDate = new Date(file.lastModified());
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(lastModDate.getTime());
                    formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                    dateText = formatter.format(calendar.getTime());
                    if (dateText.toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(recyclerData);
                    }
                }
                viewList = filteredList;
            } else {
                viewList = getViewListHashtag(newText);

            }


            if (viewList.isEmpty()) {
                binding.textNotImageFound.setVisibility(TextView.VISIBLE);
                binding.textNotImageFound.setText(R.string.no_matching_image);
            } else {
                binding.textNotImageFound.setVisibility(TextView.INVISIBLE);
            }

            setOnClick(activity, viewList);
            imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
            binding.searchRecycleView.setAdapter(imageFragmentAdapterSearch);
        } else {
            if (mode.equals("date")) {
                viewList = viewListDate;
            } else {
                viewList = viewListHashtag;
            }
            binding.textNotImageFound.setVisibility(TextView.INVISIBLE);
            setOnClick(activity, viewList);
            imageFragmentAdapterSearch = new ImageFragmentAdapterSearch(viewList, onItemClick, onItemLongClick);
            imageFragmentAdapterSearch.setState(ImageFragmentAdapterSearch.State.Normal);
            binding.searchRecycleView.setAdapter(imageFragmentAdapterSearch);
        }
    }


    private void toViewList() {
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            File file = new File(images.get(0).getPath());
            String label = getDateTaken(file, "dd/MM/yyyy");
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                File file2 = new File(images.get(i).getPath());
                String labelCur = getDateTaken(file2, "dd/MM/yyyy");
                labelCur += '.';
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }

    private ArrayList<RecyclerData> getViewListDate() {
        if (images.size() > 0) {
            ArrayList<RecyclerData> viewList = new ArrayList<>();
            File file = new File(images.get(0).getPath());
            String label = getDateTaken(file, "dd/MM/yyyy");
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                File file2 = new File(images.get(i).getPath());
                String labelCur = getDateTaken(file2, "dd/MM/yyyy");
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
            return viewList;
        }
        return null;
    }

    private ArrayList<String> getAllListHashtag() {
        ArrayList<String> listAllHashtag = new ArrayList<>();
        for (int k = 0; k < images.size(); k++) {
            List<String> listHashtag = images.get(k).getListHashtag(getContext());
            String label = "";
            if (listHashtag.size() > 0) {
                for (int i = 0; i < listHashtag.size(); i++) {
                    label = listHashtag.get(i);
                    if (!listAllHashtag.contains(label)) {
                        listAllHashtag.add(label);
                    }
                }
            }
        }
        return listAllHashtag;
    }

    private ArrayList<String> getListHashtagContains(ArrayList<String> listAllHashtag, String textSearch) {
        ArrayList<String> listHashtag = new ArrayList<>();
        for (int k = 0; k < listAllHashtag.size(); k++) {
            if (listAllHashtag.get(k).contains(textSearch)) {
                listHashtag.add(listAllHashtag.get(k));
            }
        }
        return listHashtag;
    }

    private ArrayList<RecyclerData> getViewListHashtag(String textSearch) {
        if (images.size() > 0) {
            ArrayList<RecyclerData> viewList = new ArrayList<>();
            ArrayList<String> AllListHashtag = getAllListHashtag();
            ArrayList<String> ListHashtagContains = getListHashtagContains(AllListHashtag, textSearch);
            int count = 0;
            int temp = 0;
            for (int k = 0; k < ListHashtagContains.size(); k++) {
                Log.d("ListHashtagContains.size() ", String.valueOf(ListHashtagContains.size()));
                Log.d("ListHashtagContains.size(), name ", ListHashtagContains.get(k));
                viewList.add(new RecyclerData(RecyclerData.Type.Label, " #" + ListHashtagContains.get(k), images.get(0), count));
                count++;
                for (int i = 0; i < images.size(); i++) {
                    List<String> listHashtag = images.get(i).getListHashtag(getContext());
                    count = count + k;
                    if (listHashtag.contains(ListHashtagContains.get(k))) {
                        viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), count));
                    }
                }
            }
            return viewList;
        }
        return null;
    }


//    private ArrayList<RecyclerData> getViewListNormal() {
//        if (images.size() > 0) {
//            viewList = new ArrayList<>();
//            for (int i = 0; i < images.size(); i++) {
//                viewList.add(new RecyclerData(RecyclerData.Type.Image, ".", images.get(i), i));
//            }
//            return viewList;
//        }
//        return null;
//    }


    private String getDateTaken(File file, String patternDate) {
        Date lastModDate = new Date(file.lastModified());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lastModDate.getTime());
        formatter = new SimpleDateFormat(patternDate, Locale.UK);
        dateText = formatter.format(calendar.getTime());
        return dateText;
    }


    public void setRecyclerViewLayoutManager(int newTypeView) {
        typeView = newTypeView;
        gridLayoutManager = new GridLayoutManager(getContext(), typeView);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return viewList.get(position).type == RecyclerData.Type.Label ? typeView : 1;
            }
        });
        binding.searchRecycleView.setLayoutManager(gridLayoutManager);
    }
}

