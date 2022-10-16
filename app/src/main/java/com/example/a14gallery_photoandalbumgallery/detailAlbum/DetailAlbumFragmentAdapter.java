package com.example.a14gallery_photoandalbumgallery.detailAlbum;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.photo.Photo;

import java.util.Vector;

public class DetailAlbumFragmentAdapter extends RecyclerView.Adapter<DetailAlbumFragmentAdapter.AlbumFragmentViewHolder>{
    Context _context;
    Pair<Vector<Album>, Vector<String>> _album;
    private final RecyclerViewInterface recyclerViewInterface;

    public DetailAlbumFragmentAdapter(Context context, Pair<Vector<Album>, Vector<String>> album, RecyclerViewInterface recyclerViewInterface) {
        this._context = context;
        this._album = album;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public DetailAlbumFragmentAdapter.AlbumFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AlbumFragmentViewHolder(inflater.inflate(R.layout.single_image_view, parent, false), recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailAlbumFragmentAdapter.AlbumFragmentViewHolder holder, int position) {
        String albumTitle = _album.second.elementAt(position);
        Vector<Photo> albumPhotos = _album.first.get(position).getAlbumPhotos();
        int albumsCount = albumPhotos.size();
        if (albumsCount > 0) {
            Glide.with(_context)
                    .load(albumPhotos.get(albumsCount - 2).getPhotoUri())
                    .into(holder.binding.albumImg);
        } else {
            holder.binding.albumImg.setImageResource(R.drawable.album_empty);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
    }


    @Override
    public int getItemCount() {
        return _album.first.size();
    }

    public static class AlbumFragmentViewHolder extends RecyclerView.ViewHolder {
        SingleAlbumViewBinding binding;

        public AlbumFragmentViewHolder(View view, RecyclerViewInterface recyclerViewInterface) {
            super(view);
            if (recyclerViewInterface != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(pos);
                }
            }
        }
    }


}
