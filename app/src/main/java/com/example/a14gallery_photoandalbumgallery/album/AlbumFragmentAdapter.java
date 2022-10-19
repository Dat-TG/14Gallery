package com.example.a14gallery_photoandalbumgallery.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.Image;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;

import java.util.List;

public class AlbumFragmentAdapter extends RecyclerView.Adapter<AlbumFragmentAdapter.AlbumFragmentViewHolder> {
    Context _context;
    List<Album> _album;

    public AlbumFragmentAdapter(Context context, List<Album> album) {
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
        String albumTitle = _album.get(position).getName();
        List<Image> albumImages = _album.get(position).getAlbumImages();

        int albumsCount = albumImages.size();
        if (albumsCount > 0) {
            Glide.with(_context)
                    .load(albumImages.get(albumsCount - 1).getPath())
                    .into(holder.binding.albumImg);
        } else {
            holder.binding.albumImg.setImageResource(R.drawable.album_empty);
        }

        holder.binding.albumTitle.setText(albumTitle);
        holder.binding.albumCount.setText(String.format("%s", albumsCount));
        holder.binding.albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(_context, DetailAlbumFragment.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("ALBUM_PHOTOS", albumImages);
//                intent.putExtras(bundle);
                // Navigation to Detail Album 
            }
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