package com.example.a14gallery_photoandalbumgallery.CombineAlbum;

import static com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity.activityMoveLauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.DetailAlbumMoveActivity;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombineAlbumAdapter extends RecyclerView.Adapter<CombineAlbumAdapter.CombineAlbumAdapterViewHolder>{
    Context _context;
    static List<Album> _album;

    public CombineAlbumAdapter(Context context, List<Album> albums) {
        this._context = context;
        this._album = albums;
    }

    @NonNull
    @Override
    public CombineAlbumAdapter.CombineAlbumAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new CombineAlbumAdapter.CombineAlbumAdapterViewHolder(SingleAlbumViewBinding.inflate(inflater, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull CombineAlbumAdapter.CombineAlbumAdapterViewHolder holder, int position) {
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
        holder.binding.albumLayout.setAlpha(albumPos.isSelected() ? 0.5f: 1);
        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
        holder.binding.albumLayout.setOnClickListener(view -> {
            albumPos.setSelected(!albumPos.isSelected());
            holder.binding.albumLayout.setAlpha(albumPos.isSelected() ? 0.5f : 1);
        });
    }

    @Override
    public int getItemCount() {
        return _album.size();
    }

    public static class CombineAlbumAdapterViewHolder extends RecyclerView.ViewHolder {
        SingleAlbumViewBinding binding;

        public CombineAlbumAdapterViewHolder(SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    public static List<Album> getSelectedAlbum() {
        List<Album>l=new ArrayList<>();
        for (int i=0;i<_album.size();i++) {
            if (_album.get(i).isSelected()) {
                l.add(_album.get(i));
            }
        }
        return l;
    }
}