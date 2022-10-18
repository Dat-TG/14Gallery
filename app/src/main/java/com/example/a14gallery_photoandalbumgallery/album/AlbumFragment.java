package com.example.a14gallery_photoandalbumgallery.album;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a14gallery_photoandalbumgallery.BuildConfig;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentAlbumBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.RecyclerViewInterface;

import java.io.File;
import java.util.Vector;

public class AlbumFragment extends Fragment implements RecyclerViewInterface {
    private static final int APP_STORAGE_ACCESS_REQUEST_CODE = 501;
    FragmentAlbumBinding binding;
    Pair<Vector<Album>, Vector<String>> album;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.top_bar_menu_album, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        binding.albumFragmentRecycleView.setHasFixedSize(true);
        binding.albumFragmentRecycleView.setLayoutManager(layoutManager);
        binding.albumFragmentRecycleView.setNestedScrollingEnabled(false);
        album = AlbumGallery.getPhoneAlbums(requireContext());
        binding.albumFragmentRecycleView.setAdapter(new AlbumFragmentAdapter(getContext(), album));
        return binding.getRoot();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.alb_add:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!Environment.isExternalStorageManager()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setTitle("PERMISSION NEEDED");
                        alert.setMessage("This app need mange your storage to be able to create album folder");

// Set an EditText view to get user input

                        alert.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE);
                            }
                        });

                        alert.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                        alert.setTitle("Tạo album mới");
                        alert.setMessage("Tên album");

// Set an EditText view to get user input
                        final EditText input = new EditText(getContext());
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();

                                // Do something with value!
                                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/14Gallery/" + value);
                                Log.e("DIR", Environment.getExternalStorageDirectory().toString());
                                if (!file.exists()) {
                                    Boolean success = file.mkdirs();
                                    if (success) {
                                        Log.e("RES", "Success");
                                    } else {
                                        Log.e("RES", "Failed");
                                    }

                                    Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(getActivity(), "Folder Already Exists", Toast.LENGTH_SHORT).show();

                                }
                                //This is where you would put your make directory code

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                        alert.show();
                    }
                }
                return true;
            case R.id.alb_camera:
                // Click camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(intent);
                return true;
            case R.id.alb_choose:
                // Click choose(Lựa chọn)
                return true;
            case R.id.alb_grid_col_2:
                // Click grid_col_2
                return true;
            case R.id.alb_grid_col_3:
                // Click grid_col_3
                return true;
            case R.id.alb_grid_col_4:
                // Click grid_col_4
                return true;
            case R.id.alb_grid_col_5:
                // Click cgrid_col_5
                return true;
            case R.id.alb_view_mode_normal:
                // Click Lên rồi xuống
                return true;
            case R.id.alb_view_mode_convert:
                // Click Đảo ngược
                return true;
            case R.id.alb_view_mode_day:
                // Click Xếp theo ngày
                return true;
            case R.id.alb_view_mode_month:
                // Click Xếp theo tháng
                return true;
            case R.id.alb_setting:
                // Click Setting
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int pos) {
        Toast.makeText(getContext(), pos, Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(getContext(), DetailAlbumFragment.class);
//        intent.putExtra("ALBUM_CHILDREN", album.first.get(pos).getAlbumChildren());
//        intent.putExtra("ALBUM_PHOTOS", album.first.get(pos).getAlbumPhotos());
//        startActivity(intent);
    }

}