package com.example.namkeep.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class RecyclerImagesAddNoteAdapter extends RecyclerView.Adapter<RecyclerImagesAddNoteAdapter.PhotoNoteViewHolder>{

    private List<Bitmap> list;
    private Context context;
    private IClickDeleteCheckBox iClickDeleteCheckBox;

    public RecyclerImagesAddNoteAdapter(Context context, List<Bitmap> list, IClickDeleteCheckBox iClickDeleteCheckBox) {
        this.list = list;
        this.context = context;
        this.iClickDeleteCheckBox = iClickDeleteCheckBox;
    }

    @NonNull
    @Override
    public PhotoNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_note, parent, false);
        return new PhotoNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoNoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageBitmap(list.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.layout_dialog_image);

                Window window = dialog.getWindow();
                if (window == null) {
                    return;
                }

                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                window.setAttributes(windowAttributes);
                dialog.setCancelable(true);

                ImageView imageView = dialog.findViewById(R.id.image_view_detail);
                Button button = dialog.findViewById(R.id.button_delete_image);

                imageView.setImageBitmap(list.get(position));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iClickDeleteCheckBox.onClickDeleteItem(position);
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
            }
        });
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
