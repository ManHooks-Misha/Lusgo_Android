package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.app.SyrianskaTaekwondo.hejtelge.adapters.GroupchatAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.CompressImg;
import com.app.SyrianskaTaekwondo.hejtelge.database.Chatfloatmessage;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityGroupChatBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupChatMessage;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ApiCall;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CountingRequestBody;
import com.app.SyrianskaTaekwondo.hejtelge.utility.RequestBuilder;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubEventListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubMessage;
import com.smartarmenia.dotnetcoresignalrclientjava.WebSocketHubConnectionP2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import aspnet.signalr.WebSocketClientListener;
import aspnet.signalr.WebSocketTransportExtension;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import vk.help.Common;
import vk.help.MasterActivity;
import vk.help.imagepicker.features.ImagePicker;
import vk.help.imagepicker.model.Image;
import vk.help.views.TextDrawable;

import static android.os.Build.VERSION.SDK_INT;

public class GroupChatActivity extends MasterActivity implements HubConnectionListener, HubEventListener {
    GroupchatAdapter mAdapter;
    ArrayList<GroupChatMessage> arr_chat = new ArrayList<>();
    ActivityGroupChatBinding binding;
    HubConnection connection;
    CommonMethods cmn = new CommonMethods();
    private String userid, usertype, groupid, group_name, coachid = "", group_img, chatfor, startchat;
    private String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Ijc5NzhjMjI3LWViMGItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTYxYzMxYmIiLCJuYmYiOjE1MDYyODQ4NzMsImV4cCI6NDY2MTk1ODQ3MywiaWF0IjoxNTA2Mjg0ODczLCJpc3MiOiJCbGVuZCIsImF1ZCI6IkJsZW5kIn0.QUh241IB7g3axLcfmKR2899Kt1xrTInwT6BBszf6aP4";
    GroupList data_item;
    Teamlist data;
    String User_name = "";
    boolean hidden = true;
    ProgressBar progressbar;
    public static int index_post = 0;
    public static ArrayList<File> images = new ArrayList<>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static String path;
    Uri image_uri;

    String isopen, id, responseMsg, timeoutMessage;
    MediaType MEDIA_TYPE;
    public static boolean OpenActivity = false;
    Chatfloatmessage chattablefloat;
    int index = 0;
    ArrayList<HashMap<String, String>> postimage = new ArrayList<>();
    public final int REQUEST_IMAGE = 100;
    private OkHttpClient client = new OkHttpClient();
    private String response;
    private String backValue = "";

    private static String URL = "https://web.lusgo.se/NotificationHub?UserId=" + "1";
    private aspnet.signalr.HubConnection connection1;
    private WebSocketTransportExtension webSocketTransport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        setContentView(binding.getRoot());
        // progressbar=findViewById(R.id.chat_progressar);
        //  Log.d("fadmsklkf;lka;sf","1111111");
        OpenActivity = true;
        userid = cmn.getPrefsData(GroupChatActivity.this, "id", "");
        usertype = cmn.getPrefsData(GroupChatActivity.this, "usertype", "");
        if (userid.length() == 0) {
            binding.bottomBar.setVisibility(View.GONE);
            binding.userRecord.setText("Chatten är stängd");
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
        }


        connection = new WebSocketHubConnectionP2("https://web.lusgo.se/NotificationHub?UserId=" + userid, authHeader);
        connection.addListener(GroupChatActivity.this);
        connection.subscribeToEvent("GroupChattingNew", GroupChatActivity.this);
        connection.connect();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        groupid = bundle.getString("id");
        group_name = bundle.getString("name");
        coachid = bundle.getString("coachid");
        group_img = bundle.getString("image");
        chatfor = bundle.getString("chatfor");
        backValue = bundle.getString("backValue");
        chattablefloat = new Chatfloatmessage(this).getInstance(this);
        binding.revealItems2.setVisibility(View.INVISIBLE);
        data_item = (GroupList) getObject(getIntent().getStringExtra("data"), GroupList.class);
        data = (Teamlist) getObject(getIntent().getStringExtra("data"), Teamlist.class);
        startchat = bundle.getString("isopen");

        isopen = data_item.getChatstarted();
        id = intent.getStringExtra("id");
        binding.attach.setOnClickListener(v -> onProfileImageClick());
        binding.galleryImg.setOnClickListener(v -> {
            binding.revealItems2.setVisibility(View.GONE);
            launchGalleryIntent();
        });
        binding.cameraImg.setOnClickListener(v -> {
            //    launchCameraIntent();
            if (checkAndRequestPermissions(GroupChatActivity.this)) {
                chooseImage(GroupChatActivity.this);
            }


            binding.revealItems2.setVisibility(View.GONE);

        });
        List<UserList> arr_user = data_item.getUsers();
        for (int i = 0; i < arr_user.size(); i++) {
            String user = arr_user.get(i).getFirstname();
            User_name = User_name + ", " + user;
        }
        if (isopen.equals("True") || startchat.equals("true")) {
            binding.bottomBar.setVisibility(View.VISIBLE);
        } else {

            binding.bottomBar.setVisibility(View.GONE);
            binding.userRecord.setText("Chatten är stängd");

        }
        User_name = User_name.replaceFirst(", ", "");
        binding.users.setText(User_name);

        group_img = bundle.getString("image");
        binding.txt.setText(group_name);
        String name = String.valueOf(data_item.getName().trim().charAt(0));
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
        Glide.with(this)
                .load(group_img)
                .fitCenter()
                .placeholder(drawable)
                .into(binding.pos);
        binding.back.setOnClickListener(v -> {
            if (backValue.equals("1")) {
                startActivity(new Intent(GroupChatActivity.this, EditGroup.class)
                        .putExtra("data", Common.INSTANCE.getJSON(data_item))
                        .putExtra("id", groupid)
                        .putExtra("name", group_name)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (backValue.equals("2")) {
                startActivity(new Intent(GroupChatActivity.this, EditTeam.class)
                        .putExtra("data", Common.INSTANCE.getJSON(data))
                        .putExtra("id", groupid).putExtra("name", group_name).putExtra("coachid", coachid));
            } else {
                startActivity(new Intent(GroupChatActivity.this, GroupChatList.class));
            }

            finish();
        });
        binding.llUsers.setOnClickListener(v -> {
            if (!data_item.getGroup_id().equalsIgnoreCase("")) {
                startActivity(new Intent(GroupChatActivity.this, GroupChatDetailsUser.class)
                        .putExtra("data", Common.INSTANCE.getJSON(data_item))
                        .putExtra("id", data_item.getGroup_id())
                        .putExtra("name", data_item.getName())
                        .putExtra("image", data_item.getImage())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
            if (!id.equalsIgnoreCase("")) {
                startActivity(new Intent(GroupChatActivity.this, GroupChatDetailsUser.class)
                        .putExtra("data", Common.INSTANCE.getJSON(data_item))
                        .putExtra("id", id).putExtra("name", data_item.getName())
                        .putExtra("image", data_item.getImage())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

            }

        });
        runOnUiThread(() -> loadData());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commentsList.setLayoutManager(linearLayoutManager);
        mAdapter = new GroupchatAdapter(arr_chat, binding.commentsList, GroupChatActivity.this);
        binding.commentsList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        binding.sendMessage.setOnClickListener(view -> {
            if (Objects.requireNonNull(binding.editMessage.getText()).toString().isEmpty()) {
                cmn.showAlert("Skriv kommentar",this);
              //  showToast("Skriv kommentar");
            } else {
                //  Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_anim);
                //  binding.sendMessage.startAnimation(animation);
                //  binding.chatProgressar.setVisibility(View.VISIBLE);
                String desc = Html.toHtml(binding.editMessage.getText());
                desc = desc.replaceAll("<u>", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                desc = desc.replaceAll("</u>", "");

                try {
                   // connection1.start();
                   // connection1.send("GroupChattingNew","5b3635543abb33403ca433c1:5b3635543abb33403ca433c1");
                    connection.invoke("GroupChattingNew", userid, groupid, desc, chatfor, authHeader);
                } catch (Exception e) {
                    Toast.makeText(GroupChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                binding.editMessage.setText("");

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  Log.d("dfsajkljfk","onDestory");
        OpenActivity = false;

    }
    @Override
    public void onPause() {
        super.onPause();
        OpenActivity = false;
      //  Log.d("messgae","onPause");
    }

  /*  @Override
    public void onStart() {
        super.onStart();
        Log.d("messgae","onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("messgae","onResume");
    }



    @Override
    public void onStop() {
        super.onStop();
        Log.d("messgae","onStop");
    }
    */
    private void initConnection() {
        try {
            webSocketTransport = new WebSocketTransportExtension(URL, new WebSocketClientListener() {
                @Override
                public void onOpen() {
                    System.out.println("Connected to " + URL);
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Connected to " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection Closed");
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            });

            connection1 = new aspnet.signalr.HubConnection(URL, webSocketTransport);
            connection1.on("newMessage", (message) ->
                    System.out.println("REGISTERED HANDLER: " + message), String.class);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void launchGalleryIntent() {
        ImagePicker.create(this).theme(R.style.AppTheme_No).limit(10).toolbarArrowColor(Color.BLACK).showCamera(false).toolbarImageTitle("Välj bild").toolbarDoneButtonText("Välj").start();

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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            int cx = binding.revealItems2.getRight();
                            int cy = 307;
                            makeEffect(binding.revealItems2, cx, cy);
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

    private void launchCameraIntent() {

        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 800);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void makeEffect(final CardView layout, int cx, int cy) {

        int radius = Math.max(layout.getWidth(), layout.getHeight());

        if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(800);

            SupportAnimator animator_reverse = animator.reverse();

            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {
                animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;

                    }

                    @Override
                    public void onAnimationCancel() {

                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
                animator_reverse.start();

            }
        } else {
            if (hidden) {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
                layout.setVisibility(View.VISIBLE);
                anim.start();
                hidden = false;

            } else {
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.start();

            }
        }
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
        if (resultCode != 0) {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                images.clear();
                List<Image> images_ = ImagePicker.getImages(data);
                if (images_.size() > 0) {
                    for (int i = 0; i < images_.size(); i++) {
                        Image dataimg = images_.get(i);
                        String path = dataimg.getPath();
                        // File compressedImageFile = Compressor.getDefault(this).compressToFile(new File(path));
                        //    File compressedImageFile = new File(path);
                        File compressedImageFile = CompressImg.getDefault(this).compressToFile(new File(path));
                        images.add(compressedImageFile);
                    }
                    new AsyncTask<Integer, Integer, String>() {
                        @Override
                        protected String doInBackground(Integer... params) {
                            try {
                                //response= uploadFiles(file);
                                // publishProgress();
                                MultipartBody body = RequestBuilder.uploadRequestBody(userid, groupid, chatfor, images);

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
                                response = ApiCall.POST(client, ConsURL.BASE_URL_TEST + "GroupChattingNew", monitoredRequest);
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
                            binding.ppDialog.setVisibility(View.GONE);

                            // Removes the progress bar
                            //mBuilder.setProgress(0, 0, false);
                            //  mNotifyManager.notify(0, mBuilder.build());
                        }

                        @Override
                        protected void onPreExecute() {
                            //  txt.setText("Task Starting...");
                        /*mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mBuilder = new NotificationCompat.Builder(context);
                        mBuilder.setContentTitle("Uploading")
                                .setContentText("Upload in progress")
                                .setSmallIcon(R.drawable.ic_launcher_background);*/
                            binding.ppDialog.setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            //txt.setText("Running..." + values[0]);
                            binding.ppDialog.setProgress(values[0]);
//                        if ((values[0]) % 25 == 0) {
//                            mBuilder.setProgress(100, values[0], false);
//                            // Displays the progress bar on notification
//                            mNotifyManager.notify(0, mBuilder.build());
//                        }

                        }


                    }.execute();


                }
            }
            if (requestCode == REQUEST_IMAGE) {

                if (resultCode == Activity.RESULT_OK) {
                    images.clear();
                    binding.sampleImage.setImageURI(image_uri);
                    //get drawable bitmap for text recognition
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.sampleImage.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();

                    File finalFile = new File(getRealPathFromURI(getImageUri(this, bitmap)));
                    String imagepath = String.valueOf(finalFile);
                    images.add(new File(imagepath));

                }

                new AsyncTask<Integer, Integer, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            //response= uploadFiles(file);
                            // publishProgress();


                            MultipartBody body = RequestBuilder.uploadRequestBody(userid, groupid, chatfor, images);

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
                            response = ApiCall.POST(client, ConsURL.BASE_URL_TEST + "GroupChattingNew", monitoredRequest);
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
                        binding.ppDialog.setVisibility(View.GONE);

                        // Removes the progress bar
                        //mBuilder.setProgress(0, 0, false);
                        //  mNotifyManager.notify(0, mBuilder.build());
                    }

                    @Override
                    protected void onPreExecute() {
                        //  txt.setText("Task Starting...");
                        /*mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mBuilder = new NotificationCompat.Builder(context);
                        mBuilder.setContentTitle("Uploading")
                                .setContentText("Upload in progress")
                                .setSmallIcon(R.drawable.ic_launcher_background);*/
                        binding.ppDialog.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        //txt.setText("Running..." + values[0]);
                        binding.ppDialog.setProgress(values[0]);
//                        if ((values[0]) % 25 == 0) {
//                            mBuilder.setProgress(100, values[0], false);
//                            // Displays the progress bar on notification
//                            mNotifyManager.notify(0, mBuilder.build());
//                        }

                    }


                }.execute();

            }


            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
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
                if (ContextCompat.checkSelfPermission(GroupChatActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                  /*  Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();*/
                } else if (ContextCompat.checkSelfPermission(GroupChatActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                   /* Toast.makeText(getApplicationContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    chooseImage(GroupChatActivity.this);
                }
                break;
        }
    }


    private void chooseImage(Context context) {
        pickCamera();

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
        // Log.e("path", path);

        return Uri.parse(path);
    }

    @Override
    public void onMessage(HubMessage message) {
        if (message.getTarget().equals("ReceiveChatNew")) {
            cmn.hideKeyboard(this);
            postimage.clear();
          //  chattable.deleteAll(groupid);
           // chattablefloat.clearTable();
            JsonElement[] object = message.getArguments();
            JsonElement o = object[0];
            JsonObject datObj = o.getAsJsonObject();
            JsonObject obj = datObj.getAsJsonObject("data");
            String gId = obj.get("groupId").getAsString();
            JsonArray array = obj.getAsJsonArray("post_images");
            for (int i = 0; i < array.size(); i++) {
                JsonElement object1 = array.get(i);
                JsonObject datObj1 = object1.getAsJsonObject();

                HashMap map = new HashMap();
                map.put("img_id", datObj1.get("imageId").getAsString());
                map.put("image", datObj1.get("image").getAsString());
                postimage.add(map);
            }
            if (postimage.size() > 0) {
                for (int i = 0; i < postimage.size(); i++) {
                    ArrayList<HashMap<String, String>> postimage1 = new ArrayList<>();

                    HashMap map = new HashMap();
                    map.put("img_id", postimage.get(i).get("img_id"));
                    map.put("image", postimage.get(i).get("image"));
                    postimage1.add(map);
                    GroupChatMessage commentModel = new GroupChatMessage();

                    String date = obj.get("date").getAsString();

                    commentModel.setDate(date);
                    if (obj.get("message").getAsString() != null) {
                        commentModel.setMessage(obj.get("message").getAsString());
                        commentModel.setMessageId(obj.get("messageId").getAsString());
                        commentModel.setSenderId(obj.get("senderId").getAsString());
                        commentModel.setPost_images(postimage1);
                        commentModel.setSenderImage(obj.get("senderImage").getAsString());
                        commentModel.setSenderName(obj.get("senderName").getAsString());
                        commentModel.setTime(obj.get("time").getAsString());
                        arr_chat.add(commentModel);

                    }
                }


                runOnUiThread(() -> {
                    binding.userRecord.setVisibility(View.GONE);
                    binding.commentsList.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                });
            }
            else {
                GroupChatMessage commentModel = new GroupChatMessage();
                String date = obj.get("date").getAsString();
                commentModel.setDate(date);
                commentModel.setMessage(obj.get("message").getAsString());
                commentModel.setMessageId(obj.get("messageId").getAsString());
                commentModel.setSenderId(obj.get("senderId").getAsString());
                commentModel.setGroupId(obj.get("groupId").getAsString());
                commentModel.setPost_images(postimage);
                commentModel.setSenderImage(obj.get("senderImage").getAsString());
                commentModel.setSenderName(obj.get("senderName").getAsString());
                commentModel.setTime(obj.get("time").getAsString());
                if (gId.equals(groupid)) {
                    arr_chat.add(commentModel);
                }
                runOnUiThread(() -> {
                    binding.userRecord.setVisibility(View.GONE);
                    binding.commentsList.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();


                });

            }
            binding.commentsList.smoothScrollToPosition(mAdapter.getItemCount());
            runOnUiThread(() -> loadData());
        }

    }

    @Override
    public void onError(Exception exception) {
        Log.d("checkmessges", exception.getMessage());

    }

    @Override
    public void onEventMessage(HubMessage message) {

    }
    private void loadData() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_key", ConsURL.accessKey);
            jsonObject.put("user_id", cmn.getPrefsData(GroupChatActivity.this, "id", ""));
            jsonObject.put("group_id", groupid);
            jsonObject.put("limit", 10000);
            jsonObject.put("offset", 0);
            jsonObject.put("chat_for", chatfor);

            new NetworkCall(null, result -> {
                try {
                    if (result.isStatus()) {
                        arr_chat.clear();
                        JSONArray jsonArray = new JSONArray(result.getData());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                arr_chat.add((GroupChatMessage) getObject(jsonArray.getJSONObject(i).toString(), GroupChatMessage.class));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        // showErrorToast(getResources().getString(R.string.internet));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (arr_chat.isEmpty()) {
                        binding.userRecord.setVisibility(View.VISIBLE);
                        // binding.userRecord.setText("Chatten är stängd");
                        binding.commentsList.setVisibility(View.GONE);
//                        showErrorToast("No Comments Found");
                    } else {
                        // Collections.reverse(arr_chat);
                        binding.userRecord.setVisibility(View.GONE);
                        binding.commentsList.setVisibility(View.VISIBLE);
                        mAdapter.notifyDataSetChanged();
                        // binding.commentsList.smoothScrollToPosition(mAdapter.getItemCount());
                        binding.commentsList.scrollToPosition(arr_chat.size() - 1);

                        //binding.commentsList.smoothScrollToPosition(arr_chat.size());

                    }

                }
                return null;
                }, jsonObject.toString()).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "group_chat_list");
        } catch (Exception e) {
            e.printStackTrace();

            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backValue.equals("1")) {
            startActivity(new Intent(GroupChatActivity.this, EditGroup.class)
                    .putExtra("data", Common.INSTANCE.getJSON(data_item))
                    .putExtra("id", groupid)
                    .putExtra("name", group_name)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        } else if (backValue.equals("2")) {
            startActivity(new Intent(GroupChatActivity.this, EditTeam.class)
                    .putExtra("data", Common.INSTANCE.getJSON(data))
                    .putExtra("id", groupid).putExtra("name", group_name).putExtra("coachid", coachid));
            finish();
        } else {
            startActivity(new Intent(GroupChatActivity.this, GroupChatList.class));
            finish();
        }


    }


}