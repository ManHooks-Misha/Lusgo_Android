package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAddContactBinding;
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

import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;

public class Add_Contact extends MasterActivity {
    ActivityAddContactBinding binding;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    String imagePath = "", id, name, order, flag, role, email, mobile_no, image;
    CommonMethods cmn = new CommonMethods();
    private static String path;
    AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            order = getIntent().getStringExtra("order");
            flag = getIntent().getStringExtra("flag");
            role = getIntent().getStringExtra("role");
            email = getIntent().getStringExtra("email");
            mobile_no = getIntent().getStringExtra("mobile");
            imagePath = getIntent().getStringExtra("image");
            if (flag.equals("edit")) {
                binding.name.setText(name);
                binding.desc.setText(role);
                binding.email.setText(email);
                binding.mobile.setText(mobile_no);
                if (name.length() > 0) {
                    String namea = String.valueOf(name.trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(drawable)
                            .centerCrop()
                            .into(binding.ppImg);
                } else {
                    String namea = String.valueOf(role.trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(namea.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(imagePath)
                            .placeholder(drawable)
                            .centerCrop()
                            .into(binding.ppImg);
                }

                // Glide.with(context).load(imagePath).into(binding.ppImg);

            }

        }
        binding.ppImg.setOnClickListener(view -> {
           // onProfileImageClick();
            if (checkAndRequestPermissions(Add_Contact.this)) {
                chooseImage(Add_Contact.this);
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
                if (ContextCompat.checkSelfPermission(Add_Contact.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(Add_Contact.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(Add_Contact.this);
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
                    ImagePicker.create(Add_Contact.this).theme(R.style.AppTheme_No).single().toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
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
                    bitmap = cmn.getResizedBitmap(bitmap, 500);

                } catch (Exception e) {

                }


                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // bitmap.compress(Bitmap.CompressFormat.PNG, 0, os) ;// YOU can also save it in JPEG
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);

                Uri uri =  cmn.getImageUri(this, bitmap);

                UCrop.Options option = new UCrop.Options();
                option.setToolbarColor(Color.parseColor("#ffffff"));
                option.setToolbarTitle("          Beskär bild");
                option.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                option.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
                option.setCropGridColumnCount(0);
                option.setCropGridRowCount(0);
                option.setShowCropFrame(false);
                option.setCircleDimmedLayer(true);
                Uri destinationUri = Uri.fromFile(new File(getExternalFilesDir(null).getAbsoluteFile() + "/" + Common.INSTANCE.nameFromURL(images.get(0).getPath())));
                UCrop.of(uri, destinationUri).withAspectRatio(2f, 1.5f).withOptions(option).start(this);


            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri uri = data.getParcelableExtra("path");
               // Log.e("imagepath1", String.valueOf(uri));
                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));
                imagePath = String.valueOf(finalFile);

                Glide.with(context)
                        .load(imagePath)
                        .centerCrop()
                        .into(binding.ppImg);
               // Glide.with(Add_Contact.this).load(imagePath).into(binding.ppImg);


            }
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            imagePath = resultUri.getPath();
            Glide.with(Add_Contact.this).load(imagePath).centerCrop().into(binding.ppImg);
          //  Glide.with(Add_Contact.this).load(imagePath).into(binding.ppImg);


        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            cropError.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
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
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        showErrorToast("Permission Required");
                    }
                }
            } else {
                showErrorToast("Permission Required");
            }
        }
    }
*/

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_link, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }

        return true;
    }
    @Override
    public void onBackPressed() {
        saveContactDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveContactDialog();
            //finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            saveContact();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addContactAPI(String name, String imagepath, String email, String web, String mob, String id, String order) {
        String profileBase64 = "";
        if (imagepath != null) {
            if (imagepath.length() > 0) {
                File file = new File(imagepath);

                int length1 = (int) file.length();
//            txt_doc.setText(file.getName());
//            ff_doc.setVisibility(View.VISIBLE);

                byte[] bytes = new byte[length1];
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

        } else {
            imagepath = "";
        }
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("Email", email);
            object.put("Website", web);
            object.put("Id", Integer.parseInt(id));
            object.put("MobileNo", mob);
            object.put("Name", name);
            object.put("Image", profileBase64);
            object.put("ContactOrder", order);
            object.put("user_id", Integer.parseInt(new CommonMethods().getPrefsData(Add_Contact.this, "id", "")));

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {

                if (result.isStatus()) {
                    startActivity(new Intent(this, Update_info.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("Flag", "contact"));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "contactInsertupdate_information");
    }

    private void saveContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Vill du spara ändringarna?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (new CommonMethods().isOnline(context)) {
                saveContact();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
            finish();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
    private static String nullChecker(String value){
        if (value == null){
            value ="";
        }
        return value;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void saveContact(){
        String name = Objects.requireNonNull(binding.name.getText()).toString();
        String desc = Objects.requireNonNull(binding.desc.getText()).toString();
        String email = Objects.requireNonNull(binding.email.getText()).toString();
        String mob = Objects.requireNonNull(binding.mobile.getText()).toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (flag.equals("edit")) {
            if (cmn.isOnline(this)) {
                if (name.length() == 0 && desc.length() == 0) {
                    cmn.showAlert("Fyll i antingen namn eller beskrivning", this);
                }else if (!nullChecker(email).equals("")){
                            if (isValidEmail(email)){
                                addContactAPI(name, imagePath, email, desc, mob, id, order);
                            }else{
                                cmn.showAlert("Ogiltig e-postadress", this);
                            }
                 }else if (!nullChecker(mob).equals("")){
                            if (mob.length() < 10){
                                cmn.showAlert("Fyll i telefonnummer", this);
                            }else{
                                addContactAPI(name, imagePath, email, desc, mob, id, order);
                            }
                }else {
                    cmn.showAlert("Fyll i telefonnummer eller e-postadress", this);
                }

            } else {
                Toast.makeText(Add_Contact.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        }
        else {
            if (cmn.isOnline(this)) {
                if (name.length() == 0 && desc.length() == 0) {
                    cmn.showAlert("Fyll i antingen namn eller beskrivning", this);
                }else if (!nullChecker(email).equals("")){
                    if (isValidEmail(email)){
                        addContactAPI(name, imagePath, email, desc, mob,"0", "0");
                    }else{
                        cmn.showAlert("Ogiltig e-postadress", this);
                    }
                }else if (!nullChecker(mob).equals("")){
                    if (mob.length() < 10){
                        cmn.showAlert("Fyll i telefonnummer", this);
                    }else{
                        addContactAPI(name, imagePath, email, desc, mob, "0", "0");
                    }
                }else {
                    cmn.showAlert("Fyll i telefonnummer eller e-postadress", this);
                }

            } else {
                Toast.makeText(Add_Contact.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        }
    }
}