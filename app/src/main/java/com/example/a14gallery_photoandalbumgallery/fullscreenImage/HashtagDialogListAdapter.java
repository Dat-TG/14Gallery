package com.example.a14gallery_photoandalbumgallery.fullscreenImage;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.database.AppDatabase;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.Hashtag;
import com.example.a14gallery_photoandalbumgallery.database.image.hashtag.ImageHashtag;
import com.example.a14gallery_photoandalbumgallery.databinding.TextDeleteButtonRowItemBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HashtagDialogListAdapter extends RecyclerView.Adapter<HashtagDialogListAdapter.ViewHolder> {
    public String imagePath;
    public List<String> hashtags;
    public TextView hashtagCountText;

    public HashtagDialogListAdapter(String imagePath, String[] hashtags, TextView hashtagCountText) {
        // Returns a unmodifiable list so we have to convert it to a mutable lis
        List<String> tempFixedList = Arrays.asList(hashtags);
        this.hashtags = new ArrayList<>(tempFixedList);
        this.imagePath = imagePath;
        this.hashtagCountText = hashtagCountText;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextDeleteButtonRowItemBinding binding;

        public ViewHolder(TextDeleteButtonRowItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    @NonNull
    @Override
    public HashtagDialogListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext().getApplicationContext());
        return new ViewHolder(TextDeleteButtonRowItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagDialogListAdapter.ViewHolder holder, int position) {
        String hashtagName = (position + 1) + " " + hashtags.get(position);
        holder.binding.txtHashtagName.setText(hashtagName);
        holder.binding.btnClear.setOnClickListener(v -> {
            Hashtag deletingHashtag = AppDatabase.getInstance(v.getContext()).hashtagDao().findByName(hashtags.get(position));
            int id = deletingHashtag.id;
            ImageHashtag deletingImgHashtag = AppDatabase.getInstance(v.getContext()).imageHashtagDao().findByPathAndId(imagePath, id);
            AppDatabase.getInstance(v.getContext()).imageHashtagDao().delete(deletingImgHashtag);
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return hashtags.size();
    }

    public void removeAt(int position) {
        hashtags.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
        updateCountText();
    }

    public void addItem(String newHashtag) {
        hashtags.add(newHashtag);
        notifyItemInserted(getItemCount() - 1);
        notifyItemRangeChanged(getItemCount() - 1, getItemCount());
        updateCountText();
    }

    public void updateCountText() {
        if (getItemCount() != 0) {
            hashtagCountText.setText(String.format(Locale.ENGLISH, "Số lượng hashtag đã thêm: %d", getItemCount()));
        }
        else {
            hashtagCountText.setText(R.string.no_hashtag_message);
        }
    }
}
