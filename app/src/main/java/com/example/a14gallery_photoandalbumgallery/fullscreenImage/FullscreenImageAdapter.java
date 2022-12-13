package com.example.a14gallery_photoandalbumgallery.fullscreenImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.ImagesHolderFullscreenBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.util.List;

public class FullscreenImageAdapter extends RecyclerView.Adapter<FullscreenImageAdapter.ViewHolder> {

    private final Context _context;
    private final List<Image> _images;

    public FullscreenImageAdapter(Context context, List<Image> images) {
        this._context = context;
        this._images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        return new ViewHolder(ImagesHolderFullscreenBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(_context)
                .load(_images.get(position).getPath())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.binding.images);
    }

    @Override
    public int getItemCount() {
        if (_images != null) {
            return  _images.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImagesHolderFullscreenBinding binding;

        public ViewHolder(@NonNull ImagesHolderFullscreenBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
