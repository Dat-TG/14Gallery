package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleImageViewBinding;

import java.util.ArrayList;
import java.util.List;

public class ImageFragmentAdapter extends RecyclerView.Adapter<ImageFragmentAdapter.ImageFragmentViewHolder> {
    Context _context;
    //List<String> _images;
    ArrayList<Photo> _photos;


    public ImageFragmentAdapter(Context context,  ArrayList<Photo> photos) {
        _context = context;
        _photos=photos;
    }

    public static class ImageFragmentViewHolder extends RecyclerView.ViewHolder {
        SingleImageViewBinding binding;
        public View scrim;
        public CheckBox checkBox;

        public ImageFragmentViewHolder(SingleImageViewBinding b) {
            super(b.getRoot());
            binding = b;
            scrim = (View) itemView.findViewById(R.id.pictureItemScrim);
            checkBox = (CheckBox) itemView.findViewById(R.id.pictureItemCheckCircle);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClick(getAdapterPosition(), view);
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition(), view);
                }
            });
        }
    }

    @NonNull
    @Override
    public ImageFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageFragmentViewHolder(SingleImageViewBinding.inflate(inflater, parent, false));
    }

    private int ACTION_MODE = 0;
    private static OnItemLongClickListener onItemLongClickListener = null;
    private static OnItemClickListener onItemClickListener = null;
    @Override
    public void onBindViewHolder(@NonNull ImageFragmentViewHolder holder, int position) {
        String name = _photos.get(position).getImgName();
        String imgPath = _photos.get(position).getImgPath();
        // holder.binding.image.setImageResource(_images[position]);
        if (_photos != null) {
            Glide.with(_context)
                    .load(imgPath)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.binding.image);
            Photo imageData = _photos.get(position);
            /*TO DO: Implement onClickListener on this method*/
            if (ACTION_MODE == 0) {
                holder.scrim.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.GONE);
                imageData.setChecked(false);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.scrim.setVisibility(View.VISIBLE);
                if (imageData.isChecked()) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
            }
        }
    }

    public int getACTION_MODE() {
        return ACTION_MODE;
    }

    public void setACTION_MODE(int ACTION_MODE) {
        this.ACTION_MODE = ACTION_MODE;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = (OnItemLongClickListener) onItemLongClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = (OnItemClickListener) onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);
    }

    @Override
    public int getItemCount() {
        return _photos.size();
    }
}
