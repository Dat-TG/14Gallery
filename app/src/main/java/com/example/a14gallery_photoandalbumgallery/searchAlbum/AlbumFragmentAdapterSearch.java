package com.example.a14gallery_photoandalbumgallery.searchAlbum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.DetailAlbumActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.util.List;
import java.util.Objects;

public class AlbumFragmentAdapterSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context _context;
    List<Album> _album;
    public static int albumsCount;

    public AlbumFragmentAdapterSearch(Context context, List<Album> albums) {
        this._context = context;
        this._album = albums;
    }

    public void setFilteredList(List<Album> filteredList) {
        this._album= filteredList;
        notifyDataSetChanged();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textDate;
        public TextView textFilePath;

        AlbumViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.album_img);
            textDate = itemView.findViewById(R.id.textDate);
            textFilePath = itemView.findViewById(R.id.textFilePath);


        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_album_view_search, parent, false);
        return new AlbumViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Album albumPos = _album.get(position);
        String albumTitle = albumPos.getName();
        List<Image> albumImages = albumPos.getAlbumImages();
        final AlbumViewHolder albumHolder = (AlbumViewHolder) holder;
        albumsCount = albumImages.size();
        if (Objects.equals(albumPos.getAlbumCover(), "")) {
            if (albumsCount > 0) {
                Glide.with(_context)
                        .load(albumImages.get(albumImages.size() - 1).getPath())
                        .into(albumHolder.imageView);
            } else {
                albumHolder.imageView.setImageResource(R.drawable.album_empty);
            }
        } else {
            Glide.with(_context)
                    .load(albumPos.getAlbumCover())
                    .into(albumHolder.imageView);
        }

        albumHolder.textDate.setText("Album "+albumTitle);
        albumHolder.textFilePath.setText("Tổng số ảnh: "+String.format("%s", albumsCount));
        albumHolder.imageView.setOnClickListener(view -> {
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
        public SingleAlbumViewBinding binding;

        public AlbumFragmentViewHolder(SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}