package com.example.a14gallery_photoandalbumgallery.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.DetailAlbumFragment;
import com.example.a14gallery_photoandalbumgallery.photo.Photo;

import java.util.Vector;

public class AlbumFragmentAdapter extends RecyclerView.Adapter<AlbumFragmentAdapter.AlbumFragmentViewHolder> {
    Context _context;
    Pair<Vector<Album>, Vector<String>> _album;

    public AlbumFragmentAdapter (Context context, Pair<Vector<Album>, Vector<String>> album) {
        this._context = context;
        this._album = album;
    }

    @NonNull
    @Override
    public AlbumFragmentAdapter.AlbumFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new AlbumFragmentViewHolder(SingleAlbumViewBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumFragmentAdapter.AlbumFragmentViewHolder holder, int position) {
        String albumTitle = _album.second.elementAt(position);
        Vector<Photo> albumPhotos = _album.first.get(position).getAlbumPhotos();
        int albumsCount = albumPhotos.size();
        if(albumsCount > 0){
            Glide.with(_context)
                    .load(albumPhotos.get(albumsCount - 1).getPhotoUri())
                    .into(holder.binding.albumImg);
        }else{
            holder.binding.albumImg.setImageResource(R.drawable.album_empty);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
        holder.binding.albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, DetailAlbumFragment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ALBUM_PHOTOS", albumPhotos);
                intent.putExtras(bundle);
                // Navigation to Detail Album 
            }
        });
    }

    @Override
    public int getItemCount() {
        return _album.first.size();
    }

    public static class AlbumFragmentViewHolder extends RecyclerView.ViewHolder {
        SingleAlbumViewBinding binding;
        public AlbumFragmentViewHolder(SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }


}