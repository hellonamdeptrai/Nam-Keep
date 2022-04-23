package com.example.namkeep.ui.label.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.example.namkeep.object.Label;

import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {

    private List<Label> list;
    private ILabelClick iLabelClick;

    public LabelAdapter(List<Label> list, ILabelClick iLabelClick) {
        this.list = list;
        this.iLabelClick = iLabelClick;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_label, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Label labelItem = list.get(position);

        holder.editText.setText(labelItem.getLabel());
        holder.editLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iLabelClick.OnClickEdit(position, holder.editText);
            }
        });
        holder.deleteLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iLabelClick.OnClickDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        ImageButton editLabel, deleteLabel;
        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edit_text_label);
            editLabel = itemView.findViewById(R.id.image_button_edit_label);
            deleteLabel = itemView.findViewById(R.id.image_button_delete_label);
        }
    }
}
