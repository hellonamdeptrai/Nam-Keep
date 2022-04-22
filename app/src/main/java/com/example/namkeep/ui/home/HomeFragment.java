package com.example.namkeep.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.R;
import com.example.namkeep.databinding.FragmentHomeBinding;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.home.Adapter.MyRecyclerAdapter;
import com.example.namkeep.ui.home.Helper.IClickItemDetail;
import com.example.namkeep.ui.home.Helper.MyItemTouchHelperCallback;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;
    DatabaseHelper myDB;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.mainRecycler;
        myDB = new DatabaseHelper(getActivity());
        init();
        generateItem();

        return root;
    }

    public void generateItem() {
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(getActivity(), viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        }, new IClickItemDetail() {
            @Override
            public void onClickItemTour(View view, Note note) {
                Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.EXTRA_PARAM_ID, note.getId());
                intent.putExtra(EditNoteActivity.VIEW_NAME_TITLE, note.getTitle());
                intent.putExtra(EditNoteActivity.VIEW_NAME_CONTENT, note.getContent());
                intent.putExtra(EditNoteActivity.VIEW_NAME_BACKGROUND, note.getBackground());
                intent.putExtra(EditNoteActivity.VIEW_NAME_EDIT_COLOR, note.getColor());

                Pair isColorData;
                if (note.getColor() != Color.rgb(255,255,255)) {
                    isColorData = new Pair<>(view.findViewById(R.id.color_background_imaged_home),
                            EditNoteActivity.VIEW_NAME_EDIT_COLOR);
                } else {
                    isColorData = new Pair<>(view.findViewById(R.id.content_note_home),
                            EditNoteActivity.VIEW_NAME_EDIT_COLOR);
                }

                @SuppressWarnings("unchecked")
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        new Pair<>(view.findViewById(R.id.main_images_note_home),
                                EditNoteActivity.VIEW_NAME_IMAGE),
                        new Pair<>(view.findViewById(R.id.title_note_home),
                                EditNoteActivity.VIEW_NAME_TITLE),
                        new Pair<>(view.findViewById(R.id.content_note_home),
                                EditNoteActivity.VIEW_NAME_CONTENT),
                        isColorData);
                ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
            }
        }, getListNotes());
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private ArrayList<Note> getListNotes() {
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = myDB.readAllNote();
        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                byte[] blob = cursor.getBlob(4);
//                Bitmap bitmap = null;
//                if (blob != null) {
//                    bitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
//                }

                notes.add(new Note(Integer.parseInt(cursor.getString(0))
                        ,cursor.getString(1)
                        ,cursor.getString(2)
                        ,Integer.parseInt(cursor.getString(3))
                        ,blob
                        ,Integer.parseInt(cursor.getString(5))
                ));
            }
        }

        return notes;
    }

    private void init() {
        ButterKnife.bind(getActivity());
        recyclerView = binding.mainRecycler;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2, StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}