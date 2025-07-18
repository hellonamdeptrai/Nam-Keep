package com.example.namkeep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.namkeep.Adapter.IClickAddLabel;
import com.example.namkeep.Adapter.IClickChecked;
import com.example.namkeep.Adapter.IClickDeleteCheckBox;
import com.example.namkeep.Adapter.ITextWatcherCheckBox;
import com.example.namkeep.Adapter.RecyclerCheckBoxNoteAdapter;
import com.example.namkeep.Adapter.RecyclerImagesAddNoteAdapter;
import com.example.namkeep.Adapter.RecyclerLabelDialogAdapter;
import com.example.namkeep.Adapter.RecyclerLabelNoteAdapter;
import com.example.namkeep.object.CheckBoxContentNote;
import com.example.namkeep.object.Label;
import com.example.namkeep.ui.home.Helper.MyItemTouchHelperCallback;
import com.example.namkeep.ui.home.Helper.OnStartDangListener;
import com.example.namkeep.ui.label.Adapter.ILabelClick;
import com.example.namkeep.ui.label.Adapter.LabelAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTitle, mContent;
    private Uri imagePath;
    private Bitmap imageToStore, imageBackground;
    private CoordinatorLayout mMainAddNote;
    private RoundedImageView mRoundedImageColor;
    private Button addCheckBox;
    ItemTouchHelper itemTouchHelper;

    DatabaseHelper myDB;
    List<Bitmap> listBitmap = new ArrayList<>();
    List<CheckBoxContentNote> listCheckBox = new ArrayList<>();
    List<Label> listLabelNote = new ArrayList<>();

    private int colorNote = Color.rgb(255,255,255);
    private int isCheckBoxOrContent = 0;
    private boolean isSaveData = true;

    private static final int PICK_IMAGE_REQUEST = 1;

    public static boolean isOpenCheckBox = false;
    public static boolean isOpenAddImage = false;

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
        mRoundedImageColor = findViewById(R.id.color_background_imaged);

        findViewById(R.id.bottom_app_bar_add_note).setBackground(null);

        Button mSheetAddButton = findViewById(R.id.sheet_add_note_button);
        Button mSheetColorButton = findViewById(R.id.sheet_color_note_button);
        Button mSheetThreeDotsNoteButton = findViewById(R.id.sheet_three_dots_note_button);

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
                        openImageContentProvider();
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddNoteActivity.this);
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
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AddNoteActivity.this);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.layout_bottom_sheet_three_dots_note,null
                        );

                bottomSheetView.findViewById(R.id.delete_add_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isSaveData = false;
                        finish();
                    }
                });

                bottomSheetView.findViewById(R.id.add_category_note).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(AddNoteActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.layout_dialog_categories);

                        Window window = dialog.getWindow();
                        if (window == null) {
                            return;
                        }

                        WindowManager.LayoutParams windowAttributes = window.getAttributes();
                        windowAttributes.gravity = Gravity.CENTER;
                        window.setAttributes(windowAttributes);
                        dialog.setCancelable(true);

                        RecyclerView recyclerViewLabel = dialog.findViewById(R.id.main_label_dialog);
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
                        recyclerViewLabel.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                        RecyclerLabelDialogAdapter adapter = new RecyclerLabelDialogAdapter(list, new IClickAddLabel() {
                            @Override
                            public void ClickAddLabel(int idLabel, String contentLabel) {
                                boolean isAddLabel = true;
                                for (int i = 0; i < listLabelNote.size(); i++) {
                                    if (listLabelNote.get(i).getId() == idLabel){
                                        listLabelNote.remove(i);
                                        isAddLabel = false;
                                    }
                                }
                                if (isAddLabel) {
                                    listLabelNote.add(new Label(idLabel, contentLabel, 0));
                                }
                                RecyclerView recyclerViewLabelNote = findViewById(R.id.main_categories_note);
                                recyclerViewLabelNote.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
                                RecyclerLabelNoteAdapter adapter1 = new RecyclerLabelNoteAdapter(listLabelNote);
                                recyclerViewLabelNote.setAdapter(adapter1);
                            }
                        });
                        recyclerViewLabel.setAdapter(adapter);

                        dialog.show();
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        if (isOpenCheckBox){
            changeTextToCheckbox();
            isOpenCheckBox = false;
        }

        if (isOpenAddImage){
            openImageContentProvider();
            isOpenAddImage = false;
        }
    }

    private void openImageContentProvider() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void changeTextToCheckbox() {
        String[] arr = mContent.getText().toString().split("\n");
        addCheckBox = findViewById(R.id.bottom_add_check_box);

        if (isCheckBoxOrContent == 0) {
            for (int i = 0; i < arr.length; i++) {
                listCheckBox.add(new CheckBoxContentNote(arr[i],false));
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
        RecyclerView mainCheckBoxNote = findViewById(R.id.main_checkbox_note);

        mainCheckBoxNote.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        mainCheckBoxNote.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mainCheckBoxNote);
    }

    private void checkBackgroundColorOrImage(int color) {
        if (imageBackground != null) {
            if(colorNote == Color.rgb(255,255,255)) {
                mRoundedImageColor.setBackground(null);
            } else {
                mRoundedImageColor.setBackgroundColor(color);
            }
        }else {
            mRoundedImageColor.setBackground(null);
            mMainAddNote.setBackgroundColor(color);
        }
    }

    private void bottomActionColorImage(View bottomSheetView) {
        bottomSheetView.findViewById(R.id.color_note_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorNote = Color.rgb(255,255,255);

                if (imageBackground == null) {
                    mRoundedImageColor.setBackground(null);
                    mMainAddNote.setBackground(null);
                } else {
                    mRoundedImageColor.setBackground(null);
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
                Drawable drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.gg1);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainAddNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.gg2);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainAddNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.gg3);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainAddNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.gg4);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainAddNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
        bottomSheetView.findViewById(R.id.image_note_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = ContextCompat.getDrawable(AddNoteActivity.this, R.drawable.gg5);
                imageBackground = ((BitmapDrawable)drawable).getBitmap();
                mMainAddNote.setBackground(drawable);
                checkBackgroundColorOrImage(colorNote);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSaveData){
            if (isCheckBoxOrContent == 1){
                editTextAddIdCheckBox();
            }
            myDB.addNote(mTitle.getText().toString(), mContent.getText().toString(), isCheckBoxOrContent, colorNote, imageBackground, 1);

            int idNewNote = myDB.getNoteIdNew();
            for (Bitmap bitmap : listBitmap) {
                myDB.addImage(bitmap, idNewNote);
            }
            for (Label label: listLabelNote) {
                myDB.updateLabelIdNote(label.getId()+"", idNewNote);
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
//                BitmapDrawable ob = new BitmapDrawable(getResources(), imageToStore);
//                mMainAddNote.setBackground(ob);
                listBitmap.add(imageToStore);
                addListImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addListImage() {
        RecyclerView mainImagesNote = findViewById(R.id.main_images_note);
        mainImagesNote.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        RecyclerImagesAddNoteAdapter adapter = new RecyclerImagesAddNoteAdapter(this, listBitmap, new IClickDeleteCheckBox() {
            @Override
            public void onClickDeleteItem(int position) {
                listBitmap.remove(position);
                addListImage();
            }
        });
        mainImagesNote.setAdapter(adapter);
    }

}