package com.example.a14gallery_photoandalbumgallery.addImage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.ClassifyDate;
import com.example.a14gallery_photoandalbumgallery.FullscreenImageActivity;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.ImageAdapter;
import com.example.a14gallery_photoandalbumgallery.databinding.ItemClassifyDateBinding;

import java.util.List;

public class RecyclerImageViewAdapter extends RecyclerView.Adapter<RecyclerImageViewAdapter.RecyclerImageViewHolder> {
    private final Context _context;
    private List<ClassifyDate> _listClassifyDate;
    private int typeView;
    public ImageAdapter imageAdapter;

    public RecyclerImageViewAdapter(Context context, List<ClassifyDate> listClassifyDate, int typeView) {
        this._context = context;
        _listClassifyDate = listClassifyDate;
        this.typeView = typeView;
    }

    public void setData(List<ClassifyDate> listClassifyDate) {
        this._listClassifyDate = listClassifyDate;
        notifyDataSetChanged();
    }


    public static class RecyclerImageViewHolder extends RecyclerView.ViewHolder {
        ItemClassifyDateBinding binding;

        public RecyclerImageViewHolder(ItemClassifyDateBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull
    @Override
    public RecyclerImageViewAdapter.RecyclerImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RecyclerImageViewHolder(ItemClassifyDateBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerImageViewAdapter.RecyclerImageViewHolder holder, int position) {
        ClassifyDate classifyDate = _listClassifyDate.get(position);
        List<Image> listImage = _listClassifyDate.get(position).getListImage();

        holder.binding.txtNameClassifyDate.setText(classifyDate.getNameClassifyDate());
        holder.binding.rcvImages.setLayoutManager(new GridLayoutManager(_context, typeView));
        ImageAdapter imageAdapter = new ImageAdapter(classifyDate.getListImage(),
                image -> {

                },
                image -> {
                    return false;
                }
        );
        imageAdapter.setACTION_MODE(0);
        imageAdapter.setData(classifyDate.getListImage());
        imageAdapter.setOnLongClickListener(image -> {
            imageAdapter.setACTION_MODE(1);
            imageAdapter.notifyDataSetChanged();
            return false;
        });
        imageAdapter.setOnClickListener(image -> {
            if (imageAdapter.getACTION_MODE() == 1) {
                if (!listImage.get(holder.getAdapterPosition()).isChecked()) {
                    listImage.get(holder.getAdapterPosition()).setChecked(true);
                } else {
                    listImage.get(holder.getAdapterPosition()).setChecked(false);
                }
                imageAdapter.notifyDataSetChanged();
            } else {
                Intent intent = new Intent(_context, FullscreenImageActivity.class);
                intent.putExtra("path", image.getPath());
                _context.startActivity(intent);
            }
        });

        holder.binding.rcvImages.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        if (_listClassifyDate != null) {
            return _listClassifyDate.size();
        }
        return 0;
    }
}
