package com.example.namkeep.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.R;
import com.example.namkeep.databinding.FragmentHomeBinding;
import com.example.namkeep.object.Note;
import com.example.namkeep.ui.home.Adapter.MyRecyclerAdapter;
import com.example.namkeep.ui.home.Helper.IClickItemDetail;
import com.example.namkeep.ui.home.Helper.MyItemTouchHelperCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra(DetailActivity.EXTRA_PARAM_ID, note.getId());
//
//                @SuppressWarnings("unchecked")
//                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        new Pair<>(view.findViewById(R.id.imageview_item),
//                                DetailActivity.VIEW_NAME_HEADER_IMAGE),
//                        new Pair<>(view.findViewById(R.id.textview_name),
//                                DetailActivity.VIEW_NAME_HEADER_TITLE));
//                ActivityCompat.startActivity(getActivity(), intent, activityOptions.toBundle());
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
                Bitmap bitmap = null;
                byte[] blob = cursor.getBlob(4);
                if (blob != null) {
                    bitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
                }

                notes.add(new Note(Integer.parseInt(cursor.getString(0))
                        ,cursor.getString(1)
                        ,cursor.getString(2)
                        ,Integer.parseInt(cursor.getString(3))
                        ,bitmap
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