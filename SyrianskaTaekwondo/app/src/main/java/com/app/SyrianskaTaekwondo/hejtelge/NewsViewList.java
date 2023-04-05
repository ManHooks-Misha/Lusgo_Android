package com.app.SyrianskaTaekwondo.hejtelge;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.SliderImageNews;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.News;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityNewsViewListBinding;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import vk.help.MasterActivity;

public class NewsViewList extends MasterActivity {
    String userid, nid, type, title, id, desc,action="0", usertype, location, startdate, enddate, profile_image, timediff, created_at, name, attending, may_be, denied, created_by;
    private long mLastClickTime = 0;
    private JSONArray arr_event;
    private long mLastClickTime1 = 0;
    private long mLastClickTime2 = 0;
    long stdate_, enddate_;
    private List<News> arr_news = new ArrayList<>();
    private List<InboxList> arr_message = new ArrayList<>();
    ActivityNewsViewListBinding binding;
    AlertDialog alertDialog;

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

        binding = ActivityNewsViewListBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Locale loc = new Locale("sv", "SE");
        Locale.setDefault(loc);

        userid = new CommonMethods().getPrefsData(context, "id", "");
        usertype = new CommonMethods().getPrefsData(context, "usertype", "");
        if (getIntent() != null) {
            nid = getIntent().getStringExtra("nid");
            type = getIntent().getStringExtra("type");
        }
        assert type != null;
        if (type.equals("news")) {
            binding.txt.setText("Nyhetsdetaljer");
        }
        if (type.equals("event")) {
            binding.txt.setText("Evenemangsdetaljer");

        }
        if (type.equals("message")) {
            binding.txt.setText("Meddelandedetaljer");

        }
        if (type.equals("permission")) {
            binding.txt.setText("Behörighet");

        }
        if (new CommonMethods().isOnline(context)) {
            getNotificationAPI(nid);
        }
    }

    private void getNotificationAPI(String nid) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("nid", nid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {

                if (result.isStatus()) {

                    if (!type.equals("permission")) {

                        JSONArray obj = new JSONArray(result.getData());
                        for (int i = 0; i < obj.length(); i++) {
                            if (type.equals("news")) {
                                arr_news.add((News) (getObject(obj.getString(i), News.class)));
                            }
                            if (type.equals("message")) {
                                arr_message.add((InboxList) (getObject(obj.getString(i), InboxList.class)));

                            }
                            if (type.equals("event")) {
                                JSONObject object = obj.getJSONObject(i);
                                title = object.getString("title");
                                id = object.getString("id");
                                desc = object.getString("description");
                                location = object.getString("place");
                                startdate = object.getString("start_date");
                                enddate = object.getString("end_date");
                                created_at = object.getString("created_at");
                                created_by = object.getString("created_by");
                                name = object.getString("name");
                                profile_image = object.getString("profile_image");
                                attending = object.getString("attending");
                                may_be = object.getString("may_be");
                                denied = object.getString("denied");
                                arr_event = object.getJSONArray("details");
                            }
                        }
                    } else {
                        binding.profileName.setVisibility(View.GONE);
                        binding.newsProfile.setVisibility(View.GONE);
                        JSONObject obj = new JSONObject(result.getData());
                        String message = obj.getString("message");
                        binding.newsTxt.setVisibility(View.VISIBLE);
                        binding.newsTxt.setText(message);

                    }
                    if (arr_news.size() > 0) {
                        binding.llMain.setVisibility(View.VISIBLE);
                        if (arr_news.get(0).getDescription().length() > 0) {
                            binding.newsTxt.setVisibility(View.VISIBLE);
                            binding.newsTxt.setText(arr_news.get(0).getDescription());
                        } else {
                            binding.newsTxt.setVisibility(View.GONE);
                        }
                        if (arr_news.get(0).getDoc().length() > 0) {
                            binding.docTxt.setVisibility(View.VISIBLE);

                            File file = new File(arr_news.get(0).getDoc());
                            binding.docTxt.setText(" " + file.getName());
                        } else {
                            binding.docTxt.setVisibility(View.GONE);

                        }

                        binding.profileName.setText(arr_news.get(0).getName());
                        if (arr_news.get(0).getLink().length() > 0) {
                            binding.linkTxt.setVisibility(View.VISIBLE);

                            binding.linkTxt.setText(arr_news.get(0).getLink());
                        } else {
                            binding.linkTxt.setVisibility(View.GONE);
                        }
                        binding.linkTxt.setOnClickListener(view -> {
                            if(arr_news.get(0).getLink().toString().contains("http")){
                                Uri uri = Uri.parse(arr_news.get(0).getLink().toString()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }else {
                                Uri uri = Uri.parse("http://"+arr_news.get(0).getLink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                            /*context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", arr_news.get(0).getLink()));*/
                        });
                        binding.docTxt.setOnClickListener(view -> context.startActivity(new Intent(context, ShowDocumentActivity.class).putExtra("Url", arr_news.get(0).getDoc())));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = null;
                        try {
                            date = dateFormat.parse(arr_news.get(0).getCreated());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long now = System.currentTimeMillis() - 1000;

                        assert date != null;
                        long result1 = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);

                        updateLabel(binding.time, result1);

                        Glide.with(context)
                                .load(arr_news.get(0).getProfile_image())
                                .placeholder(R.drawable.user_diff)
                                .fitCenter()
                                .into(binding.newsProfile);
                        if (arr_news.get(0).getImages().size() > 0) {

                            SliderImageNews adapter = new SliderImageNews(NewsViewList.this, arr_news.get(0).getImages());
                            binding.imageSlider.setVisibility(View.GONE);

                            if (arr_news.get(0).getImages().size() > 0) {
                                binding.llMain.setVisibility(View.VISIBLE);
                                binding.imageSlider.setVisibility(View.VISIBLE);
                                binding.llRight.setVisibility(View.VISIBLE);
                                adapter.setCount(arr_news.get(0).getImages().size());
                                binding.imageSlider.setSliderAdapter(adapter);
                                binding.imageSlider.setIndicatorVisibility(false);
                                binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                                binding.imageSlider.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                                binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                                binding.imageSlider.setIndicatorSelectedColor(Color.BLUE);
                                binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                                binding.imageSlider.setAutoCycle(false);
                            } else {
                                binding.imageSlider.setVisibility(View.GONE);
                            }


                        } else {
                            binding.llMain.setVisibility(View.VISIBLE);

                            binding.llRight.setVisibility(View.GONE);
                        }
                        binding.llEvent.setVisibility(View.GONE);

//                       binding.newsImage.setOnClickListener(view -> startActivity(new Intent(context, ImageShowDetails.class).putExtra("image", arr_news.get(0).getImages())));
                    }

                    if (type.equals("event")) {
                        binding.DocumentTxt.setVisibility(View.GONE);
                        binding.linkTxt.setVisibility(View.GONE);
                        binding.newsTxt.setVisibility(View.GONE);
                        if (userid.equals(created_by)) {

                            binding.llUser.setVisibility(View.GONE);
                        } else {
                            if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                                binding.llUser.setVisibility(View.GONE);
                            } else {
                                binding.llUser.setVisibility(View.VISIBLE);

                            }
                            for (int i = 0; i < arr_event.length(); i++) {
                                try {
                                    JSONObject object = arr_event.getJSONObject(i);

                                    if (object.getString("action").equals("1") && (userid.equals(object.getString("user_id")))) {
                                        binding.ok.setBackgroundColor(Color.parseColor("#1da0fc"));
                                        binding.ok.setTextColor(Color.parseColor("#ffffff"));
                                        binding.cancel.setBackgroundResource(R.drawable.outline_button);
                                        binding.cancel.setTextColor(Color.parseColor("#000000"));
                                        binding.maybeUser.setBackgroundResource(R.drawable.outline_button);
                                        binding.maybeUser.setTextColor(Color.parseColor("#000000"));
                                        action = object.getString("action");

                                    }
                                    if (object.getString("action").equals("2") && (userid.equals(object.getString("user_id")))) {
                                        binding.cancel.setBackgroundColor(Color.parseColor("#1da0fc"));
                                        binding.cancel.setTextColor(Color.parseColor("#ffffff"));
                                        binding.ok.setBackgroundResource(R.drawable.outline_button);
                                        binding.ok.setTextColor(Color.parseColor("#000000"));
                                        binding.maybeUser.setBackgroundResource(R.drawable.outline_button);
                                        binding.maybeUser.setTextColor(Color.parseColor("#000000"));
                                        action = object.getString("action");

                                    }
                                    if (object.getString("action").equals("3") && (userid.equals(object.getString("user_id")))) {
                                        binding.maybeUser.setBackgroundColor(Color.parseColor("#1da0fc"));
                                        binding.maybeUser.setTextColor(Color.parseColor("#ffffff"));
                                        binding.cancel.setBackgroundResource(R.drawable.outline_button);
                                        binding.cancel.setTextColor(Color.parseColor("#000000"));
                                        binding.ok.setBackgroundResource(R.drawable.outline_button);
                                        binding.ok.setTextColor(Color.parseColor("#000000"));
                                        action = object.getString("action");

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        binding.ok.setOnClickListener(v -> {

                            if (new CommonMethods().isOnline(context)) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 5000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                if (!action.equals("1")) {
                                    onProfileImageClick(title, desc, location, stdate_, enddate_);
                                    getEventAPI("1", id, binding.ok, binding.cancel, binding.maybeUser);

                                }else{
                                    Toast.makeText(this, "Händelse har redan lagts till i din kalender", Toast.LENGTH_SHORT).show();
                                }
                                //pushAppointmentsToCalender(title, desc, location, stdate_, enddate_);

                            } else {
                                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

                            }
                        });
                        binding.cancel.setOnClickListener(v -> {

                            if (new CommonMethods().isOnline(context)) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime1 < 5000) {
                                    return;
                                }
                                mLastClickTime1 = SystemClock.elapsedRealtime();

                                getEventAPI("2", id, binding.ok, binding.cancel, binding.maybeUser);

                            } else {
                                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

                            }
                        });
                        binding.maybeUser.setOnClickListener(v -> {

                            if (new CommonMethods().isOnline(context)) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime2 < 5000) {
                                    return;
                                }
                                mLastClickTime2 = SystemClock.elapsedRealtime();

                                getEventAPI("3", id, binding.ok, binding.cancel, binding.maybeUser);

                            } else {
                                Toast.makeText(context, Objects.requireNonNull(context).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

                            }
                        });
                        Glide.with(context)
                                .load(profile_image)
                                .placeholder(R.drawable.user_diff)
                                .fitCenter()
                                .into(binding.newsProfile);

                        Locale loc = new Locale("sv", "SE");
                        Locale.setDefault(loc);

                        try {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date past = format.parse(created_at);
                            Date now = new Date();
                            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
                            if (days < 9 && days > 1) {
                                timediff = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " Dagar sedan";
                            } else if (days == 0) {
                                timediff = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " Timmar sedan";

                                long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                                if (hours == 0) {
                                    long mins = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                                    timediff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minuter sedan";

                                    if (mins == 0) {
                                        timediff = "Precis nu";

                                    }
                                }

                            } else {
                                timediff = new SimpleDateFormat("dd MMM, yyyy").format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                            }

                        } catch (Exception j) {
                            j.printStackTrace();
                        }
                        binding.llEvent.setVisibility(View.VISIBLE);

                        binding.txt.setText(Html.fromHtml(title));
                        binding.title.setVisibility(View.GONE);
                        //   binding.title.setText(Html.fromHtml(title));

                        if (desc != null) {
                            if (desc.length() > 0) {
                                binding.lldesc.setVisibility(View.VISIBLE);
                            } else {
                                binding.lldesc.setVisibility(View.GONE);

                            }
                        }
                        if (location != null) {
                            if (location.length() > 0) {
                                binding.llloc.setVisibility(View.VISIBLE);
                            } else {
                                binding.llloc.setVisibility(View.GONE);

                            }
                        }
                        binding.loc.setText(Html.fromHtml(location));
                        binding.desc.setText(Html.fromHtml(desc));
                        binding.time.setText(timediff);
                        binding.profileName.setText(name);
                        String[] arrstart = startdate.split(" ");
//        String txt_stdate = arrstart[0];
                        String txt_sttime = arrstart[1];

                        String[] sttime_ = txt_sttime.split(":");
                        txt_sttime = sttime_[0] + ":" + sttime_[1];

                        String[] arrend = enddate.split(" ");
//        String enddt = arrend[0];
                        String endtime = arrend[1];
                        String[] endtime_ = endtime.split(":");
                        endtime = endtime_[0] + ":" + endtime_[1];
                        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        //In which you need put here

                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat1);

                        try {
                            Date stpast = format.parse(startdate);
                            Date endpast = format.parse(enddate);
                            assert stpast != null;
                            startdate = sdf.format(stpast.getTime());
                            assert endpast != null;
                            enddate = sdf.format(endpast.getTime());
                            stdate_ = stpast.getTime();
                            enddate_ = endpast.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        binding.sttime.setText(txt_sttime + " - " + endtime);
                        if (startdate.equals(enddate)) {
                            binding.date.setVisibility(View.GONE);
                        } else {
                            binding.date.setVisibility(View.VISIBLE);
                        }
                        binding.date.setText(startdate + " - " + enddate);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "notificationDetails");
    }

    private void updateLabel(AppCompatTextView txt, long result) {
        if (result == 0) {
            txt.setText(String.format("%s", "I dag"));

        } else if (result == 1) {
            txt.setText(String.format("%s", "I går"));

        } else {

            txt.setText(result + String.format("%s", " dagar sedan"));

        }

    }


    private void getEventAPI(String action1, String event_id, AppCompatTextView txt_show_attend, AppCompatTextView txt_show_no, AppCompatTextView txt_show_maybe) {
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("action", action1);
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
                    action=data;
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

/*

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {


        private List<IsReadUserArr> horizontalList;
        private Context context;


        public UserAdapter(List<IsReadUserArr> horizontalList, Context context) {
            this.horizontalList = horizontalList;
            this.context = context;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout ll_menu;
            CircleImageView imageView;
            AppCompatImageView cancel;
            TextView txtview;

            MyViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.group_name);
                imageView = view.findViewById(R.id.pos);
                //   pos = view.findViewById(R.id.pos);
//            ll_menu = view.findViewById(R.id.ll_menu);
            }
        }


        @NotNull
        @Override
        public UserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_adapter, parent, false);

            return new UserAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final UserAdapter.MyViewHolder holder, final int position) {
            if (horizontalList.get(position).getFirst_name().length() > 0) {
                holder.txtview.setText(horizontalList.get(position).getFirst_name() + " " + horizontalList.get(position).getLast_name());
            } else {
                holder.txtview.setText(horizontalList.get(position).getUsername());

            }
            Glide.with(context)
                    .load(horizontalList.get(position).getProfile_image())
                    .fitCenter()
                    .placeholder(R.drawable.user_diff)
                    .into(holder.imageView);

        }


        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

*/

    public void pushAppointmentsToCalender(String title, String addInfo, String place, long startDate, long enddate) {
        /***************** Event: note(without alert) *******************/

        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, enddate)
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.ALLOWED_REMINDERS, true)
                    .putExtra(CalendarContract.Events.DESCRIPTION, addInfo)
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, place)
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
            startActivity(intent);
        } else {
            Calendar cal = Calendar.getInstance();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=YEARLY");
            intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
            intent.putExtra("title", "A Test Event from android app");
            startActivity(intent);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewsViewList.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setCancelable(false);
                            builder.setMessage("Vill du lägga till en händelse i din kalender?");
                            builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                                pushAppointmentsToCalender(NewsViewList.this, title, about, loc, 1, stdate_, enddate_, true, false);


                                alertDialog.dismiss();

                            });
                            builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
                            alertDialog = builder.create();
                            alertDialog.show();

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


    public static long pushAppointmentsToCalender(Activity curActivity, String title, String addInfo, String place, int status, long startDate, long enddate, boolean needReminder, boolean needMailService) {
        /***************** Event: note(without alert) *******************/

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
        Toast.makeText(curActivity, "Händelsen har lagts till i din kalender.", Toast.LENGTH_SHORT).show();

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

          //  Uri attendeuesesUri = curActivity.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
        }

        return eventID;

    }

}
