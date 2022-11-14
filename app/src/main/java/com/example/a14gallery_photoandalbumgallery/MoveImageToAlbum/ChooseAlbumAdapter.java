package com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum;
import static  com.example.a14gallery_photoandalbumgallery.MoveImageToAlbum.ChooseAlbumActivity.activityMoveLauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.album.Album;
import com.example.a14gallery_photoandalbumgallery.album.AlbumFragmentAdapter;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleAlbumViewBinding;
import com.example.a14gallery_photoandalbumgallery.detailAlbum.DetailAlbumActivity;
import com.example.a14gallery_photoandalbumgallery.image.Image;

import java.util.List;
import java.util.Objects;

public class ChooseAlbumAdapter extends RecyclerView.Adapter<ChooseAlbumAdapter.ChooseAlbumAdapterViewHolder>{
    Context _context;
    List<Album> _album;

    public ChooseAlbumAdapter(Context context, List<Album> albums) {
        this._context = context;
        this._album = albums;
    }

    @NonNull
    @Override
    public ChooseAlbumAdapter.ChooseAlbumAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ChooseAlbumAdapterViewHolder(SingleAlbumViewBinding.inflate(inflater, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ChooseAlbumAdapter.ChooseAlbumAdapterViewHolder holder, int position) {
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
            Intent intent = new Intent(_context, DetailAlbumMoveActivity.class);
            intent.putExtra("NAME", albumPos.getName());
            activityMoveLauncher.launch(intent);
        });
    }

    @Override
    public int getItemCount() {
        return _album.size();
    }

    public static class ChooseAlbumAdapterViewHolder extends RecyclerView.ViewHolder {
        SingleAlbumViewBinding binding;

        public ChooseAlbumAdapterViewHolder(SingleAlbumViewBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}
