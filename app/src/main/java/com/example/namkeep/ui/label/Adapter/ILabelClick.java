package com.example.namkeep.ui.label.Adapter;

import android.widget.EditText;

public interface ILabelClick {
    void OnClickEdit(int position, EditText editText);
    void OnClickDelete(int position);
}
