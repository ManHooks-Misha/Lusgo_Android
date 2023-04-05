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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Groupuser_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Group;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;

public class CreateGroup extends AppCompatActivity {
    private RecyclerView usergroup;
    private AppCompatTextView size_participate;
    private AppCompatEditText group_text;
    private CircleImageView profile;
    private String msg, status, imagepath = "", profileBase64 = "", userid = "";
    private CommonMethods cmn;
    private FloatingActionButton add;
    private String team_id;
    private ArrayList<String> arr_user = new ArrayList<>();
    public final int REQUEST_IMAGE = 100;
    private AlertDialog alertDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setTitle("            Skapa grupp");
        loadID();
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(CreateGroup.this, "id", "");
        team_id = cmn.getPrefsData(CreateGroup.this, "team_id", "");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Groupuser_Adapter user = new Groupuser_Adapter(
                GroupActivity.arr_user_participate, CreateGroup.this);
        size_participate.setText("Deltagare: " + GroupActivity.arr_user_participate.size());

        for (int i = 0; i < GroupActivity.arr_user_participate.size(); i++) {
            String id = GroupActivity.arr_user_participate.get(i).getId();
            arr_user.add(id);
        }
        arr_user.add(userid);
        usergroup.setLayoutManager(new LinearLayoutManager(CreateGroup.this, LinearLayoutManager.HORIZONTAL, false));
        usergroup.setAdapter(user);

        profile.setOnClickListener(view -> {
            if (checkAndRequestPermissions(CreateGroup.this)) {
                chooseImage(CreateGroup.this);
            }
        });

        add.setOnClickListener(view -> {

            String name = Objects.requireNonNull(group_text.getText()).toString();
            if (group_text.length() > 0) {
                if (cmn.isOnline(CreateGroup.this)) {
                    alert(name, team_id);
                } else {
                    Toast.makeText(CreateGroup.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

                }
            } else {
                cmn.showAlert("Ange grupptitel",this);
               // Toast.makeText(CreateGroup.this, "Ange grupptitel", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadID() {
        usergroup = findViewById(R.id.user_group);
        size_participate = findViewById(R.id.size_participate);
        profile = findViewById(R.id.profile);
        add = findViewById(R.id.add);
        group_text = findViewById(R.id.group_text);

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
                if (ContextCompat.checkSelfPermission(CreateGroup.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(CreateGroup.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(CreateGroup.this);
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
                    ImagePicker.create(CreateGroup.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("             Välj bild").toolbarDoneButtonText("Välj").start();

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
        inImage.compress(Bitmap.CompressFormat.JPEG, 0, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_test" + System.currentTimeMillis(), null);
        Log.e("path", path);

        return Uri.parse(path);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//
//            if (requestCode == REQUEST_PICK_IMAGE) {
//                imagepath = data.getStringExtra("image_path");
//                Glide.with(CreateGroup.this)
//                        .load(imagepath)
//                        .into(profile);
//
//
//            } else {
//                System.out.println("Failed to load image");
//            }

        Glide.with(CreateGroup.this)
                .load(R.drawable.camera)
                .into(profile);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);

            if (images.size() > 0) {
                Image dataimg = images.get(0);
                String path = dataimg.getPath();
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
                //      bitmap = Bitmap.createScaledBitmap(bitmap,1000,1000,true);

                try {
                    ExifInterface exif = new ExifInterface(path);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    }
                    else if (orientation == 3) {
                        matrix.postRotate(180);
                    }
                    else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                }
                catch (Exception e) {

                }
                bitmap = cmn.getResizedBitmap(bitmap, 800, 800);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);


                Uri uri =  cmn.getImageUri(this, bitmap);

                UCrop.Options option = new UCrop.Options();
                option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(CreateGroup.this, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(CreateGroup.this, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(CreateGroup.this, R.color.white));
                option.setCropGridColumnCount(0);
                option.setCropGridRowCount(0);
                option.setShowCropFrame(false);
                option.setCircleDimmedLayer(true);
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(uri, destinationUri).withAspectRatio(2f, 1.5f).withOptions(option).start(this);
                Glide.with(CreateGroup.this)
                        .load(imagepath)
                        .centerCrop()
                        .into(profile);
            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                // binding.sampleImage.setImageBitmap(selectedImage);

                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));
                String imagepath1 = String.valueOf(finalFile);
                // File path = compressFile.getCompressedImageFile(new File(imagepath1), CreateSponsers.this);
                imagepath = imagepath1;

//                CompressFile compressFile = new CompressFile();
//                assert imagepath1 != null;
//                File path = compressFile.getCompressedImageFile(new File(imagepath1), CreateGroup.this);
//                imagepath = path.getPath();

                Glide.with(CreateGroup.this).load(imagepath1).into(profile);
                // imagepath = resultUri.getPath();

            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();
//            CompressFile compressFile = new CompressFile();
//            File path = compressFile.getCompressedImageFile(new File(imagepath), CreateGroup.this);
//            Glide.with(CreateGroup.this).load(path).into(profile);
            // imagepath = path.getPath();
            Glide.with(CreateGroup.this)
                    .load(imagepath)
                    .centerCrop()
                    .into(profile);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
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


    private void create_Group(String name, String team_id) {


        ProgressDialog mprogdialog = ProgressDialog.show(CreateGroup.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);
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
                } catch (Exception e) {

                }
            }
        }

        Gson gson = new Gson();
        Group asgn = new Group();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.name = name;
        asgn.team_id = team_id;
        asgn.users = arr_user;
        asgn.file = profileBase64;

        asgn.user_id = userid;


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "add_group";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(CreateGroup.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                mprogdialog.dismiss();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res;
                    if (response.body() != null) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");
                        msg = objvalue.optString("message");
                        if (status.equals("true")) {
                            GroupActivity.arr_user_participate.clear();
                        }

                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {

                            GroupActivity.arr_user_participate.clear();
                            showCustomDialog();
                            mprogdialog.dismiss();
                        } else {
                            mprogdialog.dismiss();

                            cmn.showAlert(msg, CreateGroup.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }


    private void alert(String name, String team_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroup.this);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill skapa denna grupp?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(CreateGroup.this)) {
                create_Group(name, team_id);
            } else {
                Toast.makeText(CreateGroup.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
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
        create.setText("Gruppen skapades.");
        ok.setOnClickListener(view -> {

            startActivity(new Intent(CreateGroup.this, GroupListActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TOP));
            cmn.setPrefsData(this,"value","");
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

}
