package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.SponsersPojo;
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
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;

public class CreateSponsers extends MasterActivity {
    private AppCompatEditText txt_name, txt_link;
    private AppCompatImageView profile,sampleImage;
    private final int REQUEST_PICK_IMAGE = 1002;
    private String imagepath = "", status = "", msg = "", profileBase64 = "", userid = "";
    private LinearLayout create_sponsr;
    private long mLastClickTime = 0;
    private CommonMethods cmn;
    public final int REQUEST_IMAGE = 100;
    private AlertDialog alertDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String path;
    Uri image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sponsers);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //  getSupportActionBar().setTitle("Skapa sponsor");
        loadID();

        cmn = new CommonMethods();
        userid = cmn.getPrefsData(CreateSponsers.this, "id", "");

        profile.setOnClickListener(view -> {
            /* onProfileImageClick()*/
            if (checkAndRequestPermissions(CreateSponsers.this)) {
                chooseImage(CreateSponsers.this);
            }
        });
        create_sponsr.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            String name = Objects.requireNonNull(txt_name.getText()).toString();
            String link = Objects.requireNonNull(txt_link.getText()).toString();
            if (name.length() > 0) {
                if (imagepath.length() > 0) {
                    if (link.length() > 0) {
                        sponsorAdd(name, link);
                    } else {
                        cmn.showAlert("Vänligen ange länk",context);
                    //    showToast("Vänligen ange länk");
                    }
                } else {
                    cmn.showAlert("Välj bild",context);
                  //  showToast("Välj bild");


                }
            } else {
                cmn.showAlert("Vänligen ange namn",context);
              //  showToast("Vänligen ange namn");

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:

                String name = Objects.requireNonNull(txt_name.getText()).toString();
                String link = Objects.requireNonNull(txt_link.getText()).toString();
                if (name.length() > 0) {
                    if (imagepath.length() > 0) {
                        if (link.length() > 0) {
                            alert(name, link);
                        } else {

                            Toast.makeText(CreateSponsers.this, "Vänligen ange länk", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CreateSponsers.this, "Välj bild", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(CreateSponsers.this, "Vänligen ange namn", Toast.LENGTH_SHORT).show();
                }
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
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
                if (ContextCompat.checkSelfPermission(CreateSponsers.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(CreateSponsers.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(CreateSponsers.this);
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
//                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, REQUEST_IMAGE);
                    pickCamera();
                } else if (optionsMenu[i].equals("Välj från galleriet")) {
                    // choose from  external storage
                    ImagePicker.create(CreateSponsers.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

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

    private void sponsorAdd(String name, String link) {


        ProgressDialog mprogdialog = ProgressDialog.show(CreateSponsers.this, "", "Vänta", true);
        mprogdialog.setCancelable(false);

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

        Gson gson = new Gson();
        SponsersPojo asgn = new SponsersPojo();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";

        asgn.file = profileBase64;
        asgn.link = link;
        asgn.name = name;
        asgn.user_id = userid;


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "add_Sponser";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(CreateSponsers.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                mprogdialog.dismiss();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.body() != null) {
                        res = response.body().string();


                        JSONObject objvalue = new JSONObject(res);
                        status = objvalue.optString("status");

                        msg = objvalue.optString("message");


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {

                            showCustomDialog(msg);

                        } else {
                            cmn.showAlert(msg, CreateSponsers.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


    }


    public void loadID() {
        txt_name = findViewById(R.id.sponsor_name);
        sampleImage = findViewById(R.id.sampleImage);
        txt_link = findViewById(R.id.txt_link);
        profile = findViewById(R.id.img_sponser);
        create_sponsr = findViewById(R.id.create_sponsr);


    }

    private void pickCamera() {

        //intent to take image from camera, it will also be save to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPick"); //title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text"); //title of the picture
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                } catch (Exception e) {

                }
                bitmap = cmn.getResizedBitmap(bitmap, 1000);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                Uri uri = cmn.getImageUri(this, bitmap);
                UCrop.Options option = new UCrop.Options();
                option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(CreateSponsers.this, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(CreateSponsers.this, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(CreateSponsers.this, R.color.white));
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(uri, destinationUri).withAspectRatio(3f, 1.5f).withOptions(option).start(this);
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                sampleImage.setImageURI(image_uri);
                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) sampleImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));
                imagepath = String.valueOf(finalFile);
                File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(imagepath));
                imagepath = compressedImageFile.getAbsolutePath();
                Glide.with(context).load(imagepath).centerCrop().into(profile);

                // imagepath = resultUri.getPath();
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();


            //   CompressFile compressFile = new CompressFile();
            // File path = compressFile.getCompressedImageFile(new File(imagepath), CreateSponsers.this);
            Glide.with(CreateSponsers.this).load(imagepath).into(profile);
            // imagepath = path.getPath();


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void alert(String name, String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateSponsers.this);
        builder.setCancelable(false);
       // builder.setMessage("Är du säker på att du skapar den här sponsorn?");
        builder.setMessage("Vill du skapa den här sponsorn?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(CreateSponsers.this)) {
                sponsorAdd(name, link);
            } else {
                Toast.makeText(CreateSponsers.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialog(String msg) {
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
        create.setText(msg);
        ok.setOnClickListener(view -> {

            startActivity(new Intent(CreateSponsers.this, SponserListActivity.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}
