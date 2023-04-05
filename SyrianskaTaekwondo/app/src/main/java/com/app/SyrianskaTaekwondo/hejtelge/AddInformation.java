package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.RealPathUtil;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import vk.help.MasterActivity;

public class AddInformation extends MasterActivity {
    private String userid;
    private AppCompatTextView doc_text, doc_btn, btn_update;
    private AppCompatEditText contact_info, aboutus, link_text, link_text1, link_text2, email_Text, website_text;
    String about, link, image, document, email, links, name, phone, website, id, encodedDoc = "";
    private final int FILE_SELECT_CODE = 0;
    private SharedPreferences permissionStatus;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_information);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uppdatera information");
        loadID();
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        userid = new CommonMethods().getPrefsData(AddInformation.this, "id", "");
        getUserAPI();

        aboutus.setOnTouchListener((v, event) -> {
            if (aboutus.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });link_text.setOnTouchListener((v, event) -> {
            if (link_text.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });link_text1.setOnTouchListener((v, event) -> {
            if (link_text1.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });link_text2.setOnTouchListener((v, event) -> {
            if (link_text2.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });
        btn_update.setOnClickListener(view -> {
            String email = Objects.requireNonNull(email_Text.getText()).toString();
            String link1 = Objects.requireNonNull(link_text.getText()).toString();
            String link2 = Objects.requireNonNull(link_text1.getText()).toString();
            String link3 = Objects.requireNonNull(link_text2.getText()).toString();
            link = link1 + "," + link2 + "," + link3;
            String phone = Objects.requireNonNull(contact_info.getText()).toString();
            String website = Objects.requireNonNull(website_text.getText()).toString();
            String about = Objects.requireNonNull(aboutus.getText()).toString();
            updateInfoAPI(about, encodedDoc, email, link, phone, website, id);
        });

        doc_btn.setOnClickListener(v -> {


            if (ActivityCompat.checkSelfPermission(AddInformation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddInformation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddInformation.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission.");
                    builder.setPositiveButton("Ja", (dialog, which) -> {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(AddInformation.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    });
                    builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddInformation.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission.");
                    builder.setPositiveButton("Ja", (dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                    });
                    builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(AddInformation.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }


                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                editor.apply();


            } else {
                //You already have the permission, just go ahead.
                proceedAfterPermission();
            }


        });
    }

    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further


        showFileChooser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(AddInformation.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddInformation.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("Ja", (dialog, which) -> {
                        dialog.cancel();


                        ActivityCompat.requestPermissions(AddInformation.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


                    });
                    builder.setNegativeButton("Avbryt", (dialog, which) -> dialog.cancel());
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword", "application/pdf"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                FILE_SELECT_CODE);
    }


    private void loadID() {
        contact_info = findViewById(R.id.phone);
        email_Text = findViewById(R.id.email);
        website_text = findViewById(R.id.website);
        link_text = findViewById(R.id.link_txt);
        link_text1 = findViewById(R.id.link_txt1);
        link_text2 = findViewById(R.id.link_txt2);
        aboutus = findViewById(R.id.about);
        doc_text = findViewById(R.id.doc_text);
        doc_btn = findViewById(R.id.doc);
        btn_update = findViewById(R.id.btn_update);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {

            try {
                Uri uri = data.getData();
                //String pathHolder = Objects.requireNonNull(uri).getEncodedPath();
                String pathHolder = RealPathUtil.getRealPath(AddInformation.this, uri);
                if (pathHolder != null) {
                    File file = new File(pathHolder);

                    int length = (int) file.length();
                    doc_text.setText(file.getName());

                    byte[] bytes = new byte[length];
                    try {
                        try (FileInputStream fileInputStream = new FileInputStream(file)) {
                            fileInputStream.read(bytes);
                        }
                        encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);

                        // upload.setText(namefile);
                    } catch (Exception ignored) {

                    }


                } else {
                    Uri returnUri = data.getData();

                    pathHolder = RealPathUtil.getRealPath(AddInformation.this, returnUri);


                    if (pathHolder != null) {
                        File file = new File(pathHolder);
                        doc_text.setText(file.getName());
                        int length = (int) file.length();

                        byte[] bytes = new byte[length];
                        try {
                            //fileInputStream.read(b);
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                fileInputStream.read(bytes);
                            }
                            encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);

                            // upload.setText(namefile);
                        } catch (Exception ignored) {

                        }
                    }

                }

            } catch (Exception e) {
                Toast.makeText(AddInformation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            assert cropError != null;
            cropError.printStackTrace();
            showErrorToast(Objects.requireNonNull(cropError.getMessage()));
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
/*

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private  String getPath(final Context context, final Uri uri) {

        if (isDocumentUri(context, uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);



                final String[] split = docId.split(":");
                final String type = split[0];

                if (type.equalsIgnoreCase("primary")) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);


                return getDataColumn(context, uri, null, null);
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
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
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

*/

    private void getUserAPI() {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());
                    about = object.getString("about_us");
                    document = object.getString("document");
                    email = object.getString("email");
                    image = object.getString("image");
                    links = object.getString("links");
                    name = object.getString("name");
                    phone = object.getString("phone");
                    website = object.getString("website");
                    id = object.getString("id");


                   /* for (int i = 0; i < obj.length(); i++) {
                        //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                aboutus.setText(Html.fromHtml(about));
                if (email.equals("null")) {
                    email_Text.setText("");

                } else {
                    email_Text.setText(email);

                }
                if (phone.equals("null")) {
                    contact_info.setText("");
                } else {
                    contact_info.setText(phone);

                }
                if (website.equals("null")) {
                    website_text.setText("");
                } else {
                    website_text.setText(website);

                }
                if (links.equals("null")) {
                    link_text.setText("");
                    link_text1.setText("");
                    link_text2.setText("");
                } else {
                    String[] link = links.split(",");
                    int ll = link.length;
                  /*  if (ll <= 3) {
                        links = link[0];
                        String links1 = link[1];
                        String links2 = link[2];
                        link_text.setText(links.trim());

                        link_text1.setText(links1.trim());
                        link_text2.setText(links2.trim());
                    }*/

                    if (ll == 3) {
                        links = link[0];
                        String links1 = link[1];

                        String links2 = link[2];
                        link_text.setVisibility(View.VISIBLE);
                        link_text1.setVisibility(View.VISIBLE);


                        link_text2.setVisibility(View.VISIBLE);


                        link_text.setText(links.trim());
                        link_text1.setText(links1.trim());
                        link_text2.setText(links2.trim());


                    }
                    if (ll == 2) {
                        links = link[0];
                        String links1 = link[1];
                        link_text.setVisibility(View.VISIBLE);
                        link_text1.setVisibility(View.VISIBLE);
                        link_text2.setVisibility(View.VISIBLE);

                        link_text.setText(links.trim());
                        link_text2.setText(links1.trim());

                    }
                    if (ll == 1) {
                        links = link[0];

                        link_text.setVisibility(View.VISIBLE);
                        link_text1.setVisibility(View.VISIBLE);
                        link_text2.setVisibility(View.VISIBLE);

                        link_text.setText(links.trim());


                    }
                }
                File f = new File(document);
                String namefile = f.getName();
                doc_text.setText(namefile);
                /*mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();*/
            }
            return null;
        }, requestData).

                execute(ConsURL.BASE_URL_TEST + "data_information");

    }

    private void updateInfoAPI(String about, String doc, String email, String link, String phone, String website, String id) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("about_us", about);
            object.put("document", doc);
            object.put("email", email);
            object.put("image", image);
            object.put("links", link);
            object.put("name", "");
            object.put("phone", phone);
            object.put("website", website);
            object.put("id", id);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    Toast.makeText(this, "Information har uppdaterats framgångsrikt", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddInformation.this, HomePage.class));
                    finish();
                   /* for (int i = 0; i < obj.length(); i++) {
                        //arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "update_information");
    }


}
