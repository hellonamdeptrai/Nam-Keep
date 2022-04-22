/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.namkeep.ui.home;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.Adapter.RecyclerImagesNoteAdapter;
import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.MainActivity;
import com.example.namkeep.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Our secondary Activity which is launched from {@link MainActivity}. Has a simple detail UI
 * which has a large banner image, title and body text.
 */
public class EditNoteActivity extends AppCompatActivity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "edit:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_IMAGE = "edit:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_TITLE = "edit:title";
    public static final String VIEW_NAME_CONTENT = "edit:content";
    public static final String VIEW_NAME_BACKGROUND = "edit:background";
    public static final String VIEW_NAME_EDIT_COLOR = "edit:color";

    private RecyclerView mImageView;
    private TextView mTitle, mContent;
    private RoundedImageView mEditColor;
    private CoordinatorLayout mMainContainerEditNote;

    private DatabaseHelper myDB;

    private Toolbar mToolbar;

    String titleNote, contentNote;
    int idNote, colorNote;
    byte[] backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mToolbar = findViewById(R.id.toolbar_edit);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sửa ghi chú");

        myDB = new DatabaseHelper(this);

        mImageView = findViewById(R.id.edit_main_images_note);
        mTitle = findViewById(R.id.edit_title_note);
        mContent = findViewById(R.id.edit_content_note);
        mEditColor = findViewById(R.id.edit_color_background_imaged);
        mMainContainerEditNote = findViewById(R.id.main_container_edit_note);

        ViewCompat.setTransitionName(mImageView, VIEW_NAME_IMAGE);
        ViewCompat.setTransitionName(mTitle, VIEW_NAME_TITLE);
        ViewCompat.setTransitionName(mContent, VIEW_NAME_CONTENT);
        ViewCompat.setTransitionName(mMainContainerEditNote, VIEW_NAME_BACKGROUND);
        ViewCompat.setTransitionName(mEditColor, VIEW_NAME_EDIT_COLOR);

        loadNote();
    }

    private void loadNote() {
        if(getIntent().hasExtra(VIEW_NAME_BACKGROUND) && getIntent().hasExtra(EXTRA_PARAM_ID) && getIntent().hasExtra(VIEW_NAME_TITLE) &&
                getIntent().hasExtra(VIEW_NAME_CONTENT) && getIntent().hasExtra(VIEW_NAME_EDIT_COLOR)){
            //Getting Data from Intent
            idNote = getIntent().getIntExtra(EXTRA_PARAM_ID,1);
            titleNote = getIntent().getStringExtra(VIEW_NAME_TITLE);
            contentNote = getIntent().getStringExtra(VIEW_NAME_CONTENT);
            backgroundImage = getIntent().getByteArrayExtra(VIEW_NAME_BACKGROUND);
            colorNote = getIntent().getIntExtra(VIEW_NAME_EDIT_COLOR, Color.rgb(255,255,255));

            //Setting Intent Data
            mImageView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            RecyclerImagesNoteAdapter adapter = new RecyclerImagesNoteAdapter(getListImages(idNote));
            mImageView.setAdapter(adapter);

            mTitle.setText(titleNote);
            mContent.setText(contentNote);

            if (backgroundImage != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(backgroundImage,0,backgroundImage.length);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                mMainContainerEditNote.setBackground(ob);
                mEditColor.setBackgroundColor(colorNote);
                if (colorNote == Color.rgb(255,255,255)){
                    mEditColor.setBackground(null);
                }
            } else {
                mMainContainerEditNote.setBackgroundColor(colorNote);
                mEditColor.setBackground(null);
            }
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<Bitmap> getListImages(int idNote) {
        ArrayList<Bitmap> list = new ArrayList<>();

        if (idNote != 0){
            Cursor cursor = myDB.readNoteImage(idNote);
            if(cursor.getCount() != 0 ){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (cursor.moveToNext()){
                            if (cursor.getCount() != list.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap bitmap = null;
                                        byte[] blob = cursor.getBlob(1);
                                        if (blob != null) {
                                            bitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
                                        }
                                        list.add(bitmap);
                                    }
                                });

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }}
                    }
                }).start();
            }
        }
        return list;
    }
}
