package com.example.namkeep.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.namkeep.R;
import com.example.namkeep.object.CheckBoxContentNote;

import java.util.List;

public class RecyclerCheckBoxNoteAdapter extends RecyclerView.Adapter<RecyclerCheckBoxNoteAdapter.CheckBoxNoteViewHolder> {

    private List<CheckBoxContentNote> list;
    private Context context;
    private IClickDeleteCheckBox iClickDeleteCheckBox;

    public RecyclerCheckBoxNoteAdapter(Context context,List<CheckBoxContentNote> list,IClickDeleteCheckBox iClickDeleteCheckBox) {
        this.list = list;
        this.context = context;
        this.iClickDeleteCheckBox = iClickDeleteCheckBox;
    }

    @NonNull
    @Override
    public CheckBoxNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CheckBoxNoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CheckBoxNoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final CheckBoxContentNote checkBoxItem = list.get(position);

        holder.checkBox.setChecked(checkBoxItem.isCheckBox());
        holder.editText.setText(checkBoxItem.getContent());
        holder.mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickDeleteCheckBox.onClickItemTour(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckBoxNoteViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        EditText editText;
        ImageButton mDeleteItem;
        EditText mEditTextCheckBox;
        LinearLayout linearLayout;

        public CheckBoxNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box_note);
            editText = itemView.findViewById(R.id.edit_text_check_box);
            mDeleteItem = itemView.findViewById(R.id.image_button_delete_item);
            mEditTextCheckBox = itemView.findViewById(R.id.edit_text_check_box);
            linearLayout = itemView.findViewById(R.id.main_check_box_item);
        }
    }
}
