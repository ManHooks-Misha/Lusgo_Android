package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Html;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.NewsImageAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityMessageForProfileBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Messege;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
//import com.app.hejtelge.utility.CompressFile;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;

public class MessageForProfile extends MasterActivity {
    ActivityMessageForProfileBinding binding;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public final int REQUEST_IMAGE = 100;
    private final int FILE_SELECT_CODE = 0;
    AlertDialog alertDialog;
    private Uri contentUri;
    private String encodedDoc1, txt_desc, txt_link = "", role = "", teamid, userid;
    private long mLastClickTime = 0;
    private ArrayList<HashMap<String, String>> images_post = new ArrayList<>();

    public static ArrayList<HashMap<String, File>> images_path = new ArrayList<>();

    private NewsImageAdapter libraryHotAdapter;
    Uri image_uri;
    private static String path;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessageForProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        libraryHotAdapter = new NewsImageAdapter(images_path, context, "msg");
        binding.imagelist.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        binding.imagelist.setAdapter(libraryHotAdapter);
        binding.attach.setOnClickListener(view -> binding.llattachments.setVisibility(binding.llattachments.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
        binding.lllink.setOnClickListener(view -> {
            binding.fflink.requestFocus();
            binding.fflink.setVisibility(View.VISIBLE);
        });
        binding.llimg.setOnClickListener(v -> {
            if (checkAndRequestPermissions(MessageForProfile.this)) {
                chooseImage(MessageForProfile.this);
            }
        });



        CommonMethods cmn = new CommonMethods();
        role = cmn.getPrefsData(context, "usertype", "");
        userid = cmn.getPrefsData(context, "id", "");
        teamid = cmn.getPrefsData(context, "team_id", "");
        if (getIntent() != null) {
            uid = getIntent().getStringExtra("u_id");
            String uname = getIntent().getStringExtra("u_name");
            String image = getIntent().getStringExtra("img");
            binding.name.setText(uname);

            if (uname.trim().length() > 0) {
                String name = String.valueOf(uname.trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(this)
                        .load(image)
                        .placeholder(drawable)
                        .fitCenter()
                        .into(binding.img);
            }

        }
        binding.lldoc.setOnClickListener(v -> {
            //  GetImageFromGallery();
            if (isPermissionGranted()) {
                showFileChooser();
            } else {
                ActivityCompat.requestPermissions(MessageForProfile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            ArrayList<String> selectedGroup = new ArrayList<>();
            txt_desc = Html.toHtml(binding.desc.getText());
            txt_desc = txt_desc.replaceFirst("<u>", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            txt_desc = txt_desc.replaceAll("</u>", "");
            txt_link = Objects.requireNonNull(binding.linkedit.getText()).toString();
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return true;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (txt_desc.length() > 0) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(uid);
                alert(txt_desc, txt_link, selectedGroup, arrayList);
//
            } else {
                showToast("Skriv meddelande");
            }
        }

        return super.onOptionsItemSelected(item);
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
        create.setText("Meddelande har skickats");
        ok.setOnClickListener(view -> {

            startActivity(new Intent(MessageForProfile.this, HomePage.class).putExtra("Open", "message"));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void createMessageAPI(String txt_desc, String txt_link, ArrayList<String> groups, ArrayList<String> users/*,ArrayList<HashMap<String, String>> users_key*/) {
        //  arr.clear();
        images_post.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        Messege asgn = new Messege();
        asgn.message = txt_desc;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.doc = encodedDoc1;
        asgn.link = txt_link;
        asgn.user_id = userid;
        asgn.groups = groups;
        //    asgn.Team_users = users_key;

        asgn.users = users;
        asgn.team_id = teamid;

        asgn.all_coaches = false;
        asgn.all_users = false;
        asgn.all_groups = false;
        asgn.all_teams = false;

        if (images_path.size() > 0) {
            for (int i = 0; i < images_path.size(); i++) {
                File file = Objects.requireNonNull(images_path.get(i).get("Path"));

                int length = (int) file.length();
                binding.txtdoc.setText(file.getName());
                binding.ffdoc.setVisibility(View.VISIBLE);

                byte[] bytes = new byte[length];
                //fileInputStream.read(b);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    fileInputStream.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                HashMap<String, String> map = new HashMap<>();
                map.put("file", encodedImage);
                images_post.add(map);
            }
            asgn.images = images_post;
        } else {
            asgn.images = new ArrayList<>();
        }

        requestData = getJSON(asgn);
        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    images_path.clear();
                    showCustomDialog();
                }
                else {
                    showToast(result.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "add_message");
    }


    private void alert(String txt_desc, String txt_link, ArrayList<String> groups, ArrayList<String> users) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill skicka meddelandet?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                createMessageAPI(txt_desc, txt_link, groups, users);
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showFileChooser() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"application/msword", "application/pdf"};
//        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(Intent.createChooser(intent, "Välj en fil som ska laddas upp"), FILE_SELECT_CODE);
    }

    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) || (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);

        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);
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
                if (ContextCompat.checkSelfPermission(MessageForProfile.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                 /*   Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(MessageForProfile.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(MessageForProfile.this);
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
                    pickCamera();
                } else if (optionsMenu[i].equals("Välj från galleriet")) {
                    // choose from  external storage
                    ImagePicker.create(MessageForProfile.this).theme(R.style.AppTheme_No).limit(10).toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
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
            //images_path.clear();
            List<Image> images = ImagePicker.getImages(data);
            if (images.size() > 0) {

                for (int i = 0; i < images.size(); i++) {
                    String path = images.get(i).getPath();
                    //    CompressFile compressFile = new CompressFile();
                    // File pathfile = compressFile.getCompressedImageFile(new File(path), context);
                    try {
                        File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(path));

                        HashMap<String, File> map = new HashMap<>();
                        map.put("Path", compressedImageFile);
                        images_path.add(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                libraryHotAdapter.notifyDataSetChanged();


            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.sampleImage1.setImageURI(image_uri);


                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.sampleImage1.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));


                //CompressFile compressFile = new CompressFile();
                assert String.valueOf(finalFile) != null;
                //  File path = compressFile.getCompressedImageFile(new File(imagepath1), context);
                HashMap<String, File> map = new HashMap<>();
                map.put("Path", new File(String.valueOf(finalFile)));
                images_path.add(map);
                // imagepath = resultUri.getPath();


            }
            libraryHotAdapter.notifyDataSetChanged();
        }

        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
            try {

                Uri uri = data.getData();
                ConvertToString(uri);
                binding.txtdoc.setText("Dokument");
                binding.ffdoc.setVisibility(View.VISIBLE);


            } catch (Exception e) {
                showToast(Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ConvertToString(Uri uri) {
        String uriString = uri.toString();
        Log.d("data", "onActivityResult: uri" + uriString);
        //            myFile = new File(uriString);
        //            ret = myFile.getAbsolutePath();
        //Fpath.setText(ret);
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(in);
            encodedDoc1 = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        Log.e("pathwwwwwww", path);

        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}