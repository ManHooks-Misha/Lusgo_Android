package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.NewsImageAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCreateMessegeBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CoachModel;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Messege;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Team_search;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.utils.ProgressViewHolder;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;


public class CreateMessage extends MasterActivity {

    private String userid, teamid;
    private final int FILE_SELECT_CODE = 0;
    private long mLastClickTime = 0;
    private Uri contentUri;
    private UserListAdapter.FriendFilters friendFilters;
    private ArrayList<HashMap<String, String>> images_post = new ArrayList<>();
    public final int REQUEST_IMAGE = 100;
    AlertDialog alertDialog;
    private CommonMethods cmn = new CommonMethods();
    public static ArrayList<HashMap<String, File>> images_path = new ArrayList<>();
    private ActivityCreateMessegeBinding binding;

    private static int VIEW_ITEM = 1, VIEW_PROGRESS = 0, LIMIT = 100;
    private boolean loadMoreForGroup = true, loadMoreForTeam = true, loadMoreForUser = true, loadMoreForCoach = true;

    private String encodedDoc1, txt_desc, txt_link = "", role = "";
    public static ArrayList<UserList> selectedUserList = new ArrayList<>();
    public static ArrayList<UserList> selectedUserListSearch = new ArrayList<>();
    public ArrayList<Teamlist> arr_team = new ArrayList<>();
    private CustomAdapter customAdapter;
    private ArrayList<String> selectedGroup = new ArrayList<>(), selectedCoach = new ArrayList<>();

    private ArrayList<Teamlist> teamArrayList = new ArrayList<>();
    public static ArrayList<UserList> userArrayList = new ArrayList<>();
    private ArrayList<GroupList> groupArrayList = new ArrayList<>();
    private ArrayList<CoachModel> coachArrayList = new ArrayList<>();
    private TeamListAdapter teamListAdapter;
    private UserListAdapter userListAdapter;
    private GroupListAdapter groupListAdapter;
    private CoachListAdapter coachListAdapter;
    private Teamlist selectedTeam = null;
    private NewsImageAdapter libraryHotAdapter;
    private static String path;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateMessegeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setTitle("Skriv meddelande");
        // Toast.makeText(context, "dfsdfdsdfdsffds", Toast.LENGTH_LONG).show();
        loadID();
        SearchActivityforMessage.isSearch = false;
        CommonMethods cmn = new CommonMethods();
        role = cmn.getPrefsData(context, "usertype", "");
        userid = cmn.getPrefsData(context, "id", "");
        teamid = cmn.getPrefsData(context, "team_id", "");
        images_path.clear();
        if (role.equals("3")){
            binding.infoCreateMsgTxt.setVisibility(View.GONE);
        }else {
            binding.infoCreateMsgTxt.setVisibility(View.VISIBLE);
        }
       /* if (role.equals("2") || role.equals("5")) {
            binding.llteamgroup.setVisibility(View.VISIBLE);
        }*/
        selectedUserListSearch.clear();
        selectedUserList.clear();
        if (role.equals("2") || role.equals("5")) {
            if (cmn.isOnline(context)) {
                getGroupAPI();
                getTeamAPI();
                binding.llTeams.setVisibility(View.GONE);
                binding.llmessageuser.setVisibility(View.GONE);
                coachListAdapter = new CoachListAdapter();
                binding.coachList.setAdapter(coachListAdapter);
                coachListAdapter.setOnLoadMoreListener(() -> getHandler().post(() -> {
                    coachArrayList.add(null);
                    coachListAdapter.notifyItemInserted(coachArrayList.size() - 1);
                    binding.coachList.postDelayed(this::getCoachAPI, 1000);
                }));
                getCoachAPI();
            } else {
                showToast(getResources().getString(R.string.internet));
            }


            binding.search.setOnClickListener(v -> {
                startActivity(new Intent(this, SearchActivityforMessage.class));

                //  binding.search.setIconified(false);
            });

            binding.infoCreateMsgTxt.setOnClickListener(v -> {
                cmn.customDialogCalander(CreateMessage.this,getResources().getString(R.string.msg_text));
            });
            binding.infoCreateMSGIMG.setOnClickListener(v -> {
                cmn.customDialogCalander(CreateMessage.this,getResources().getString(R.string.message_text));
            });
            SearchView.OnCloseListener closeListener = () -> {
                userArrayList.clear();
                userListAdapter.notifyDataSetChanged();

                return false;
            };
            //  binding.search.setOnCloseListener(closeListener);
            binding.ffGroup.setOnClickListener(view -> {
                binding.llmessagegroup.setVisibility(View.VISIBLE);
                if (binding.gMinus.getVisibility() == View.VISIBLE) {
                    binding.gMinus.setVisibility(View.GONE);
                    binding.gPlus.setVisibility(View.VISIBLE);
                    binding.llmessagegroup.setVisibility(View.GONE);
                } else {
                    binding.gPlus.setVisibility(View.GONE);
                    //   binding.llmessagegroup.setVisibility(View.GONE);
                }
                if (binding.llmessagegroup.getVisibility() == View.VISIBLE) {
                    binding.gMinus.setVisibility(View.VISIBLE);
                    binding.gPlus.setVisibility(View.GONE);
                    binding.teamPlus.setVisibility(View.VISIBLE);
                    binding.teamMinus.setVisibility(View.GONE);
                    binding.tPlus.setVisibility(View.VISIBLE);
                    binding.tMinus.setVisibility(View.GONE);
                } else {
                    binding.gPlus.setVisibility(View.VISIBLE);

                    binding.gMinus.setVisibility(View.GONE);

                }
                binding.coachHolder.setVisibility(View.GONE);
                binding.llteamgroup.setVisibility(View.GONE);
                binding.llmessageuser.setVisibility(View.GONE);

            });
            binding.ffCoach.setOnClickListener(view -> {
                binding.coachHolder.setVisibility(View.VISIBLE);
                if (binding.tMinus.getVisibility() == View.VISIBLE) {
                    binding.tMinus.setVisibility(View.GONE);
                    binding.tPlus.setVisibility(View.VISIBLE);
                    binding.coachHolder.setVisibility(View.GONE);
                } else {
                    binding.tPlus.setVisibility(View.GONE);
                    //   binding.llmessagegroup.setVisibility(View.GONE);
                }
                if (binding.coachHolder.getVisibility() == View.VISIBLE) {
                    binding.tMinus.setVisibility(View.VISIBLE);
                    binding.tPlus.setVisibility(View.GONE);
                    binding.teamPlus.setVisibility(View.VISIBLE);
                    binding.teamMinus.setVisibility(View.GONE);
                    binding.gPlus.setVisibility(View.VISIBLE);
                    binding.gMinus.setVisibility(View.GONE);
                } else {
                    binding.tPlus.setVisibility(View.VISIBLE);

                    binding.tMinus.setVisibility(View.GONE);

                }
                binding.llmessageuser.setVisibility(View.GONE);

                binding.llmessagegroup.setVisibility(View.GONE);
                binding.llteamgroup.setVisibility(View.GONE);

            });
            binding.ffTeam.setOnClickListener(view -> {
                binding.llteamgroup.setVisibility(View.VISIBLE);
                binding.llmessageuser.setVisibility(View.VISIBLE);
                if (binding.teamMinus.getVisibility() == View.VISIBLE) {
                    binding.teamMinus.setVisibility(View.GONE);
                    binding.teamPlus.setVisibility(View.VISIBLE);
                    binding.llteamgroup.setVisibility(View.GONE);
                    binding.llmessageuser.setVisibility(View.GONE);
                } else {
                    binding.teamPlus.setVisibility(View.GONE);
                    //   binding.llmessagegroup.setVisibility(View.GONE);
                }
                if (binding.llteamgroup.getVisibility() == View.VISIBLE) {
                    binding.teamMinus.setVisibility(View.VISIBLE);
                    binding.teamPlus.setVisibility(View.GONE);
                    binding.gPlus.setVisibility(View.VISIBLE);
                    binding.gMinus.setVisibility(View.GONE);
                    binding.tPlus.setVisibility(View.VISIBLE);
                    binding.tMinus.setVisibility(View.GONE);
                } else {
                    binding.teamPlus.setVisibility(View.VISIBLE);

                    binding.teamMinus.setVisibility(View.GONE);

                }
                binding.llmessagegroup.setVisibility(View.GONE);
                binding.coachHolder.setVisibility(View.GONE);

            });
        }

        else {
            binding.ffCoach.setVisibility(View.GONE);
            binding.ffTeam.setVisibility(View.GONE);
            binding.selectAll.setVisibility(View.GONE);
            binding.search.setOnClickListener(v -> {

                startActivity(new Intent(this, SearchActivityforMessage.class));

                //  binding.search.setIconified(false);
            });
            binding.infoCreateMSGIMG.setOnClickListener(v -> {
                cmn.customDialogMsg(CreateMessage.this,getResources().getString(R.string.message_text));
            });
            binding.ffGroup.setOnClickListener(view -> {
                binding.llmessagegroup.setVisibility(View.VISIBLE);
                if (binding.gMinus.getVisibility() == View.VISIBLE) {
                    binding.gMinus.setVisibility(View.GONE);
                    binding.gPlus.setVisibility(View.VISIBLE);
                    binding.llmessagegroup.setVisibility(View.GONE);
                } else {
                    binding.gPlus.setVisibility(View.GONE);
                    //   binding.llmessagegroup.setVisibility(View.GONE);
                }
                if (binding.llmessagegroup.getVisibility() == View.VISIBLE) {
                    binding.gMinus.setVisibility(View.VISIBLE);
                    binding.gPlus.setVisibility(View.GONE);

                } else {
                    binding.gPlus.setVisibility(View.VISIBLE);

                    binding.gMinus.setVisibility(View.GONE);

                }
                binding.coachHolder.setVisibility(View.GONE);
                binding.llteamgroup.setVisibility(View.GONE);
                //   binding.llmessageuser.setVisibility(View.GONE);

            });
//            binding.llmessagegroup.setVisibility(View.VISIBLE);
            binding.llmessageuser.setVisibility(View.VISIBLE);

            if (cmn.isOnline(context)) {
                userListAdapter.setOnLoadMoreListener(() -> getHandler().post(() -> {
                    userArrayList.add(null);
                    userListAdapter.notifyItemInserted(userArrayList.size() - 1);
                    binding.userList.postDelayed(this::getUserAPI, 2000);
                }));
                getGroupAPI();
                //getUserAPI();
                getTeamListAPI();


            } else {
                showToast(getResources().getString(R.string.internet));
            }

            binding.team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedUserList.clear();
                    userArrayList.clear();
                    binding.selectAllUser.setChecked(false);
                    teamid = arr_team.get(position).getId();
                    userListAdapter.notifyDataSetChanged();
                    getUserAPI();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        binding.lllink.setOnClickListener(view -> {
            binding.fflink.requestFocus();
            binding.fflink.setVisibility(View.VISIBLE);
        });
        binding.llimg.setOnClickListener(v -> {
            if (checkAndRequestPermissions(CreateMessage.this)) {
                chooseImage(CreateMessage.this);
            }
        });

        binding.lldoc.setOnClickListener(v -> {
            //  GetImageFromGallery();
            if (isPermissionGranted()) {
                showFileChooser();
            } else {
                ActivityCompat.requestPermissions(CreateMessage.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        });

        binding.attach.setOnClickListener(view -> binding.llattachments.setVisibility(binding.llattachments.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));

        binding.desc.setOnTouchListener((v, event) -> {
            if (v.getId() == R.id.desc) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
            return false;
        });

        binding.createmsg.setOnClickListener(view -> {

            txt_desc = Html.toHtml(binding.desc.getText());

            txt_link = Objects.requireNonNull(binding.linkedit.getText()).toString();
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (txt_desc.length() > 0) {
                ArrayList<String> arrayList = new ArrayList<>();
                ArrayList<HashMap<String, String>> arrayList_key = new ArrayList<>();
                for (UserList list : selectedUserList) {

                    if (!arrayList.contains(list.getId())) {
                        arrayList.add(list.getId());
                        HashMap map = new HashMap();
                        map.put("team_id", teamid);
                        map.put("User_id", list.getId());
                        arrayList_key.add(map);

                    }
                }

                for (UserList list : selectedUserListSearch) {

                    if (!arrayList.contains(list.getId())) {
                        arrayList.add(list.getId());
                        HashMap map = new HashMap();
                        map.put("team_id", list.getTeam_id());
                        map.put("User_id", list.getId());
                        arrayList_key.add(map);

                    }
                }
                if (binding.selectAllGroup.isChecked()) {
                    if (groupArrayList.size() == 0) {
                        cmn.showAlert("Det finns ingen grupp, avmarkera gruppen",this);
                       // showToast("Det finns ingen grupp, avmarkera gruppen");
                        return;
                    }
                }
                if (binding.selectAllCoach.isChecked()) {
                    if (coachArrayList.size() == 0) {
                        cmn.showAlert("Det finns ingen tränare, avmarkera tränaren",this);
                       // showToast("Det finns ingen tränare, avmarkera tränaren");
                        return;
                    }
                }

                if (role.equals(ConsURL.coach)) {
                    if (selectedGroup.size() > 0 || selectedCoach.size() > 0 || arrayList.size() > 0 || arrayList_key.size() > 0) {
                        createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList, arrayList_key);
                    } else {
                        cmn.showAlert("Välj grupp eller användare",this);
                       // showToast("Välj grupp eller användare");
                    }
                } else if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
                    if (binding.selectAll.isChecked() || selectedGroup.size() > 0 || selectedCoach.size() > 0 || arrayList.size() > 0)
                        createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList, arrayList_key);

                } else {

                }
            } else {
                cmn.showAlert("Skriv meddelande",this);
               // showToast("Skriv meddelande");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            userArrayList.clear();
            selectedUserList.clear();
            return true;
        }
        if (item.getItemId() == R.id.save) {
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
                ArrayList<HashMap<String, String>> arrayList_key = new ArrayList<>();
                for (UserList list : selectedUserList) {
                    HashMap map = new HashMap();


                    map.put("team_id", teamid);
                    map.put("User_id", list.getId());
                    arrayList_key.add(map);
                    arrayList.add(list.getId());


                }
                for (UserList list : selectedUserListSearch) {

                    HashMap map = new HashMap();
                    map.put("team_id", list.getTeam_id());
                    map.put("User_id", list.getId());
                    arrayList_key.add(map);
                    arrayList.add(list.getId());


                }
                if (binding.selectAllGroup.isChecked()) {
                    if (groupArrayList.size() == 0) {
                        cmn.showAlert("Det finns ingen grupp, avmarkera gruppen",this);
                       // showToast("Det finns ingen grupp, avmarkera gruppen");
                        return true;
                    }
                }
                if (binding.selectAllCoach.isChecked()) {
                    if (coachArrayList.size() == 0) {
                        cmn.showAlert("Det finns ingen tränare, avmarkera tränaren",this);
                        //showToast("Det finns ingen tränare, avmarkera tränaren");
                        return true;
                    }
                }
             /*   if (binding.selectAllUser.isChecked()) {
                    if (userArrayList.size() == 0) {
                        showToast("Det finns ingen användare, avmarkera användaren");
                        return;
                    }
                }*/

                if (role.equals(ConsURL.coach)) {
                    if (selectedGroup.size() > 0 || selectedCoach.size() > 0 || arrayList.size() > 0 || arrayList_key.size() > 0) {
                        // alert(txt_desc, txt_link, selectedGroup, arrayList);
                        createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList, arrayList_key);

                    } else {
                        cmn.showAlert("Välj grupp eller användare",this);
                       // showToast("Välj grupp eller användare");
                    }
                } else if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
                    if (binding.selectAll.isChecked() || selectedGroup.size() > 0 || selectedCoach.size() > 0 || arrayList.size() > 0 || arrayList_key.size() > 0) {
                        createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList, arrayList_key);
                    } else {
                        cmn.showAlert("Välj grupp eller användare eller alla användare",this);
                       // showToast("Välj grupp eller användare eller alla användare");

                    }
                } else if (role.equals(ConsURL.members) || role.equals(ConsURL.sub_coach)) {
                    createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList, arrayList_key);

                }
                /*if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin) || role.equals(ConsURL.coach)) {
                    if (selectedGroup.size() > 0 || selectedCoach.size() > 0 || arrayList.size() > 0) {
                        createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList);
                    } else {
                        showToast("Välj grupp eller användare");
                    }
                } else {
                    createMessageAPI(txt_desc, txt_link, selectedGroup, arrayList);

                }*/
            } else {
                cmn.showAlert("Skriv meddelande", this);
              //  showToast("Skriv meddelande");
            }
        }

        return super.onOptionsItemSelected(item);
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
                if (ContextCompat.checkSelfPermission(CreateMessage.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(CreateMessage.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(CreateMessage.this);
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
                    ImagePicker.create(CreateMessage.this).theme(R.style.AppTheme_No).limit(10).toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

                }
            }
        });
        builder.show();
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

    public void loadID() {
        libraryHotAdapter = new NewsImageAdapter(images_path, context, "msg");
        binding.imagelist.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        binding.imagelist.setAdapter(libraryHotAdapter);
        binding.llteamgroup.setVisibility(View.GONE);
        binding.selectAllUser.setOnClickListener(view -> {
            if (role.equals("3")) {
                if (binding.selectAllUser.isChecked()) {
                    for (UserList temp : userArrayList) {
                        if (temp != null) {
                            if (!selectedUserList.contains(temp)) {
                                selectedUserList.add(temp);
                            }
                        }
                    }
                } else {
                    selectedUserList.clear();
                }
                userListAdapter.notifyDataSetChanged();
                updateUI();
            } else {
                if (binding.selectAllUser.isChecked()) {// Click change checked states already
                    for (UserList temp : userArrayList) {
                        if (!selectedUserList.contains(temp)) {
                            selectedUserList.add(temp);
                        }
                    }
                } else {
                    selectedUserList.clear();
                }
                userListAdapter.notifyDataSetChanged();
                teamListAdapter.notifyDataSetChanged();
                updateUI();
            }
        });

        binding.selectAllTeam.setOnClickListener(view -> {
            if (binding.selectAllTeam.isChecked()) {
                binding.selectAll.setChecked(false);

                for (Teamlist teamlist : teamArrayList) {
                    if (teamlist != null) {
                        if (!teamlist.getUsers().isEmpty()) {
                            for (UserList userList : teamlist.getUsers()) {
                                if (!selectedUserList.contains(userList)) {
                                    selectedUserList.add(userList);
                                }
                                if (!userArrayList.contains(userList)) {
                                    userArrayList.add(userList);
                                }
                            }
                        }
                    }
                }
            } else {
                selectedUserList.clear();
            }
            teamListAdapter.notifyDataSetChanged();
            userListAdapter.notifyDataSetChanged();
            updateUI();
        });
        binding.selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.selectAllUser.setChecked(false);
                binding.selectAllGroup.setChecked(false);
                binding.selectAllCoach.setChecked(false);
                binding.selectAllTeam.setChecked(false);
                groupListAdapter.notifyDataSetChanged();
                userListAdapter.notifyDataSetChanged();
                teamListAdapter.notifyDataSetChanged();
                coachListAdapter.notifyDataSetChanged();
                selectedGroup.clear();
                selectedCoach.clear();
                selectedUserList.clear();
            }
        });
        teamListAdapter = new TeamListAdapter();
        binding.teamList.setAdapter(teamListAdapter);
        teamListAdapter.setOnLoadMoreListener(() -> getHandler().post(() -> {
            teamArrayList.add(null);
            teamListAdapter.notifyItemInserted(teamArrayList.size() - 1);
            binding.teamList.postDelayed(this::getTeamAPI, 1000);
        }));

        userListAdapter = new UserListAdapter(userArrayList);
        binding.userList.setAdapter(userListAdapter);

        binding.selectAllGroup.setOnClickListener(view -> {
            if (binding.selectAllGroup.isChecked()) {
                binding.selectAll.setChecked(false);

                for (GroupList temp : groupArrayList) {
                    if (temp != null) {
                        if (!selectedGroup.contains(temp.getGroup_id())) {
                            selectedGroup.add(temp.getGroup_id());
                        }
                    }
                }
            } else {
                selectedGroup.clear();
            }
            groupListAdapter.notifyDataSetChanged();
            updateUI();
        });

        binding.selectAllCoach.setOnClickListener(view -> {
            if (binding.selectAllCoach.isChecked()) {
                binding.selectAll.setChecked(false);

                for (CoachModel temp : coachArrayList) {
                    if (temp != null) {
                        if (!selectedCoach.contains(temp.getId())) {
                            selectedCoach.add(temp.getId());
                        }
                    }
                }
            } else {
                selectedCoach.clear();
            }
            coachListAdapter.notifyDataSetChanged();
            updateUI();
        });

        groupListAdapter = new GroupListAdapter();
        binding.grouplist.setAdapter(groupListAdapter);
        groupListAdapter.setOnLoadMoreListener(() -> getHandler().post(() -> {
            groupArrayList.add(null);
            LIMIT = groupArrayList.size() + 20;
            groupListAdapter.notifyItemInserted(groupArrayList.size() - 1);
            binding.grouplist.postDelayed(this::getGroupAPI, 1000);
        }));
    }

    private void getGroupAPI() {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", groupArrayList.isEmpty() ? 0 : groupArrayList.size() - 1);
            object.put("user_id", userid);
            object.put("team_id", teamid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (!groupArrayList.isEmpty()) {
                    int position = groupArrayList.size() - 1;
                    groupArrayList.remove(position);
                    groupListAdapter.notifyItemRemoved(position);
                }
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    loadMoreForGroup = obj.length() >= LIMIT;
                    for (int i = 0; i < obj.length(); i++) {
                        groupArrayList.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                groupListAdapter.setLoaded();
                groupListAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupList");
    }

    private void getCoachAPI() {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", LIMIT);
            object.put("offset", coachArrayList.isEmpty() ? 0 : coachArrayList.size() - 1);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (!coachArrayList.isEmpty()) {
                    int position = coachArrayList.size() - 1;
                    coachArrayList.remove(position);
                    coachListAdapter.notifyItemRemoved(position);
                }
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    loadMoreForCoach = obj.length() >= LIMIT;
                    for (int i = 0; i < obj.length(); i++) {
                        coachArrayList.add((CoachModel) (getObject(obj.getString(i), CoachModel.class)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                coachListAdapter.setLoaded();
                coachListAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }

    private void getTeamAPI() {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", LIMIT);
            object.put("offset", teamArrayList.isEmpty() ? 0 : teamArrayList.size() - 1);
            object.put("user_id", userid);
            object.put("coach_id", "");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (!teamArrayList.isEmpty()) {
                    int position = teamArrayList.size() - 1;
                    teamArrayList.remove(position);
                    teamListAdapter.notifyItemRemoved(position);
                }
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    loadMoreForTeam = obj.length() >= LIMIT;
                    for (int i = 0; i < obj.length(); i++) {
                        Teamlist teamlist = (Teamlist) (getObject(obj.getString(i), Teamlist.class));
                        teamArrayList.add(teamlist);
                        if (binding.selectAllTeam.isChecked()) {
                            for (UserList userList : teamlist.getUsers()) {
                                if (!selectedUserList.contains(userList)) {
                                    selectedUserList.add(userList);
                                }
                                if (!userArrayList.contains(userList)) {
                                    userArrayList.add(userList);
                                }
                            }
                            userListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                teamListAdapter.setLoaded();
                teamListAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "teamList");
    }

    private void getUserAPI() {
        userArrayList.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", LIMIT);
            object.put("offset", userArrayList.isEmpty() ? 0 : userArrayList.size() - 1);
            object.put("user_id", userid);
            object.put("team_id", teamid);
            object.put("block_users", "0");

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }
        new NetworkCall(null, result -> {
            try {
                if (!teamArrayList.isEmpty()) {
                    int position = teamArrayList.size() - 1;
                    teamArrayList.remove(position);
                    teamListAdapter.notifyItemRemoved(position);
                }
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());
                    if (objarr.length() > 0) {
                        JSONArray obj = objarr.getJSONArray("userList");
                        loadMoreForUser = obj.length() >= LIMIT;
                        for (int i = 0; i < obj.length(); i++) {
                            userArrayList.add((UserList) (getObject(obj.getString(i), UserList.class)));
                        }
                        if (binding.selectAllUser.isChecked()) {
                            for (UserList list : userArrayList) {
                                //Usser List jb milti hoh team ki id bhi leni pdegi aapko fir wo kaam apne aap ho jaaega
                                if (!selectedUserList.contains(list)) {
                                    selectedUserList.add(list);
                                    //  updateSelectedUserText();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                userListAdapter.setLoaded();
                userListAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    @Override
    protected void onResume() {
        super.onResume();
        userListAdapter.notifyDataSetChanged();
        if (SearchActivityforMessage.isSearch == true) {
            binding.llmessageuser.setVisibility(userArrayList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    public class GroupListAdapter extends RecyclerView.Adapter {

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        private GroupListAdapter() {
            if (binding.grouplist.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.grouplist.getLayoutManager();
                binding.grouplist.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (loadMoreForGroup) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return groupArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new GroupViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_small, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof GroupViewHolder) {
                ((GroupViewHolder) holder).setData(groupArrayList.get(position));
            } else {
                ((ProgressViewHolder) holder).setData();
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return groupArrayList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView selecttext;
            private AvatarView img;
            private TextView txtview;
            private RelativeLayout rl_parent;

            GroupViewHolder(View view) {
                super(view);
                rl_parent = view.findViewById(R.id.rl_parent);
                txtview = view.findViewById(R.id.tv_name);
                img = view.findViewById(R.id.iv_profile);
                selecttext = view.findViewById(R.id.iv_selected);
            }

            void setData(GroupList data) {
                txtview.setText(data.getName());
                selecttext.setVisibility(selectedGroup.contains(data.getGroup_id()) ? View.VISIBLE : View.GONE);

                if (data.getName().length() > 0) {
                    String name = String.valueOf(data.getName().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context).load(data.getImage()).centerCrop().placeholder(drawable).into(img);
                }
                rl_parent.setOnClickListener(view -> {
                    if (selectedGroup.contains(data.getGroup_id())) {
                        selectedGroup.remove(data.getGroup_id());
                    } else {
                        binding.selectAll.setChecked(false);

                        selectedGroup.add(data.getGroup_id());
                    }
                    log(String.valueOf(groupArrayList.indexOf(data)));
                    notifyItemChanged(groupArrayList.indexOf(data));
                    updateUI();
                });
            }
        }

        private void updateUI() {
            ArrayList<String> tempList = new ArrayList<>();
            for (GroupList list : groupArrayList) {
                if (list != null) {
                    if (!tempList.contains(list.getGroup_id())) {
                        tempList.add(list.getGroup_id());
                    }
                }
            }
            Collections.sort(tempList);
            Collections.sort(selectedGroup);
            binding.selectAllGroup.setChecked(tempList.equals(selectedGroup));
        }
    }

    public class CoachListAdapter extends RecyclerView.Adapter {

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        private CoachListAdapter() {
            if (binding.coachList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.coachList.getLayoutManager();
                binding.coachList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (loadMoreForCoach) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return coachArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new CoachViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_small, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CoachViewHolder) {
                ((CoachViewHolder) holder).setData(coachArrayList.get(position));
            } else {
                ((ProgressViewHolder) holder).setData();
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return coachArrayList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        class CoachViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView selecttext;
            private AvatarView img;
            private TextView txtview;
            private RelativeLayout rl_parent;

            CoachViewHolder(View view) {
                super(view);
                rl_parent = view.findViewById(R.id.rl_parent);
                txtview = view.findViewById(R.id.tv_name);
                img = view.findViewById(R.id.iv_profile);
                selecttext = view.findViewById(R.id.iv_selected);
            }

            void setData(CoachModel data) {
                txtview.setText(data.getFirstname());
                selecttext.setVisibility(selectedCoach.contains(data.getId()) ? View.VISIBLE : View.GONE);
                if (data.getFirstname().length() > 0) {
                    String name = String.valueOf(data.getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);
                }

                rl_parent.setOnClickListener(view -> {
                    if (selectedCoach.contains(data.getId())) {
                        selectedCoach.remove(data.getId());
                    } else {
                        binding.selectAll.setChecked(false);

                        selectedCoach.add(data.getId());
                    }
                    notifyItemChanged(coachArrayList.indexOf(data));
                    updateUI();
                });
            }
        }

        private void updateUI() {
            ArrayList<String> tempList = new ArrayList<>();
            for (CoachModel list : coachArrayList) {
                if (list != null) {
                    if (!tempList.contains(list.getId())) {
                        tempList.add(list.getId());
                    }
                }
            }
            Collections.sort(tempList);
            Collections.sort(selectedCoach);
            binding.selectAllCoach.setChecked(tempList.equals(selectedCoach));

        }
    }

    public class TeamListAdapter extends RecyclerView.Adapter {

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;

        private TeamListAdapter() {
            if (binding.teamList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.teamList.getLayoutManager();
                binding.teamList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (loadMoreForTeam) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return teamArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new TeamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_small, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TeamViewHolder) {
                ((TeamViewHolder) holder).setData(teamArrayList.get(position));
            } else {
                ((ProgressViewHolder) holder).setData();
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return teamArrayList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        class TeamViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView selecttext;
            private AvatarView img;
            private TextView txtview;
            private RelativeLayout rl_parent;

            TeamViewHolder(View view) {
                super(view);
                rl_parent = view.findViewById(R.id.rl_parent);
                txtview = view.findViewById(R.id.tv_name);
                img = view.findViewById(R.id.iv_profile);
                selecttext = view.findViewById(R.id.iv_selected);
            }

            void setData(Teamlist data) {
                if (data.getName().length() > 0) {
                    String name = String.valueOf(data.getName().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView).load(data.getLogo()).centerCrop().placeholder(drawable).into(img);

                } else if (data.getUsername().length() > 0) {
                    String name = String.valueOf(data.getUsername().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView).load(data.getLogo()).centerCrop().placeholder(drawable).into(img);

                }
                txtview.setText(data.getName());
//                if (selectedTeam == data) {
//                    userArrayList.removeAll(data.getUsers());
//                    rl_parent.setBackgroundResource(R.drawable.item_select);
//                    if (!data.getUsers().isEmpty()) {
//                        binding.selectAllUser.setChecked(selectedUserList.containsAll(data.getUsers()));
//                    }
//                } else {
//                    rl_parent.setBackgroundColor(Color.TRANSPARENT);
//                }
                selecttext.setVisibility(data.getUsers().isEmpty() ? View.GONE : (selectedUserList.containsAll(data.getUsers()) ? View.VISIBLE : View.GONE));


                itemView.setOnClickListener(view -> {
                    if (data.getUsers().isEmpty()) {
                        cmn.showAlert("Ingen användare hittades i detta team",context);

                      //  showToast("Ingen användare hittades i detta team");
                    } else {
                        if (userArrayList.containsAll(data.getUsers())) {
                            userArrayList.removeAll(data.getUsers());
                            selectedUserList.removeAll(data.getUsers());
                            selectedTeam = null;
                        } else {
                            int i = teamArrayList.indexOf(selectedTeam);
                            selectedTeam = data;
                            if (!data.getUsers().isEmpty()) {
                                for (UserList temp : data.getUsers()) {
                                    if (!userArrayList.contains(temp)) {
                                        userArrayList.add(0, temp);
                                    }
                                    if (!selectedUserList.contains(temp)) {
                                        selectedUserList.add(temp);
                                    }
                                }
                            }
                            notifyItemChanged(i);
                        }
                        userListAdapter.notifyDataSetChanged();
                        notifyItemChanged(getAdapterPosition());
                        updateUI();
                    }
                });
            }
        }
    }

    private class UserListAdapter extends RecyclerView.Adapter implements Filterable {
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        ArrayList<UserList> filtedlist;

        private UserListAdapter(ArrayList<UserList> filtedlist) {
            this.filtedlist = filtedlist;
            if (binding.userList.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.userList.getLayoutManager();
                binding.userList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (loadMoreForUser) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public Filter getFilter() {

            if (friendFilters == null) {
                friendFilters = new FriendFilters();
            }

            return friendFilters;
        }

        class FriendFilters extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    ArrayList<UserList> tempList = new ArrayList<>();

                    // search content in friend list
                    for (UserList user : filtedlist) {
                        if (user.getFirstname().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tempList.add(user);
                        }
                    }
                    for (UserList user : filtedlist) {
                        for (Team_search team : user.getTeams()) {
                            if (team.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                tempList.add(user);
                            }
                        }
                    }

                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                } else {
                    filterResults.count = filtedlist.size();
                    filterResults.values = filtedlist;
                }

                return filterResults;
            }


            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userArrayList = (ArrayList<UserList>) results.values;
                if (userArrayList == null) {
                    userArrayList = new ArrayList<>();
                }

                notifyDataSetChanged();

            }
        }

        @Override
        public int getItemViewType(int position) {
            return userArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_user, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                ((UserViewHolder) holder).setData(userArrayList.get(position));
            } else {
                ((ProgressViewHolder) holder).setData();
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return userArrayList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        private class UserViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView iv_selected;
            private AvatarView img;
            private TextView txtview, tv_teamName;

            UserViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);
                tv_teamName = view.findViewById(R.id.team_name);
                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);
            }

            void setData(UserList data) {
                tv_teamName.setVisibility(View.GONE);
                if (role.equals("2") || role.equals("5")) {

                    tv_teamName.setVisibility(View.VISIBLE);
                    if (data.getTeam_name().length() > 0) {
                        tv_teamName.setText(data.getTeam_name());
                    } else {
                        tv_teamName.setText(data.getRole());

                    }
                }
                if (data.getFirstname().length() > 0) {
                    txtview.setText(data.getFirstname());
                } else {
                    txtview.setText(data.getUsername());

                }
                iv_selected.setVisibility(selectedUserList.contains(data) ? View.VISIBLE : View.GONE);

                if (data.getFirstname().length() > 0) {
                    String name = String.valueOf(data.getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);

                }
//                if (data.getUsername().length() > 0) {
//                    String name = String.valueOf(data.getUsername().trim().charAt(0));
//                    TextDrawable drawable = TextDrawable.builder()
//                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
//                    Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);
//
//                }

                itemView.setOnClickListener(view -> {
                    if (selectedUserList.contains(data)) {
                        selectedUserList.remove(data);
                    } else {
                        binding.selectAll.setChecked(false);
                        selectedUserList.add(data);
                    }
//                    updateSelectedUserText();
                    if (role.equals("2") || role.equals("5")) {
                        teamListAdapter.notifyDataSetChanged();
                    }
//                    new Handler(Looper.getMainLooper()).postDelayed(() -> userListAdapter.notifyItemChanged(userArrayList.indexOf(data)), 100);
                    userListAdapter.notifyItemChanged(userArrayList.indexOf(data));
                    updateUI();
                });
            }
        }
    }

    private void updateUI() {
        if (role.equals("3")) {
            ArrayList<UserList> tempList = new ArrayList<>();
            for (UserList userList : userArrayList) {
                if (userList != null) {
                    if (!tempList.contains(userList)) {
                        tempList.add(userList);
                    }
                }
            }
            Collections.sort(tempList);
            Collections.sort(selectedUserList);
            binding.selectAllUser.setChecked(tempList.equals(selectedUserList));
        } else if (role.equals("2") || role.equals("5")) {
            ArrayList<UserList> tempList = new ArrayList<>();
            for (Teamlist tempTeam : teamArrayList) {
                if (tempTeam != null) {
                    for (UserList userList : tempTeam.getUsers()) {
                        if (!tempList.contains(userList)) {
                            tempList.add(userList);
                        }
                    }
                }
            }
            binding.llmessageuser.setVisibility(userArrayList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.selectAllUser.setChecked(selectedUserList.containsAll(userArrayList));
            binding.selectAllTeam.setChecked(selectedUserList.containsAll(tempList));
        }
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
                        if (images_path.size() > 9) {
                            cmn.showAlert("max 10 bilder tillåtna", this);
                        } else {
                            images_path.add(map);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                libraryHotAdapter.notifyDataSetChanged();

            }
        }
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getParcelableExtra("path");
                // String imagepath1 = uri.getPath();
                Log.e("imagepath1", String.valueOf(uri));

                Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                // binding.sampleImage.setImageBitmap(selectedImage);

                File finalFile = new File(getRealPathFromURI(getImageUri(this, selectedImage)));


                //CompressFile compressFile = new CompressFile();
                assert String.valueOf(finalFile) != null;
                //  File path = compressFile.getCompressedImageFile(new File(imagepath1), context);
                HashMap<String, File> map = new HashMap<>();
                map.put("Path", new File(String.valueOf(finalFile)));
                if (images_path.size() > 9) {
                    cmn.showAlert("max 10 bilder tillåtna", this);
                } else {
                    images_path.add(map);
                }

                // imagepath = resultUri.getPath();


            }
            libraryHotAdapter.notifyDataSetChanged();
        }
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            assert resultUri != null;
            String imagepath = resultUri.getPath();


            //CompressFile compressFile = new CompressFile();
            //  File path = compressFile.getCompressedImageFile(new File(imagepath), context);
            HashMap<String, File> map = new HashMap<>();
            map.put("Path", new File(imagepath));
            images_path.add(map);
            libraryHotAdapter.notifyDataSetChanged();


        }
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
            try {
                Uri uri = data.getData();
                ConvertToString(uri);
                getFileName(uri);
              //  binding.txtdoc.setText("Dokument");
                binding.ffdoc.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                showToast(Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
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
                    binding.txtdoc.setText(result);
                    binding.docImg.setVisibility(View.VISIBLE);
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

    private void createMessageAPI(String txt_desc, String txt_link, ArrayList<String> groups, ArrayList<String> users, ArrayList<HashMap<String, String>> users_key) {
        //  arr.clear();
        images_post.clear();
        Gson gson = new Gson();

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
        asgn.Team_users = users_key;

        for (String id : selectedCoach) {
            if (!users.contains(id)) {
                users.add(id);
            }
        }

        asgn.users = users;
        asgn.team_id = teamid;

        asgn.all_coaches = binding.selectAllCoach.isChecked();
        asgn.all_users = binding.selectAll.isChecked();
        asgn.all_groups = binding.selectAllGroup.isChecked();
        asgn.all_teams = binding.selectAllTeam.isChecked();

        if (images_path.size() > 0) {
            for (int i = 0; i < images_path.size(); i++) {
                File file = Objects.requireNonNull(images_path.get(i).get("Path"));

                int length = (int) file.length();
               // binding.txtdoc.setText(file.getName());
               // binding.ffdoc.setVisibility(View.VISIBLE);

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

        requestData = gson.toJson(asgn);
        Log.d("dfaskjjlfjsal", requestData);

        //requestData = getJSON(asgn);
        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    images_path.clear();
                    selectedUserList.clear();
                    selectedUserListSearch.clear();
                    showCustomDialog();
                    //showToast("Meddelande sänt framgångsrikt");
//                    startActivity(new Intent(context, HomePage.class));
//                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "add_message");
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
        create.setText("Meddelandet har skickats");
        ok.setOnClickListener(view -> {

            startActivity(new Intent(CreateMessage.this, HomePage.class).putExtra("Open", "message"));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void getTeamListAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
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
                        arr_team.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }
                    customAdapter = new CustomAdapter(CreateMessage.this, arr_team);
                    binding.team.setAdapter(customAdapter);

                    //}


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
                        .centerCrop()
                        .into(icon);
            } else if (teams.get(i).getUsername().trim().length() > 0) {

                String name = String.valueOf(teams.get(i).getUsername().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));

                Glide.with(context)
                        .load(teams.get(i).getLogo())
                        .placeholder(drawable)
                        .centerCrop()
                        .into(icon);
            }
            names.setText(teams.get(i).getName());
            return view;
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + System.currentTimeMillis(), null);
        Log.e("path", path);

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