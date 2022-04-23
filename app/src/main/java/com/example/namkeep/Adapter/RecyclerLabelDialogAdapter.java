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

public class RecyclerLabelDialogAdapter extends RecyclerView.Adapter<RecyclerLabelDialogAdapter.LabelDialogViewHolder> {

    private List<Label> list;
    private IClickAddLabel iClickAddLabel;

    public RecyclerLabelDialogAdapter(List<Label> list, IClickAddLabel iClickAddLabel) {
        this.list = list;
        this.iClickAddLabel = iClickAddLabel;
    }

    @NonNull
    @Override
    public LabelDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelDialogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LabelDialogViewHolder holder, int position) {
        final Label labelItem = list.get(position);

        holder.textView.setText(labelItem.getLabel());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickAddLabel.ClickAddLabel(labelItem.getId(), labelItem.getLabel());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LabelDialogViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public LabelDialogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_label_dialog);
        }
    }
}
