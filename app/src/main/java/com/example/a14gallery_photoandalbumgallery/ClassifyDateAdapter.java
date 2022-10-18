package com.example.a14gallery_photoandalbumgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassifyDateAdapter extends RecyclerView.Adapter<ClassifyDateAdapter.ClassifyDateViewHolder>{
    private Context context;
    private List<ClassifyDate> listClassifyDate;

    public ClassifyDateAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<ClassifyDate> listClassifyDate){
        this.listClassifyDate = listClassifyDate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassifyDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classify_date, parent, false);
        return new ClassifyDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassifyDateViewHolder holder, int position) {
        ClassifyDate ClassifyDate = listClassifyDate.get(position);

        holder.txtNameClassifyDate.setText(ClassifyDate.getNameClassifyDate());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        holder.rcvImages.setLayoutManager(gridLayoutManager);

        ImageAdapter imageAdapter = new ImageAdapter(context.getApplicationContext());
        imageAdapter.setData(ClassifyDate.getListImage());
        holder.rcvImages.setAdapter(imageAdapter);
    }

    @Override
    public int getItemCount() {
        if (listClassifyDate != null){
            return listClassifyDate.size();
        }
        return 0;
    }

    public class ClassifyDateViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNameClassifyDate;
        private RecyclerView rcvImages;

        public ClassifyDateViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNameClassifyDate = itemView.findViewById(R.id.txtNameClassifyDate);
            rcvImages = itemView.findViewById(R.id.rcvImages);
        }
    }
}
