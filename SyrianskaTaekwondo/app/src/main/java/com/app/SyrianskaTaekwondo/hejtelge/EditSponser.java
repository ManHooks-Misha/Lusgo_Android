package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Update_Sponser;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
//import com.app.hejtelge.utility.CompressFile;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

public class EditSponser extends MasterActivity {
    private AppCompatEditText title, link;
    private AppCompatTextView btn_update;
    private AppCompatImageView imgtitle, imglink, img_sponser, edit_image;
    private String id, userid, imagepath, status, msg, profileBase64;
    private CommonMethods cmn;
    private long mLastClickTime = 0;
    private boolean checkUpdate = false;
    public  final int REQUEST_IMAGE = 100;

    private void launchCameraIntent() {
        Intent intent = new Intent(EditSponser.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 4); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 3);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }



    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void launchGalleryIntent() {
        ImagePicker.create(this).theme(R.style.AppTheme_No).single().showCamera(false).toolbarArrowColor(Color.BLACK).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditSponser.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sponser);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setTitle("Redigera sponsor");
        cmn = new CommonMethods();
        loadID();
        String name = getIntent().getStringExtra("name");
        imagepath = getIntent().getStringExtra("image");
        String linktxt = getIntent().getStringExtra("link");
        id = getIntent().getStringExtra("id");
        userid = cmn.getPrefsData(EditSponser.this, "id", "");

        title.setEnabled(false);
        link.setEnabled(false);
        title.setText(name);
        link.setText(linktxt);
        Glide.with(EditSponser.this)
                .load(imagepath)
                .fitCenter()
                .into(img_sponser);
        imgtitle.setOnClickListener(view -> {
                    title.setEnabled(true);

                    title.requestFocus();
                    title.setSelection(Objects.requireNonNull(title.getText()).length());
                    title.setCursorVisible(true);
                }
        );
        imglink.setOnClickListener(view -> {

            link.setEnabled(true);

            link.requestFocus();
            link.setSelection(Objects.requireNonNull(link.getText()).length());
            link.setCursorVisible(true);
        });
        edit_image.setOnClickListener(view ->




                ImagePicker.create(this).theme(R.style.AppTheme_No).single().showCamera(false).toolbarArrowColor(Color.BLACK).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start());
        btn_update.setOnClickListener(view -> {
            String txtname = Objects.requireNonNull(title.getText()).toString();
            String txtlink = Objects.requireNonNull(link.getText()).toString();


            if (cmn.isOnline(EditSponser.this)) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                sponsorAdd(txtname, txtlink);

            } else {
                cmn.showAlert("Kontrollera din internetanslutning ", EditSponser.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (checkUpdate) {
                showErrorToast("Du har ändrat din bild men sparade inte din profil! Klicka på knappen \"Uppdatera profil\" för att fortsätta.");
            } else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void loadID() {

        title = findViewById(R.id.title);
        imgtitle = findViewById(R.id.img_title);
        link = findViewById(R.id.link);
        img_sponser = findViewById(R.id.img_sponser);
        imglink = findViewById(R.id.img_link);
        edit_image = findViewById(R.id.edit_image);
        btn_update = findViewById(R.id.btn_update);

    }

    private void sponsorAdd(String name, String link) {


        ProgressDialog mprogdialog = ProgressDialog.show(EditSponser.this, "", "Vänta", true);
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
        Update_Sponser asgn = new Update_Sponser();
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.sponser_id = id;
        asgn.file = profileBase64;
        asgn.link = link;
        asgn.name = name;
        asgn.user_id = userid;


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "update_Sponser";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(EditSponser.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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


                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        mprogdialog.dismiss();


                        if (status.equals("true")) {
                            checkUpdate = false;

                            Toast.makeText(EditSponser.this, msg, Toast.LENGTH_SHORT).show();
                           // startActivity(new Intent(EditSponser.this, SponserListActivity.class));
                            finish();

                        } else {
                            cmn.showAlert(msg, EditSponser.this);
                        }
                    });
                } catch (Exception e) {
                    mprogdialog.dismiss();
                    e.printStackTrace();
                }


            }
        });


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
                bitmap = cmn.getResizedBitmap(bitmap, 800);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
                Uri uri =  cmn.getImageUri(this, bitmap);



                UCrop.Options option = new UCrop.Options();
                option.setToolbarTitle("          Beskär bild");

                option.setToolbarColor(ContextCompat.getColor(EditSponser.this, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(EditSponser.this, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(EditSponser.this, R.color.white));
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(uri, destinationUri).withAspectRatio(3f,1.5f).withOptions(option).start(this);
            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                assert uri != null;
                String imagepath1 = uri.getPath();


               // CompressFile compressFile = new CompressFile();
                assert imagepath1 != null;
              //  File path = compressFile.getCompressedImageFile(new File(imagepath1), EditSponser.this);
                imagepath = imagepath1;

                Glide.with(EditSponser.this).load(imagepath1).into(img_sponser);
                checkUpdate = true;
                // imagepath = resultUri.getPath();

            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagepath = resultUri.getPath();


        //    CompressFile compressFile = new CompressFile();
         //   File path = compressFile.getCompressedImageFile(new File(imagepath), EditSponser.this);
            Glide.with(EditSponser.this).load(imagepath).into(img_sponser);
         //   imagepath = path.getPath();
            checkUpdate = true;


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
}
