package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Group_list_edit_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import id.zelory.compressor.Compressor;
import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;

public class EditGroup extends MasterActivity {
    private RecyclerView participate_list;
    public static List<UserList> arr = new ArrayList<>();
    private AppCompatImageView title, profile;
    private AppCompatEditText edit_title;
    private AppCompatTextView add_user, add_invite;
    private GroupList data;
    RelativeLayout rllImg;
    private AppCompatTextView groupDeleteBTN;
    SwitchCompat startchat;
    private String imagepath = "", profileBase64 = "";
    private Group_list_edit_Adapter libraryHotAdapter;
    private String groupid, userid, groupname;
    private CommonMethods cmn;
    private long mLastClickTime = 0;
    private String team_id = "";
    public final int REQUEST_IMAGE = 100;
    private AlertDialog alertDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String path;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, GroupListActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            // finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            String name = Objects.requireNonNull(edit_title.getText()).toString();
            if (cmn.isOnline(EditGroup.this)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return true;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                alert(name, team_id);

            } else {
                Toast.makeText(EditGroup.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
    }

    private void addStartChatAPI(String groupid, String status) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("group_id", groupid);
            object.put("user_id", userid);
            object.put("Status", status);
            object.put("team_id","");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    if (startchat.isChecked()) {

                        cmn.customChatStartMsg(this, result.getMessage());

                    }else {
                        cmn.customChatStartMsg(this, "Chatten har avaktiverats");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "StartGroupChat");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);
        loadID();
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(EditGroup.this, "id", "");
        team_id = cmn.getPrefsData(EditGroup.this, "team_id", "");


        if (getIntent() != null && getIntent().hasExtra("data")) {
            data = (GroupList) getObject(getIntent().getStringExtra("data"), GroupList.class);
            groupid = getIntent().getStringExtra("id");
            groupname = getIntent().getStringExtra("name");
            String status = data.getChatstarted();

            arr = data.getUsers();

            if (status.equals("True")) {
                startchat.setChecked(true);
            } else {
                startchat.setChecked(false);

            }

        }
        imagepath = data.getImage();

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        libraryHotAdapter = new Group_list_edit_Adapter(
                arr, EditGroup.this, userid, groupid);
        participate_list.setLayoutManager(new GridLayoutManager(EditGroup.this, 1));
        participate_list.setAdapter(libraryHotAdapter);


        // edit_title.setEnabled(false);
        edit_title.setText(data.getName());
        title.setOnClickListener(view ->
                edit_title.setCursorVisible(true));
        edit_title.setFocusable(true);
        Glide.with(EditGroup.this)
                .load(imagepath)
                .fitCenter()
                .placeholder(R.drawable.camera)
                .into(profile);

        add_user.setOnClickListener(view -> {
            startActivity(new Intent(EditGroup.this, GroupUser.class).putExtra("id", groupid));
        });
        profile.setOnClickListener(view -> {
            if (checkAndRequestPermissions(EditGroup.this)) {
                chooseImage(EditGroup.this);
            }

        });
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(EditGroup.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(EditGroup.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(EditGroup.this);
                }
                break;
        }
    }

    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Ta en bild", "Välj från galleriet"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Ta en bild")) {
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, REQUEST_IMAGE);
                } else if (optionsMenu[i].equals("Välj från galleriet")) {
                    // choose from  external storage
                    ImagePicker.create(EditGroup.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        Log.e("path", path);

        return Uri.parse(path);
    }

    public void loadID() {

        participate_list = findViewById(R.id.list_edit);
        title = findViewById(R.id.edit_title);
        rllImg = findViewById(R.id.rllImg);
        edit_title = findViewById(R.id.title);
        profile = findViewById(R.id.profile);
        add_user = findViewById(R.id.add_user);
        add_invite = findViewById(R.id.add_invite);
        groupDeleteBTN = findViewById(R.id.btn_update);
        startchat = findViewById(R.id.startchat);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                addStartChatAPI(groupid, "1");
            } else {
                addStartChatAPI(groupid, "0");

            }
        };
        startchat.setOnTouchListener((v, event) -> {

            startchat.setOnCheckedChangeListener(onCheckedChangeListener);

            return false;
        });

        groupDeleteBTN.setOnClickListener(view ->
                showCustomDeleteGroupDialog(groupname, groupid));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            if (images.size() > 0) {
                Image dataimg = images.get(0);
                String path = dataimg.getPath();
                File compressedImageFile = Compressor.getDefault(this).compressToFile(new File(path));

                UCrop.Options option = new UCrop.Options();
                option.setToolbarTitle("             Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(Uri.fromFile(compressedImageFile), destinationUri).withAspectRatio(1f, 1f).withOptions(option).start(this);

            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                //  Uri uri = data.getParcelableExtra("path");
                // Log.e("imagepath1", String.valueOf(uri));
                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));
                imagepath = String.valueOf(finalFile);
                Glide.with(EditGroup.this).load(imagepath).into(profile);


            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();
            Glide.with(EditGroup.this).load(imagepath).into(profile);
            Glide.with(EditGroup.this)
                    .load(imagepath)
                    .into(profile);


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onResume() {
        super.onResume();
        libraryHotAdapter.notifyDataSetChanged();
    }


    private void UpdateGroupAPI(String name, String team_id) {
        //arr.clear();
        String requestData;

        if (imagepath != null) {
            if (imagepath.length() > 0) {
                File file = new File(imagepath);

                int length = (int) file.length();
//            txt_doc.setText(file.getName());
//            ff_doc.setVisibility(View.VISIBLE);

                byte[] bytes = new byte[length];
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    //fileInputStream.read(b);
                    try {
                        fileInputStream.read(bytes);
                    } finally {
                        fileInputStream.close();
                    }
                    profileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                    // upload.setText(namefile);
                } catch (Exception ignored) {

                }
            }
        }
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );

        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("group_id", groupid);
            object.put("name", name);
            object.put("user_id", userid);
            object.put("team_id", team_id);
            object.put("file", profileBase64);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(EditGroup.this, result -> {
            try {
                if (result.isStatus()) {

                    showCustomDialog();
                } else {
                    cmn.showAlert(result.getMessage(), EditGroup.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_group");
    }

    private void alert(String name, String team_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditGroup.this);
        builder.setCancelable(false);
        //   builder.setMessage("Är du säker på att du uppdaterar den här gruppen?");
        builder.setMessage("Är du säker på att du vill uppdatera?");
        builder.setPositiveButton("Ja", (dialogInterface, which) -> {

            if (new CommonMethods().isOnline(EditGroup.this)) {
                UpdateGroupAPI(name, team_id);
            } else {
                Toast.makeText(EditGroup.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, which) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    private void deleteGroupAPI(String groupid, String groupname) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("group_id", groupid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(this, result -> {
            try {
                if (result.isStatus()) {
                    this.startActivity(new Intent(this, GroupListActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_group");
    }

    public void showCustomDeleteGroupDialog(String group_name, String groupid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Vill du ta bort den '" + group_name + "' grupp?");


        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (new CommonMethods().isOnline(this)) {
                deleteGroupAPI(groupid, "");
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();

        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, GroupListActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.news_alert, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        AppCompatTextView create = dialogView.findViewById(R.id.create);
        create.setText("Gruppen uppdaterades.");
        ok.setOnClickListener(view -> {
            startActivity(new Intent(EditGroup.this, GroupListActivity.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}
