package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleImageViewBinding;

import java.util.List;

public class ImageFragmentAdapter extends RecyclerView.Adapter<ImageFragmentAdapter.ImageFragmentViewHolder> {
    Context _context;
    List<Image> _images;


    public ImageFragmentAdapter(Context context, List<Image> images) {
        _context = context;
        _images = images;
    }

    public static class ImageFragmentViewHolder extends RecyclerView.ViewHolder {
        SingleImageViewBinding binding;
        public ImageFragmentViewHolder(SingleImageViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull
    @Override
    public ImageFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageFragmentViewHolder(SingleImageViewBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFragmentViewHolder holder, int position) {
        Image image = _images.get(position);
        Glide.with(_context)
                .load(image.getPath())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.binding.image);
    }

    @Override
    public int getItemCount() {
        return _images.size();
    }
}