package com.example.a14gallery_photoandalbumgallery.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.DetailAlbumActivity;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class AlbumFragmentAdapter extends RecyclerView.Adapter<AlbumFragmentAdapter.AlbumFragmentViewHolder> {
    Context _context;
    List<Album> _album;
    Album album;

    public AlbumFragmentAdapter(Context context, List<Album> albums) {
        this._context = context;
        this._album = albums;
    }

    @NonNull
    @Override
    public AlbumFragmentAdapter.AlbumFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AlbumFragmentViewHolder(SingleAlbumViewBinding.inflate(inflater, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull AlbumFragmentAdapter.AlbumFragmentViewHolder holder, int position) {
        Album albumPos = _album.get(position);
        String albumTitle = albumPos.getName();
        List<Image> albumImages = albumPos.getAlbumImages();

        int albumsCount = albumImages.size();
        if (Objects.equals(albumPos.getAlbumCover(), "")) {
            if (albumsCount > 0) {
                Glide.with(_context)
                        .load(albumImages.get(albumImages.size() - 1).getPath())
                        .into(holder.binding.albumImg);
            } else {
                holder.binding.albumImg.setImageResource(R.drawable.album_empty);
            }
        } else {
            Glide.with(_context)
                    .load(albumPos.getAlbumCover())
                    .into(holder.binding.albumImg);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
        holder.binding.albumImg.setOnClickListener(view -> {
            Intent intent = new Intent(_context, DetailAlbumActivity.class);
            intent.putExtra("NAME", albumPos.getName());
            _context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return _album.size();
    }

    public static class AlbumFragmentViewHolder extends RecyclerView.ViewHolder {
        SingleAlbumViewBinding binding;

        public AlbumFragmentViewHolder(SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}