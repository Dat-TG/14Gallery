package com.example.a14gallery_photoandalbumgallery.searchAlbum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.searchImage.ImageSearchFragment;
import com.google.android.material.tabs.TabLayout;

public class SearchFragment extends Fragment implements MenuProvider {
    FrameLayout simpleFrameLayout;
    TabLayout tabLayout;

    public SearchFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a new Tab named "First"

    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentActivity activity = requireActivity();
        View view = inflater.inflate(R.layout.fragment_album_search, container, false);
        simpleFrameLayout = view.findViewById(R.id.simpleFrameLayout);
        tabLayout =view.findViewById(R.id.tab_add_image_layout);
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("Ảnh"); // set the Text for the first Tab
// first tab
        tabLayout.addTab(firstTab); // add  the tab at in the TabLayout
// Create a new Tab named "Second"
        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Album"); // set the Text for the second Tab
        tabLayout.addTab(secondTab);
        Fragment fragment = new ImageSearchFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.simpleFrameLayout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment =null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new ImageSearchFragment();
                        break;
                    case 1:
                        fragment = new AlbumSearchFragment();
                        break;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.simpleFrameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
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

}