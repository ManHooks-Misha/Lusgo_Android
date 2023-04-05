package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAddDocumentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.jaiselrahman.filepicker.activity.FilePickerActivity2;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import vk.help.MasterActivity;

import static android.provider.DocumentsContract.isDocumentUri;

public class Add_Document extends MasterActivity {
    ActivityAddDocumentBinding binding;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private static Uri contentUri;
    private String encodedDoc;
    CommonMethods cmn = new CommonMethods();
    String id, name, url, order, flag;
    private final int FILE_SELECT_CODE = 0;
    int file_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDocumentBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            url = getIntent().getStringExtra("linkurl");
            order = getIntent().getStringExtra("order");
            flag = getIntent().getStringExtra("flag");
            if (flag.equals("edit")) {
                binding.linkName.setText(name);
                File f = new File(url);
                //  binding.uploadTxt.setVisibility(View.VISIBLE);
                binding.uploadTxt.setText(f.getName());
            }

        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.uploadDoc.setOnClickListener(view -> {

            if (isPermissionGranted()) {
                if (android.os.Build.VERSION.SDK_INT >= 12) {
                    showFileChooser();
                } else {
                    Intent intent = new Intent(Add_Document.this, FilePickerActivity2.class);
                    intent.putExtra(FilePickerActivity2.CONFIGS, new Configurations.Builder()
                            .setCheckPermission(true)
                            .setSelectedMediaFiles(mediaFiles)
                            .setShowFiles(true)
                            .setShowImages(false)
                            .setShowVideos(false)
                            .setMaxSelection(1)
                            .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                            .build());
                    startActivityForResult(intent, FILE_REQUEST_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(Add_Document.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        });
    }

    private void showFileChooser() {
        Intent intent = null;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                FILE_REQUEST_CODE);
    }

    public void ConvertToString(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(in);
            encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (android.os.Build.VERSION.SDK_INT >= 12) {
                Uri uri = data.getData();
                ConvertToString(uri);
                getFileName(uri);
                // binding.uploadTxt.setText((CharSequence) uri);
                binding.uploadTxt.setVisibility(View.VISIBLE);
            } else {
                mediaFiles.clear();
                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity2.MEDIA_FILES));

                File file = new File(getPath(context, mediaFiles.get(0).getUri()));
                file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                // Log.d("fsdajlk",file_size+"");
                binding.uploadTxt.setVisibility(View.VISIBLE);
                binding.uploadTxt.setText(mediaFiles.get(0).getName());
            }


            //  fileListAdapter.notifyDataSetChanged();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    binding.uploadTxt.setText(result);
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                    final boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            String linkname = Objects.requireNonNull(binding.linkName.getText()).toString();
            if (file_size > 11000) {
                cmn.showAlert("Du kan inte välja fil från 10 mb ovan", this);
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 12) {
                    if (new CommonMethods().isOnline(this)) {
                        if (linkname.length() > 0) {
                            if (flag.equals("newActi")) {
                                addDocumentAPI(linkname, encodedDoc, Integer.parseInt("0"), "0");
                            } else {
                                addDocumentAPI(linkname, encodedDoc, Integer.parseInt(id), order);
                            }


                        } else {
                            cmn.showAlert("Ange dokument", this);

                        }
                    }


                } else {
                    if (flag.equals("edit")) {
                        if (mediaFiles.size() > 0) {
                            File file = new File(getPath(context, mediaFiles.get(0).getUri()));
                            //   int file_size = Integer.parseInt(String.valueOf(file.length()/1024));
                            //  Log.d("fsdajlk",file_size+"");

                            int length = (int) file.length();

                            byte[] bytes = new byte[length];
                            try {
                                //fileInputStream.read(b);
                                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                    fileInputStream.read(bytes);
                                }
                                String encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);
                                Log.e("encodedDoc", encodedDoc);

                                if (new CommonMethods().isOnline(this)) {
                                    if (linkname.length() > 0 && encodedDoc.length() > 0) {
                                        addDocumentAPI(linkname, encodedDoc, Integer.parseInt(id), order);
                                    } else {
                                        cmn.showAlert("Ange dokument", this);
                                        // Toast.makeText(context, "Ange dokument", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                // upload.setText(namefile);
                            } catch (Exception ignored) {
                                Log.e("TAG", ignored.getMessage());
                            }
                        } else {
                            if (new CommonMethods().isOnline(this)) {
                                if (linkname.length() > 0) {
                                    addDocumentAPI(linkname, "", Integer.parseInt(id), order);
                                } else {
                                    cmn.showAlert("Ange dokument", this);
                                    // Toast.makeText(context, "Ange dokument", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    } else {
                        if (mediaFiles.size() > 0) {
                            File file = new File(getPath(context, mediaFiles.get(0).getUri()));

                            int length = (int) file.length();

                            byte[] bytes = new byte[length];
                            try {
                                //fileInputStream.read(b);
                                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                    fileInputStream.read(bytes);
                                }
                                // encodedDoc1 = Base64.encodeToString(bytes, Base64.DEFAULT);

                                String encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);
                                // Log.e("encodedDoc", encodedDoc);

                                if (new CommonMethods().isOnline(this)) {
                                    if (linkname.length() > 0 && encodedDoc.length() > 0) {
                                        addDocumentAPI(linkname, encodedDoc, Integer.parseInt(id), order);
                                        //addDocumentAPI(linkname, encodedDoc, Integer.parseInt("0"), "0");
                                    } else {
                                        cmn.showAlert("Ange dokument", this);
                                        //Toast.makeText(context, "Ange dokument", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                // upload.setText(namefile);
                            } catch (Exception ignored) {
                                Log.e("TAG", ignored.getMessage());
                            }
                        } else {
                            cmn.showAlert("välj dokument", this);

                            //   showToast("välj dokument");
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void addDocumentAPI(String linkname, String linkurl, int id, String order) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {

            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("DocumentName", linkname);
            object.put("DocumentUrl", linkurl);
            object.put("Id", id);
            object.put("DocumentOrder", order);

            object.put("user_id", Integer.parseInt(new CommonMethods().getPrefsData(Add_Document.this, "id", "")));

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {

                if (result.isStatus()) {
                    startActivity(new Intent(this, Update_info.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("Flag", "doc"));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "documentinsertupdate_information");
    }

}