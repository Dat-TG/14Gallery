package com.example.a14gallery_photoandalbumgallery.searchImage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a14gallery_photoandalbumgallery.image.Image;
import com.example.a14gallery_photoandalbumgallery.R;
import com.example.a14gallery_photoandalbumgallery.image.RecyclerData;

import java.util.List;
import java.util.function.BiConsumer;

public class ImageFragmentAdapterSearch extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public void setFilteredList(List<RecyclerData> filteredList) {
        this.imageDataList = filteredList;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public View scrim;
        public CheckBox check;
        public TextView textDate;

        ImageViewHolder(@NonNull View view,
                        BiConsumer<Integer, View> onItemClick,
                        BiConsumer<Integer, View> onItemLongClick) {
            super(view);
            imageView = view.findViewById(R.id.image);
            scrim = itemView.findViewById(R.id.pictureItemScrim);
            check = itemView.findViewById(R.id.pictureItemCheckCircle);
            textDate = itemView.findViewById(R.id.textDate);

            itemView.setOnLongClickListener(view1 -> {
                onItemLongClick.accept(getAdapterPosition(), view1);
                return false;
            });
            itemView.setOnClickListener(
                    view1 -> onItemClick.accept(getAdapterPosition(), view1));
        }
    }

    public static class TimelineViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        TimelineViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.timelineItemText);
        }
    }

    public enum State {
        Normal,
        MultipleSelect
    }

    public static int ITEM_TYPE_TIME = 0;
    public static int ITEM_TYPE_IMAGE = 1;
    @NonNull
    private List<RecyclerData> imageDataList;
    @NonNull
    private final BiConsumer<Integer, View> onItemClick;
    @NonNull
    private final BiConsumer<Integer, View> onItemLongClick;

    private State state = State.Normal;

    public ImageFragmentAdapterSearch(@NonNull List<RecyclerData> imageDataList,
                                      @NonNull BiConsumer<Integer, View> onItemClick,
                                      @NonNull BiConsumer<Integer, View> onItemLongClick) {
        this.imageDataList = imageDataList;
        this.onItemClick = onItemClick;
        this.onItemLongClick = onItemLongClick;
    }

    @Override
    public int getItemViewType(int position) {
        if (imageDataList.get(position).type == RecyclerData.Type.Label) {
            return ITEM_TYPE_TIME;
        } else {
            return ITEM_TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ITEM_TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.single_image_view_search, parent, false);
            return new ImageViewHolder(view, onItemClick, onItemLongClick);
        } else {
            return new TimelineViewHolder(inflater.inflate(R.layout.timeline_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (imageDataList.get(position).type == RecyclerData.Type.Label) {
            final TimelineViewHolder timelineHolder = (TimelineViewHolder) holder;
            timelineHolder.textView.setText(imageDataList.get(position).labelData);
        } else {
            final ImageViewHolder imageHolder = (ImageViewHolder) holder;
            ImageView imageView = imageHolder.imageView;
            TextView textView = imageHolder.textDate;
            Image imageData = imageDataList.get(position).imageData;
            Glide.with(imageView.getContext())
                    .load(imageData.getPath())
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imageView);
            textView.setText(imageData.getDateTaken());

            if (state == State.Normal) {
                imageHolder.scrim.setVisibility(View.GONE);
                imageHolder.check.setVisibility(View.GONE);
                imageData.setChecked(false);
            } else {
                imageHolder.check.setVisibility(View.VISIBLE);
                imageHolder.scrim.setVisibility(View.VISIBLE);
                if (imageData.isChecked()) {
                    imageHolder.check.setChecked(true);
                } else {
                    imageHolder.check.setChecked(false);
                }
            }
        }
    }

    public void setData(List<RecyclerData> imageDataList) {
        this.imageDataList = imageDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (imageDataList == null) return 0;
        else if (imageDataList.size() > 0) {
            return imageDataList.size();
        }
        return 0;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
