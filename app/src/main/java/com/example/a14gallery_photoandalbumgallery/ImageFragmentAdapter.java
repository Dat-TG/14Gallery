package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.databinding.SingleImageViewBinding;

public class ImageFragmentAdapter extends RecyclerView.Adapter<ImageFragmentAdapter.ImageFragmentViewHolder> {
    Context _context;
    Integer[] _images;

    public ImageFragmentAdapter(Context context, Integer[] images) {
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
        holder.binding.image.setImageResource(_images[position]);

        /*Implement onClickListener on this method*/
    }

    @Override
    public int getItemCount() {
        Log.i("LOG_TAG", String.valueOf(_images.length));
        return _images.length;
    }
}
