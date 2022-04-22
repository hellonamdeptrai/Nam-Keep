package com.example.namkeep.ui.home.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecyclerImagesNoteHomeAdapter extends RecyclerView.Adapter<RecyclerImagesNoteHomeAdapter.PhotoNoteHomeViewHolder>{

    private List<Bitmap> list;

    public RecyclerImagesNoteHomeAdapter(List<Bitmap> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PhotoNoteHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_note, parent, false);
        return new PhotoNoteHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoNoteHomeViewHolder holder, int position) {
        holder.imageView.setImageBitmap(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class PhotoNoteHomeViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        public PhotoNoteHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_add_note);
        }
    }
}
