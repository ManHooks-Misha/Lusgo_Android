package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.CollapsibleCalendarView;
import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.SliderViewDemo;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.CalendarAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.MonthEventChildAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.MonthwiseCalendarAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.ApiClient;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.ApiInterface;
import com.app.SyrianskaTaekwondo.hejtelge.customClass.Progressbar;
import com.app.SyrianskaTaekwondo.hejtelge.model.MonthlyVMDateModel;
import com.app.SyrianskaTaekwondo.hejtelge.model.EventsScrollPositionModel;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Details;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Event;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ListNews;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.MonthlyEventResponse;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.gson.Gson;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Common;

public class Calendar_Fragment extends Fragment implements CollapsibleCalendarView.Listener<Event> {
    private CollapsibleCalendarView calander;
    private LinearLayout ll_add, lladd_month;
    NestedScrollView nestedScrollView;
    int selectedPos = 0;
    private FrameLayout ff_news, ff_inbox, ff_event;
    private String msg, eventdate, title, detail, id, location, isEvent;
    private List<Event> events = new ArrayList<>();
    private List<Details> events_detail = new ArrayList<>();
    private List<CampaignPojo> list_cam = new ArrayList<>();
    private String status, userid, usertype, teamid, lastDate;
    private LinearLayout ll_create;
    private ProgressBar  progress_dialog;
    Progressbar progressbar;
    RecyclerView recyclerView_day, recyclerView_month;
    private CommonMethods cmn = new CommonMethods();
    private SliderView sliderView;
    private ArrayList<HashMap<String, String>> arr_img = new ArrayList<>();
    private SliderViewDemo adapter;
    private MonthwiseCalendarAdapter adapter_month;
    private ViewPager viewpager;
    private AppCompatTextView calander_txt, month_txt;
    private CalendarAdapter calendarAdapter;
    private ArrayList<HashMap<String, ArrayList<HashMap<String, List<Event>>>>> eventsmap__ = new ArrayList<>();
    ArrayList<MonthlyEventResponse.Datum> eventDataList = new ArrayList<>();
    ArrayList<MonthlyEventResponse.MonthlyDateVM> eventMonthList = new ArrayList<>();
    ArrayList<EventsScrollPositionModel> arrListScrollPos = new ArrayList<>();
    ArrayList<MonthlyVMDateModel> arrListMonthDate = new ArrayList<>();
    MonthEventChildAdapter monthEventChildAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateUI() {
        String news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        String event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        String Is_Coach = cmn.getPrefsData(getActivity(), "Is_Coach", "");

        if (userid.length() > 0) {
            if (Is_Coach.equals("true")) {
                ff_news.setVisibility(View.VISIBLE);
                ff_inbox.setVisibility(View.VISIBLE);
                ff_event.setVisibility(View.VISIBLE);
                ll_create.setVisibility(View.VISIBLE);
            } else {
                ff_news.setVisibility(View.GONE);
                ff_inbox.setVisibility(View.GONE);
                ff_event.setVisibility(View.GONE);
                ll_create.setVisibility(View.GONE);
            }
        } else {
            ll_create.setVisibility(View.GONE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.calendar_fragment, container, false);
        events.clear();

        //  eventsmap__.clear();
        progressbar = new Progressbar();
        Locale locale = new Locale("sv", "SE"); // ( language code, country code );
        Locale.setDefault(locale);
        userid = cmn.getPrefsData(getActivity(), "id", "");
        usertype = cmn.getPrefsData(getActivity(), "usertype", "");
        teamid = cmn.getPrefsData(getActivity(), "team_id", "");
        loadID(root);
        calendarAdapter = new CalendarAdapter(
                events, getActivity(), usertype, userid);
        recyclerView_day.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView_day.setAdapter(calendarAdapter);
        calander_txt.setBackgroundColor(getResources().getColor(R.color.bluedark));
        calander_txt.setTextColor(Color.WHITE);
        month_txt.setBackgroundColor(getResources().getColor(R.color.white));
        month_txt.setTextColor(getResources().getColor(R.color.bluedark));

        adapter_month = new MonthwiseCalendarAdapter(
                eventsmap__, getActivity(), usertype, userid);
        recyclerView_month.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView_month.setAdapter(adapter_month);

        if (cmn.isOnline(getActivity())) {
            getCampaignAPI();
            ll_add.setVisibility(View.VISIBLE);
            lladd_month.setVisibility(View.GONE);
            EventRequest();
        }


        updateUI();
        ff_news.setOnClickListener(view ->
        {
            startActivity(new Intent(getActivity(), CreateNews.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        ff_inbox.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), CreateMessage.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        ff_event.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), CreateEvent.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    public void loadID(View view) {
        recyclerView_day = view.findViewById(R.id.list_day);
        calander = view.findViewById(R.id.calendar);
        nestedScrollView = view.findViewById(R.id.swipe);
        recyclerView_month = view.findViewById(R.id.list_month);
        ff_inbox = view.findViewById(R.id.ff_inbox);
        ff_news = view.findViewById(R.id.ff_news);
        ff_event = view.findViewById(R.id.ff_event);
        ll_create = view.findViewById(R.id.ll_create);
        ll_add = view.findViewById(R.id.ll_add1);
        lladd_month = view.findViewById(R.id.ll_addmonth);
        progress_dialog = view.findViewById(R.id.progress_dialog);
        sliderView = view.findViewById(R.id.imageSlider);
        //     swipe = view.findViewById(R.id.swipe);
        viewpager = view.findViewById(R.id.viewpager);
        calander_txt = view.findViewById(R.id.calendar_txt);
        month_txt = view.findViewById(R.id.month_txt);
        adapter = new SliderViewDemo(getActivity(), arr_img);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        //sliderView.setIndicatorSelectedColor(Color.BLUE);
        sliderView.setIndicatorVisibility(false);
        Locale locale = new Locale("sv", "SE"); // ( language code, country code );
        Locale.setDefault(locale);
        Calendar c = Calendar.getInstance(locale); // here I am using the method

        calander_txt.setOnClickListener(v -> {
            calander_txt.setBackgroundColor(getResources().getColor(R.color.bluedark));
            calander_txt.setTextColor(Color.WHITE);
            month_txt.setBackgroundColor(getResources().getColor(R.color.white));
            month_txt.setTextColor(getResources().getColor(R.color.bluedark));
            if (cmn.isOnline(getActivity())) {
                ll_add.setVisibility(View.VISIBLE);
                lladd_month.setVisibility(View.GONE);

                // events.clear();
                 // EventRequest();
            } else {
                Toast.makeText(getActivity(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }


        });


        month_txt.setOnClickListener(v -> {
            month_txt.setBackgroundColor(getResources().getColor(R.color.bluedark));
            month_txt.setTextColor(Color.WHITE);
            calander_txt.setBackgroundColor(getResources().getColor(R.color.white));
            calander_txt.setTextColor(getResources().getColor(R.color.bluedark));


            if (cmn.isOnline(getActivity())) {
                getEventMonthListApi();

            } else {
                Toast.makeText(getActivity(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }


        });
        calander.setListener(this);


    }

    private void EventRequest() {
        events.clear();
        //   bindin.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        ListNews asgn = new ListNews();
        asgn.user_id = userid;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.offset = "0";
        asgn.limit = "1000";
        asgn.team_id = teamid;


        String tset = gson.toJson(asgn);
        Log.e("tset", tset);
        String url = ConsURL.BASE_URL_TEST + "eventList";

        Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).post(RequestBody.create(MediaType.parse("application/json"), tset)).build());


        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Log.e("Calander", e.getMessage()));
                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) {
                try {
                    if (response.body() != null) {
                        String res = Objects.requireNonNull(Objects.requireNonNull(response).body()).string();
                        JSONObject obje = new JSONObject(res);
                        status = obje.optString("status");
                        msg = obje.optString("message");
                        JSONArray object = obje.getJSONArray("data");
                        events.clear();
                        if (status.equals("true")) {
                            for (int i = 0; i < object.length(); i++) {
                                JSONObject objvalue = object.getJSONObject(i);
                                eventdate = objvalue.getString("start_date");
                                String[] arrstart = eventdate.split(" ");
                                String stdate = arrstart[0];
                                String enddate = objvalue.getString("end_date");
                                String[] arrend = enddate.split(" ");
                                String enddt = arrend[0];

                                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = null, date1 = null;
                                try {
                                    date = sdf.parse(stdate);
                                    date1 = sdf.parse(enddt);
                                    //  calander.getEventsForDate(date);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                assert date != null;
                                long millis = date.getTime();
                                //   event.setmDate(millis);
//                                LocalDate d = convertToLocalDateViaInstant(date);
//                                List<? extends CollapsibleCalendarEvent> event_ = calander.getEventsForDate(d);
//                                for (int j = 0; j < event_.size(); j++) {
//                                    CollapsibleCalendarEvent et = event_.get(j);
//                                    calander.removeEvent(et);
//                                }

//                                int days = Days.daysBetween(date1, date2).getDays();

                                int days = getDaysDifference(date, date1);
                                JSONArray arr = objvalue.getJSONArray("details");
                                events_detail = new ArrayList<>();


                                for (int j = 0; j <= days; j++) {
                                    Event event = new Event();
                                    event.setDetails(events_detail);
                                    event.setStartdate(eventdate);
                                    event.setEnddate(enddate);
                                    title = objvalue.getString("title");
                                    event.setTitle(title);
                                    detail = objvalue.getString("description");
                                    event.setDetail(detail);
                                    long ml = millis + (86400000 * j);
                                    String dt = getDate(ml);
                                    event.setmDate(ml);
                                    event.setEventdate(dt);
                                    location = objvalue.getString("place");
                                    String team_id = objvalue.getString("team_id");
                                    String teamname = objvalue.getString("team_name");
                                    event.setLocation(location);
                                    id = objvalue.getString("id");
                                    event.setId(id);
                                    isEvent = objvalue.getString("is_public");
                                    String attending = objvalue.getString("attending");
                                    String maybe = objvalue.getString("may_be");
                                    String no_response = objvalue.getString("no_response");
                                    String denied = objvalue.getString("denied");
                                    event.setIsEvent(isEvent);
                                    event.setTeam_id(team_id);
                                    event.setTeam_name(teamname);
                                    event.setAttending(attending);
                                    event.setDenied(denied);
                                    event.setNo_response(no_response);
                                    event.setMay_be(maybe);
                                    // calander.removeEvent(event);

                                    events.add(event);

                                }
                                // events.add(new Event(title, millis, endinghour, endingmin, eventcolor, detail, id, startinghour, startingmin, location, isEvent, userstatus, insertby, Attending, NotAnswer, NotAtending, Maybe, eventdate));


                            }

                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (status.equals("true")) {
                            calander.addEvents(events);
                            //dialog.setVisibility(View.GONE);
                            ll_add.setVisibility(View.VISIBLE);
                            lladd_month.setVisibility(View.GONE);

                        } else {
                            cmn.showAlert(msg, getActivity());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // dialog.setVisibility(View.GONE);
                }


            }
        });


    }


    private String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public void onDateSelected(LocalDate date, List<Event> event) {
        events.clear();
        events.addAll(event);
        calendarAdapter.notifyDataSetChanged();
        lladd_month.setVisibility(View.GONE);
        ll_add.setVisibility(View.VISIBLE);

    }

    @Override
    public void onMonthChanged(LocalDate date) {

    }

    @Override
    public void onHeaderClick() {

    }

    private static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    private void getCampaignAPI() {
        list_cam.clear();
        //     AddCampaign.img.clear();
        //arr.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        list_cam.add((CampaignPojo) (Common.INSTANCE.getObject(obj.getString(i), CampaignPojo.class)));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                arr_img.clear();
                if (list_cam.size() > 0) {
                    arr_img.addAll(list_cam.get(0).getSponsers());
                } else {
                    sliderView.setVisibility(View.GONE);
                }
                adapter.setCount(arr_img.size());

                adapter.notifyDataSetChanged();

               /* if (arr.size() > 0) {

                } else {
                    ll_norecord.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);

                }*/
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "Campaigns");
    }


    private void getEventMonthListApi() {
        arrListMonthDate.clear();
        progress_dialog.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        MonthlyEventResponse model = new MonthlyEventResponse("f76646abb2bb5408ecc6d8e36b64c9d8", "1000", "0", "", userid);
        retrofit2.Call<MonthlyEventResponse> call = apiService.getMonthlyEvents(model);
        call.enqueue(new retrofit2.Callback<MonthlyEventResponse>() {
            @Override
            public void onResponse(retrofit2.Call<MonthlyEventResponse> call, retrofit2.Response<MonthlyEventResponse> response) {
                MonthlyEventResponse res = response.body();

                events.clear();
                // String respnsee = new Gson().toJson(res);
                ll_add.setVisibility(View.GONE);
                lladd_month.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    if (res.status.equals("true")) {
                        progress_dialog.setVisibility(View.GONE);
                        eventDataList = res.data;
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        for (int i = 0; i < eventDataList.size(); i++) {
                            for (int k = 0; k < eventDataList.get(i).monthlyDateVM.size(); k++) {
                                MonthlyVMDateModel monthlyVMDateModel = new MonthlyVMDateModel();
                                monthlyVMDateModel.date = eventDataList.get(i).monthlyDateVM.get(k).date;
                                monthlyVMDateModel.eventDetails = eventDataList.get(i).monthlyDateVM.get(k).eventDetails;
                                arrListMonthDate.add(monthlyVMDateModel);
                            }
                        }

                        for (int n = 0; n < arrListMonthDate.size(); n++) {
                            if (currentDate.contains(arrListMonthDate.get(n).date)) {
                                selectedPos = n;
                            }
                            try {
                                boolean value = isDateFuture(arrListMonthDate.get(n).date);
                                if (value) {
                                    EventsScrollPositionModel positionModel = new EventsScrollPositionModel();
                                    positionModel.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(arrListMonthDate.get(n).date));
                                    positionModel.setPosition(n);
                                    arrListScrollPos.add(positionModel);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (selectedPos == 0) {
                            getNearestDate(arrListScrollPos, currentDate);
                        }

                        recyclerView_month.setHasFixedSize(true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recyclerView_month.setLayoutManager(layoutManager);
                        monthEventChildAdapter = new MonthEventChildAdapter(requireContext(), arrListMonthDate);
                        recyclerView_month.setAdapter(monthEventChildAdapter);
                        recyclerView_month.post(() -> {
                            try {
                                float y = recyclerView_month.getY() + recyclerView_month.getChildAt(selectedPos).getY();
                                nestedScrollView.smoothScrollTo(0, (int) y);
                                monthEventChildAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "" + res.message, Toast.LENGTH_SHORT).show();

                    }


                } else {
                    Toast.makeText(getActivity(), "Serverfel", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(retrofit2.Call<MonthlyEventResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_LONG).show();
                progress_dialog.setVisibility(View.GONE);
            }
        });
    }

    public boolean isDateFuture(String date) {
        boolean value;
        Date enteredDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            enteredDate = sdf.parse(date);
        } catch (Exception ex) {
            // enteredDate will be null if date="287686";
        }
        Date currentDate = new Date();
        if (enteredDate.after(currentDate)) {
            value = true;
        } else
            value = false;

        return value;
    }

    public Date getNearestDate(List<EventsScrollPositionModel> dates, String currentDates) {
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentDates);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long minDiff = -1, currentTime = currentDate.getTime();
        Date minDate = null;
        for (EventsScrollPositionModel date : dates) {
            long diff = Math.abs(currentTime - date.getDate().getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = date.getDate();
                selectedPos = date.getPosition();
            }
        }
        return minDate;
    }

}
