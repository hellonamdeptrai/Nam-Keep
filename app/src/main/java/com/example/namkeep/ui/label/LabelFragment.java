package com.example.namkeep.ui.label;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.R;
import com.example.namkeep.databinding.FragmentLabelBinding;
import com.example.namkeep.object.Label;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.label.Adapter.ILabelClick;
import com.example.namkeep.ui.label.Adapter.LabelAdapter;

import java.util.ArrayList;
import java.util.List;

public class LabelFragment extends Fragment {

    private FragmentLabelBinding binding;

    private DatabaseHelper myDB;
    private RecyclerView recyclerView;
    private EditText editTextLabel;
    private ImageButton buttonAddLabel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLabelBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.mainLabel;
        recyclerView.setHasFixedSize(true);

        editTextLabel = binding.addTextLabel;
        buttonAddLabel = binding.imageButtonAddLabel;

        myDB = new DatabaseHelper(getActivity());

        getListLabel();

        buttonAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.addLabel(editTextLabel.getText().toString());
                getListLabel();
                editTextLabel.setText("");
            }
        });
        return root;
    }

    private void getListLabel() {
        List<Label> list = new ArrayList<>();
        Cursor cursor = myDB.readAllLabel();
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                list.add(new Label(Integer.parseInt(cursor.getString(0))
                        ,cursor.getString(1)
                        ,Integer.parseInt(cursor.getString(2) != null ? cursor.getString(2) : "0")
                ));
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LabelAdapter adapter = new LabelAdapter(list, new ILabelClick() {
            @Override
            public void OnClickEdit(int position, EditText editText) {
                myDB.updateLabel(list.get(position).getId()+"", editText.getText().toString());
                getListLabel();
            }

            @Override
            public void OnClickDelete(int position) {
                myDB.deleteOneRowLabel(list.get(position).getId()+"");
                getListLabel();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}