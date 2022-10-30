package com.example.a14gallery_photoandalbumgallery.addImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;

import java.util.List;

public class RecyclerAlbumViewAdapter extends RecyclerView.Adapter<RecyclerAlbumViewAdapter.RecyclerAlbumViewHolder> {
    List<Album> albums;
    Context context;
    static ItemClickListener mClickListener;

    public RecyclerAlbumViewAdapter(Context context, List<Album> album) {
        this.context = context;
        this.albums = album;
    }

    @NonNull
    @Override
    public RecyclerAlbumViewAdapter.RecyclerAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RecyclerAlbumViewAdapter.RecyclerAlbumViewHolder(SingleAlbumViewBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAlbumViewAdapter.RecyclerAlbumViewHolder holder, int position) {
        String albumTitle = albums.get(position).getName();
        List<Image> albumImages = albums.get(position).getAlbumImages();

        int albumsCount = albumImages.size();
        if (albumsCount > 0) {
            Glide.with(context)
                    .load(albumImages.get(albumsCount - 1).getPath())
                    .into(holder.binding.albumImg);
        } else {
            holder.binding.albumImg.setImageResource(R.drawable.album_empty);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public static class RecyclerAlbumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SingleAlbumViewBinding binding;

        public RecyclerAlbumViewHolder(@NonNull SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }
    Album getItem(int id) {
        return albums.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

