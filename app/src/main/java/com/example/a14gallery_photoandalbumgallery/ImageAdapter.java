package com.example.a14gallery_photoandalbumgallery;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleImageViewBinding;

import java.util.List;

public class  ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Image image);
    }

    private List<Image> _listImages;
    private final OnItemClickListener _listener;

    public ImageAdapter(List<Image> listImage, OnItemClickListener listener) {
        _listImages = listImage;
        _listener = listener;
    }

    public void setData(List<Image> listImages) {
        _listImages = listImages;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        SingleImageViewBinding binding;

        public ImageViewHolder(SingleImageViewBinding b) {
            super(b.getRoot());
            binding = b;
        }

        public void bind(final Image image, final OnItemClickListener listener) {
            Glide.with(binding.getRoot().getContext())
                    .load(image.getPath())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.image);
            binding.image.setOnClickListener(view -> listener.onItemClick(image));
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageViewHolder(SingleImageViewBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = _listImages.get(position);
        if (image == null) {
            return;
        }

        holder.bind(image, _listener);
    }

    @Override
    public int getItemCount() {
        if (_listImages != null)
            return _listImages.size();
        return 0;
    }
}

