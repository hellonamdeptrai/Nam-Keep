package com.example.namkeep.ui.home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.TimeUnit;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.Adapter.RecyclerImagesNoteAdapter;
import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.MainActivity;
import com.example.namkeep.R;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.home.Helper.IClickItemDetail;
import com.example.namkeep.ui.home.Helper.ItemTouchHelperAdapter;
import com.example.namkeep.ui.home.Helper.OnStartDangListener;
import com.example.namkeep.ui.home.Item;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> implements ItemTouchHelperAdapter {

    Context context;
    private Activity activity;
    OnStartDangListener listener;
    private ArrayList<Note> notesList;
    DatabaseHelper myDB;
//    row_id, title, content, color, background, categoryId

    private IClickItemDetail iClickItemDetail;

    public MyRecyclerAdapter(Context context, OnStartDangListener listener, IClickItemDetail iClickItemDetail
    ,ArrayList<Note> notesList) {
        this.context = context;
        this.listener = listener;
        this.iClickItemDetail = iClickItemDetail;
        this.notesList = notesList;

        myDB = new DatabaseHelper(context);
//        this.row_id = row_id;
//        this.title = title;
//        this.content = content;
//        this.color = color;
//        this.background = background;
//        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.mission_item_home, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Note noteItem = notesList.get(position);

//        Picasso.with(holder.image.getContext()).load(item.getThumbnailUrl()).into(holder.image);
//        holder.imageBackground.setImageBitmap(noteItem.getBackground());

        holder.title.setText(noteItem.getTitle());
        holder.content.setText(noteItem.getContent());

        holder.mainImagesNoteHome.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        RecyclerImagesNoteAdapter adapter = new RecyclerImagesNoteAdapter(getListImages(noteItem.getId()));
        holder.mainImagesNoteHome.setAdapter(adapter);

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onStartDrag(holder);
                return false;
            }
        });
//        BitmapDrawable ob = new BitmapDrawable(holder.itemView.getContext().getResources(), noteItem.getBackground());
//        holder.linearLayout.setBackground(ob);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickItemDetail.onClickItemTour(view ,noteItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (notesList != null) {
            return notesList.size();
        }
        return 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
//        Collections.swap(stringList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void inItemDismiss(int position) {
//        stringList.remove(position);
        notifyItemRemoved(position);
    }

    private ArrayList<Bitmap> getListImages(int idNote) {
        ArrayList<Bitmap> list = new ArrayList<>();

        if (idNote != 0){
            Cursor cursor = myDB.readNoteImage(idNote);
            if(cursor.getCount() != 0 ){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (cursor.moveToNext()){
                            if (cursor.getCount() != list.size()) {
                            ((MainActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap bitmap = null;
                                    byte[] blob = cursor.getBlob(1);
                                    if (blob != null) {
                                        bitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
                                    }
                                    list.add(bitmap);
//                                    Toast.makeText(context, list.size()+""+cursor.getCount(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }}
                    }
                }).start();
            }
        }

        return list;
    }

//    private ArrayList<Bitmap> getListImages(int idNote) {
//        ArrayList<Bitmap> list = new ArrayList<>();
//
//        if (idNote != 0){
//            Cursor cursor = myDB.readNoteImage(idNote);
//            boolean a = true;
//            if(cursor.getCount() != 0){
//                while (cursor.moveToFirst()&&a){
//                    try {
//                        Bitmap bitmap = null;
//
//
//                        byte[] blob = cursor.getBlob(1);
//                        if (blob != null) {
//                            ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
//                            Thread.sleep( 1000);
//                            bitmap = BitmapFactory.decodeStream(inputStream);
//                        }
//                        list.add(bitmap);
//                        a = false;
//                    } catch (InterruptedException ie) {
//                        Thread.currentThread().interrupt();
//                    }
//
//                }
//            }
//        }
//        return list;
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBackground;
        TextView title, content;
        LinearLayout linearLayout;
        RecyclerView mainImagesNoteHome;

        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
            imageBackground = itemView.findViewById(R.id.imageview_item);
            title = itemView.findViewById(R.id.title_note_home);
            content = itemView.findViewById(R.id.content_note_home);
            linearLayout = itemView.findViewById(R.id.layout_item);
            mainImagesNoteHome = itemView.findViewById(R.id.main_images_note_home);
        }
    }
}
