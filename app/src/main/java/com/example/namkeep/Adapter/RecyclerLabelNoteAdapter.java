package com.example.namkeep.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.example.namkeep.object.Label;

import java.util.List;

public class RecyclerLabelNoteAdapter extends RecyclerView.Adapter<RecyclerLabelNoteAdapter.LabelNoteViewHolder> {

    private List<Label> list;

    public RecyclerLabelNoteAdapter(List<Label> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public LabelNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelNoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LabelNoteViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getLabel());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LabelNoteViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public LabelNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_label_note);
        }
    }
}
