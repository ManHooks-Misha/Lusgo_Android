
package com.app.SyrianskaTaekwondo.hejtelge.customClass;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.app.SyrianskaTaekwondo.hejtelge.Eventshow;
import com.app.SyrianskaTaekwondo.hejtelge.GroupChatActivity;
import com.app.SyrianskaTaekwondo.hejtelge.GroupChatList;
import com.app.SyrianskaTaekwondo.hejtelge.HomePage;
import com.app.SyrianskaTaekwondo.hejtelge.MessagePriviewActivity;
import com.app.SyrianskaTaekwondo.hejtelge.Page_withOut_Login;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Report_detailsActivity;
import com.app.SyrianskaTaekwondo.hejtelge.database.ChatPush;
import com.app.SyrianskaTaekwondo.hejtelge.database.Chatfloatmessage;
import com.app.SyrianskaTaekwondo.hejtelge.database.MuteNotify;
import com.app.SyrianskaTaekwondo.hejtelge.database.NotificationTable;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import vk.help.Common;

import static com.app.SyrianskaTaekwondo.hejtelge.GroupChatList.CHAT_NOTIFY_BELL;
import static com.app.SyrianskaTaekwondo.hejtelge.HomePage.CHAT_FLOAT_NOTIFY_BELL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public NotificationTable notificationTable;
    public ChatPush chatTable;
    public MuteNotify muteNotify;
    public Chatfloatmessage chatTable1;
    private static final String TAG = "MyFirebaseMsgService";
    private Bitmap bitmap;
    private String logo;
    private String uname, g_name, type = "", link;
    private CommonMethods cmn = new CommonMethods();
    AlertDialog alertDialog;

    // remoteMessage Object representing the message received from Firebase Cloud Messaging.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

      //  Log.d(TAG, "From: " + remoteMessage.getFrom());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        notificationTable = new NotificationTable(this).getInstance(this);
        chatTable = new ChatPush(this).getInstance(this);
        chatTable1 = new Chatfloatmessage(this).getInstance(this);
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> arr = remoteMessage.getData();

            String data = arr.get("title");
            String date = arr.get("time");
            String type_id = arr.get("type_id");
            String msg = arr.get("message");
            String user_id = arr.get("user_id");
            String uData = arr.get("uData");
            String type = arr.get("type");
            if (type.equals("app_ads")) {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");
                    link = obj.getString("link");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("logo",logo);
                    editor.putString("uname",uname);
                    editor.putString("link",link);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("group_chat")) {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");
                    g_name = obj.getString("gname");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
          //  Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (type.equals("news")) {

            } else if (type.equals("delete_event")) {

            } else if (type.equals("block")) {

            } else if (type.equals("app_block")) {

            } else if (type.equals("seen_notification")) {

            } else if (type.equals("app_freeze")) {

            } else if (type.equals("change_role")) {

            } else if (type.equals("change_team")) {

            } else if (type.equals("add_team")) {

            } else if (type.equals("delete_user")) {

            } else if (type.equals("group_chat")) {
                chatTable.insertdata(this, type_id, user_id);
                chatTable1.insertdata(this, type_id, user_id);

            }
//            else if (type.equals("app_ads")) {
//
//            }
            else {
                notificationTable.insertdata(this);

            }
            assert type != null;
            if (type.equals("Permission")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.NOTIFY_Permission));
            }
            if (type.equals("change_role") || type.equals("change_team") || type.equals("delete_user")) {
                Common.INSTANCE.saveString("login_sub", "true");
                sendBroadcast(new Intent(HomePage.Changerole).putExtra("msg", msg).putExtra("type",type));
            }
            if ( type.equals("add_team")){
                sendBroadcast(new Intent(HomePage.Changerole).putExtra("msg", msg));
                Common.INSTANCE.saveString("login_sub", "true");
            }
            if (type.equals("block")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.BLOCK));
            }
            if (type.equals("app_freeze")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.APP_FREEZE));
            }
            if (type.equals("app_block")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.APP_BLOCK).putExtra("msg", msg));
            }
            if (type.equals("seen_notification")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.SEEN_NOTIFICATION));
            }
            if (type.equals("app_ads")) {
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.App_Ads).putExtra("link", link).putExtra("txt", uname).putExtra("path", logo));
                sendBroadcast(new Intent(Page_withOut_Login.App_Ads).putExtra("link", link).putExtra("txt", uname).putExtra("path", logo));
            }
            if (type.equals("cron_report")) {
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.Report).putExtra("msg", msg).putExtra("reportid", type_id));
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                assert user_id != null;
                if (user_id.equals(new CommonMethods().getPrefsData(this, "user_id", ""))) {
                    return;
                } else if (type.equals("block")) {
                    return;
                } else if (type.equals("app_freeze")) {
                    return;
                } else if (type.equals("app_block")) {
                    return;
                } else if (type.equals("seen_notification")) {
                    return;
                } else if (type.equals("change_team")) {
                    return;
                } else if (type.equals("change_role")) {
                    return;
                } else if (type.equals("add_team")) {
                    return;
                } else if (type.equals("delete_user")) {
                    return;
                }
//                else if (type.equals("app_ads")) {
//                    return;
//                }
                else {
                    sendNotification(msg, data, date, uname, logo, user_id, type, type_id);

                }
            });

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            //Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(@NotNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

    }

    @Override
    public void handleIntent(Intent intent) {
        try {
            Bundle data = intent.getExtras();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            String mute = cmn.getPrefsData(this, "Mute", "false");
            notificationTable = new NotificationTable(this).getInstance(this);
            chatTable = new ChatPush(this).getInstance(this);
            muteNotify = new MuteNotify(this).getInstance(this);
            chatTable1 = new Chatfloatmessage(this).getInstance(this);

            String type = data.get("type").toString();
            String title = data.get("title").toString();
            String date = data.get("time").toString();
            String type_id = data.get("type_id").toString();
            String msg = data.get("message").toString();
            String user_id = data.get("user_id").toString();
            String uData = data.get("uData").toString();

            if (type.equals("app_ads")) {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");
                    link = obj.getString("link");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("logo",logo);
                    editor.putString("" +
                            "ame",uname);
                    editor.putString("link",link);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("group_chat")) {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");
                    g_name = obj.getString("gname");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    assert uData != null;
                    JSONObject obj = new JSONObject(uData);
                    logo = obj.getString("logo");
                    uname = obj.getString("uname");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (type.equals("news")) {

            } else if (type.equals("delete_event")) {

            } else if (type.equals("block")) {

            } else if (type.equals("app_block")) {

            } else if (type.equals("seen_notification")) {

            } else if (type.equals("app_freeze")) {

            } else if (type.equals("change_role")) {

            } else if (type.equals("change_team")) {

            } else if (type.equals("add_team")) {

            } else if (type.equals("delete_user")) {

            } else if (type.equals("group_chat")) {
                if (GroupChatActivity.OpenActivity == false) {
                    chatTable.insertdata(this, type_id, user_id);
                    chatTable1.insertdata(this, type_id, user_id);
                    sendBroadcast(new Intent(CHAT_NOTIFY_BELL));
                    sendBroadcast(new Intent(CHAT_FLOAT_NOTIFY_BELL));


                }
                sendBroadcast(new Intent(CHAT_FLOAT_NOTIFY_BELL));


            }
//            else if (type.equals("app_ads")) {
//
//            }
            else {
                notificationTable.insertdata(this);

            }
            //  notificationTable.insertdata(this);
            assert type != null;
            if (type.equals("Permission")) {
                Common.INSTANCE.saveString("login_sub", "true");
              //  Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.NOTIFY_Permission));
            }
            if (type.equals("change_role") || type.equals("change_team") || type.equals("delete_user")) {
                Common.INSTANCE.saveString("login_sub", "true");
                sendBroadcast(new Intent(HomePage.Changerole).putExtra("msg", msg).putExtra("type",type));
                sendNotification(msg, title, date, uname, logo, user_id, type, type_id);
            }if ( type.equals("add_team")){
                sendBroadcast(new Intent(HomePage.Changerole).putExtra("msg", msg).putExtra("type",type));
                Common.INSTANCE.saveString("login_sub", "true");
            }
            if (type.equals("block")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                //  logoutblock("Du har blockerats av admin. Du kan inte använda funktionen i den här appen");

                sendBroadcast(new Intent(HomePage.BLOCK));
            }
            if (type.equals("app_freeze")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.APP_FREEZE));
            }
            if (type.equals("app_block")) {
                Common.INSTANCE.saveString("login_sub", "true");
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.APP_BLOCK).putExtra("msg", msg));
            }
            if (type.equals("seen_notification")) {
               // Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.SEEN_NOTIFICATION));
            }
            if (type.equals("app_ads")) {
           //     Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.App_Ads).putExtra("link", link).putExtra("txt", uname).putExtra("path", logo));
                sendBroadcast(new Intent(Page_withOut_Login.App_Ads).putExtra("link", link).putExtra("txt", uname).putExtra("path", logo));
            }
            if (type.equals("cron_report")) {
                Log.e("test", "post");
                sendBroadcast(new Intent(HomePage.Report).putExtra("msg", msg).putExtra("reportid", type_id));
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                assert user_id != null;
                if (user_id.equals(new CommonMethods().getPrefsData(this, "user_id", ""))){
                    return;
                } else if (type.equals("block")) {
                    return;
                }
                else if (type.equals("app_freeze")) {
                    return;
                }
                else if (type.equals("app_block")) {
                    return;
                }
                else if (type.equals("seen_notification")) {
                    return;
                }
                else if (type.equals("change_team")) {
                    return;
                }
                else if (type.equals("change_role")) {
                    return;
                }
                else if (type.equals("add_team")) {
                    return;
                }
                else if (type.equals("delete_user")) {
                    return;
                }
                else if (type.equals("app_ads")) {
                    if (HomePage.OpenActivityAd == false) {
                        sendNotification(msg, title, date, uname, logo, user_id, type, type_id);
                    }

                    return;
                }
                else if (type.equals("group_chat")) {
                    ArrayList<HashMap<String, String>> muteArr = muteNotify.getDataNotificationDataMute();
                    for (int i = 0; i < muteArr.size(); i++) {
                        String mutestatus = muteArr.get(i).get("mutestatus");
                        String groupidstr = muteArr.get(i).get("groupid");
                        if (type_id.equals(groupidstr) && mutestatus.equals("true") && GroupChatActivity.OpenActivity == false) {
                            sendNotification(msg, title, date, uname, logo, user_id, type, type_id);
                        } else {
                            Log.d("checktedt","djsafl");
                           //  sendNotification(msg, title, date, uname, logo, user_id, type, type_id);
                        }
                    }
                    return;
                }
                else {
                    sendNotification(msg, title, date, uname, logo, user_id, type, type_id);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendNotification(String messageBody, String title, String time, String uname, String logo, String userid, String type, String type_id) {
        Intent intent = null;
        if (type.equals("event") && (new CommonMethods().getPrefsData(this, "id", "").length() > 0)) {
            intent = new Intent(this, Eventshow.class);
            intent.putExtra("id", type_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (type.equals("group_chat") && (new CommonMethods().getPrefsData(this, "id", "").length() > 0)) {
            intent = new Intent(this, GroupChatList.class);
            intent.putExtra("id", type_id);
            intent.putExtra("name", g_name);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }  else if (type.equals("app_ads") && (new CommonMethods().getPrefsData(this, "id", "").length() > 0)) {
            intent = new Intent(this, HomePage.class);
            intent.putExtra("link", link);
            intent.putExtra("name", uname);
            intent.putExtra("logo", logo);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (type.equals("message") && (new CommonMethods().getPrefsData(this, "id", "").length() > 0)) {
            intent = new Intent(this, MessagePriviewActivity.class);
            intent.putExtra("id", type_id);
            intent.putExtra("flag", "inbox");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (new CommonMethods().getPrefsData(this, "id", "").length() == 0 && type.equals("event")) {
            intent = new Intent(this, Eventshow.class);
            intent.putExtra("id", type_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && type.equals("delete_comment_ok")) {
            intent = new Intent(this, Report_detailsActivity.class);
            intent.putExtra("report_id", type_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && type.equals("reported_words")) {
            intent = new Intent(this, Report_detailsActivity.class);
            intent.putExtra("report_id", type_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && type.equals("cron_report")) {
            intent = new Intent(this, Report_detailsActivity.class);
            intent.putExtra("report_id", type_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && type.equals("delete_comment")) {
            intent = new Intent(this, Report_detailsActivity.class);
            intent.putExtra("report_id", type_id);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && type.equals("news")) {
            intent = new Intent(this, Page_withOut_Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (new CommonMethods().getPrefsData(this, "id", "").length() == 0 && type.equals("Permission")) {
            intent = new Intent(this, Page_withOut_Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } /*else if (new CommonMethods().getPrefsData(this, "id", "").length() > 0 && !(type.equals("permission"))) {

            intent = new Intent(this, HomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }*/ else {
            intent = new Intent(this, Page_withOut_Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }
        // Intent intent = new Intent(this, HomePage.class);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(logo);
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = format.parse(time);
            Date now = new Date();
            assert past != null;
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            String timediff = "";
            if (days < 9 && days > 1) {
                timediff = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " Dagar sedan";
            } else if (days == 0) {
                timediff = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h sedan";
                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                if (hours == 0) {
                    long mins = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                    timediff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m sedan";
                    if (mins == 0) {
                        timediff = "Precis nu";
                    }
                }

            } else {
                timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.logo_app)
                    // .setContentText(timediff)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(defaultSoundUri);
            if (bitmap != null) {
                notificationBuilder.setLargeIcon(bitmap);
            }
//
            if (new CommonMethods().getPrefsData(this, "id", "").equals(userid)) {

                notificationBuilder.setContentTitle(Html.fromHtml(messageBody));
//                notificationBuilder.setContentText(Html.fromHtml(messageBody));

//                notificationBuilder.setContentTitle(messageBody + " av dig");
            } else if (type.equals("group_chat")) {
                notificationBuilder.setContentTitle((g_name));
                notificationBuilder.setContentText(Html.fromHtml(messageBody));
            } else {
                notificationBuilder.setContentTitle(Html.fromHtml(messageBody));

            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            Objects.requireNonNull(notificationManager).notify(null, 2/* ID of notification */, notificationBuilder.build());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}