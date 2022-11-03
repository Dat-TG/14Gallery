package com.example.a14gallery_photoandalbumgallery;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.databinding.SingleImageViewBinding;

import java.util.List;

public class  ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Image image);
    }
    public interface OnItemLongClickListener {
        boolean onItemLongClick(Image image);
    }

    private List<Image> _listImages;
    private OnItemClickListener _listener;
    private OnItemLongClickListener _listeners;

    public ImageAdapter(List<Image> listImage, OnItemClickListener listener,OnItemLongClickListener listeners) {
        _listImages = listImage;
        _listener = listener;
        _listeners=listeners;
    }

    public void setData(List<Image> listImages) {
        _listImages = listImages;
        notifyDataSetChanged();
    }

    public void setOnLongClickListener(OnItemLongClickListener listeners){
        _listeners=listeners;
    }

    public void setOnClickListener(OnItemClickListener listener){
        _listener=listener;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        SingleImageViewBinding binding;
        public View scrim;
        public CheckBox checkBox;

        public ImageViewHolder(SingleImageViewBinding b) {
            super(b.getRoot());
            binding = b;
            scrim = itemView.findViewById(R.id.pictureItemScrim);
            checkBox = itemView.findViewById(R.id.pictureItemCheckCircle);
        }

        public void bind(final Image image, final OnItemClickListener listener,final OnItemLongClickListener listeners) {
            Glide.with(binding.getRoot().getContext())
                    .load(image.getPath())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.image);
            binding.image.setOnClickListener(view -> listener.onItemClick(image));
            binding.image.setOnLongClickListener(view->listeners.onItemLongClick(image));
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ImageViewHolder(SingleImageViewBinding.inflate(inflater, parent, false));
    }

    public int ACTION_MODE = 0;
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Image image = _listImages.get(position);
        if (image == null) {
            return;
        }

        holder.bind(image, _listener,_listeners);
        if (ACTION_MODE == 0) {
            holder.scrim.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
            image.setChecked(false);
        } else if (ACTION_MODE==1) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.scrim.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(image.isChecked());
        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.scrim.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        }
    }

    public int getACTION_MODE() {
        return ACTION_MODE;
    }

    public void setACTION_MODE(int ACTION_MODE) {
        this.ACTION_MODE = ACTION_MODE;
    }

    @Override
    public int getItemCount() {
        if (_listImages != null)
            return _listImages.size();
        return 0;
    }

}
