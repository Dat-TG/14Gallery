package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.ItemClassifyDateBinding;

import java.util.List;

public class ImageFragmentAdapter extends RecyclerView.Adapter<ImageFragmentAdapter.ImageFragmentViewHolder> {
    private final Context _context;
    private List<ClassifyDate> _listClassifyDate;
    private int typeView;
    public ImageAdapter imageAdapter;


    public ImageFragmentAdapter(Context context, List<ClassifyDate> listClassifyDate,int typeView) {
        _context = context;
        _listClassifyDate = listClassifyDate;
        this.typeView=typeView;
    }

    public void setData(List<ClassifyDate> listClassifyDate){
        this._listClassifyDate = listClassifyDate;
        notifyDataSetChanged();
    }

    public static class ImageFragmentViewHolder extends RecyclerView.ViewHolder {
        ItemClassifyDateBinding binding;
        public ImageFragmentViewHolder(ItemClassifyDateBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull
    @Override
    public ImageFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageFragmentViewHolder(ItemClassifyDateBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFragmentViewHolder holder, int position) {
        ClassifyDate classifyDate = _listClassifyDate.get(position);
        List<Image> listImage=_listClassifyDate.get(position).getListImage();

        holder.binding.txtNameClassifyDate.setText(classifyDate.getNameClassifyDate());
        holder.binding.rcvImages.setLayoutManager(new GridLayoutManager(_context, typeView));
        ImageAdapter imageAdapter = new ImageAdapter(classifyDate.getListImage(),
                image -> {

                },
                image-> {
                    return false;
                }
        );
        imageAdapter.setACTION_MODE(0);
        imageAdapter.setData(classifyDate.getListImage());
        imageAdapter.set_listeners(image->{
            imageAdapter.setACTION_MODE(1);
            imageAdapter.notifyDataSetChanged();
            return false;
        });
        imageAdapter.set_listener(image -> {
            if (imageAdapter.getACTION_MODE() == 1) {
                if (!listImage.get(holder.getAdapterPosition()).isChecked()) {
                    listImage.get(holder.getAdapterPosition()).setChecked(true);
                }
                else {
                    listImage.get(holder.getAdapterPosition()).setChecked(false);
                }
                imageAdapter.notifyDataSetChanged();
            }
            else{
                Intent intent = new Intent(_context, FullscreenImageActivity.class);
                intent.putExtra("path",image.getPath());
                _context.startActivity(intent);
            }
        });

        holder.binding.rcvImages.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        if (_listClassifyDate != null){
            return _listClassifyDate.size();
        }
        return 0;
    }
}