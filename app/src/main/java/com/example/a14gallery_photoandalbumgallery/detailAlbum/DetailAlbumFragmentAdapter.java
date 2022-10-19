package com.example.a14gallery_photoandalbumgallery.detailAlbum;

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

public class DetailAlbumFragmentAdapter extends RecyclerView.Adapter<DetailAlbumFragmentAdapter.AlbumFragmentViewHolder> {
    Context _context;
    List<Album> _album;
    private final RecyclerViewInterface recyclerViewInterface;

    public DetailAlbumFragmentAdapter(Context context, List<Album> album, RecyclerViewInterface recyclerViewInterface) {
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
        String albumTitle = _album.get(position).getName();
        List<Image> albumPhotos = _album.get(position).getAlbumImages();
        int albumsCount = albumPhotos.size();
        if (albumsCount > 0) {
            Glide.with(_context)
                    .load(albumPhotos.get(albumsCount - 2).getPath())
                    .into(holder.binding.albumImg);
        } else {
            holder.binding.albumImg.setImageResource(R.drawable.album_empty);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
    }


    @Override
    public int getItemCount() {
        return _album.size();
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
