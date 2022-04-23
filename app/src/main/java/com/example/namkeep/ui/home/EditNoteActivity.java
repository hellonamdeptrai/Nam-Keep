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

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.namkeep.Adapter.IClickChecked;
import com.example.namkeep.Adapter.IClickDeleteCheckBox;
import com.example.namkeep.Adapter.ITextWatcherCheckBox;
import com.example.namkeep.Adapter.RecyclerCheckBoxNoteAdapter;
import com.example.namkeep.Adapter.RecyclerCheckBoxNoteHomeAdapter;
import com.example.namkeep.Adapter.RecyclerImagesAddNoteAdapter;
import com.example.namkeep.Adapter.RecyclerImagesEditNoteAdapter;
import com.example.namkeep.Adapter.RecyclerImagesNoteAdapter;
import com.example.namkeep.AddNoteActivity;
import com.example.namkeep.DatabaseHelper;
import com.example.namkeep.MainActivity;
import com.example.namkeep.R;
import com.example.namkeep.object.CheckBoxContentNote;
import com.example.namkeep.object.Image;
import com.example.namkeep.ui.home.Helper.MyItemTouchHelperCallback;
import com.example.namkeep.ui.home.Helper.OnStartDangListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public static final String VIEW_NAME_LIST_CHECKBOX = "edit:list_checkbox";
    public static final String VIEW_NAME_IS_CHECKBOX = "edit:is_checkbox";
    public static final String VIEW_NAME_BACKGROUND = "edit:background";
    public static final String VIEW_NAME_EDIT_COLOR = "edit:color";

    private RecyclerView mImageView, mMainCheckboxNote;
    private TextView mTitle, mContent;
    private RoundedImageView mEditColor;
    private CoordinatorLayout mMainContainerEditNote;
    private Button addCheckBox;
    ItemTouchHelper itemTouchHelper;

    private DatabaseHelper myDB;

    private Toolbar mToolbar;

    String titleNote, contentNote;
    int idNote, colorNote, isContentOrCheckbox;
    byte[] backgroundImage;

    private ArrayList<Bitmap> listBitmap;
    private ArrayList<Image> listImage;

    List<CheckBoxContentNote> listCheckBox = new ArrayList<>();

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagePath;
    private Bitmap imageToStore, imageBackground;

    private int isCheckBoxOrContent = 0;
    private boolean isSaveData = true;

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
        mMainCheckboxNote = findViewById(R.id.edit_main_checkbox_note);
        mEditColor = findViewById(R.id.edit_color_background_imaged);
        mMainContainerEditNote = findViewById(R.id.main_container_edit_note);

        findViewById(R.id.bottom_app_bar_edit_note).setBackground(null);

        ViewCompat.setTransitionName(mImageView, VIEW_NAME_IMAGE);
        ViewCompat.setTransitionName(mTitle, VIEW_NAME_TITLE);
        ViewCompat.setTransitionName(mContent, VIEW_NAME_CONTENT);
        ViewCompat.setTransitionName(mMainCheckboxNote, VIEW_NAME_LIST_CHECKBOX);
        ViewCompat.setTransitionName(mMainContainerEditNote, VIEW_NAME_BACKGROUND);
        ViewCompat.setTransitionName(mEditColor, VIEW_NAME_EDIT_COLOR);

        Button mSheetAddButton = findViewById(R.id.edit_sheet_add_note_button);
        Button mSheetColorButton = findViewById(R.id.edit_sheet_color_note_button);
        Button mSheetThreeDotsNoteButton = findViewById(R.id.edit_sheet_three_dots_note_button);

        mSheetAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditNoteActivity.this);
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
                bottomSheetView.findViewById(R.id.add_checkbox_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeTextToCheckbox();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        mSheetColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditNoteActivity.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_color_note,null
                        );
                bottomActionColorImage(bottomSheetView);

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        mSheetThreeDotsNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EditNoteActivity.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_three_dots_note,null
                        );

                bottomSheetView.findViewById(R.id.delete_add_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
                        builder.setTitle("Xóa ghi chú ?");
                        builder.setMessage("Bạn có chắc chắn muốn xóa ghi chú này không ?");
                        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isSaveData = false;
                                myDB.deleteOneRow(idNote+"");
                                for (Image image: listImage) {
                                    myDB.deleteOneRowImage(image.getId()+"");
                                }
                                finish();
                            }
                        });
                        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                        nbutton.setTextColor(Color.GREEN);
                        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        pbutton.setTextColor(Color.RED);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        loadNote();
    }

    private void loadNote() {
        if(getIntent().hasExtra(VIEW_NAME_IS_CHECKBOX) && getIntent().hasExtra(VIEW_NAME_BACKGROUND) && getIntent().hasExtra(EXTRA_PARAM_ID) && getIntent().hasExtra(VIEW_NAME_TITLE) &&
                getIntent().hasExtra(VIEW_NAME_CONTENT) && getIntent().hasExtra(VIEW_NAME_EDIT_COLOR)){
            //Getting Data from Intent
            idNote = getIntent().getIntExtra(EXTRA_PARAM_ID,1);
            titleNote = getIntent().getStringExtra(VIEW_NAME_TITLE);
            contentNote = getIntent().getStringExtra(VIEW_NAME_CONTENT);
            isContentOrCheckbox = getIntent().getIntExtra(VIEW_NAME_IS_CHECKBOX,0);
            backgroundImage = getIntent().getByteArrayExtra(VIEW_NAME_BACKGROUND);
            colorNote = getIntent().getIntExtra(VIEW_NAME_EDIT_COLOR, Color.rgb(255,255,255));

            //Setting Intent Data
            getListImages(idNote);
            addListImage();

            mTitle.setText(titleNote);
            mContent.setText(contentNote);

            if (backgroundImage != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(backgroundImage,0,backgroundImage.length);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                imageBackground = bitmap;
                mMainContainerEditNote.setBackground(ob);
                mEditColor.setBackgroundColor(colorNote);
                if (colorNote == Color.rgb(255,255,255)){
                    mEditColor.setBackground(null);
                }
            } else {
                mMainContainerEditNote.setBackgroundColor(colorNote);
                mEditColor.setBackground(null);
            }

            if (isContentOrCheckbox == 1){
                changeTextToCheckbox();
            }
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeTextToCheckbox() {
        String[] arr = mContent.getText().toString().split("\n");
        addCheckBox = findViewById(R.id.edit_bottom_add_check_box);

        if (isCheckBoxOrContent == 0) {
            for (int i = 0; i < arr.length; i++) {
                boolean isId = false;
                String contentCheckBox = arr[i];
                if (arr[i].startsWith("!!$")) {
                    isId = true;
                    contentCheckBox = arr[i].substring(3);
                }
                listCheckBox.add(new CheckBoxContentNote(contentCheckBox,isId));
            }
            mContent.setVisibility(View.GONE);
            addCheckBox.setVisibility(View.VISIBLE);
            isCheckBoxOrContent = 1;
        } else {
            endCheckBoxList();
        }
        addListCheckBox(listCheckBox);
        addCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextDeleteIdCheckBox();
                listCheckBox.add(new CheckBoxContentNote());
                addListCheckBox(listCheckBox);
            }
        });
    }

    private void endCheckBoxList(){
        String data = "";
        editTextDeleteIdCheckBox();
        for (int i = 0; i < listCheckBox.size(); i++) {
            String row = "";
            row = row + listCheckBox.get(i).getContent() + "\n";
            data += row;
        }
        mContent.setVisibility(View.VISIBLE);
        mContent.setText(data);
        addCheckBox.setVisibility(View.GONE);
        listCheckBox = new ArrayList<>();
        isCheckBoxOrContent = 0;
    }

    private void editTextDeleteIdCheckBox() {
        for (int i = 0; i < listCheckBox.size(); i++) {
            if (listCheckBox.get(i).getContent().startsWith("!!$")) {
                listCheckBox.get(i).setContent(listCheckBox.get(i).getContent().substring(3));
                listCheckBox.get(i).setCheckBox(true);
            }
        }
    }

    private void editTextAddIdCheckBox() {
        editTextDeleteIdCheckBox();
        for (int i = 0; i < listCheckBox.size(); i++) {
            if (listCheckBox.get(i).isCheckBox()) {
                listCheckBox.get(i).setContent("!!$"+listCheckBox.get(i).getContent());
            }
        }
        String data = "";
        for (int i = 0; i < listCheckBox.size(); i++) {
            String row = "";
            row = row + listCheckBox.get(i).getContent() + "\n";
            data += row;
        }
        mContent.setText(data);
    }

    private void addListCheckBox(List<CheckBoxContentNote> list) {
        mMainCheckboxNote.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerCheckBoxNoteAdapter adapter = new RecyclerCheckBoxNoteAdapter( list, new IClickDeleteCheckBox() {
            @Override
            public void onClickDeleteItem(int position) {
                listCheckBox.remove(position);
                addListCheckBox(listCheckBox);
                if (listCheckBox.size() == 0) {
                    endCheckBoxList();
                }
            }
        }, new ITextWatcherCheckBox() {
            @Override
            public void TextWatcherItem(int position, String text) {
                listCheckBox.get(position).setContent(text);
            }
        }, new OnStartDangListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        }, new IClickChecked() {
            @Override
            public void ClickChecked(int position, EditText editText) {

                if (listCheckBox.get(position).getContent().startsWith("!!$")) {
                    listCheckBox.get(position).setContent(listCheckBox.get(position).getContent().substring(3));
                    listCheckBox.get(position).setCheckBox(false);
                    editText.setEnabled(true);
                } else {
                    listCheckBox.get(position).setContent("!!$"+editText.getText().toString());
                    listCheckBox.get(position).setCheckBox(true);
                    editText.setEnabled(false);
                }

            }
        });
        mMainCheckboxNote.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mMainCheckboxNote);
    }

    private void checkBackgroundColorOrImage(int color) {
        if (imageBackground != null) {
            if(colorNote == Color.rgb(255,255,255)) {
                mEditColor.setBackground(null);
            } else {
                mEditColor.setBackgroundColor(color);
            }
        }else {
            mEditColor.setBackground(null);
            mMainContainerEditNote.setBackgroundColor(color);
        }
    }

    private void bottomActionColorImage(View bottomSheetView) {
        bottomSheetView.findViewById(R.id.color_note_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(255,255,255);

                if (imageBackground == null) {
                    mEditColor.setBackground(null);
                    mMainContainerEditNote.setBackground(null);
                } else {
                    mEditColor.setBackground(null);
                }
            }
        });
        bottomSheetView.findViewById(R.id.color_note_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(250,175,168);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.color_note_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(243,159,118);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.color_note_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(255,248,184);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.color_note_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(226,246,211);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.color_note_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(180,221,221);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.color_note_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(212,228,237);
                checkBackgroundColorOrImage(colorNote);
            }
        });

//        image background
        bottomSheetView.findViewById(R.id.image_note_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBackgroundColorOrImage(colorNote);
                imageBackground = null;
                checkBackgroundColorOrImage(colorNote);

            }
        });
        bottomSheetView.findViewById(R.id.image_note_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(EditNoteActivity.this, R.drawable.gg1);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainContainerEditNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(EditNoteActivity.this, R.drawable.gg2);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainContainerEditNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(EditNoteActivity.this, R.drawable.gg3);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainContainerEditNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(EditNoteActivity.this, R.drawable.gg4);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainContainerEditNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(EditNoteActivity.this, R.drawable.gg5);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainContainerEditNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
    }

    private void getListImages(int idNote) {
        listBitmap = new ArrayList<>();
        listImage = new ArrayList<>();

        if (idNote != 0){
            Cursor cursor = myDB.readNoteImage(idNote);
            if(cursor.getCount() != 0 ){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (cursor.moveToNext()){
                            if (cursor.getCount() != listBitmap.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap bitmap = null;
                                        byte[] blob = cursor.getBlob(1);
                                        if (blob != null) {
                                            bitmap = BitmapFactory.decodeByteArray(blob,0,blob.length);
                                        }
                                        listBitmap.add(bitmap);
                                        listImage.add(new Image(Integer.parseInt(cursor.getString(0)), bitmap, Integer.parseInt(cursor.getString(2))));
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imagePath = data.getData();
            try {
                imageToStore = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                listBitmap.add(imageToStore);
                addListImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addListImage() {
        mImageView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        RecyclerImagesAddNoteAdapter adapter = new RecyclerImagesAddNoteAdapter(this, listBitmap, new IClickDeleteCheckBox() {
            @Override
            public void onClickDeleteItem(int position) {
                listBitmap.remove(position);
                addListImage();
            }
        });
        mImageView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSaveData){
            if (isCheckBoxOrContent == 1){
                editTextAddIdCheckBox();
            }
            myDB.updateData(idNote+"" ,mTitle.getText().toString(), mContent.getText().toString(), isCheckBoxOrContent, colorNote, imageBackground, 1);

            if (listImage.size() > listBitmap.size()) {
                for (int i = listImage.size()-listBitmap.size()-1; i >= 0 ; i--) {
                    myDB.deleteOneRowImage(listImage.get(i).getId()+"");
                }
            }

            int i = 0;
            for (Bitmap bitmap : listBitmap) {
                if (listImage.size() > i){
                    myDB.updateImage(listImage.get(i).getId()+"", bitmap);
                } else {
                    myDB.addImage(bitmap, idNote);
                }
                i++;
            }
        }
    }
}
