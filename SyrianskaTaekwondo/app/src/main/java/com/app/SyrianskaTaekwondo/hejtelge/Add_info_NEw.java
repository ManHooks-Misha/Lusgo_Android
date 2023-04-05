package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.FileListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityAddInfoNEwBinding;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Contact_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Doc_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.InfoAbout_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.fragments.Link_fragment;
import com.app.SyrianskaTaekwondo.hejtelge.model.LinksModel;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.update;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.gson.Gson;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.Fragment;
import vk.help.MasterActivity;

import static android.provider.DocumentsContract.isDocumentUri;

public class Add_info_NEw extends MasterActivity {
    ActivityAddInfoNEwBinding binding;
    AppCompatEditText et_1, et_11, et_22, et_33, et_44;
    List<HashMap<String, AppCompatEditText>> list_link = new ArrayList<>();
    List<String> arr_link = new ArrayList<>();
    List<HashMap<String, String>> arr_doc = new ArrayList<>();
    List<String> arr_about = new ArrayList<>();
    List<HashMap<String, AppCompatEditText>> list_contact = new ArrayList<>();
    List<HashMap<String, String>> arr_contact = new ArrayList<>();
    List<HashMap<String, String>> arr_links = new ArrayList<>();
    private int id = 0;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private FileListAdapter fileListAdapter;
    private int id_contact = 0;
    private String userid;
    private static Uri contentUri;
    private AlertDialog alertDialog;


    int i;
    CommonMethods commonMethods=new CommonMethods();
    List<String> arr_l = new ArrayList<>();
    ArrayList<LinksModel> arrLinks=new ArrayList<>();
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.done); // extract the menu item here
        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        arr_contact.clear();
        arr_link.clear();
        arr_about.clear();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.done) {
            // String link=et_1.getText().toString();
            for (HashMap edit1 : list_link) {
                AppCompatEditText edit = (AppCompatEditText) edit1.get("link");
                String link = Objects.requireNonNull(edit.getText()).toString();
                if (link.length() > 0)
                    arr_link.add(link);
            }
            if (list_link.size() != arr_link.size()) {
                commonMethods.showAlert("Ange länk",context);
                //Toast.makeText(context, "Ange länk", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (fileListAdapter.getlist().size() > 0) {
                arr_doc.clear();
                for (MediaFile files : fileListAdapter.getlist()) {
                    if (!files.getName().startsWith("http")) {
                        HashMap map = new HashMap();
                        map.put("document_name", files.getName());
                        String encodedDoc = ConvertToString(files.getUri());
                      /*  File file = new File(getPath(context, files.getUri()));

                        int length = (int) file.length();

                        byte[] bytes = new byte[length];
                        try {
                            //fileInputStream.read(b);
                            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                                fileInputStream.read(bytes);
                            }
                            String encodedDoc = Base64.encodeToString(bytes, Base64.DEFAULT);*/
                        map.put("document", encodedDoc);

                        arr_doc.add(map);
                        // upload.setText(namefile);

                    }

                }
            }
            for (HashMap edit : list_contact) {
                AppCompatEditText name = (AppCompatEditText) edit.get("contact1");
                AppCompatEditText mobile = (AppCompatEditText) edit.get("contact2");
                AppCompatEditText email = (AppCompatEditText) edit.get("contact3");
                AppCompatEditText role = (AppCompatEditText) edit.get("contact5");
                String mob = Objects.requireNonNull(mobile.getText()).toString();
                String eml = Objects.requireNonNull(email.getText()).toString();
                String na = Objects.requireNonNull(name.getText()).toString();
                String rol = Objects.requireNonNull(role.getText()).toString();
                HashMap map = new HashMap();
                if (mob.length() > 0) {
                    map.put("mobile_no", mob);
                } else {
                    map.put("mobile_no", "");

                }
                if (na.length() == 0 && rol.length() == 0) {
                    commonMethods.showAlert("Fyll i antingen namn eller beskrivning", this);
                   // showToast("Fyll i antingen namn eller beskrivning");
                    break;
                }

                if (eml.length() == 0 && mob.length() == 0) {
                    commonMethods.showAlert("Fyll i telefonnummer eller e-postadress", this);

                   // showToast("Fyll i telefonnummer eller e-postadress");
                    break;
                }
                if (eml.length() > 0) {
                    if (new CommonMethods().isEmailValid(eml)) {
                        map.put("email", eml);
                    } else {
                        commonMethods.showAlert("Ange rätt e-postadress", this);

                        //showToast("Ange rätt e-postadress");
                        break;
                    }
                } else {
                    map.put("email", "");
                }
                if (rol.length() > 0) {
                    map.put("role", rol);
                } else {
                    map.put("role", "");

                }
                if (na.length() > 0) {
                    map.put("name", na);
                } else {
                    map.put("name", "");

                }

                map.put("website", "");

                arr_contact.add(map);


            }
            String lusgo_about = Html.toHtml(binding.about.getText());
            arr_about.add(lusgo_about);
            if (arr_contact.size() == 0 && list_contact.size() == 0) {
                alert(arr_about, arr_links, arr_contact, arr_doc);

            }
            if (list_contact.size() > 0) {
                if (arr_contact.size() == list_contact.size()) {
                    alert(arr_about, arr_links, arr_contact, arr_doc);
                }
            }

        }

        return super.onOptionsItemSelected(item);

    }

    public String ConvertToString(Uri uri) {
        String encodeDoc = "";
        String uriString = uri.toString();
        Log.d("data", "onActivityResult: uri" + uriString);
        //            myFile = new File(uriString);
        //            ret = myFile.getAbsolutePath();
        //Fpath.setText(ret);
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(in);
//            Log.d("data", "onActivityResult: bytes size="+bytes.length);
//            Log.d("data", "onActivityResult: Base64string="+Base64.encodeToString(bytes,Base64.DEFAULT));
            // String ansValue = Base64.encodeToString(bytes,Base64.DEFAULT);
            encodeDoc = Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.d("error", "onActivityResult: " + e.toString());
        }
        return encodeDoc;
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

    private void updateInfoAPI(List<String> about, List<HashMap<String, String>> arr_link, List<HashMap<String, String>> arr_contact, List<HashMap<String, String>> document) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
      //  et_1.setCursorVisible(false);
        try {

            Gson gson = new Gson();
            update asgn = new update();
            asgn.contactlist = arr_contact;
            asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
            asgn.user_id = userid;
            asgn.link = arr_link;
            asgn.about_us = arr_about;
            asgn.document = arr_doc;
            requestData = gson.toJson(asgn);
            Log.d("checkrequddata",requestData);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    arr_doc.clear();
                    new CommonMethods().showAlert("Informationen har uppdaterats", context);
                    getDocumentaAPI();
                }else {
                    commonMethods.showAlert(result.getMessage(),context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_information");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddInfoNEwBinding.inflate(getLayoutInflater());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uppdatera information");
        setContentView(binding.getRoot());
        userid = new CommonMethods().getPrefsData(Add_info_NEw.this, "id", "");
        fileListAdapter = new FileListAdapter(Add_info_NEw.this, mediaFiles);
        binding.fileList.setAdapter(fileListAdapter);
        getUserAPI();
//
//
//        ItemTouchHelper.SimpleCallback simple = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN, ItemTouchHelper.START|ItemTouchHelper.END) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//
//                RecyclerView.Adapter adapter = recyclerView.getAdapter();
//                int from = viewHolder.getAdapterPosition();
//                int to = target.getAdapterPosition();
//
//                adapter.notifyItemMoved(from, to);
//                Collections.swap(mediaFiles,from,to);
//                adapter.notifyDataSetChanged();
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//            }
//        };
//
//        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simple);
        //  itemTouchHelper.attachToRecyclerView(binding.fileList);

        //   binding.about.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        //  Add_link(id);
        binding.documentBtn.setOnClickListener(view -> {

            if (isPermissionGranted()) {
                Intent intent = new Intent(Add_info_NEw.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setSelectedMediaFiles(mediaFiles)
                        .setShowFiles(true)
                        .setShowImages(false)
                        .setShowVideos(false)

                        .setMaxSelection(10)
                        .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                        .build());
                startActivityForResult(intent, FILE_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(Add_info_NEw.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

        });
        //  Adddocument();
        binding.addLink.setOnClickListener(view -> {
            Add_link(id++);
        });
        binding.addContact.setOnClickListener(view -> {

            Add_Contact(id_contact++);

        });
        binding.llInfo.setVisibility(View.VISIBLE);
        binding.llContact1.setVisibility(View.GONE);
        binding.llDocument.setVisibility(View.GONE);
        binding.llLink1.setVisibility(View.GONE);
        binding.link.setOnClickListener(view -> {
            binding.link.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.link.setTextColor(Color.WHITE);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);
            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);
            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.VISIBLE);
        });
        binding.document.setOnClickListener(view -> {
            binding.document.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.document.setTextColor(Color.WHITE);
            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);

            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);
            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.VISIBLE);
            binding.llLink1.setVisibility(View.GONE);
        });
        binding.contact.setOnClickListener(view -> {
            binding.contact.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.contact.setTextColor(Color.WHITE);

            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);
            binding.info.setBackgroundColor(getResources().getColor(R.color.white));
            binding.info.setTextColor(Color.BLACK);

            binding.llInfo.setVisibility(View.GONE);
            binding.llContact1.setVisibility(View.VISIBLE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.GONE);
        });
        binding.info.setOnClickListener(view -> {
            binding.info.setBackgroundColor(getResources().getColor(R.color.bluedark));
            binding.info.setTextColor(Color.WHITE);

            binding.link.setBackgroundColor(getResources().getColor(R.color.white));
            binding.link.setTextColor(Color.BLACK);
            binding.document.setBackgroundColor(getResources().getColor(R.color.white));
            binding.document.setTextColor(Color.BLACK);

            binding.contact.setBackgroundColor(getResources().getColor(R.color.white));
            binding.contact.setTextColor(Color.BLACK);
            binding.llInfo.setVisibility(View.VISIBLE);
            binding.llContact1.setVisibility(View.GONE);
            binding.llDocument.setVisibility(View.GONE);
            binding.llLink1.setVisibility(View.GONE);
        });

        //setupViewPager(binding.viewpager);
        // Set Tabs inside Toolbar
        //   binding.resultTabs.setupWithViewPager(binding.viewpager);

      /*for (int i = 0; i < binding.resultTabs.getTabCount(); i++) {
            TabLayout.Tab tab = binding.resultTabs.getTabAt(i);
            RelativeLayout relativeLayout = (RelativeLayout)
                    LayoutInflater.from(this).inflate(R.layout.tab_layout, binding.resultTabs, false);

            TextView tabTextView = relativeLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText());
            tab.setCustomView(relativeLayout);
            tab.select();
        }*/
    }

    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new InfoAbout_fragment(), "Info");
        adapter.addFragment(new Link_fragment(), "Länkar");
        adapter.addFragment(new Doc_fragment(), "Dokument");
        adapter.addFragment(new Contact_fragment(), "Kontakt");

        viewPager.setAdapter(adapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            // mediaFiles.clear();
            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));


            fileListAdapter.notifyDataSetChanged();
        }
    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void Add_Contact(int id) {
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMarginStart(20);
        params1.setMarginEnd(20);
        params1.setMargins(0, 10, 0, 10);
        if (list_contact.size() > 0) {


            String val = Objects.requireNonNull(list_contact.get(list_contact.size() - 1).get("contact1")).getText().toString();
            String val1 = Objects.requireNonNull(list_contact.get(list_contact.size() - 1).get("contact5")).getText().toString();
            if (Objects.requireNonNull(val.length() > 0 || val1.length() > 0)) {
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(70, 70);
                params1.setMarginEnd(20);

                HashMap map = new HashMap();

                LinearLayout ll_main1 = new LinearLayout(Add_info_NEw.this);
                ll_main1.setOrientation(LinearLayout.VERTICAL);
                ll_main1.setBackgroundResource(R.drawable.layout_info);
                ll_main1.setPadding(20, 20, 20, 20);
                ll_main1.setLayoutParams(params1);
                ll_main1.setId(id);
                ll_main1.setGravity(Gravity.END);
                AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
                img.setImageResource(R.drawable.delete);
                img.setLayoutParams(params2);
                et_11 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
                et_11.setBackgroundResource(R.drawable.edittext);
                et_11.setHint("Namn");
                et_11.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(25), (src, start, end, dst, dsstart, dend) -> {
                    if (src.equals("")) {
                        return src;
                    }
                    if (src.toString().matches("[a-zA-Z ]+")) {
                        return src;
                    }
                    return "";
                }
                });
                et_11.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                et_11.setPadding(30, 30, 30, 30);
                et_44 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
                et_44.setBackgroundResource(R.drawable.edittext);
                et_44.setHint("Beskrivning");
                et_44.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                et_44.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                et_44.setPadding(30, 30, 30, 30);
                et_22 = new AppCompatEditText(Add_info_NEw.this);
                et_22.setBackgroundResource(R.drawable.edittext);
                et_22.setHint("Telefonnummer");
                et_22.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

                et_22.setPadding(30, 30, 30, 30);
                et_22.setInputType(InputType.TYPE_CLASS_NUMBER);

                et_33 = new AppCompatEditText(Add_info_NEw.this);
                et_33.setBackgroundResource(R.drawable.edittext);
                et_33.setHint("E-post");
                et_33.setPadding(30, 30, 30, 30);
                et_33.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                et_11.setLayoutParams(params1);
                et_22.setLayoutParams(params1);
                et_33.setLayoutParams(params1);
                et_44.setLayoutParams(params1);
                map.put("contact4", img);
                map.put("contact1", et_11);
                map.put("contact2", et_22);
                map.put("contact3", et_33);
                map.put("contact5", et_44);
                // et_1.setId(i);
                ll_main1.addView(et_11);
                ll_main1.addView(et_44);

                ll_main1.addView(et_22);
                ll_main1.addView(et_33);
                ll_main1.addView(img);

                list_contact.add(map);
                img.setOnClickListener(view -> {
                    //list_contact.remove(id);
                    View namebar = ll_main1.findViewById(ll_main1.getId());
                    ViewGroup parent = (ViewGroup) namebar.getParent();
                    if (parent != null) {
                        parent.removeView(namebar);
                        list_contact.remove(map);
                    }
                });
                binding.llContact.addView(ll_main1, 0);
            } else {
                commonMethods.showAlert("Vänligen ange kontakt", this);

                //showToast("Vänligen ange kontakt");

            }

        } else {
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(70, 70);
            params1.setMarginEnd(20);

            HashMap map = new HashMap();

            LinearLayout ll_main1 = new LinearLayout(Add_info_NEw.this);
            ll_main1.setOrientation(LinearLayout.VERTICAL);
            ll_main1.setBackgroundResource(R.drawable.layout_info);
            ll_main1.setPadding(20, 20, 20, 20);
            ll_main1.setLayoutParams(params1);
            ll_main1.setId(id);
            ll_main1.setGravity(Gravity.END);
            AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
            img.setImageResource(R.drawable.delete);
            img.setLayoutParams(params2);
            et_11 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
            et_11.setBackgroundResource(R.drawable.edittext);
            et_11.setHint("Skriv namn");
            et_11.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(25), (src, start, end, dst, dsstart, dend) -> {
                if (src.equals("")) {
                    return src;
                }
                if (src.toString().matches("[a-zA-Z ]+")) {
                    return src;
                }
                return "";
            }
            });
            et_11.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            et_11.setPadding(30, 30, 30, 30);
            et_44 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
            et_44.setBackgroundResource(R.drawable.edittext);
            et_44.setHint("Ange roll");

            et_44.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});

            et_44.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            et_44.setPadding(30, 30, 30, 30);
            et_22 = new AppCompatEditText(Add_info_NEw.this);
            et_22.setBackgroundResource(R.drawable.edittext);
            et_22.setHint("Ange mobilnummer");
            et_22.setPadding(30, 30, 30, 30);
            et_22.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_22.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

            et_33 = new AppCompatEditText(Add_info_NEw.this);
            et_33.setBackgroundResource(R.drawable.edittext);
            et_33.setHint("Skriv in e-mail");
            et_33.setPadding(30, 30, 30, 30);
            et_33.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            et_11.setLayoutParams(params1);
            et_22.setLayoutParams(params1);
            et_33.setLayoutParams(params1);
            et_44.setLayoutParams(params1);
            map.put("contact4", img);
            map.put("contact1", et_11);
            map.put("contact2", et_22);
            map.put("contact3", et_33);
            map.put("contact5", et_44);
            // et_1.setId(i);
            ll_main1.addView(et_11);
            ll_main1.addView(et_44);
            ll_main1.addView(et_22);
            ll_main1.addView(et_33);
            ll_main1.addView(img);

            list_contact.add(map);
            img.setOnClickListener(view -> {
                View namebar = ll_main1.findViewById(ll_main1.getId());
                ViewGroup parent = (ViewGroup) namebar.getParent();
                if (parent != null) {
                    parent.removeView(namebar);
                    list_contact.remove(map);

                }
            });
            binding.llContact.addView(ll_main1, 0);
        }

        et_11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {

                }
            }
        });
    }

    public void Add_link(int id) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMarginStart(20);
        params.setMarginEnd(20);
        params.setMargins(0, 10, 0, 10);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(60, 60);


        LinearLayout ll_main = new LinearLayout(Add_info_NEw.this);

        //  ll_main.setBackgroundResource(R.drawable.layout_info);
        ll_main.setPadding(20, 20, 20, 20);
        ll_main.setLayoutParams(params);
        ll_main.setId(id);
        if (et_1 != null) {
            if (Objects.requireNonNull(et_1.getText()).toString().length() > 0) {
                AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
                img.setImageResource(R.drawable.delete);
                img.setLayoutParams(params2);
                ll_main.setOrientation(LinearLayout.HORIZONTAL);
                ll_main.setGravity(Gravity.CENTER_VERTICAL);
                et_1 = new AppCompatEditText(Add_info_NEw.this);
                et_1.setBackgroundResource(R.drawable.edittext);
                et_1.setHint("Ange Länk");
                et_1.setPadding(30, 30, 30, 30);
                et_1.setId(id);
                et_1.setLayoutParams(params);
                HashMap map = new HashMap();
                map.put("link", et_1);
                // et_1.setId(i);
                list_link.add(0, map);
                img.setOnClickListener(view -> {
                    View namebar = ll_main.findViewById(ll_main.getId());
                    ViewGroup parent = (ViewGroup) namebar.getParent();
                    if (parent != null) {
                        parent.removeView(namebar);
                        int idd = et_1.getId();
                        list_link.remove(idd);
                    }
                });
                ll_main.addView(et_1, 0);
                ll_main.addView(img);

                binding.llLink.addView(ll_main, 0);

            } else {
                commonMethods.showAlert("Vänligen ange länk", this);
              //  showToast("Vänligen ange länk");
            }
        } else {
            AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
            img.setImageResource(R.drawable.delete);
            img.setLayoutParams(params2);
            ll_main.setOrientation(LinearLayout.HORIZONTAL);
            ll_main.setId(id);
            ll_main.setGravity(Gravity.CENTER_VERTICAL);
            et_1 = new AppCompatEditText(Add_info_NEw.this);
            et_1.setBackgroundResource(R.drawable.edittext);
            et_1.setHint("Ange Länk");
            et_1.setPadding(30, 30, 30, 30);
            et_1.setId(id);
            et_1.setLayoutParams(params);
            // et_1.setId(i);
            HashMap map = new HashMap();
            map.put("link", et_1);
            list_link.add(map);
            img.setOnClickListener(view -> {
                View namebar = ll_main.findViewById(ll_main.getId());
                ViewGroup parent = (ViewGroup) namebar.getParent();
                if (parent != null) {
                    parent.removeView(namebar);
                    int idd = et_1.getId();
                    // list_link.remove(idd);
                    list_link.remove(map);
                }
            });
            ll_main.addView(et_1, 0);
            ll_main.addView(img);
            binding.llLink.addView(ll_main);
        }
    }
/*
    public void Adddocument() {
        LinearLayout.LayoutParams params_main = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params_main.setMarginStart(20);
        params_main.setMarginEnd(20);
        params_main.setMargins(0, 10, 0, 10);


        LinearLayout ll_main_ = new LinearLayout(Add_info_NEw.this);

        ll_main_.setId(0);
        //ll_main_.setBackgroundResource(R.drawable.layout_info);
        ll_main_.setPadding(20, 20, 20, 20);
        ll_main_.setLayoutParams(params_main);
        AppCompatTextView et_1_ = new AppCompatTextView(Objects.requireNonNull(Add_info_NEw.this));
        et_1_.setBackgroundResource(R.drawable.outline_button);

        et_1_.setPadding(30, 30, 30, 30);
        et_1_.setGravity(Gravity.CENTER);

        et_1_.setLayoutParams(params_main);
        // et_1.setId(i);
        ll_main_.addView(et_1_);
        binding.llDocument.addView(ll_main_);
    }*/

    private void getUserAPI() {
        list_link.clear();
        arr_contact.clear();
        mediaFiles.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
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

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());
                    JSONArray document = object.getJSONArray("document");
                    JSONArray links = object.getJSONArray("links");
                    JSONArray about_us = object.getJSONArray("about_us");
                    JSONArray contactlist = object.getJSONArray("contactList");
                    for (int i = 0; i < contactlist.length(); i++) {
                        JSONObject obj = contactlist.getJSONObject(i);
                        HashMap map = new HashMap();
                        String email = obj.getString("email");
                        String mobile_no = obj.getString("mobile_no");
                        String name = obj.getString("name");
                        String role = obj.getString("role");
                        map.put("email", email);
                        map.put("mobile_no", mobile_no);
                        map.put("name", name);
                        map.put("role", role);
                        arr_contact.add(map);
                    }
                    runOnUiThread(() -> {
                        for (int i = 0; i < about_us.length(); i++) {
                            try {
                                //binding.about.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                                binding.about.setText(Html.fromHtml(about_us.get(0).toString()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        for (int i = 0; i < document.length(); i++) {
                            try {
                                JSONObject object1 = document.getJSONObject(i);
                                MediaFile file = new MediaFile();
                                try {
                                    file.setActual_name(object1.getString("documentName"));
                                    file.setPath(object1.getString("documentUrl"));
                                    File f = new File(object1.getString("documentUrl"));
                                    file.setName("http");
                                    file.setMimeType(f.getName());
                                    file.setBucketName(object1.getString("documentName"));
                                    mediaFiles.add(file);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                        for (int i = 0; i < links.length(); i++) {
                            try {
                                 JSONObject jsonObject=links.getJSONObject(i);
                                HashMap map = new HashMap();
                                String linkName=jsonObject.getString("linkName");
                                String id=jsonObject.getString("id");
                                String linkOrder=jsonObject.getString("linkOrder");
                                map.put("id",Integer.parseInt(id));
                                map.put("orderid",linkOrder);
                                arr_links.add(map);
                                arr_l.add(linkName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (arr_l.size() > 0) {
                            Collections.reverse(arr_l);
                        }
                        for ( i = 0; i < arr_l.size(); i++) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                            params.setMarginStart(20);
                            params.setMarginEnd(20);

                            params.setMargins(0, 10, 0, 10);
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(60, 60);
                            LinearLayout ll_main = new LinearLayout(Add_info_NEw.this);
                            //  ll_main.setBackgroundResource(R.drawable.layout_info);
                            ll_main.setPadding(20, 20, 20, 20);
                            ll_main.setLayoutParams(params);
                            ll_main.setOrientation(LinearLayout.HORIZONTAL);
                            ll_main.setGravity(Gravity.CENTER_VERTICAL);
                            ll_main.setId(i);
                            AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
                            img.setImageResource(R.drawable.delete);
                            img.setLayoutParams(params2);
                            et_1 = new AppCompatEditText(Add_info_NEw.this);
                            et_1.setBackgroundResource(R.drawable.edittext);
                            et_1.setText(arr_l.get(i));
                            et_1.setPadding(30, 30, 30, 30);
                            //    et_1.setId(i);

                            et_1.setLayoutParams(params);
                            HashMap map = new HashMap();
                            map.put("link", et_1);
                            // et_1.setId(i);
                            list_link.add(map);

                            img.setOnClickListener(view -> {
                              //  getDeleteAPI(arrLinks.get(i).getId(), Integer.parseInt(arr_l.get(i)));
                                int id = et_1.getId();
                                View namebar = ll_main.findViewById(ll_main.getId());
                                ViewGroup parent = (ViewGroup) namebar.getParent();
                                if (parent != null) {
                                    parent.removeView(namebar);
                                    //int idd = et_1.getId();
                                    list_link.remove(map);


                                }
                            });
                            ll_main.addView(et_1);
                            ll_main.addView(img);

                            binding.llLink.addView(ll_main);
                        }

                        fileListAdapter.notifyDataSetChanged();
                        if (arr_contact.size() > 0) {
                            for (int i = 0; i < arr_contact.size(); i++) {
                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params1.setMarginStart(20);
                                params1.setMarginEnd(20);
                                params1.setMargins(0, 10, 0, 10);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(70, 70);
                                params1.setMarginEnd(20);
                                params1.setMargins(0, 10, 0, 10);
                                HashMap map = new HashMap();
                                LinearLayout ll_main1 = new LinearLayout(Add_info_NEw.this);
                                ll_main1.setOrientation(LinearLayout.VERTICAL);
                                ll_main1.setBackgroundResource(R.drawable.layout_info);
                                ll_main1.setPadding(20, 20, 20, 20);
                                ll_main1.setLayoutParams(params1);
                                ll_main1.setId(i);
                                ll_main1.setGravity(Gravity.END);
                                AppCompatImageView img = new AppCompatImageView(Add_info_NEw.this);
                                img.setImageResource(R.drawable.delete);

                                et_11 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
                                et_11.setBackgroundResource(R.drawable.edittext);
                                et_11.setText(arr_contact.get(i).get("name"));
                                et_11.setPadding(30, 30, 30, 30);
                                et_11.setHint("Namn");
                                et_11.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                                et_11.setFilters(new InputFilter[]{
                                        new InputFilter.LengthFilter(25), new InputFilter() {
                                    @Override
                                    public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dsstart, int dend) {
                                        if (src.equals("")) {
                                            return src;
                                        }
                                        if (src.toString().matches("[a-zA-Z ]+")) {
                                            return src;
                                        }
                                        return "";
                                    }
                                }
                                });
                                /*et_11.setFilters(new InputFilter[]{
                                        (src, start, end, dst, dstart, dend) -> {
                                            if (src.equals("")) {
                                                return src;
                                            }
                                            if (src.toString().matches("[a-zA-Z ]+")) {
                                                return src;
                                            }
                                            return "";
                                        }
                                });*/

                                et_44 = new AppCompatEditText(Objects.requireNonNull(Add_info_NEw.this));
                                et_44.setBackgroundResource(R.drawable.edittext);
                                et_44.setHint("Beskrivning");
                                et_44.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                                et_44.setText(arr_contact.get(i).get("role"));
                                et_44.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                                et_44.setPadding(30, 30, 30, 30);
                                et_22 = new AppCompatEditText(Add_info_NEw.this);
                                et_22.setBackgroundResource(R.drawable.edittext);
                                et_22.setText(arr_contact.get(i).get("mobile_no"));
                                et_22.setPadding(30, 30, 30, 30);
                                et_22.setHint("Telefonnummer");
                                et_22.setInputType(InputType.TYPE_CLASS_NUMBER);
                                et_22.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

                                et_33 = new AppCompatEditText(Add_info_NEw.this);
                                et_33.setBackgroundResource(R.drawable.edittext);
                                et_33.setText(arr_contact.get(i).get("email"));
                                et_33.setPadding(30, 30, 30, 30);
                                et_33.setHint("E-post");
                                et_33.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                                img.setLayoutParams(params2);
                                et_11.setLayoutParams(params1);
                                et_22.setLayoutParams(params1);
                                et_33.setLayoutParams(params1);
                                et_44.setLayoutParams(params1);
                                map.put("contact4", img);
                                map.put("contact1", et_11);
                                map.put("contact2", et_22);
                                map.put("contact3", et_33);
                                map.put("contact5", et_44);
                                // et_1.setId(i);
                                ll_main1.addView(et_11);
                                ll_main1.addView(et_44);
                                ll_main1.addView(et_22);
                                ll_main1.addView(et_33);
                                ll_main1.addView(img);

                                list_contact.add(map);

                                img.setOnClickListener(view -> {
                                    View namebar = ll_main1.findViewById(ll_main1.getId());
                                    ViewGroup parent = (ViewGroup) namebar.getParent();
                                    if (parent != null) {
                                        parent.removeView(namebar);
                                        list_contact.remove(map);

                                    }
                                });
                                binding.llContact.addView(ll_main1);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
    }

    private void getDeleteAPI(String linkid, int pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map = new HashMap<>();
        map.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
//            object.put("user_id", userid);
            object.put("Id", linkid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }


        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    arr_l.remove(pos);
                    startActivity(new Intent(this,Add_info_NEw.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map).execute(ConsURL.BASE_URL_TEST2 + "delete_link");

    }


    private void getDocumentaAPI() {
        mediaFiles.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
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

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    List<String> arr_l = new ArrayList<>();
                    JSONObject object = new JSONObject(result.getData());
                    JSONArray document = object.getJSONArray("document");

                    runOnUiThread(() -> {
                        for (int i = 0; i < document.length(); i++) {
                            try {
                                JSONObject object1 = document.getJSONObject(i);
                                MediaFile file = new MediaFile();
                                try {
                                    file.setActual_name(object1.getString("documentName"));
                                    file.setPath(object1.getString("documentUrl"));
                                    File f = new File(object1.getString("documentUrl"));
                                    file.setName("http");
                                    file.setMimeType(f.getName());
                                    file.setBucketName(object1.getString("documentName"));
                                    mediaFiles.add(file);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        fileListAdapter.notifyDataSetChanged();


                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
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


    private void alert(List<String> about, List<HashMap<String, String>> arr_link, List<HashMap<String, String>> arr_contact, List<HashMap<String, String>> document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Add_info_NEw.this);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att uppdatera denna information?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(Add_info_NEw.this)) {
                if (binding.about.getText().toString().equals("")){
                      commonMethods.showAlert("Ange Information",this);
                }else {
                    updateInfoAPI(arr_about, arr_link, arr_contact, arr_doc);
                }

            } else {
                Toast.makeText(Add_info_NEw.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


}
