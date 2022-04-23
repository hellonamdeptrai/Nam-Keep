package com.example.namkeep.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.R;
import com.example.namkeep.databinding.FragmentHomeBinding;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.home.Adapter.MyRecyclerAdapter;
import com.example.namkeep.ui.home.Helper.IClickItemDetail;
import com.example.namkeep.ui.home.Helper.MyItemTouchHelperCallback;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;
    RecyclerView recyclerView;
    ItemTouchHelper itemTouchHelper;
    DatabaseHelper myDB;
    SwipeRefreshLayout swipeRefreshLayout;
    MyRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.mainRecycler;
        swipeRefreshLayout = root.findViewById(R.id.refresh_note_home);
        myDB = new DatabaseHelper(getActivity());
        init();
        generateItem();
        swipeRefreshLayout.setOnRefreshListener(this);

        return root;
    }

    public void generateItem() {
        adapter = new MyRecyclerAdapter(getActivity(), viewHolder -> {
            itemTouchHelper.startDrag(viewHolder);
        }, new IClickItemDetail() {
            @Override
            public void onClickItemNote(View view, Note note) {
                Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.EXTRA_PARAM_ID, note.getId());
                intent.putExtra(EditNoteActivity.VIEW_NAME_TITLE, note.getTitle());
                intent.putExtra(EditNoteActivity.VIEW_NAME_CONTENT, note.getContent());
                intent.putExtra(EditNoteActivity.VIEW_NAME_IS_CHECKBOX, note.getIsCheckBoxOrContent());
                intent.putExtra(EditNoteActivity.VIEW_NAME_BACKGROUND, note.getBackground());
                intent.putExtra(EditNoteActivity.VIEW_NAME_EDIT_COLOR, note.getColor());

                Pair isColorData, isContentOrCheckbox;
                if (note.getColor() != Color.rgb(255,255,255)) {
                    isColorData = new Pair<>(view.findViewById(R.id.color_background_image_home),
                            EditNoteActivity.VIEW_NAME_EDIT_COLOR);
                } else {
                    isColorData = new Pair<>(view.findViewById(R.id.title_note_home),
                            EditNoteActivity.VIEW_NAME_EDIT_COLOR);
                }

                if (note.getIsCheckBoxOrContent() == 1) {
                    isContentOrCheckbox = new Pair<>(view.findViewById(R.id.main_checkbox_note_home),
                            EditNoteActivity.VIEW_NAME_LIST_CHECKBOX);
                } else {
                    isContentOrCheckbox = new Pair<>(view.findViewById(R.id.content_note_home),
                            EditNoteActivity.VIEW_NAME_CONTENT);
                }

                @SuppressWarnings("unchecked")
                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        new Pair<>(view.findViewById(R.id.main_images_note_home),
                                EditNoteActivity.VIEW_NAME_IMAGE),
                        new Pair<>(view.findViewById(R.id.title_note_home),
                                EditNoteActivity.VIEW_NAME_TITLE),
                        isContentOrCheckbox,
                        isColorData);
                ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
            }
        });
        adapter.setData(getListNotes());
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
                byte[] blob = cursor.getBlob(5);

                notes.add(new Note(Integer.parseInt(cursor.getString(0))
                        ,cursor.getString(1)
                        ,cursor.getString(2)
                        ,Integer.parseInt(cursor.getString(3))
                        ,Integer.parseInt(cursor.getString(4))
                        ,blob
                        ,Integer.parseInt(cursor.getString(6))
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

    @Override
    public void onRefresh() {
        adapter.setData(getListNotes());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}