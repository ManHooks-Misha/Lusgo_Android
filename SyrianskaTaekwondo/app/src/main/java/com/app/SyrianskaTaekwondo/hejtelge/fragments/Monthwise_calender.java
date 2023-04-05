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
import com.app.SyrianskaTaekwondo.hejtelge.adapters.MonthwiseCalendarAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.MonthwiseCalenderBinding;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import vk.help.Fragment;

public class Monthwise_calender extends Fragment implements CollapsibleCalendarView.Listener<Event> {
    MonthwiseCalenderBinding binding;
    private String role, userid, teamid, usertype;
    private CommonMethods cmn = new CommonMethods();
    private String msg, status, eventdate, title, detail, id, location, isEvent;
//    private ArrayList<HashMap<String, List<Event>>> events__ = new ArrayList<>();
    private ArrayList<HashMap<String, ArrayList<HashMap<String, List<Event>>>>> eventsmap__ = new ArrayList<>();
    private List<Details> events_detail = new ArrayList<>();
    private List<Date> events_date = new ArrayList<>();
    private MonthwiseCalendarAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = MonthwiseCalenderBinding.inflate(inflater, container, false);
        role = cmn.getPrefsData(getActivity(), "usertype", "");
        userid = cmn.getPrefsData(getContext(), "id", "");
        teamid = cmn.getPrefsData(getContext(), "team_id", "");
        usertype = cmn.getPrefsData(getActivity(), "usertype", "");

        adapter = new MonthwiseCalendarAdapter(
                eventsmap__, getActivity(), usertype, userid);
    binding.calendarEventList.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.calendarEventList.setAdapter(adapter);
//      LinearLayoutManager  linearLayoutManager = new LinearLayoutManager(getActivity()) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
//              binding.calendarEventList.setLayoutManager(linearLayoutManager);

        if (cmn.isOnline(getActivity())) {
            EventRequest();
        } else {
            Toast.makeText(getActivity(), Objects.requireNonNull(getActivity()).getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();

        }
        return binding.getRoot();
    }



    @Override
    public void onDateSelected(LocalDate date, List<Event> events) {

    }

    @Override
    public void onMonthChanged(LocalDate date) {

    }

    @Override
    public void onHeaderClick() {

    }


    private void EventRequest() {
        eventsmap__.clear();
       // events__.clear();
        //   bindin.setVisibility(View.VISIBLE);

        Gson gson = new Gson();
        ListNews asgn = new ListNews();
        asgn.user_id = userid;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.offset = "0";
        asgn.limit = "100";
        asgn.team_id = teamid;


        String tset = gson.toJson(asgn);
        String url = ConsURL.BASE_URL_TEST + "MonthlyeventList";

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


                        if (status.equals("true")) {
                            JSONArray object_details = obje.getJSONArray("data");
                            for (int l = 0; l < object_details.length(); l++) {
                                ArrayList<HashMap<String, List<Event>>> events__=new ArrayList<>()   ;
                                JSONObject obj_ = object_details.getJSONObject(l);
                                HashMap<String, ArrayList<HashMap<String, List<Event>>>> maplist = new HashMap();
                                String month = obj_.getString("months");

                                JSONArray object = obj_.getJSONArray("monthlyDateVM");
                                for (int m = 0; m < object.length(); m++) {
                                    List<Event> events = new ArrayList<>();

                                    JSONObject objdate_ = object.getJSONObject(m);
                                    String datestr = objdate_.getString("date");

                                    JSONArray objectArr = objdate_.getJSONArray("eventDetails");
                                    HashMap<String, List<Event>> map = new HashMap();
                                    for (int i = 0; i < objectArr.length(); i++) {
                                        JSONObject objvalue = objectArr.getJSONObject(i);
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
                                        for (int k = 0; k < arr.length(); k++) {
                                            JSONObject obj = arr.getJSONObject(k);
                                            Details details = new Details();
                                            details.setUser_id(obj.getString("user_id"));
                                            details.setName(obj.getString("name"));
                                            details.setAction(obj.getString("action"));
                                            details.setProfile_image(obj.getString("profile_image"));
                                            events_detail.add(details);
                                        }
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
                                            String team_name = objvalue.getString("team_name");
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
                                            event.setTeam_name(team_name);
                                            event.setAttending(attending);
                                            event.setDenied(denied);
                                            event.setNo_response(no_response);
                                            event.setMay_be(maybe);
                                            events.add(event);

                                        }
                                        // events.add(new Event(title, millis, endinghour, endingmin, eventcolor, detail, id, startinghour, startingmin, location, isEvent, userstatus, insertby, Attending, NotAnswer, NotAtending, Maybe, eventdate));


                                    }
                                    map.put(datestr, events);
                                    events__.add(map);

                                }
                                maplist.put(month, events__);
                                eventsmap__.add(maplist);
                            }


                        }
                    }
                    new Handler(Looper.getMainLooper()).post(() -> {


                        if (status.equals("true")) {

                            binding.llAdd.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();

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

    private static int getDaysDifference(Date fromDate, Date toDate) {
        if (fromDate == null || toDate == null)
            return 0;

        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }


//    public Event group(Event : List<Event>) : Map<String,List<Event>> {
//        val map = HashMap<String,List<Income>>()
//        list.foreach {
//            val month = getMonthOf(it.amountDate)
//            if(map.contains(mounth)){
//                map[month].add(it);
//            } else {
//                val arrayList = ArrayList<Income>()
//                arrayList.add(it)
//                map[month] = arrayList
//            }
//        }
//        return map
//    }

}
