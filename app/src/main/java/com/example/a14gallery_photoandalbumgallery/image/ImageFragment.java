package com.example.a14gallery_photoandalbumgallery.image;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.GIF.AnimatedGIFWriter;
import com.example.a14gallery_photoandalbumgallery.MainActivity;
import com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.album.AlbumGallery;
import com.example.a14gallery_photoandalbumgallery.fullscreenImage.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.FragmentImageBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;



public class ImageFragment extends Fragment implements MenuProvider {
    FragmentImageBinding binding;

    List<Image> images;
    List<ClassifyDate> classifyDateList;
    public int typeView = 4;

    public static ImageFragmentAdapter imageFragmentAdapter;
    RecyclerView.LayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;
    boolean upToDown = true;
    boolean sortByDate = true;

    private Uri imageUri;
    ActivityResultLauncher<String> requestPermissionLauncher;
    private ArrayList<RecyclerData> viewList = null;
    BiConsumer<Integer, View> onItemClick;
    BiConsumer<Integer, View> onItemLongClick;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityMoveLauncher;

    String nameGIF="animation";
    int delay=500;


    public ImageFragment() {

    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FragmentActivity activity = requireActivity();
        binding = FragmentImageBinding.inflate(inflater, container, false);
        images = ImageGallery.getInstance().getListOfImages(getContext());
        toViewList();
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        binding.imageFragmentRecycleView.setHasFixedSize(true);
        binding.imageFragmentRecycleView.setNestedScrollingEnabled(true);
        binding.imageFragmentRecycleView.setLayoutManager(layoutManager);
        setRecyclerViewLayoutManager(4);

        onItemClick = (position, view1) -> {
            if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
                if (!viewList.get(position).imageData.isChecked()) {
                    viewList.get(position).imageData.setChecked(true);
                    images.get(viewList.get(position).index).setChecked(true);
                } else {
                    viewList.get(position).imageData.setChecked(false);
                    images.get(viewList.get(position).index).setChecked(false);
                }
                imageFragmentAdapter.notifyItemChanged(position);
            } else {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("path", viewList.get(position).imageData.getPath());
                requireContext().startActivity(intent);
            }
        };

        onItemLongClick = (position, view1) -> {
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
            viewList.get(position).imageData.setChecked(true);
            images.get(viewList.get(position).index).setChecked(true);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            activity.invalidateOptionsMenu();
        };

        imageFragmentAdapter = new ImageFragmentAdapter(viewList, onItemClick, onItemLongClick);
        imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
        binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);

        // Menu
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // permission camera
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        imageUri = getActivity().getApplicationContext().getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        activityResultLauncher.launch(intent);
                    } else {
                        Toast.makeText(getContext(), "There is no app that support this action", Toast.LENGTH_SHORT).show();
                    }
                });
        //
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    LoadAsyncTask loadAsyncTask = new LoadAsyncTask();
                    loadAsyncTask.execute();
                }
            }
        });
        activityMoveLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.e("result code image frag", Integer.toString(result.getResultCode()));
                        if (result.getResultCode() == 123) {
                            Intent data = result.getData();
                            String dest = data.getStringExtra("DEST");
                            Log.e("ImageFragment",dest);
                            moveToAlbum(dest);
                        }
                        imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                        imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                    }
                });
        return binding.getRoot();
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menu.clear();
        menuInflater.inflate(R.menu.top_bar_menu_image, menu);
        if (imageFragmentAdapter.getState() == ImageFragmentAdapter.State.MultipleSelect) {
            menu.getItem(0).setVisible(true);
            menu.getItem(6).setVisible(false);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);
            menu.getItem(5).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(6).setVisible(true);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ImageGallery.getInstance().update(getActivity());
        images = ImageGallery.listOfImages(requireContext());
        toViewList();
        if (this.images.size() > 0) {
            binding.textView.setVisibility(View.GONE);
            binding.imageFragmentRecycleView.setVisibility(View.VISIBLE);
        } else {
            binding.textView.setVisibility(View.VISIBLE);
            binding.imageFragmentRecycleView.setVisibility(View.GONE);
        }
        imageFragmentAdapter.setData(viewList);
    }


    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        Activity activity = requireActivity();
        if (menuItem.getItemId() == R.id.img_camera) {
            // Click camera
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_choose) {
            images.forEach(imageData -> imageData.setChecked(true));
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.MultipleSelect);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            activity.invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.clear_choose) {
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            activity.invalidateOptionsMenu();
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_2) {
            setRecyclerViewLayoutManager(2);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_3) {
            setRecyclerViewLayoutManager(3);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_4) {
            setRecyclerViewLayoutManager(4);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_grid_col_5) {
            setRecyclerViewLayoutManager(5);
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_normal) {
            // Click { sort UP-TO-DOWN
            if (!upToDown) {
                toViewList();
                imageFragmentAdapter.setData(viewList);
                binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
                upToDown = true;
            } else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_convert) {
            // Click { sort DOWN-TO-UP
            if (upToDown) {
                setDownToUp();
                imageFragmentAdapter.setData(viewList);
                binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
                upToDown = false;
            } else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_day) {
            // Click Sort by day
            if (!sortByDate) {
                toViewList();
                imageFragmentAdapter.setData(viewList);
                binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
                sortByDate = true;
            } else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_view_mode_month) {
            // Click Sort by month
            if (sortByDate) {
                toViewListMonth();
                imageFragmentAdapter.setData(viewList);
                binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
                sortByDate = false;
            } else {
                Toast.makeText(getContext(), "view has been set", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (menuItem.getItemId() == R.id.img_setting) {
            // Click Setting
            return true;
        }
        if (menuItem.getItemId() == R.id.delete_images) {
            moveToAlbum(Environment.getExternalStorageDirectory().getAbsolutePath()+"/14Gallery/RecycleBin");
            toViewList();
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            imageFragmentAdapter.setData(viewList);
            binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
            onResume();
            activity.invalidateOptionsMenu();
        }
        if (menuItem.getItemId() == R.id.move_images) {
            //Show album to choose
            Intent intent=new Intent(getActivity(), ChooseAlbumActivity.class);
            activityMoveLauncher.launch(intent);
            toViewList();
            imageFragmentAdapter.setData(viewList);
            binding.imageFragmentRecycleView.setAdapter(imageFragmentAdapter);
            activity.invalidateOptionsMenu();
        }
        if (menuItem.getItemId()==R.id.create_GIF) {
            inputGIF();
        }
        return false;
    }

    public class LoadAsyncTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            ImageGallery.getInstance().update(getActivity());
            images = ImageGallery.listOfImages(requireContext());
            if (sortByDate) {
                toViewList();
            } else {
                toViewListMonth();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            imageFragmentAdapter.setData(viewList);
        }
    }

    private void toViewListMonth() {
        if (images.size() > 0) {
            int beg = 3;
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label = label.substring(beg, label.length() - 6);
            label += '.';
            for (int i = 0; i < images.size(); i++) {
                String labelCur = images.get(i).getDateTaken();
                labelCur = labelCur.substring(beg, labelCur.length() - 6);
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
    }

    private void toViewList() {
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

    private void setDownToUp() {
        if (images.size() > 0) {
            viewList = new ArrayList<>();
            String label = images.get(0).getDateTaken();
            label += '.';
            for (int i = images.size() - 1; i >= 0; i--) {
                String labelCur = images.get(i).getDateTaken();
                if (!labelCur.equals(label)) {
                    label = labelCur;
                    viewList.add(new RecyclerData(RecyclerData.Type.Label, label, images.get(i), i));
                }
                viewList.add(new RecyclerData(RecyclerData.Type.Image, "", images.get(i), i));
            }
        }
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
        binding.imageFragmentRecycleView.setLayoutManager(gridLayoutManager);
    }

    private void addToTrash() {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> path = new ArrayList<String>();
        if (selectedImages.size() > 0) {
            for (Image image : selectedImages) {
                path.add(image.getPath());
            }
            androidx.appcompat.app.AlertDialog.Builder confirmDialog =
                    new androidx.appcompat.app.AlertDialog.Builder(getContext(), R.style.AlertDialog);
            confirmDialog.setMessage("Bạn có chắc chắn muốn xóa những hình ảnh này?");
            confirmDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    for (int index = 0; index < path.size(); index++) {
                        File a = new File(path.get(index));
                        a.delete();
                        callScanIntent(getContext(), path.get(index));
                    }
                    Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                    onResume();
                }
            });
            confirmDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            confirmDialog.create();
            confirmDialog.show();
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn hình ảnh để xóa", Toast.LENGTH_SHORT).show();
        }
    }

    public void callScanIntent(Context context, String path) {
        MediaScannerConnection.scanFile(context,
                new String[]{path}, null, null);
    }

    private void moveToAlbum(String dest) {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        for (Image image : selectedImages) {
            Log.e("src",image.getPath());
            Path result = null;
            String src = image.getPath();
            String name[] = src.split("/");
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result = Files.move(Paths.get(src), Paths.get(dest + "/" + name[name.length - 1]), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Di chuyển ảnh không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (result != null) {
                //Toast.makeText(getActivity().getApplicationContext(), "Đã di chuyển ảnh thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Di chuyển ảnh không thành công", Toast.LENGTH_SHORT).show();
            }
        }
        String name[]=dest.split("/");
        if (Objects.equals(name[name.length - 1], "RecycleBin")) {
            Snackbar.make(requireView(), "Xóa ảnh thành công", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(requireView(), "Di chuyển ảnh thành công", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void createGIF(String dest, int delay) {
        ArrayList<Image> selectedImages = images.stream()
                .filter(Image::isChecked)
                .collect(Collectors.toCollection(ArrayList::new));
        AnimatedGIFWriter writer = new AnimatedGIFWriter(true);
        writer.setDelay(delay);
        OutputStream os;
        try {
            os = new FileOutputStream(dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // Use -1 for both logical screen width and height to use the first frame dimension
        try {
            writer.prepareForWrite(os, -1, -1);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
        for (Image image : selectedImages) {
            Bitmap bitmap = BitmapFactory.decodeFile(image.getPath()); // Grab the Bitmap whatever way you can
            try {
                writer.writeFrame(os, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                return;
            }
// Keep adding frame here
        }
        try {
            writer.finishWrite(os);
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
            return;
        }
// And you are done!!!
        Snackbar.make(requireView(), "Tạo ảnh GIF thành công", Snackbar.LENGTH_SHORT).show();
    }
    public void inputGIF() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Tạo ảnh GIF");
        LinearLayout layout=new LinearLayout(getContext());
        final TextView textView1=new TextView(getContext());
        final EditText input1 = new EditText(getContext()); // Set an EditText view to get user input
        final TextView textView2=new TextView(getContext());
        final EditText input2 = new EditText(getContext());
        textView1.setText("Nhập tên ảnh (không cần .gif)");
        textView2.setText("Nhập thời gian delay giữa các frame (ms)");
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView1);
        layout.addView(input1);
        layout.addView(textView2);
        layout.addView(input2);
        layout.setPadding(50,50,50,0);
        alert.setView(layout);
        String dest=Environment.getExternalStorageDirectory().getAbsolutePath() + "/14Gallery/GIF/";
        File file = new File(dest);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (success) {
                Log.e("RES", "Success");
            } else {
                Log.e("RES", "Failed");
            }
        }
        alert.setPositiveButton("Ok", (dialog, whichButton) -> {
            nameGIF=input1.getText().toString();
            if (nameGIF.isEmpty()) {
                Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                toViewList();
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                requireActivity().invalidateOptionsMenu();
                return;
            }
            try {delay=Integer.parseInt(input2.getText().toString());}
            catch(Exception e){
                Snackbar.make(requireView(), "Tạo ảnh GIF không thành công", Snackbar.LENGTH_SHORT).show();
                toViewList();
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                requireActivity().invalidateOptionsMenu();
                return;
            }
            File anh=new File(dest+nameGIF+".gif");
            if (anh.exists()) {
                AlertDialog.Builder confirm = new AlertDialog.Builder(getContext());
                confirm.setTitle("Đợi một chút");
                confirm.setCancelable(true);
                confirm.setMessage("File "+nameGIF+".gif đã tồn tại. Bạn có muốn ghi đè không?")
                        .setPositiveButton("Có", (dialog1, id) -> {
                            try {
                                createGIF(dest+nameGIF+".gif",delay);
                                toViewList();
                                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                                requireActivity().invalidateOptionsMenu();
                                onResume();
                            } catch (Exception e) {
                                //Exception
                            }
                        })
                .setNegativeButton("Không", (dialog12, id) -> {
                    dialog12.cancel();
                    toViewList();
                    imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                    imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                    requireActivity().invalidateOptionsMenu();
                })
                .show();
            }
            else {
                createGIF(dest + nameGIF + ".gif", delay);
                toViewList();
                imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
                imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
                requireActivity().invalidateOptionsMenu();
                onResume();
            }
        });
        alert.setNegativeButton("Hủy", (dialog, whichButton) -> {toViewList();
            imageFragmentAdapter.setState(ImageFragmentAdapter.State.Normal);
            imageFragmentAdapter.notifyItemRangeChanged(0, imageFragmentAdapter.getItemCount());
            requireActivity().invalidateOptionsMenu();
            });
        alert.show();
    }
}