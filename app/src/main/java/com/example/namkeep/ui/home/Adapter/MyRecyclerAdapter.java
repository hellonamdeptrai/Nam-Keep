package com.example.namkeep.ui.home.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.home.Helper.IClickItemDetail;
import com.example.namkeep.ui.home.Helper.ItemTouchHelperAdapter;
import com.example.namkeep.ui.home.Helper.OnStartDangListener;
import com.example.namkeep.ui.home.Item;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

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
//    row_id, title, content, color, background, categoryId

    private IClickItemDetail iClickItemDetail;

    public MyRecyclerAdapter(Context context, OnStartDangListener listener, IClickItemDetail iClickItemDetail
    ,ArrayList<Note> notesList) {
        this.context = context;
        this.listener = listener;
        this.iClickItemDetail = iClickItemDetail;
        this.notesList = notesList;
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
        holder.image.setImageBitmap(noteItem.getBackground());

        holder.name.setText(noteItem.getTitle());
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onStartDrag(holder);
                return false;
            }
        });
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview_item)
        RoundedImageView image;

        @BindView(R.id.textview_name)
        TextView name;

        @BindView(R.id.layout_item)
        LinearLayout linearLayout;

        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
