package com.app.SyrianskaTaekwondo.hejtelge;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.EventDetailsUser;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.Progressbar;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityEventshowBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Details;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vk.help.MasterActivity;

public class Eventshow extends MasterActivity implements SwipeRefreshLayout.OnRefreshListener {
    public final DateTimeFormatter mTimeFormat = DateTimeFormat.forPattern(" h:mm a");
    ActivityEventshowBinding binding;
    int year, month, day, endYear, endMonth, endDay, startTimeHr, startTimeMin, endTimeHr, endTimeMin;
    String startdate, team_id, id, status, action = "0", senderid,created_byName,profile_image, enddat, place, description, title, title2, attending, may_be, no_response, denied;
    long stdate_, enddate_;
    List<Details> arr_show = new ArrayList<>();
    CommonMethods cmn;
    ProgressBar progress_dialog;
    Progressbar progressbar;
    private int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 23;
    private String userid = "", usertype;
    private long mLastClickTime = 0;
    private long mLastClickTime1 = 0;
    private long mLastClickTime2 = 0;
    private String eventid,data;
    private ArrayList<Details> arr_participateuser_attend = new ArrayList<>();
    private ArrayList<Details> arr_participateuser_notattend = new ArrayList<>();
    private ArrayList<Details> arr_participateuser_maybe = new ArrayList<>();
    private ArrayList<Details> arr_participateuser_notanswer = new ArrayList<>();
    private AlertDialog alertDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventshowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        progressbar = new Progressbar();
        binding.back.setOnClickListener(v -> finish());
        //  Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(context, "id", "");
        usertype = cmn.getPrefsData(context, "usertype", "");
        Locale loc = new Locale("sv", "SE");
        Locale.setDefault(loc);

        if (getIntent() != null) {
            eventid = getIntent().getStringExtra("id");
            //   data = (Event) getObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")), Event.class);
        }
        getUserAPI();

        binding.llAttend.setOnClickListener(v -> {
            showBottomDialogeAtend();

        });


        binding.llNotattend.setOnClickListener(v -> {
            showBottomDialogeNoAtend();

        });
        binding.llNotans.setOnClickListener(v -> {
            // List<Details> arr = data.getDetails();

            arr_participateuser_notattend.clear();
            arr_participateuser_attend.clear();
            arr_participateuser_maybe.clear();
            arr_participateuser_notanswer.clear();
            for (int i = 0; i < arr_show.size(); i++) {

                if (arr_show.get(i).getAction().equals("0")) {

                    arr_participateuser_notanswer.add(arr_show.get(i));

                }
            }
            binding.attendingName.setVisibility(View.VISIBLE);

            EventDetailsUser mAdapter = new EventDetailsUser(arr_participateuser_notanswer, Eventshow.this);
            binding.listUser.setAdapter(mAdapter);
        });
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.llMaybe.setOnClickListener(v -> {
            showBottomDialogeMayBe();

        });
        binding.ok.setOnClickListener(v -> {
            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Calendar calendar = Calendar.getInstance();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Vill du lägga till en händelse i din kalender?");
                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    onProfileImageClick(title2, description, place, stdate_, enddate_);
                    getEventAPI("1", id, binding.ok, binding.cancel, binding.maybeUser, binding.rlCancel, binding.rlOk);
                    alertDialog.dismiss();

                });
                builder.setNegativeButton("Nej", (dialogInterface, i) ->{
                    alertDialog.dismiss();
                    getEventAPI("1", id, binding.ok, binding.cancel, binding.maybeUser, binding.rlCancel, binding.rlOk);
                        });

                alertDialog = builder.create();
                alertDialog.show();




            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });

        binding.cancel.setOnClickListener(v -> {

            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime1 < 5000) {
                    return;
                }
                mLastClickTime1 = SystemClock.elapsedRealtime();

                getEventAPI("2", id, binding.ok, binding.cancel, binding.maybeUser, binding.rlCancel, binding.rlOk);

            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });
        binding.maybeUser.setOnClickListener(v -> {

            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime2 < 5000) {
                    return;
                }
                mLastClickTime2 = SystemClock.elapsedRealtime();

                getEventAPI("3", id, binding.ok, binding.cancel, binding.maybeUser, binding.rlCancel, binding.rlOk);

            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void getEventAPI(String action, String event_id, AppCompatTextView txt_show_attend, AppCompatTextView txt_show_no, AppCompatTextView txt_show_maybe, RelativeLayout rlcancel, RelativeLayout rlok) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("action", action);
            object.put("event_id", event_id);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                     data = result.getData();
                    if (data.equals("1")) {
                        rlok.setBackgroundResource(R.drawable.create_button);
                        txt_show_attend.setTextColor(Color.parseColor("#ffffff"));
                        rlcancel.setBackgroundResource(R.drawable.greyback);
                        txt_show_no.setTextColor(Color.parseColor("#000000"));
                        txt_show_maybe.setBackgroundResource(R.drawable.greyback);
                        txt_show_maybe.setTextColor(Color.parseColor("#000000"));

                    }
                    if (data.equals("2")) {
                        rlcancel.setBackgroundResource(R.drawable.create_button);
                        txt_show_no.setTextColor(Color.parseColor("#ffffff"));
                        txt_show_maybe.setBackgroundResource(R.drawable.greyback);
                        txt_show_maybe.setTextColor(Color.parseColor("#000000"));
                        rlok.setBackgroundResource(R.drawable.greyback);
                        txt_show_attend.setTextColor(Color.parseColor("#000000"));

                    }
                    if (data.equals("3")) {
                        txt_show_maybe.setBackgroundResource(R.drawable.create_button);
                        txt_show_maybe.setTextColor(Color.parseColor("#ffffff"));
                        txt_show_no.setBackgroundResource(R.drawable.greyback);
                        txt_show_no.setTextColor(Color.parseColor("#000000"));
                        rlok.setBackgroundResource(R.drawable.greyback);
                        txt_show_attend.setTextColor(Color.parseColor("#000000"));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "update_eventResponse");
    }

    private void getEventDeleteAPI(String event_id) {
        binding.progressDialog.setVisibility(View.VISIBLE);
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("event_id", event_id);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    binding.progressDialog.setVisibility(View.GONE);
                    startActivity(new Intent(context, HomePage.class).putExtra("flag", "cal"));
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "delete_event");
    }

    private void getpublicUserAPI(String event_id, String token) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("event_id", event_id);
            object.put("name", "");
            object.put("date_of_birth", "");
            object.put("token", token);
            object.put("action", "1");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    // Toast.makeText(context, "Du går med i det här evenemanget", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Vill du lägga till en händelse i din kalender?");
                    builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                        onProfileImageClick(title2, description, place, stdate_, enddate_);
                        alertDialog.dismiss();

                    });
                    builder.setNegativeButton("Nej", (dialogInterface, i) -> alertDialog.dismiss());
                    alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    alertDialog.dismiss();
                    //Toast.makeText(context, "Offentlig användare deltar redan i detta evenemang", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "" + result.getMessage(), Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "join_event");
    }


    private void showCustomDialog(String event_id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Vill du säkert ta bort det här evenemanget?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (cmn.isOnline(context)) {
                getEventDeleteAPI(event_id);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());


        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showCustomDialogPublicuser(String id, String token, String title, String description, String place, long stdate_, long enddate_) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.public_user, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email = dialogView.findViewById(R.id.et_name);
        AppCompatEditText date = dialogView.findViewById(R.id.date);
        DatePicker datepicker = dialogView.findViewById(R.id.date_picker_publicuser);
        date.setOnClickListener(view -> {
            datepicker.setVisibility(View.VISIBLE);
            int month = datepicker.getMonth() + 1;
            String dob = datepicker.getDayOfMonth() + "-" + month + "-" + datepicker.getYear();
            date.setText(dob);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datepicker.setOnDateChangedListener((datePicker, i, i1, i2) -> {
                int month = i1 + 1;
                String dob = i2 + "-" + month + "-" + i;
                date.setText(dob);

            });
        }

        ok.setOnClickListener(view -> {

            if (cmn.isOnline(context)) {

                getpublicUserAPI(id, token);

             /*   if (cmn.isEmailValid(email_text)) {

                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }*/

            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getUserAPI() {
        binding.progressDialog.setVisibility(View.VISIBLE);
        arr_show.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        map1.put("Content-Type", "application/json");

        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            object.put("event_id", eventid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    binding.swipeRefresh.setRefreshing(false);
                    binding.progressDialog.setVisibility(View.GONE);
                    JSONArray objject = new JSONArray(result.getData());
                    for (int i = 0; i < objject.length(); i++) {
                        JSONObject obj = objject.getJSONObject(i);
                        id = obj.getString("id");
                        team_id = obj.getString("team_id");
                        startdate = obj.getString("start_date");
                        // startdate = obj.getString("start_date");
                        enddat = obj.getString("end_date");
                        place = obj.getString("place");
                        description = obj.getString("description");
                        title = obj.getString("title");
                        title2 = String.valueOf(Html.fromHtml(title));
                        attending = obj.getString("attending");
                        may_be = obj.getString("may_be");
                        no_response = obj.getString("no_response");
                        denied = obj.getString("denied");
                        senderid = obj.getString("created_by");
                        created_byName = obj.getString("created_byName");
                        profile_image = obj.getString("profile_image");
                        status = obj.getString("status");
                        JSONArray arrjson = obj.getJSONArray("details");


                        for (int j = 0; j < arrjson.length(); j++) {
                            JSONObject objdetails = arrjson.getJSONObject(j);
                            Details detail = new Details();
                            detail.setProfile_image(objdetails.getString("profile_image"));
                            detail.setName(objdetails.getString("name"));
                            detail.setAction(objdetails.getString("action"));
                            detail.setUser_id(objdetails.getString("user_id"));
                            arr_show.add(detail);
                        }

                        //      arr.add((Event) (getObject(objject.getString(i), Event.class)));


//                    if (arr.size() > 0) {
                        //   data = arr.get(0);
                        // List<Details> arr_show = arr.get(0).getDetails();
                        if (status.equals("1")) {
                            for (int k = 0; k < arr_show.size(); k++) {
                                Details objdel = arr_show.get(k);
                                if (objdel.getAction().equals("1") && (userid.equals(objdel.getUser_id()))) {
                                    binding.rlOk.setBackgroundResource(R.drawable.create_button);
                                    binding.ok.setTextColor(Color.parseColor("#ffffff"));
                                    binding.rlCancel.setBackgroundResource(R.drawable.greyback);
                                    binding.cancel.setTextColor(Color.parseColor("#000000"));
                                    binding.maybeUser.setBackgroundResource(R.drawable.greyback);
                                    binding.maybeUser.setTextColor(Color.parseColor("#000000"));
                                    action = objdel.getAction();

                                }
                                if (objdel.getAction().equals("2") && (userid.equals(objdel.getUser_id()))) {
                                    binding.rlCancel.setBackgroundResource(R.drawable.create_button);
                                    binding.cancel.setTextColor(Color.parseColor("#ffffff"));
                                    binding.rlOk.setBackgroundResource(R.drawable.greyback);
                                    binding.ok.setTextColor(Color.parseColor("#000000"));
                                    binding.maybeUser.setBackgroundResource(R.drawable.greyback);
                                    binding.maybeUser.setTextColor(Color.parseColor("#000000"));
                                    action = objdel.getAction();

                                }
                                if (objdel.getAction().equals("3") && (userid.equals(objdel.getUser_id()))) {
                                    binding.maybeUser.setBackgroundResource(R.drawable.create_button);
                                    binding.maybeUser.setTextColor(Color.parseColor("#ffffff"));
                                    binding.rlCancel.setBackgroundResource(R.drawable.greyback);
                                    binding.cancel.setTextColor(Color.parseColor("#000000"));
                                    binding.rlOk.setBackgroundResource(R.drawable.greyback);
                                    binding.ok.setTextColor(Color.parseColor("#000000"));
                                    action = objdel.getAction();

                                }

                            }

                            String stdate = startdate;
                            String[] arrstart = stdate.split(" ");
                            String txt_stdate = arrstart[0];
                            String[] dateParts = txt_stdate.split("-");
                            year = Integer.parseInt(dateParts[0]);
                            month = Integer.parseInt(dateParts[1]);
                            day = Integer.parseInt(dateParts[2]);

                            String txt_sttime = arrstart[1];

                            String[] time1 = txt_sttime.split(":");
                            startTimeHr = Integer.parseInt(time1[0]);
                            startTimeMin = Integer.parseInt(time1[1]);

                            String enddate = enddat;
                            String[] arrend = enddate.split(" ");
                            String enddt = arrend[0];
                            String[] dateParts2 = enddt.split("-");
                            endYear = Integer.parseInt(dateParts2[0]);
                            endMonth = Integer.parseInt(dateParts2[1]);
                            endDay = Integer.parseInt(dateParts2[2]);


                            String endtime = arrend[1];

                            String[] time2 = endtime.split(":");
                            endTimeHr = Integer.parseInt(time2[0]);
                            endTimeMin = Integer.parseInt(time2[1]);

                            String myFormat1 = "dd MMM"; //In which you need put here
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //In which you need put here
                            Locale loc = new Locale("sv", "SE");
                            Locale.setDefault(loc);

                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat1);
                            try {
                                Date stpast = format.parse(stdate);
                                Date endpast = format.parse(enddate);
                                assert stpast != null;
                                stdate = sdf.format(stpast.getTime());
                                assert endpast != null;
                                enddate = sdf.format(endpast.getTime());
                                stdate_ = stpast.getTime();
                                enddate_ = endpast.getTime();
                                if ((System.currentTimeMillis() < stpast.getTime() && System.currentTimeMillis() > endpast.getTime())) {
                                    binding.endEvent.setVisibility(View.VISIBLE);
                                    binding.endEvent.setText("Avslutat");
                                } else {
                                    binding.endEvent.setVisibility(View.GONE);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                           /* if (stdate.equals(enddate)) {
                                binding.date.setVisibility(View.GONE);
                            } else {
                                binding.date.setVisibility(View.VISIBLE);
                            }*/
                            if (userid.length() == 0) {
                                binding.join.setVisibility(View.VISIBLE);
                            }
                            //binding.title.setText(Html.fromHtml(title));
                            binding.title.setVisibility(View.GONE);
                          //  binding.txt.setPaintFlags( binding.txt.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                            binding.txt.setText(Html.fromHtml(title));
                            if (description.length() > 0) {
                                binding.lldesc.setVisibility(View.VISIBLE);
                            } else {
                                binding.lldesc.setVisibility(View.GONE);
                                binding.cardviewEvnts.setVisibility(View.GONE);

                            }
                           /* if (place.length() > 0) {
                                binding.locimg.setVisibility(View.VISIBLE);
                            } else {
                                binding.locimg.setVisibility(View.GONE);

                            }*/
                            binding.desc.setText(Html.fromHtml(description));
                            binding.txtCreatedBy.setText(Html.fromHtml(created_byName));
                            Glide.with(context)
                                    .load(profile_image)
                                    .centerCrop()
                                    .into(binding.userProfile);

                            binding.loc.setText(Html.fromHtml(place));
                            String[] sttime_ = txt_sttime.split(":");
                            txt_sttime = sttime_[0] + ":" + sttime_[1];
                            String[] endtime_ = endtime.split(":");
                            endtime = endtime_[0] + ":" + endtime_[1];
                            //  binding.sttime.setText(txt_sttime + " - " + endtime);
//                            binding.date.setText(stdate+","+txt_sttime + " - " + enddate+","+endtime);
                            Date start = format.parse(startdate);
                            Date end = format.parse(enddat);
                            String startdate__ = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(start);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                            String enddat__ = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(end);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                            String start__ = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(start);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                            String end__ = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(end);
                            //  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                            if ((start__.equals(end__))) {
                                binding.date.setText(start__ + " " + txt_sttime + " - " + endtime);

                            } else {
                                binding.date.setText(startdate__ + " - " + enddat__);

                            }
                            binding.attend.setText(attending);
                            binding.notAns.setText(no_response);
                            binding.maybe.setText(no_response);
                            binding.notAttend.setText(denied);

                            if (usertype.length() == 0 || usertype.equals(ConsURL.members) || team_id.equals("0")) {
                                binding.delete.setVisibility(View.INVISIBLE);
                            } else {
                                binding.delete.setVisibility(View.VISIBLE);
                            }
                            if (userid.equals(senderid)) {
                                binding.delete.setVisibility(View.VISIBLE);

                            }

                            if (description.length() > 0) {
                                binding.desc.setVisibility(View.VISIBLE);
                            } else {
                                binding.desc.setVisibility(View.GONE);
                            }
                            if (userid.trim().length() == 0) {
                                binding.llPublic.setVisibility(View.VISIBLE);
                                binding.coachAdminBlock.setVisibility(View.GONE);
                                binding.calling.setVisibility(View.GONE);

                                binding.llUser.setVisibility(View.GONE);

                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels / 3;
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.cardviewEvnts.getLayoutParams();
                                layoutParams.height = height;
                                layoutParams.width = MATCH_PARENT;
                                binding.cardviewEvnts.setLayoutParams(layoutParams);


                            }


                            binding.delete.setOnClickListener(view -> showCustomDialog(id));
                            if (userid.length() > 0) {
                                if (usertype.equals(ConsURL.members) && userid.equals(senderid)) {
                                    binding.coachAdminBlock.setVisibility(View.VISIBLE);
                                    binding.llUser.setVisibility(View.GONE);
                                    binding.llPublic.setVisibility(View.GONE);
                                    binding.cardView.setVisibility(View.VISIBLE);

                                    binding.calling.setVisibility(View.GONE);
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    int height = displayMetrics.heightPixels / 3;
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.cardviewEvnts.getLayoutParams();
                                    layoutParams.height = height;
                                    layoutParams.width = MATCH_PARENT;
                                    binding.cardviewEvnts.setLayoutParams(layoutParams);


                                } else {
                                    binding.coachAdminBlock.setVisibility(View.VISIBLE);
                                    binding.llUser.setVisibility(View.VISIBLE);
                                    binding.llPublic.setVisibility(View.GONE);
                                    binding.calling.setVisibility(View.GONE);
                                    binding.cardView.setVisibility(View.VISIBLE);


                                }
                            }
                            if (userid.length() > 0) {
                                if (usertype.equals(ConsURL.coach) && userid.equals(senderid) || usertype.equals(ConsURL.sub_coach) && userid.equals(senderid)) {
                                    binding.coachAdminBlock.setVisibility(View.VISIBLE);
                                    binding.llUser.setVisibility(View.GONE);
                                    binding.llPublic.setVisibility(View.GONE);
                                    binding.calling.setVisibility(View.GONE);
                                    binding.cardView.setVisibility(View.VISIBLE);
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    int height = displayMetrics.heightPixels / 3;
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.cardviewEvnts.getLayoutParams();
                                    layoutParams.height = height;
                                    layoutParams.width = MATCH_PARENT;
                                    binding.cardviewEvnts.setLayoutParams(layoutParams);


                                } else {
                                    binding.coachAdminBlock.setVisibility(View.GONE);
                                    binding.llUser.setVisibility(View.VISIBLE);
                                    binding.llPublic.setVisibility(View.GONE);
                                    binding.calling.setVisibility(View.GONE);
                                    binding.cardView.setVisibility(View.VISIBLE);


                                }
                            }
                            if (userid.length() == 0) {
                                binding.cardView.setVisibility(View.VISIBLE);

                            }


                            binding.join.setOnClickListener(view -> {
                                try {
                                    Date stpast = format.parse(startdate);
                                    Date endpast = format.parse(enddat);

                                    stdate_ = Objects.requireNonNull(stpast).getTime();
                                    enddate_ = Objects.requireNonNull(endpast).getTime();

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String token = new CommonMethods().getPrefsData(context, "token", "");
                                showCustomDialogPublicuser(id, token, title, description, place, stdate_, enddate_);
                            });
                            if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                                binding.coachAdminBlock.setVisibility(View.VISIBLE);
                                binding.cardView.setVisibility(View.VISIBLE);
                                binding.llUser.setVisibility(View.GONE);
                                binding.calling.setVisibility(View.GONE);
                                binding.cardUser.setVisibility(View.GONE);
                                binding.llPublic.setVisibility(View.GONE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 180, 20, 0);
                                binding.cardView.setLayoutParams(params);


                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int height = displayMetrics.heightPixels / 3;
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.cardviewEvnts.getLayoutParams();
                                layoutParams.height = height;
                                layoutParams.width = MATCH_PARENT;
                                binding.cardviewEvnts.setLayoutParams(layoutParams);


                            }
                        } else {
                            binding.date.setText("Händelsen är redan raderad");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                binding.swipeRefresh.setRefreshing(false);
                binding.progressDialog.setVisibility(View.GONE);
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "eventInfo");
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Eventshow.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    void onProfileImageClick(String title, String about, String loc, long stdate_, long enddate_) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            //Calendar beginTime = Calendar.getInstance();
                            //  beginTime.set(year, month, day, startTimeHr, startTimeMin);

                            // Calendar endTime = Calendar.getInstance();
                            // endTime.set(endYear, endMonth, endDay, endTimeHr, endTimeMin);
                            //long endDate = stdate_ + 1000 * 60 * 60;
                            // Calendar calendarEvent = Calendar.getInstance();
                       /*     Intent i = new Intent(Intent.ACTION_EDIT);
                            i.setType("vnd.android.cursor.item/event");
                            i.putExtra("beginTime", stdate_);
                            i.putExtra("allDay", false);
                            i.putExtra("rule", "FREQ=YEARLY");
                            i.putExtra(CalendarContract.Events.EVENT_LOCATION, place);
                            i.putExtra("endTime", enddate_);
                            i.putExtra("title", title);
                            startActivity(i);
*/
                            pushAppointmentsToCalender(Eventshow.this, title, about, loc, 1, stdate_, enddate_, true, false);

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

    public void showBottomDialogeMayBe() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.eventlist_b_s_d);
        dialog.setCanceledOnTouchOutside(false);
        RecyclerView eventRV = dialog.findViewById(R.id.list_user);
        AppCompatImageView img_dialog = dialog.findViewById(R.id.CustomBottomDialog_ivCancel);
        TextView tv_maytext = dialog.findViewById(R.id.tv_maytext);
        tv_maytext.setText("Inget svar");

        arr_participateuser_notattend.clear();
        arr_participateuser_attend.clear();
        arr_participateuser_maybe.clear();
        arr_participateuser_notanswer.clear();
        for (int i = 0; i < arr_show.size(); i++) {
            if (arr_show.get(i).getAction().equals("0")) {
                arr_participateuser_notanswer.add(arr_show.get(i));
            }
        }
        EventDetailsUser mAdapter = new EventDetailsUser(arr_participateuser_notanswer, Eventshow.this);
        eventRV.setAdapter(mAdapter);

        img_dialog.setOnClickListener(view -> dialog.dismiss());
        if (arr_participateuser_notanswer.size() > 0) {
            dialog.show();
        }


    }

    public void showBottomDialogeNoAtend() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.eventlist_b_s_d);
        dialog.setCanceledOnTouchOutside(false);
        RecyclerView eventRV = dialog.findViewById(R.id.list_user);
        AppCompatImageView img_dialog = dialog.findViewById(R.id.CustomBottomDialog_ivCancel);
        TextView tv_maytext = dialog.findViewById(R.id.tv_maytext);
        tv_maytext.setText("Kommer ej");
        arr_participateuser_notattend.clear();
        arr_participateuser_attend.clear();
        arr_participateuser_maybe.clear();
        arr_participateuser_notanswer.clear();
        for (int i = 0; i < arr_show.size(); i++) {
            if (arr_show.get(i).getAction().equals("2")) {
                arr_participateuser_notattend.add(arr_show.get(i));

            }
        }
        EventDetailsUser mAdapter = new EventDetailsUser(arr_participateuser_notattend, Eventshow.this);
        eventRV.setAdapter(mAdapter);

        img_dialog.setOnClickListener(view -> dialog.dismiss());
        if (arr_participateuser_notattend.size() > 0) {
            dialog.show();
        }

    }

    public void showBottomDialogeAtend() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.eventlist_b_s_d);
        dialog.setCanceledOnTouchOutside(false);
        RecyclerView eventRV = dialog.findViewById(R.id.list_user);
        AppCompatImageView img_dialog = dialog.findViewById(R.id.CustomBottomDialog_ivCancel);
        TextView tv_maytext = dialog.findViewById(R.id.tv_maytext);
        tv_maytext.setText("Kommer");
        arr_participateuser_notattend.clear();
        arr_participateuser_attend.clear();
        arr_participateuser_maybe.clear();
        arr_participateuser_notanswer.clear();
        for (int i = 0; i < arr_show.size(); i++) {
            if (arr_show.get(i).getAction().equals("1")) {
                arr_participateuser_attend.add(arr_show.get(i));

            }
        }
        EventDetailsUser mAdapter = new EventDetailsUser(arr_participateuser_attend, Eventshow.this);
        eventRV.setAdapter(mAdapter);
        img_dialog.setOnClickListener(view -> dialog.dismiss());
        if (arr_participateuser_attend.size() > 0) {
            dialog.show();
        }


    }

    public long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, String place, int status, long startDate, long enddate, boolean needReminder, boolean needMailService) {

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", 1);
        eventValues.put("title", Html.fromHtml(title).toString());
        eventValues.put("description", Html.fromHtml(addInfo).toString());
        eventValues.put("eventLocation", Html.fromHtml(place).toString());

        long endDate = startDate + 1000 * 60 * 60; // For next 1hr

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);

        eventValues.put("allDay", 0);
        eventValues.put("eventStatus", status);
        eventValues.put("eventTimezone", "UTC/GMT +2:00");

        eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

        Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());
        // Toast.makeText(curActivity, "Händelsen har lagts till i din kalender.", Toast.LENGTH_SHORT).show();
        cmn.customDialogMsg(this, "     Händelsen har lagts till i din kalender");
        if (needReminder) {
            String reminderUriString = "content://com.android.calendar/reminders";

            ContentValues reminderValues = new ContentValues();

            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 5); // Default value of the
            // system. Minutes is a
            // integer
            reminderValues.put("method", 1); // Alert Methods: Default(0),
            // Alert(1), Email(2),
            // SMS(3)

            //   Uri reminderUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        }

        if (needMailService) {
            String attendeuesesUriString = "content://com.android.calendar/attendees";

            /********
             * To add multiple attendees need to insert ContentValues multiple
             * times
             ***********/
            ContentValues attendeesValues = new ContentValues();

            attendeesValues.put("event_id", eventID);
            attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
            attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee
            // E
            // mail
            // id
            attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
            // Relationship_None(0),
            // Organizer(2),
            // Performer(3),
            // Speaker(4)
            attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
            // Required(2), Resource(3)
            attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
            // Decline(2),
            // Invited(3),
            // Tentative(4)

            //    Uri attendeuesesUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
        }

        return eventID;

    }


    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
