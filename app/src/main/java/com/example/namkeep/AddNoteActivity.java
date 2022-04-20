package com.example.namkeep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.namkeep.Adapter.RecyclerImagesNoteAdapter;
import com.example.namkeep.ui.gallery.PhotoAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTitle, mContent;
    private ImageView mBackground, mImageNote;
    private Uri imagePath;
    private Bitmap imageToStore;
    private CoordinatorLayout mMainAddNote;

    DatabaseHelper myDB;

    List<Bitmap> listBitmap = new ArrayList<>();


    private static final int PICK_IMAGE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mToolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thêm ghi chú");

        myDB = new DatabaseHelper(AddNoteActivity.this);

        mTitle = findViewById(R.id.title_note);
        mContent = findViewById(R.id.content_note);
        mMainAddNote = findViewById(R.id.main_container_add_note);

        findViewById(R.id.bottom_app_bar_add_note).setBackground(null);

        Button mSheetAddButton = findViewById(R.id.sheet_add_note_button);
        mSheetAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddNoteActivity.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_add_note,null
                        );
                bottomSheetView.findViewById(R.id.add_image_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        myDB.addNote(mTitle.getText().toString(), mContent.getText().toString(), "red", imageToStore, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_OK) {
            imagePath = data.getData();
            try {
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
//                BitmapDrawable ob = new BitmapDrawable(getResources(), imageToStore);
//                mMainAddNote.setBackground(ob);
//                mImageNote = findViewById(R.id.imageViewHihi);
//                mImageNote.setImageBitmap(imageToStore);
                listBitmap.add(imageToStore);
                addListImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
    }

    private void addListImage() {
        RecyclerView mainImagesNote = findViewById(R.id.main_images_note);
        mainImagesNote.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        RecyclerImagesNoteAdapter adapter = new RecyclerImagesNoteAdapter(listBitmap);
        mainImagesNote.setAdapter(adapter);
    }

//    private String getPath(Uri uri) {
//        if(uri == null){
//            return null;
//        }
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        if (cursor != null) {
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//        return uri.getPath();
//    }
}