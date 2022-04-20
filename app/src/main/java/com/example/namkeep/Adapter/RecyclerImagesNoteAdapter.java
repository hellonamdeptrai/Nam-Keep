package com.example.namkeep.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecyclerImagesNoteAdapter extends RecyclerView.Adapter<RecyclerImagesNoteAdapter.PhotoNoteViewHolder>{

    private List<Bitmap> list;

    public RecyclerImagesNoteAdapter(List<Bitmap> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PhotoNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_note, parent, false);
        return new PhotoNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoNoteViewHolder holder, int position) {
        holder.imageView.setImageBitmap(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PhotoNoteViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        public PhotoNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_add_note);
        }
    }
}
