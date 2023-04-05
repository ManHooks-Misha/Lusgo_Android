package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.Eventshow;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Details;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Event;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vk.help.Common;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {
    private List<Event> horizontalList;
    private String usertype;
    private String userid;
    private Activity context;
    private long mLastClickTime = 0;
    private long mLastClickTime1 = 0;
    private long mLastClickTime2 = 0;

    public final DateTimeFormatter mTimeFormat = DateTimeFormat.forPattern(" h:mm a");
    private CommonMethods cmn;
    private ArrayList<HashMap<String, String>> arr_participateuser_attend = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_notattend = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_maybe = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_participateuser_notanswer = new ArrayList<>();
    private AlertDialog alertDialog;
    long stdate_,enddate_;
    private String title,description,place;
    public CalendarAdapter(List<Event> horizontalList, Activity context, String usertype, String userid) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.usertype = usertype;
        this.userid = userid;
        cmn = new CommonMethods();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_public;
        private ImageView img;
        private AppCompatImageView delete;
        private LinearLayout card_view;
        private LinearLayout ll_coach, ll_user, ll_maybe, ll_attend, ll_notans, ll_notattend;
        private AppCompatTextView teamname,date, stdate, end_event, title, desc, attend, maybe, not_ans, not_attend, maybe_user, ok, cancel, loc, join;

        MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            end_event = view.findViewById(R.id.end_event);
            stdate = view.findViewById(R.id.sttime);
            title = view.findViewById(R.id.title);
            desc = view.findViewById(R.id.desc);
            attend = view.findViewById(R.id.attend);
            not_ans = view.findViewById(R.id.not_ans);
            maybe = view.findViewById(R.id.maybe);
            not_attend = view.findViewById(R.id.not_attend);
            ll_user = view.findViewById(R.id.ll_user);
            ll_coach = view.findViewById(R.id.coach_admin_block);
            maybe_user = view.findViewById(R.id.maybe_user);
            ok = view.findViewById(R.id.ok);
            cancel = view.findViewById(R.id.cancel);
            ll_attend = view.findViewById(R.id.ll_attend);
            ll_notans = view.findViewById(R.id.ll_notans);
            ll_notattend = view.findViewById(R.id.ll_notattend);
            ll_maybe = view.findViewById(R.id.ll_maybe);
            loc = view.findViewById(R.id.loc);
            join = view.findViewById(R.id.join);
            ll_public = view.findViewById(R.id.ll_public);
            delete = view.findViewById(R.id.delete);
            card_view = view.findViewById(R.id.card_view);
            teamname = view.findViewById(R.id.team_name);
            img = view.findViewById(R.id.img1);

            //   pos = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
        }
    }


    @NotNull
    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calander_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {
        String stdate = horizontalList.get(position).getStartdate();
        String[] arrstart = stdate.split(" ");
//        String txt_stdate = arrstart[0];
        String txt_sttime = arrstart[1];
        String enddate = horizontalList.get(position).getEnddate();
        String[] arrend = enddate.split(" ");
//        String enddt = arrend[0];
        String endtime = arrend[1];
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //In which you need put here
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);
        String team_namestr= horizontalList.get(position).getTeam_name();
        if(team_namestr!=null) {

            if (team_namestr.length()>0) {
                if(team_namestr.equals("null")){
                    holder.img.setVisibility(View.GONE);
                    holder.teamname.setVisibility(View.GONE);
                }else {
                    holder.teamname.setText(team_namestr);
                }
            } else {
                holder.img.setVisibility(View.GONE);
                holder.teamname.setVisibility(View.GONE);
            }
        }else{
            holder.img.setVisibility(View.GONE);
            holder.teamname.setVisibility(View.GONE);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat1, Locale.US);

        try {
            Date stpast = format.parse(stdate);
            Date endpast = format.parse(enddate);
            assert stpast != null;
            stdate = sdf.format(stpast.getTime());
            assert endpast != null;
            enddate = sdf.format(endpast.getTime());

            if (System.currentTimeMillis() > stpast.getTime() && System.currentTimeMillis() < endpast.getTime()) {
                holder.end_event.setVisibility(View.GONE);
            } else {
                holder.end_event.setVisibility(View.VISIBLE);
                holder.end_event.setText("Avsluta händelse");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<Details> arr_show = horizontalList.get(position).getDetails();
        for (int i = 0; i < arr_show.size(); i++) {
            Details obj = arr_show.get(i);
            if (obj.getAction().equals("1") && (userid.equals(obj.getUser_id()))) {
                holder.ok.setBackgroundColor(Color.parseColor("#1da0fc"));
                holder.ok.setTextColor(Color.parseColor("#ffffff"));
                holder.cancel.setBackgroundResource(R.drawable.outline_button);
                holder.cancel.setTextColor(Color.parseColor("#000000"));
                holder.maybe_user.setBackgroundResource(R.drawable.outline_button);
                holder.maybe_user.setTextColor(Color.parseColor("#000000"));
            }
            if (obj.getAction().equals("2") && (userid.equals(obj.getUser_id()))) {
                holder.cancel.setBackgroundColor(Color.parseColor("#1da0fc"));
                holder.cancel.setTextColor(Color.parseColor("#ffffff"));
                holder.ok.setBackgroundResource(R.drawable.outline_button);
                holder.ok.setTextColor(Color.parseColor("#000000"));
                holder.maybe_user.setBackgroundResource(R.drawable.outline_button);
                holder.maybe_user.setTextColor(Color.parseColor("#000000"));
            }
            if (obj.getAction().equals("3") && (userid.equals(obj.getUser_id()))) {
                holder.maybe_user.setBackgroundColor(Color.parseColor("#1da0fc"));
                holder.maybe_user.setTextColor(Color.parseColor("#ffffff"));
                holder.cancel.setBackgroundResource(R.drawable.outline_button);
                holder.cancel.setTextColor(Color.parseColor("#000000"));
                holder.ok.setBackgroundResource(R.drawable.outline_button);
                holder.ok.setTextColor(Color.parseColor("#000000"));
            }

        }
        if (position % 2 == 0) {
            holder.card_view.setBackgroundResource(R.drawable.calandar_event);
        } else {
            holder.card_view.setBackgroundResource(R.drawable.calander_event_purple);
        }

        holder.itemView.setOnClickListener(view -> {
            try {
                String event_id = horizontalList.get(position).getId();
                if (userid.length() > 0) {
                    context.startActivity(new Intent(context, Eventshow.class).putExtra("DATA", Common.INSTANCE.getJSON(horizontalList.get(position))).putExtra("id", event_id));
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    context.startActivity(new Intent(context, Eventshow.class).putExtra("DATA", Common.INSTANCE.getJSON(horizontalList.get(position))).putExtra("id", event_id));
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            }catch (Exception e){
                e.printStackTrace();
            }


        });
        holder.date.setText(stdate + " - " + enddate);

        title = horizontalList.get(position).getTitle().trim();
        title = title.replaceFirst("<p dir=\"ltr\">", "");
        // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
        title = title.replaceAll("</p>", "");
        holder.title.setText(Html.fromHtml(title));

        //holder.title.setText(Html.fromHtml(horizontalList.get(position).getTitle().trim()));
        holder.desc.setText(Html.fromHtml(horizontalList.get(position).getLocation().trim()));
        holder.loc.setText(Html.fromHtml(horizontalList.get(position).getLocation().trim()));

        String[] sttime_ = txt_sttime.split(":");
        txt_sttime = sttime_[0] + ":" + sttime_[1];
        String[] endtime_ = endtime.split(":");
        endtime = endtime_[0] + ":" + endtime_[1];
        holder.stdate.setText(txt_sttime + " - " + endtime);
        holder.attend.setText(horizontalList.get(position).getAttending());
        holder.not_ans.setText(horizontalList.get(position).getNo_response());
        holder.maybe.setText(horizontalList.get(position).getMay_be());
        holder.not_attend.setText(horizontalList.get(position).getDenied());

        if (usertype.length() == 0 || usertype.equals(ConsURL.members) || horizontalList.get(position).getTeam_id().equals("0")) {
            holder.delete.setVisibility(View.GONE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        if (horizontalList.get(position).getDetail().length() > 0) {
            holder.desc.setVisibility(View.VISIBLE);
        } else {
            holder.desc.setVisibility(View.GONE);
        }
        if (userid.length() == 0) {
            holder.ll_public.setVisibility(View.VISIBLE);
            holder.ll_coach.setVisibility(View.GONE);
            holder.ll_user.setVisibility(View.GONE);
        }


        holder.delete.setOnClickListener(view -> showCustomDialog(horizontalList.get(position).getId(), position));
        if (usertype.equals(ConsURL.coach) || usertype.equals(ConsURL.members)||usertype.equals(ConsURL.sub_coach)) {
            if (usertype.equals(ConsURL.members) || horizontalList.get(position).getTeam_id().equals("0")) {
                holder.ll_coach.setVisibility(View.GONE);
                holder.ll_user.setVisibility(View.GONE);
                holder.ll_public.setVisibility(View.GONE);
            } else if (!(horizontalList.get(position).getTeam_id().equals("0"))) {
                holder.ll_coach.setVisibility(View.GONE);
                holder.ll_user.setVisibility(View.GONE);
                holder.ll_public.setVisibility(View.GONE);

            }
        } else if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
            holder.ll_coach.setVisibility(View.GONE);
            holder.ll_user.setVisibility(View.GONE);
            holder.ll_public.setVisibility(View.GONE);

        }
        holder.join.setOnClickListener(view -> {
            String id = horizontalList.get(position).getId();
            title = horizontalList.get(position).getTitle();
           description = horizontalList.get(position).getDetail();
            place = horizontalList.get(position).getLocation();
            String startdate = horizontalList.get(position).getStartdate();
            String enddates = horizontalList.get(position).getEnddate();

           // SimpleDateFormat sdf = new SimpleDateFormat(myFormat1);
            try {
                Date stpast = format.parse(startdate);
                Date endpast = format.parse(enddates);

                stdate_ = Objects.requireNonNull(stpast).getTime();
                enddate_ = Objects.requireNonNull(endpast).getTime();

            } catch (ParseException e) {
                e.printStackTrace();
            }
            String token = new CommonMethods().getPrefsData(context, "token", "");
            showCustomDialogPublicuser(id,token,title,description,place,stdate_,enddate_);
        });
        holder.ok.setOnClickListener(v -> {

            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                getEventAPI("1", horizontalList.get(position).getId(), holder.ok, holder.cancel, holder.maybe_user);

            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });
        holder.cancel.setOnClickListener(v -> {

            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime1 < 5000) {
                    return;
                }
                mLastClickTime1 = SystemClock.elapsedRealtime();

                getEventAPI("2", horizontalList.get(position).getId(), holder.ok, holder.cancel, holder.maybe_user);

            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });
        holder.maybe_user.setOnClickListener(v -> {

            if (cmn.isOnline(context)) {
                if (SystemClock.elapsedRealtime() - mLastClickTime2 < 5000) {
                    return;
                }
                mLastClickTime2 = SystemClock.elapsedRealtime();

                getEventAPI("3", horizontalList.get(position).getId(), holder.ok, holder.cancel, holder.maybe_user);

            } else {
                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

            }
        });


    }


    @Override
    public int getItemCount() {
        return horizontalList.size();

    }

    private CharSequence noTrailingwhiteLines(CharSequence text) {

        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    private void getEventAPI(String action, String event_id, AppCompatTextView txt_show_attend, AppCompatTextView txt_show_no, AppCompatTextView txt_show_maybe) {
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
                    String data = result.getData();
                    if (data.equals("1")) {
                        txt_show_attend.setBackgroundColor(Color.parseColor("#1da0fc"));
                        txt_show_attend.setTextColor(Color.parseColor("#ffffff"));
                        txt_show_no.setBackgroundResource(R.drawable.outline_button);
                        txt_show_no.setTextColor(Color.parseColor("#000000"));
                        txt_show_maybe.setBackgroundResource(R.drawable.outline_button);
                        txt_show_maybe.setTextColor(Color.parseColor("#000000"));

                    }
                    if (data.equals("2")) {
                        txt_show_no.setBackgroundColor(Color.parseColor("#1da0fc"));
                        txt_show_no.setTextColor(Color.parseColor("#ffffff"));
                        txt_show_maybe.setBackgroundResource(R.drawable.outline_button);
                        txt_show_maybe.setTextColor(Color.parseColor("#000000"));
                        txt_show_attend.setBackgroundResource(R.drawable.outline_button);
                        txt_show_attend.setTextColor(Color.parseColor("#000000"));

                    }
                    if (data.equals("3")) {
                        txt_show_maybe.setBackgroundColor(Color.parseColor("#1da0fc"));
                        txt_show_maybe.setTextColor(Color.parseColor("#ffffff"));
                        txt_show_no.setBackgroundResource(R.drawable.outline_button);
                        txt_show_no.setTextColor(Color.parseColor("#000000"));
                        txt_show_attend.setBackgroundResource(R.drawable.outline_button);
                        txt_show_attend.setTextColor(Color.parseColor("#000000"));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "update_eventResponse");
    }

    private void getEventDeleteAPI(String event_id, int pos) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
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
                    horizontalList.remove(pos);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_event");
    }


    private void getpublicUserAPI(String event_id, String token) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("event_id", event_id);
            object.put("name", "");
            object.put("date_of_birth", "");
            object.put("token", token);
            object.put("action", 1);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    Toast.makeText(context, "Du går med i det här evenemanget", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else{
                    alertDialog.dismiss();
                    Toast.makeText(context, "Offentlig användare deltar redan i detta evenemang", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "join_event");
    }


    private void showCustomDialog(String event_id, int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Vill du säkert ta bort det här evenemanget?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (cmn.isOnline(context)) {
                getEventDeleteAPI(event_id, pos);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());


        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showCustomDialogPublicuser(String id, String token,String title,String description,String place,long stdate_,long enddate_) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = context.findViewById(android.R.id.content);

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
            onProfileImageClick(title, description, place, stdate_, enddate_);

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
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, 101);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_permission_title));
        builder.setMessage(context.getString(R.string.dialog_permission_message));
        builder.setPositiveButton(context.getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(context.getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    void onProfileImageClick(String title, String about, String loc, long stdate_, long enddate_) {
        Dexter.withActivity(context)
                .withPermissions(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            pushAppointmentsToCalender(context, title, about, loc, 1, stdate_, enddate_, true, false);
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



    public static long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, String place, int status, long startDate, long enddate, boolean needReminder, boolean needMailService) {
        /***************** Event: note(without alert) *******************/

        String eventUriString = "content://com.android.calendar/events";
        ContentValues eventValues = new ContentValues();

        eventValues.put("calendar_id", 1);
        eventValues.put("title", title);
        eventValues.put("description", addInfo);
        eventValues.put("eventLocation", place);

        long endDate = startDate + 1000 * 60 * 60; // For next 1hr

        eventValues.put("dtstart", startDate);
        eventValues.put("dtend", endDate);

        eventValues.put("allDay", 0);
        eventValues.put("eventStatus", status);
        eventValues.put("eventTimezone", "UTC/GMT +2:00");

    /*eventValues.put("visibility", 3); // visibility to default (0),
                                        // confidential (1), private
                                        // (2), or public (3):
    eventValues.put("transparency", 0); // You can control whether
                                        // an event consumes time
                                        // opaque (0) or transparent
                                        // (1).
      */
        eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

        Uri eventUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

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

            Uri reminderUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
        }

        /***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

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

            Uri attendeuesesUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
        }

        return eventID;

    }
}
