package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.SyrianskaTaekwondo.hejtelge.CollapsibleCalendarView;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.CalendarAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.DateWiseCalenderBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Details;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Event;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ListNews;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.google.gson.Gson;

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
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Fragment;

public class date_wise_Calender extends Fragment implements CollapsibleCalendarView.Listener<Event> {
    private String role, userid, teamid, usertype;
    private CommonMethods cmn = new CommonMethods();
    private String msg, status, eventdate, title, detail, id, location, isEvent;
    private List<Event> events = new ArrayList<>();
    private List<Details> events_detail = new ArrayList<>();
    DateWiseCalenderBinding binding;
    private CalendarAdapter calenderAdapter;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DateWiseCalenderBinding.inflate(inflater, container, false);
        role = cmn.getPrefsData(getActivity(), "usertype", "");
        userid = cmn.getPrefsData(getContext(), "id", "");
        teamid = cmn.getPrefsData(getContext(), "team_id", "");
        usertype = cmn.getPrefsData(getActivity(), "usertype", "");
//        Locale locale = new Locale("sv", "SE"); // ( language code, country code );
//        Locale.setDefault(locale);
        calenderAdapter = new CalendarAdapter(
                events, getActivity(), usertype, userid);
        binding.calendarEventList.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.calendarEventList.setAdapter(calenderAdapter);
        if (cmn.isOnline(getActivity())) {
            EventRequest();
        } else {
            Toast.makeText(getActivity(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

        }
        binding.calendar.setListener(this);


        return binding.getRoot();
    }


    private void EventRequest() {
        events.clear();
        //   bindin.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        ListNews asgn = new ListNews();
        asgn.user_id = userid;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.offset = "0";
        asgn.limit = "100";
        asgn.team_id = teamid;


        String tset = gson.toJson(asgn);
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
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                assert date != null;
                                long millis = date.getTime();
                                //   event.setmDate(millis);


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
                                    events.add(event);

                                }
                                // events.add(new Event(title, millis, endinghour, endingmin, eventcolor, detail, id, startinghour, startingmin, location, isEvent, userstatus, insertby, Attending, NotAnswer, NotAtending, Maybe, eventdate));


                            }

                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {


                        if (status.equals("true")) {

                            binding.calendar.addEvents(events);
                            // dialog.setVisibility(View.GONE);
                            binding.llAdd1.setVisibility(View.VISIBLE);


                        } else {
                            cmn.showAlert(msg, getActivity());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
//                    dialog.setVisibility(View.GONE);
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

        calenderAdapter.notifyDataSetChanged();


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


}
