package com.app.SyrianskaTaekwondo.hejtelge;

import static android.os.Build.VERSION.SDK_INT;
import static android.provider.DocumentsContract.isDocumentUri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.NewsImagesBitmapAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCreateNewsBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ApiCall;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CountingRequestBody;
import com.app.SyrianskaTaekwondo.hejtelge.utility.RequestBuilder;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ScalingUtilities;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;

public class CreateNews extends MasterActivity {
    private static Uri contentUri;
    private String encodedDoc = "";
    private String status;
    ProgressDialog mprogdialog;
    private String txt_desc = "";
    private String txt_link = "";
    private String txt_check = "";
    private long mLastClickTime = 0;
    AlertDialog alertDialog;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private String teamLeadrMSG = "Inloggade användare kommer ha möjligheten att kommentera inlägget.";
    private CommonMethods cmn = new CommonMethods();
    public static ArrayList<File> images = new ArrayList<>();
    private ArrayList<HashMap<String, String>> images_post = new ArrayList<>();
    private final int FILE_SELECT_CODE = 0;
    private String userid = "", teamid = "", picturePath, teamtname, rolAdmin;
    private ArrayList<Teamlist> arr = new ArrayList<>();
    private ArrayList<UserList> arr_user = new ArrayList<>();
    private CustomAdapter mAdapter;
    private Group_Event_Adapter mAdapter_group;
    ActivityCreateNewsBinding binding;
    public boolean isCheckFromParent = true;
    public boolean isCheckFromParentUser = true;
    private NewsImagesBitmapAdapter libraryHotAdapter;
    public final int REQUEST_IMAGE = 100;
    private OkHttpClient client = new OkHttpClient();
    private static String response, path;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 2001;
    Uri image_uri;
    int file_size;

    public static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        deleteCache(this);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        String role = cmn.getPrefsData(CreateNews.this, "usertype", "");
        userid = cmn.getPrefsData(CreateNews.this, "id", "");
      //  Log.d("afdslkj",userid);
        rolAdmin = cmn.getPrefsData(CreateNews.this, "role", "");
        //   teamid = cmn.getPrefsData(CreateNews.this, "team_id", "");
        teamtname = cmn.getPrefsData(CreateNews.this, "team_name", "");
        binding.teamName.setText(teamtname);
        if (role.equals("2") || role.equals("5")) {
            binding.teamName.setVisibility(View.GONE);
            binding.llTeams.setVisibility(View.GONE);
        } else {
            getUserAPI();
            binding.teamName.setVisibility(View.GONE);

        }
        binding.team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teamid = arr.get(position).getId();
              //  Log.d("afdslkj",teamid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        images.clear();
        loadID();

        if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
            binding.llGroup.setVisibility(View.GONE);
            binding.llUser.setVisibility(View.GONE);
            binding.fan.setVisibility(View.VISIBLE);
        } else {
            binding.fan.setVisibility(View.GONE);
        }
        binding.llLink.setOnClickListener(view -> {
            binding.ffLink.requestFocus();
            binding.ffLink.setVisibility(View.VISIBLE);
        });
        binding.llImage.setOnClickListener(v -> {
            if (checkAndRequestPermissions(CreateNews.this)) {
                chooseImage(CreateNews.this);


            }
        });
        binding.llDoc.setOnClickListener(v -> {
            if (isPermissionGranted()) {
                showFileChooser();
            } else {
                ActivityCompat.requestPermissions(CreateNews.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        });
        binding.attach.setOnClickListener(view -> {
            if (binding.llAttachments.getVisibility() == View.VISIBLE) {
                binding.llAttachments.setVisibility(View.GONE);
            } else {
                binding.llAttachments.setVisibility(View.VISIBLE);

            }
        });
        binding.infoTxtCreateNews.setOnClickListener(view ->
                cmn.customDialogMsg(CreateNews.this, getResources().getString(R.string.msg_createnews)));

        binding.infoTxt.setOnClickListener(view -> {
            showCustomInfoTextDialog();
        });


    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            txt_desc = Html.toHtml(binding.messageText.getText());
            txt_desc = txt_desc.replaceFirst("" +
                    "", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            txt_desc = txt_desc.replaceAll("</p>", "");
            txt_desc = txt_desc.replaceFirst("<u>", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            txt_desc = txt_desc.replaceAll("</u>", "");
            txt_link = Objects.requireNonNull(binding.linkEdit.getText()).toString();


            boolean flag_checked = binding.fan.isChecked();
            if (flag_checked) {
                txt_check = "1";
            } else {
                txt_check = "0";

            }
            if (txt_desc.length() > 0) {
                if (images.size() > 10) {
                    showToast("Du kan inte välja fler än (10) bilder. Avmarkera en annan bild innan du försöker välja igen.");

                } else {
                    alert();
                    Pdffile();
                }

            } else {
                cmn.showAlert("Skriv lite text", this);
                //  showToast("Skriv lite text");
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_create, menu);
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
                if (ContextCompat.checkSelfPermission(CreateNews.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
/*                    Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(CreateNews.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(CreateNews.this);
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
                    ImagePicker.create(CreateNews.this).theme(R.style.AppTheme_No).limit(10).toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

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
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images_ = ImagePicker.getImages(data);

            if (images_.size() > 0) {
                for (int i = 0; i < images_.size(); i++) {
                    Image dataimg = images_.get(i);
                    String path = dataimg.getPath();
                    File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(path));
                    if (images.size() > 9) {
                        cmn.showAlert("max 10 bilder tillåtna", this);
                    } else {
                        images.add((compressedImageFile));
                    }

                }
                libraryHotAdapter.notifyDataSetChanged();
            }
        }

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CAMERA_CODE) {
            // Log.e("image", String.valueOf(image_uri));

            binding.sampleImage.setImageURI(image_uri);
            //get drawable bitmap for text recognition
            BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.sampleImage.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // binding.sampleImage.setImageBitmap(selectedImage);

            File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));
            String imagepath = String.valueOf(finalFile);

            File compressedImageFile = new File(imagepath);
            if (images.size() > 9) {
                cmn.showAlert("max 10 bilder tillåtna", this);
            } else {
                images.add((compressedImageFile));
            }

        }
        libraryHotAdapter.notifyDataSetChanged();

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            String imagepath = resultUri.getPath();
            File compressedImageFile = Compressor.getDefault(this).compressToFile(new File(imagepath));
            images.add(compressedImageFile);
            libraryHotAdapter.notifyDataSetChanged();
        }

        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
//            try {
//                Uri uri = data.getData();
//                ConvertToString(uri);
//                binding.txtDoc.setText("Dokument");
//                binding.ffDoc.setVisibility(View.VISIBLE);
//            } catch (Exception e) {
//                Toast.makeText(CreateNews.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
            if (android.os.Build.VERSION.SDK_INT >= 12) {
                Uri uri = data.getData();
                ConvertToString(uri);
                getFileName(uri);
                // binding.uploadTxt.setText((CharSequence) uri);
                binding.ffDoc.setVisibility(View.VISIBLE);
            } else {
                mediaFiles.clear();
                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));
                File file = new File(getPath(context, mediaFiles.get(0).getUri()));
                file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                binding.ffDoc.setVisibility(View.VISIBLE);

                binding.txtDoc.setText(mediaFiles.get(0).getName());
            }


        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    binding.txtDoc.setText(result);
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);

            }
        }
        return result;
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
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_test" + System.currentTimeMillis(), null);
        Log.e("path", path);

        return Uri.parse(path);
    }


    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    public void loadID() {
        libraryHotAdapter = new NewsImagesBitmapAdapter(
                images, CreateNews.this, "news");
        binding.imageList.setLayoutManager(new LinearLayoutManager(CreateNews.this, RecyclerView.HORIZONTAL, false));
        binding.imageList.setAdapter(libraryHotAdapter);

        //  list_part = findViewById(R.id.participant_list);
    }

    private void showFileChooser() {
        if (android.os.Build.VERSION.SDK_INT >= 12) {
            Intent intent = null;
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            String[] mimetypes = {"application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } else {
            Intent intent = new Intent(CreateNews.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSelectedMediaFiles(mediaFiles)
                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setMaxSelection(1)
                    .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                    .build());
            startActivityForResult(intent, FILE_SELECT_CODE);
        }
    }


    public void Pdffile() {
        if (mediaFiles.size() > 0) {
            File file = new File(getPath(context, mediaFiles.get(0).getUri()));
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            try {
                //fileInputStream.read(b);
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    fileInputStream.read(bytes);
                }
                encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);
                Log.e("encodedDoc", encodedDoc);

                // upload.setText(namefile);
            } catch (Exception ignored) {
                Log.e("TAG", ignored.getMessage());
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getPath(final Context context, final Uri uri) {

        if (!isDocumentUri(context, uri)) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        } else {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if (type.equalsIgnoreCase("primary")) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                if (SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    try (Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[]{
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                         /*   final Uri contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));*/

                                return getDataColumn(context, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                //In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }


                    }

                } else {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final boolean isOreo = SDK_INT >= Build.VERSION_CODES.O;
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                }
            }


        }

        return null;
    }


    public void ConvertToString(Uri uri) {
        String uriString = uri.toString();
        //  Log.d("data", "onActivityResult: uri" + uriString);
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(in);
            encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection,
                selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    @SuppressLint("StaticFieldLeak")
    private void createNewsAPI() {

        new AsyncTask<Integer, Integer, String>() {


            @Override
            protected String doInBackground(Integer... params) {
                try {
                    //response= uploadFiles(file);
                    // publishProgress();


                    MultipartBody body = RequestBuilder.uploadRequestBody1(userid, teamid, txt_desc, "f76646abb2bb5408ecc6d8e36b64c9d8", encodedDoc, txt_link, txt_check, images);

                    CountingRequestBody monitoredRequest = new CountingRequestBody(body, (bytesWritten, contentLength) -> {
                        //Update a progress bar with the following percentage
                        float percentage = 100f * bytesWritten / contentLength;
                        if (percentage >= 0) {
                            //TODO: Progress bar
                            publishProgress(Math.round(percentage));
                            Log.d("progress ", percentage + "");
                        } else {
                            //Something went wrong
                            Log.d("No progress ", 0 + "");
                        }
                    });
                    response = ApiCall.POST(client, ConsURL.BASE_URL_TEST + "add_news_formdata", monitoredRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                // progressBar.setVisibility(View.GONE);
                //  txt.setText(result);
                // mBuilder.setContentText("Upload complete");
                mprogdialog.dismiss();
                //    Toast.makeText(context, "Upload complete...", Toast.LENGTH_LONG).show();
                images.clear();
                showCustomDialog();
                // Removes the progress bar
                //mBuilder.setProgress(0, 0, false);
                //  mNotifyManager.notify(0, mBuilder.build());
            }

            @Override
            protected void onPreExecute() {
                //  txt.setText("Task Starting...");
                mprogdialog = ProgressDialog.show(CreateNews.this, "", "Vänta", true);
                mprogdialog.setCancelable(false);

/*
mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mBuilder = new NotificationCompat.Builder(context);
                        mBuilder.setContentTitle("Uploading")
                                .setContentText("Upload in progress")
                                .setSmallIcon(R.drawable.ic_launcher_background);
*/

                //  binding.ppDialog.setVisibility(View.VISIBLE);
                //  Toast.makeText(context, "Uploading files...", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                //txt.setText("Running..." + values[0]);
                //  binding.ppDialog.setProgress(values[0]);
//                        if ((values[0]) % 25 == 0) {
//                            mBuilder.setProgress(100, values[0], false);
//                            // Displays the progress bar on notification
//                            mNotifyManager.notify(0, mBuilder.build());
//                        }

            }


        }.execute();

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
        ok.setOnClickListener(view -> {

            startActivity(new Intent(CreateNews.this, HomePage.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    // show info text in dialoge
    private void showCustomInfoTextDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.info_text_layout, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        AppCompatTextView txt_info_admin = dialogView.findViewById(R.id.txt_info_admin);
        if (rolAdmin.equals("Teamleader")) {
            txt_info_admin.setVisibility(View.GONE);
        }
        ok.setOnClickListener(view -> {

            alertDialog.dismiss();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public class Group_Event_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;
        private ArrayList<GroupList> arr_grouplist = new ArrayList<>();

        private ArrayList<GroupList> horizontalList;

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        private CreateNews activity;
        private ActivityCreateNewsBinding binding;

        private Group_Event_Adapter(ArrayList<GroupList> students, RecyclerView recyclerView, CreateNews activity, ActivityCreateNewsBinding binding) {
            horizontalList = students;
            this.activity = activity;
            this.binding = binding;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new Group_Event_Adapter.StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false));
            } else {
                return new Group_Event_Adapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Group_Event_Adapter.StudentViewHolder) {
                ((Group_Event_Adapter.StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((Group_Event_Adapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView selecttext;
            private CircleImageView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);
                img = view.findViewById(R.id.iv_profile);
                selecttext = view.findViewById(R.id.iv_selected);
            }

            void setData(GroupList data) {
                txtview.setText(data.getName());
                selecttext.setVisibility(arr_grouplist.contains(data) ? View.VISIBLE : View.GONE);
                Glide.with(itemView).load(data.getImage()).fitCenter().placeholder(R.drawable.user_diff).into(img);

                itemView.setOnClickListener(view -> {
                    if (arr_grouplist.contains(data)) {
                        arr_grouplist.remove(data);
                    } else {
                        arr_grouplist.add(data);
                    }
                    notifyItemChanged(getAdapterPosition());
                    updateUI();
                });
            }
        }


        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        public ArrayList<GroupList> getList() {
            return arr_grouplist;
        }

        private void updateAllData(boolean toAdd) {
            arr_grouplist.clear();
            if (toAdd) {
                arr_grouplist.addAll(horizontalList);
            }

            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (arr_grouplist.containsAll(horizontalList)) {
                activity.isCheckFromParent = true;
                binding.selectallGroup.setChecked(true);
            } else {
                activity.isCheckFromParent = false;
                binding.selectallGroup.setChecked(false);
            }
        }
    }


    private class User_event_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;
        private ArrayList<UserList> arr_userlist = new ArrayList<>();
        private ArrayList<UserList> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        public CreateNews activity;
        private ActivityCreateNewsBinding binding;

        private User_event_Adapter(ArrayList<UserList> students, RecyclerView recyclerView, CreateNews activity, ActivityCreateNewsBinding binding) {
            horizontalList = students;
            this.activity = activity;
            this.binding = binding;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new User_event_Adapter.StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_user, parent, false));
            } else {
                return new User_event_Adapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof User_event_Adapter.StudentViewHolder) {
                ((User_event_Adapter.StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((User_event_Adapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        private class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView iv_selected;
            private CircleImageView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);

                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);

            }

            void setData(UserList data) {
                txtview.setText(data.getFirstname());
                iv_selected.setVisibility(arr_userlist.contains(data) ? View.VISIBLE : View.GONE);

                itemView.setOnClickListener(view -> {
                    if (arr_userlist.contains(data)) {
                        arr_userlist.remove(data);
                    } else {
                        arr_userlist.add(data);
                    }
                    notifyItemChanged(getAdapterPosition());
                    updateUI();
                });
                Glide.with(itemView)
                        .load(data.getProfile_image())
                        .fitCenter()
                        .placeholder(R.drawable.user_diff)
                        .into(img);
            }

        }

        class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        public ArrayList<UserList> getList() {
            return arr_userlist;
        }

        public void updateAllData(boolean toAdd) {
            arr_userlist.clear();
            if (toAdd) {
                arr_userlist.addAll(horizontalList);
            }
            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (arr_userlist.containsAll(horizontalList)) {
                activity.isCheckFromParentUser = true;
                binding.selectallUser.setChecked(true);
            } else {
                activity.isCheckFromParentUser = false;
                binding.selectallUser.setChecked(false);
            }
        }
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att publicera inlägget?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                if (file_size > 11000) {
                    cmn.showAlert("Du kan inte välja fil från 10 mb ovan", this);
                } else {
                    createNewsAPI();
                }

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

    private void getUserAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 50);
            object.put("offset", 0);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    // JSONObject objarr = new JSONObject(result.getData());
                    //  if (objarr.length() > 0) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }
                    mAdapter = new CustomAdapter(CreateNews.this, arr);
                    binding.team.setAdapter(mAdapter);

                    //   }


                }


            } catch (Exception e) {
                e.printStackTrace();
            } /*finally {
                // mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }*/
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Team_by_coachid");
    }


    public class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<Teamlist> teams;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, ArrayList<Teamlist> teams) {
            this.context = applicationContext;
            this.teams = teams;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return teams.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.team_dwopdown, null);
            AvatarView icon = (AvatarView) view.findViewById(R.id.pos);
            TextView names = (TextView) view.findViewById(R.id.txt_name);

            if (teams.get(i).getName().trim().length() > 0) {

                String name = String.valueOf(teams.get(i).getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

                Glide.with(context)
                        .load(teams.get(i).getLogo())
                        .placeholder(drawable)
                        .fitCenter()
                        .into(icon);
            }
            names.setText(teams.get(i).getName());
            return view;
        }
    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 90, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
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
                unscaledBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), matrix, true); // rotating bitmap
            } catch (Exception e) {

            }
            //if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
            // Part 2: Scale image
            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
            //  } else {
//                unscaledBitmap.recycle();
//                return path;
            //   }

            // Store to tmp file
//            File mydir = context.getDir("mydir", Context.MODE_PRIVATE); //Creating an internal dir;
//            File fileWithinMyDir = new File(mydir, "myfile"); //Getting a file within the dir.
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + getApplicationContext().getPackageName()
                    + "/Files/");
            //   String extr = Environment.getExternalStorageDirectory().toString();
            //  File mFolder = new File(extr + "/TMMFOLDER");
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdir();
            }

            String s = Math.random() + "tmp.png";

            File f = new File(mediaStorageDir.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 0, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
}
