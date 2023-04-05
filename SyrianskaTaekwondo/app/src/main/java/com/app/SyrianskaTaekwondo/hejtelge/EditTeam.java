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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Team_list_edit_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
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

public class EditTeam extends MasterActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public static List<UserList> arr = new ArrayList<>();
    private static String path;
    public final int REQUEST_IMAGE = 100;
    SwitchCompat startchat;
    private String msg = "Du är inte behörig för denna åtgärd";
    private RecyclerView participate_list;
    private AppCompatImageView titleImg, profile;
    private AppCompatEditText edit_title;
    private AppCompatTextView add_user;
    private RelativeLayout rllImg;
    private Teamlist data;
    public View view1,view2;
    private AppCompatTextView groupDeleteBTN, add_invite;
    private String imagepath = "", profileBase64 = "", role_id,adminRolId;
    private Team_list_edit_Adapter libraryHotAdapter;
    private String teamid, userid, teamname, teamcoachid,admin;
    private CommonMethods cmn;
    private long mLastClickTime = 0;

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

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);

        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);
        loadID();
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(EditTeam.this, "id", "");
        // role = cmn.getPrefsData(context, "role", "");
        role_id = cmn.getPrefsData(context, "role_id", "");
        adminRolId = cmn.getPrefsData(context, "adminRolId", "");
        Log.d("dfajskljlfjalfsk",adminRolId);

              if (adminRolId.equals("2") || adminRolId.equals("5")){
                  add_invite.setVisibility(View.GONE);
              }

        if (getIntent() != null && getIntent().hasExtra("data")) {
            data = (Teamlist) getObject(getIntent().getStringExtra("data"), Teamlist.class);
            teamid = getIntent().getStringExtra("id");
            teamname = getIntent().getStringExtra("name");
            teamcoachid = getIntent().getStringExtra("coachid");
            String status = data.getChatstarted();

            if (status.equals("True")) {
                startchat.setChecked(true);
            } else {
                startchat.setChecked(false);
            }

            arr = data.getUsers();


        }


        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        libraryHotAdapter = new Team_list_edit_Adapter(
                arr, EditTeam.this, userid, teamcoachid, teamname, "3", teamid, profile, add_user, add_invite,
                edit_title, startchat, groupDeleteBTN,titleImg,rllImg,view1,view2);
        participate_list.setLayoutManager(new GridLayoutManager(EditTeam.this, 1));
        participate_list.setAdapter(libraryHotAdapter);
        imagepath = data.getLogo();

        //edit_title.setEnabled(false);
        edit_title.setText(data.getName());
        Glide.with(EditTeam.this)
                .load(imagepath)
                .centerCrop()
                .placeholder(R.drawable.bnr_img)
                .into(profile);
        // title.setOnClickListener(view -> edit_title.setEnabled(true));
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions(EditTeam.this)) {
                    chooseImage(EditTeam.this);
                }
            }
        });


        // onProfileImageClick());
        add_user.setOnClickListener(view -> {
            startActivity(new Intent(context, TeamUser.class).putExtra("id", teamid).putExtra("name", teamname).putExtra(DATA, Common.INSTANCE.getJSON(data)));
        });
        add_invite.setOnClickListener(view -> {
            Intent i = new Intent(context, InviteActivity.class);
            i.putExtra("Teamid", teamid);

            context.startActivity(i);
           // context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //startActivity(new Intent(context, TeamInviteUser.class).putExtra("id", teamid).putExtra("name", teamname).putExtra(DATA, Common.INSTANCE.getJSON(data)));
        });


    }

    // Handled permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(EditTeam.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(EditTeam.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(EditTeam.this);
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
                    ImagePicker.create(EditTeam.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        libraryHotAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Team_ListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, Team_ListActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                //   finish();
                return true;
            case R.id.save:

                String name = Objects.requireNonNull(edit_title.getText()).toString();
                if (cmn.isOnline(context)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return true;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (name.length() > 0) {
                        UpdateGroupAPI(name, teamid);
                    } else {
                        cmn.showAlert("Vänligen ange namn", this);
                        //showToast("Vänligen ange namn");
                    }
                } else {
                    showToast(getResources().getString(R.string.internet));

                }
                //  } else {

                //}

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);
        return true;
    }

    private void addStartChatAPI(String groupid, String status, String teamid) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("group_id", "");
            object.put("user_id", userid);
            object.put("Status", status);
            object.put("team_id", teamid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    if (startchat.isChecked()) {
/*
                        startActivity(new Intent(context, GroupChatActivity.class)
                                .putExtra("data", Common.INSTANCE.getJSON(data))
                                .putExtra("id", teamid)
                                .putExtra("name", teamname)
                                .putExtra("coachid", teamcoachid)
                                .putExtra("isopen", "true")
                                .putExtra("name", data.getName())
                                .putExtra("image", data.getLogo())
                                .putExtra("chatfor", "team")
                                .putExtra("backValue", "2")
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
*/
                        // finish();
                    }


                } else {
                    Toast.makeText(this, "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "StartGroupChat");
    }

    public void loadID() {
        participate_list = findViewById(R.id.list_edit);
        titleImg = findViewById(R.id.titleImg);
        edit_title = findViewById(R.id.title);
        rllImg = findViewById(R.id.rllImg);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        profile = findViewById(R.id.profile);
        add_user = findViewById(R.id.add_user);
        groupDeleteBTN = findViewById(R.id.btn_update);
        add_invite = findViewById(R.id.add_invite);
        startchat = findViewById(R.id.startchat);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                addStartChatAPI("", "1", teamid);
            } else {
                addStartChatAPI("", "0", teamid);

            }
        };
        startchat.setOnTouchListener((v, event) -> {

            startchat.setOnCheckedChangeListener(onCheckedChangeListener);

            return false;
        });
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
                option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(Uri.fromFile(compressedImageFile), destinationUri).withAspectRatio(1f, 1f).withOptions(option).start(this);

            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
               // Uri uri = data.getParcelableExtra("path");
             //   Log.e("imagepath1", String.valueOf(uri));
                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));
                imagepath = String.valueOf(finalFile);
                Glide.with(EditTeam.this).load(imagepath).centerCrop().into(profile);


            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();
            Glide.with(EditTeam.this).load(imagepath).into(profile);
           // Glide.with(EditTeam.this).load(imagepath).into(profile);


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void UpdateGroupAPI(String name, String team_id) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        if (imagepath != null) {
            if (imagepath.length() > 0) {
                File file = new File(imagepath);

                int length = (int) file.length();
//            txt_doc.setText(file.getName());
//            ff_doc.setVisibility(View.VISIBLE);

                byte[] bytes = new byte[length];
                try {
                    //fileInputStream.read(b);
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        fileInputStream.read(bytes);
                    }
                    profileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

                    // upload.setText(namefile);
                } catch (Exception ignored) {

                }
            }
        }

        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("name", name);
            object.put("user_id", userid);
            object.put("coach_id", teamcoachid);
            object.put("team_id", team_id);
            object.put("file", profileBase64);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }
//{"access_key":"f76646abb2bb5408ecc6d8e36b64c9d8","name":"LD12","user_id":"461","coach_id":"461","team_id":"145","file":""}
        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());
                    String teamname = object.getString("name");
                    String id = object.getString("id");
                    cmn.setPrefsData(context, "team_name", teamname);
                    cmn.setPrefsData(context, "team_id", id);
                    startActivity(new Intent(context, Team_ListActivity.class));
                    finish();
                } else {
                    cmn.showAlert(result.getMessage(), context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "edit_team");
    }

}
