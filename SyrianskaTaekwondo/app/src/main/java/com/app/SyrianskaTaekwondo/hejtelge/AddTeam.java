package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Groupuser_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAddTeamBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Team;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;

public class AddTeam extends AppCompatActivity {
    private String msg, status, imagepath = "", flag = "", profileBase64 = "", userid = "", blockflag = "";
    private CommonMethods cmn;
    private ArrayList<String> arr_user = new ArrayList<>();
    private ActivityAddTeamBinding binding;
    public final int REQUEST_IMAGE = 100;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTeamBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        cmn = new CommonMethods();
        if (getIntent() != null) {
            flag = getIntent().getStringExtra("flag");
        }
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_back);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("               Skapa team");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Groupuser_Adapter user = new Groupuser_Adapter(
                GroupActivity.arr_user_participate, AddTeam.this);
        binding.sizeParticipate.setText("Deltagare: " + GroupActivity.arr_user_participate.size());
        userid = cmn.getPrefsData(AddTeam.this, "id", "");
        blockflag = cmn.getPrefsData(AddTeam.this, "blockflag", "");

        binding.userGroup.setLayoutManager(new LinearLayoutManager(AddTeam.this, LinearLayoutManager.HORIZONTAL, false));
        binding.userGroup.setAdapter(user);
        if (GroupActivity.arr_user_participate.size() > 0) {
            for (int i = 0; i < GroupActivity.arr_user_participate.size(); i++) {
                String id = GroupActivity.arr_user_participate.get(i).getId();
                arr_user.add(id);
            }
        } else {
            binding.sizeParticipate.setVisibility(View.GONE);
        }
        binding.profile.setOnClickListener(view -> {


            if (checkAndRequestPermissions(AddTeam.this)) {
                chooseImage(AddTeam.this);
            }
            //    ImagePicker.create(this).theme(R.style.AppTheme_No).single().showCamera(true).toolbarArrowColor(Color.BLACK).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Beskära").start();
        });
        binding.add.setOnClickListener(view -> {
            String name = Objects.requireNonNull(binding.groupText.getText()).toString();
            if (name.length() > 0) {
                if (cmn.isOnline(AddTeam.this)) {
                    create_team(name);
                } else {
                    Toast.makeText(AddTeam.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

                }
            } else {
                cmn.showAlert("Ange team namn",this);
               // Toast.makeText(AddTeam.this, "Ange team namn", Toast.LENGTH_SHORT).show();
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
                if (ContextCompat.checkSelfPermission(AddTeam.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(AddTeam.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(AddTeam.this);
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
                    ImagePicker.create(AddTeam.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Glide.with(AddTeam.this).load(R.drawable.camera).into(binding.profile);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            if (images.size() > 0) {
                Image dataimg = images.get(0);
                String path = dataimg.getPath();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
                //      bitmap = Bitmap.createScaledBitmap(bitmap,1000,1000,true);
                bitmap = cmn.getResizedBitmap(bitmap, 800);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

                Uri uri = cmn.getImageUri(this, bitmap);

                UCrop.Options option = new UCrop.Options();
                option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(AddTeam.this, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(AddTeam.this, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(AddTeam.this, R.color.white));
                option.setCropGridColumnCount(0);
                option.setCropGridRowCount(0);
                option.setShowCropFrame(false);
                option.setCircleDimmedLayer(true);
                Uri destinationUri = Uri.fromFile(new File(Objects.requireNonNull(getExternalFilesDir(null)).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(uri, destinationUri).withAspectRatio(2f, 1.5f).withOptions(option).start(this);

                Glide.with(AddTeam.this)
                        .load(imagepath)
                        .into(binding.profile);
            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {


                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                // binding.sampleImage.setImageBitmap(selectedImage);

                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));
                 imagepath = String.valueOf(finalFile);
                // File path = compressFile.getCompressedImageFile(new File(imagepath1), CreateSponsers.this);
//                CompressFile compressFile = new CompressFile();
//                assert imagepath1 != null;
//                File path = compressFile.getCompressedImageFile(new File(imagepath1), AddTeam.this);
                //imagepath = path.getPath();

                Glide.with(AddTeam.this).load(imagepath).into(binding.profile);
                // imagepath = resultUri.getPath();

            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();


//            CompressFile compressFile = new CompressFile();
//            File path = compressFile.getCompressedImageFile(new File(imagepath), AddTeam.this);
//            Glide.with(AddTeam.this).load(path).into(binding.profile);
            // imagepath = path.getPath();
            Glide.with(AddTeam.this)
                    .load(imagepath)
                    .into(binding.profile);


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            assert cropError != null;
            cropError.printStackTrace();
        }
           /* if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Glide.with(CreateGroup.this)
                            .load(result.getUri())
                            .into(profile);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }*/
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {


        return true;
    }

    private void create_team(String name) {
        ProgressDialog mprogdialog = ProgressDialog.show(AddTeam.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);
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

        Gson gson = new Gson();
        Team asgn = new Team();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.name = name;
        asgn.members = arr_user;
        asgn.file = profileBase64;
        asgn.coach_id = userid;
        asgn.user_id = userid;


        String tset = gson.toJson(asgn);
        Log.d("dsalfkjlfsa",tset);
        String url = ConsURL.BASE_URL_TEST + "add_team";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity"
        ).url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                       Toast.makeText(AddTeam.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                mprogdialog.dismiss();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        msg = objvalue.optString("message");

                        if (status.equals("false")) {

                        } else {

                            JSONObject name = objvalue.getJSONObject("data");
                            String teamname = name.getString("name");
                            String teamid = name.getString("id");
                            if (status.equals("true") && flag.length() > 0) {
                                GroupActivity.arr_user_participate.clear();
                                cmn.setPrefsData(AddTeam.this, "team_id", teamid);
                                cmn.setPrefsData(AddTeam.this, "team_name", teamname);

                            }  //  responseText = s1;

                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {

                            //Toast.makeText(AddTeam.this, "Skapat", Toast.LENGTH_SHORT).show();
                            GroupActivity.arr_user_participate.clear();
                            if (flag.length() == 0) {
                                startActivity(new Intent(AddTeam.this, Team_ListActivity.class));
                            } else {
                                String is_loggedIn = cmn.getPrefsData(AddTeam.this, "is_loggedIn", "");

                                if (!is_loggedIn.equals("null") && (blockflag.equals("false"))) {
                                    Common.INSTANCE.saveString("is_team", "true");
                                    startActivity(new Intent(AddTeam.this, HomePage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else if (blockflag.equals("true")) {
                                    Common.INSTANCE.saveString("is_team", "true");

                                    startActivity(new Intent(AddTeam.this, ResetPassword.class).putExtra("flag", "1").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else {
                                    Common.INSTANCE.saveString("is_team", "true");
                                    Common.INSTANCE.saveString("is_block", "true");

                                    startActivity(new Intent(AddTeam.this, EditProfile.class).putExtra("flag", "1").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                }

                            }
                            finish();

                            mprogdialog.dismiss();
                        } else {
                            mprogdialog.dismiss();

                            cmn.showAlert(msg, AddTeam.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }


}
