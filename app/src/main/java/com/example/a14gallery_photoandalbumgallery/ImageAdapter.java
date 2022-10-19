package com.example.a14gallery_photoandalbumgallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Image> listImages;
    private Context context;

    public ImageAdapter(Context context) {
        this.context = context;
    }


    public ImageAdapter() {
    }


    public void setData(List<Image> listImages) {
        this.listImages = listImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image_view, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image_pos = listImages.get(position);
        if (image_pos == null) {
            return;
        }

        Glide.with(context)
                .load(image_pos.getPath())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        if (listImages != null)
            return listImages.size();
        return 0;
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }


}

