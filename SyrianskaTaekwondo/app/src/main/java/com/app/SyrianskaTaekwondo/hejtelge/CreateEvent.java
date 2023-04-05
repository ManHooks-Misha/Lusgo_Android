package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCreateEventBinding;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivitySearchActivityforMessageBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.EventsPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.MasterActivity;
import vk.help.views.TextDrawable;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CreateEvent extends MasterActivity {
    String[] arr_event = {"Möte", "Träning", "Match"};
    private ActivityCreateEventBinding binding;
    private ArrayList<GroupList> arr = new ArrayList<>();
    private ArrayList<UserList> arr_user = new ArrayList<>();
    private ArrayList<Teamlist> arr_team = new ArrayList<>();
    private CommonMethods cmn = new CommonMethods();
    private String role, userid, teamid;
    String status, msg, txt_fan, startdatestring, startdatestring2, startdate1, enddate1, enddatestring, enddatestring2, txt_title = "";
    private User_event_Adapter mAdapter;
    private CustomAdapter mCustomAdapter;
    AlertDialog alertDialog;
    public static ArrayList<UserList> arr_userlist1 = new ArrayList<>();
    public ArrayList<UserList> arr_userlist = new ArrayList<>();

    private Group_Event_Adapter mAdapter_group;
    public ArrayList<String> arr_Ulist = new ArrayList<>();
    public ArrayList<HashMap<String, String>> arr_Ulist_key = new ArrayList<>();
    public ArrayList<String> arr_Glist = new ArrayList<>();
    public boolean isCheckFromParent = true;
    public boolean isCheckFromParentUser = true;
    private int hour;
    String tomorrowDate;
    private ArrayList<GroupList> arr_grouplist = new ArrayList<>();
    int hour_s_end, minute_s_end;
    String min1_selected = "00", hour_end = "", finalEndDate = "", currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //    getSupportActionBar().setTitle("Nytt event");
        listeners();
        arr_userlist1.clear();
        arr_Ulist.clear();
        arr_Ulist_key.clear();

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        dt = c.getTime();
        tomorrowDate = new SimpleDateFormat("dd MMM, yyyy ", Locale.getDefault()).format(dt); //16 Jun, 2022 23:25
      //  Log.d("asjlkdfj", tomorrowDate);

        Locale locale = new Locale("sv", "SE"); // ( language code, country code );

        //  Locale locale = new Locale("SE");
        Locale.setDefault(locale);
        CoachList.arr_user_participate.clear();

        Calendar calendar = Calendar.getInstance();
        int hour_s = calendar.get(Calendar.HOUR_OF_DAY);
        int minute_s = calendar.get(Calendar.MINUTE);
        String hour1 = String.valueOf(hour_s);


        Calendar calendar_end = Calendar.getInstance();
        calendar_end.add(Calendar.HOUR, 1);

        String mins1 = String.valueOf(minute_s);
        if (mins1.length() == 1) {
            mins1 = "0" + mins1;
        }
        if (hour1.length() == 1) {
            hour1 = "0" + hour1;
        }


        hour_s_end = calendar_end.get(Calendar.HOUR_OF_DAY);
        minute_s_end = calendar_end.get(Calendar.MINUTE);
        String mins1_end = String.valueOf(minute_s_end);
        String hour1_end = String.valueOf(hour_s_end);
        String hour1_end11 = String.valueOf(hour_s_end + 1);

        if (mins1_end.length() == 1) {
            mins1_end = "0" + mins1_end;
        }
        if (hour1_end.length() == 1) {
            hour1_end = "0" + hour1_end;
        }

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, arr_event);
        binding.editEvent.setAdapter(adapter);

        binding.editEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txt_title = arr_event[i];

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mins1.equals("00")) {
            binding.startTime.setText(String.format("%s:%s", hour1, mins1));
            binding.endTime.setText(String.format("%s:%s", hour1_end, mins1_end));
        } else {
            binding.startTime.setText(String.format("%s:%s", hour1_end, "00"));
            binding.endTime.setText(String.format("%s:%s", hour1_end11, "00"));
        }
//        binding.startTime.setText(String.format("%s:%s", hour1, mins1));
//        binding.endTime.setText(String.format("%s:%s", hour1_end, mins1_end));
        updateLabelStart(binding.startdate, binding.enddate, 0, calendar);
        updateLabelEnd(binding.enddate, 0, calendar);
        role = cmn.getPrefsData(context, "usertype", "");
        userid = cmn.getPrefsData(context, "id", "");
        //   teamid = cmn.getPrefsData(context, "team_id", "");
        if (role.equals("2") || role.equals("5")) {
            binding.search.setVisibility(View.GONE);
            binding.llSearch.setVisibility(View.GONE);
            binding.llTeams.setVisibility(View.GONE);
        } else {
            binding.search.setVisibility(View.VISIBLE);
            binding.llSearch.setVisibility(View.VISIBLE);
            getTeamAPI();

            binding.team.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    teamid = arr_team.get(position).getId();
                    cmn.setPrefsData(CreateEvent.this,"team_id", teamid);

                    arr_userlist.clear();
                    binding.selectAllUser.setChecked(false);
                    mAdapter.notifyDataSetChanged();
                  //  Log.d("echeckfdid",teamid);
                    getUserAPI();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        binding.infoCreateEventIMG.setOnClickListener(v -> {
            cmn.customDialogMsg(CreateEvent.this, getResources().getString(R.string.event_text));
        });


        binding.search.setOnClickListener(v -> {
            startActivity(new Intent(this, SeachUserForEvent.class));

            //  binding.search.setIconified(false);
        });

        binding.llCoach.setOnClickListener(view -> {
            binding.llUser.setVisibility(View.VISIBLE);
            if (binding.gMinus.getVisibility() == View.VISIBLE) {
                binding.gMinus.setVisibility(View.GONE);
                binding.gPlus.setVisibility(View.VISIBLE);
                binding.llUser.setVisibility(View.GONE);
            } else {
                binding.gPlus.setVisibility(View.GONE);
                //   binding.llmessagegroup.setVisibility(View.GONE);
            }
            if (binding.llUser.getVisibility() == View.VISIBLE) {
                binding.gMinus.setVisibility(View.VISIBLE);
                binding.gPlus.setVisibility(View.GONE);

            } else {
                binding.gPlus.setVisibility(View.VISIBLE);
                binding.gMinus.setVisibility(View.GONE);

            }
            binding.llGroup.setVisibility(View.GONE);
            binding.groupMinus.setVisibility(View.GONE);
            binding.groupPlus.setVisibility(View.VISIBLE);
        });
        binding.llGroupG.setOnClickListener(view -> {
            binding.llGroup.setVisibility(View.VISIBLE);
            if (binding.groupMinus.getVisibility() == View.VISIBLE) {
                binding.groupMinus.setVisibility(View.GONE);
                binding.groupPlus.setVisibility(View.VISIBLE);
                binding.llGroup.setVisibility(View.GONE);
            } else {
                binding.groupPlus.setVisibility(View.GONE);
                //   binding.llmessagegroup.setVisibility(View.GONE);
            }
            if (binding.llGroup.getVisibility() == View.VISIBLE) {
                binding.groupMinus.setVisibility(View.VISIBLE);
                binding.groupPlus.setVisibility(View.GONE);

            } else {
                binding.groupPlus.setVisibility(View.VISIBLE);

                binding.groupMinus.setVisibility(View.GONE);

            }
            binding.llUser.setVisibility(View.GONE);
            binding.gMinus.setVisibility(View.GONE);
            binding.gPlus.setVisibility(View.VISIBLE);
        });
        if (role.equals(ConsURL.sub_admin) || role.equals(ConsURL.admin)) {
            binding.llGroup.setVisibility(View.GONE);
            binding.llUser.setVisibility(View.GONE);
            binding.fans.setVisibility(View.VISIBLE);
            binding.llCoach.setVisibility(View.VISIBLE);
            binding.vId.setVisibility(View.GONE);
        } else if (role.equals("4")) {
            binding.llGroup.setVisibility(View.GONE);
            binding.llUser.setVisibility(View.GONE);
            binding.fans.setVisibility(View.GONE);
            binding.vId.setVisibility(View.GONE);
            binding.llCoach.setVisibility(View.GONE);

        } else {
            binding.llCoach.setVisibility(View.VISIBLE);
            binding.users.setText("Användare");
            binding.vId.setVisibility(View.GONE);
            //  binding.llGroup.setVisibility(View.VISIBLE);
            //  binding.llUser.setVisibility(View.VISIBLE);
            binding.fans.setVisibility(View.GONE);

        }
        if (role.equals(ConsURL.members) || role.equals(ConsURL.coach) || role.equals(ConsURL.sub_coach)) {
            if (cmn.isOnline(context)) {
                arr_user.clear();
                getGroupAPI();

                // getUserAPI();
                binding.selectAllUser.setOnCheckedChangeListener((compoundButton, b) -> {

                    getHandler().postDelayed(() -> {
                        if (isCheckFromParentUser) {
                            mAdapter.updateAllData(b);
                        }
                    }, 100);

                });

                binding.selectAllUser.setOnClickListener(view -> isCheckFromParentUser = true);

                mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
                binding.userList.setAdapter(mAdapter);
                mAdapter.setOnLoadMoreListener(() -> {

                    arr_user.add(null);
                    mAdapter.notifyItemInserted(arr_user.size() - 1);
                    binding.userList.postDelayed(() -> {
                        int end = arr_user.size();
                        getRecursionAPI(String.valueOf(end));
                    }, 2000);
                });

            } else {
                showToast(getResources().getString(R.string.internet));
            }
        } else {
            arr_user.clear();
            arr_Ulist_key.clear();
            getGroupAPI();
            getUserAPICoach();
            binding.selectAllUser.setOnCheckedChangeListener((compoundButton, b) -> {
                getHandler().postDelayed(() -> {
                    if (isCheckFromParentUser) {
                        mAdapter.updateAllData(b);
                    }
                }, 100);
            });

            binding.selectAllUser.setOnClickListener(view -> isCheckFromParentUser = true);
            mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
            binding.userList.setAdapter(mAdapter);
            mAdapter.setOnLoadMoreListener(() -> {
                arr_user.add(null);
                mAdapter.notifyItemInserted(arr_user.size() - 1);
                binding.userList.postDelayed(() -> {
                    int end = arr_user.size();
                    getRecursionAPICoach(String.valueOf(end));
                }, 2000);
            });

        }

        binding.startdate.setOnClickListener(v -> {
            Calendar instance = Calendar.getInstance();
            Locale loc = new Locale("sv", "SE");
            Locale.setDefault(loc);
            Configuration configuration = context.getResources().getConfiguration();
            configuration.setLocale(loc);
            configuration.setLayoutDirection(loc);
            createConfigurationContext(configuration);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.MONTH, monthOfYear);
                temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                long now = System.currentTimeMillis() - 1000;
                long result = TimeUnit.DAYS.convert((temp.getTime().getTime() - now), MILLISECONDS);

                updateLabelStart(binding.startdate, binding.enddate, result, temp);

            }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);

            datePickerDialog.show();
        });
        binding.enddate.setOnClickListener(v -> {
            if (startdate1 != null) {
                Calendar instance = Calendar.getInstance();
                Locale loc = new Locale("sv", "SE");
                Locale.setDefault(loc);
                Configuration configuration = context.getResources().getConfiguration();
                configuration.setLocale(loc);
                configuration.setLayoutDirection(loc);
                createConfigurationContext(configuration);
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar temp = Calendar.getInstance();
                    temp.set(Calendar.YEAR, year);
                    temp.set(Calendar.MONTH, monthOfYear);
                    temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Locale.setDefault(locale);
                    long now = System.currentTimeMillis() - 1000;
                    long result = TimeUnit.DAYS.convert((temp.getTime().getTime() - now), MILLISECONDS);
                    Log.d("Get Result", "The result id " + result);
                    updateLabelEnd(binding.enddate, result, temp);
                }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
                datePickerDialog.show();
            } else {
                cmn.showAlert("Välj startdatum", context);
            }
        });

        binding.startTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hours = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            if (minute != 0) {
                hour_s_end = hour_s_end;
                minute = 0;
            }
            mTimePicker = new TimePickerDialog(CreateEvent.this, (timePicker, selectedHour, selectedMinute) -> {
                hour_end = String.valueOf(selectedHour + 1);
                String hours1 = String.valueOf(selectedHour);
                hour_s_end = selectedHour;
                min1_selected = String.valueOf(selectedMinute);
                if (min1_selected.length() == 1) {
                    min1_selected = "0" + min1_selected;
                }
                if (hours1.length() == 1) {
                    hours1 = "0" + hours1;
                }
                if (hour_end.length() == 1) {
                    hour_end = "0" + hour_end;
                }
                binding.startTime.setText(String.format("%s:%s", hours1, min1_selected));
                // binding.endTime.setText(String.format("%s:%s", hour_end, min1_selected));
                if (hour_end.equals("24")) {
                    binding.endTime.setText(String.format("%s:%s", "00", min1_selected));
                    binding.enddate.setText(String.format("%s", "I morgon"));
                } else {
                    //  binding.enddate.setText(String.format("%s", "Idag"));
                    binding.endTime.setText(String.format("%s:%s", hour_end, min1_selected));
                }


            }, hour_s_end, minute, true);
            mTimePicker.setTitle("Välj tid");

            mTimePicker.setButton(DialogInterface.BUTTON_POSITIVE, "OK", mTimePicker);

            mTimePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", mTimePicker);
            mTimePicker.show();

        });
        binding.endTime.setOnClickListener(v -> {
            String star_time = Objects.requireNonNull(binding.startTime.getText()).toString();
            int hour_end;
            Calendar mcurrentTime = Calendar.getInstance();
            if (hour_s_end == 0) {
                hour_end = mcurrentTime.get(Calendar.HOUR_OF_DAY) + 1;

            } else {
                hour_end = hour_s_end + 1;

            }
            int minute = Integer.parseInt(min1_selected);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(CreateEvent.this, (timePicker, selectedHour, selectedMinute) -> {
                String min1 = String.valueOf(selectedMinute);
                String hours1 = String.valueOf(selectedHour);
                if (min1.length() == 1) {
                    min1 = "0" + min1;
                }
                if (hours1.length() == 1) {
                    hours1 = "0" + hours1;
                }
                String end = String.format("%s:%s", hours1, min1);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                try {
                    Date inTime = sdf.parse(star_time);
                    Date outTime = sdf.parse(end);
                    // binding.endTime.setText(String.format("%s:%s", hours1, min1));
                    if (isTimeAfter(inTime, outTime)) {
                        binding.endTime.setText(String.format("%s:%s", hours1, min1));

                    } else {
                        cmn.showAlert("Sluttiden bör vara efter starttiden", context);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }, hour_end, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Välj tid");
            mTimePicker.setButton(DialogInterface.BUTTON_POSITIVE, "OK", mTimePicker);

            mTimePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", mTimePicker);
            mTimePicker.show();
        });
    }

    boolean isTimeAfter(Date startTime, Date endTime) {
        if (endTime.before(startTime)) { //Same way you can check with after() method also.
            return false;
        } else {
            return true;
        }
    }

    public void listeners() {
        binding.selectGroup.setOnClickListener(view -> isCheckFromParent = true);

        binding.selectGroup.setOnCheckedChangeListener((compoundButton, b) -> {
            getHandler().postDelayed(() -> {
                if (isCheckFromParent) {
                    mAdapter_group.updateAllData(b);
                }
            }, 100);
        });


        mAdapter_group = new Group_Event_Adapter(arr, binding.groupList, this, binding);
        binding.groupList.setAdapter(mAdapter_group);
        mAdapter_group.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter_group.notifyItemInserted(arr.size() - 1);
            binding.groupList.postDelayed(() -> {
                int end = arr.size();
                getRecursionAPIGroup(String.valueOf(end));
            }, 2000);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mAdapter.getList().size() > 0)
//
//            binding.coach.setText(mAdapter.getList().size() + " Tränare");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
          /*  if(CoachList.arr_user_participate.size()>0) {
                for (UserList ulist : CoachList.arr_user_participate) {
                    arr_Ulist.add(ulist.getId());
                }
            }*/
            String txt_desc = Html.toHtml(binding.descEvent.getText());
            txt_desc = txt_desc.replaceFirst("<p dir=\"ltr\">", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            txt_desc = txt_desc.replaceAll("</p>", "");
            txt_desc = txt_desc.replaceFirst("<u>", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            txt_desc = txt_desc.replaceAll("</u>", "");
            //   String txt_desc = Objects.requireNonNull(binding.descEvent.getText()).toString();
            String place = Objects.requireNonNull(Html.toHtml(binding.location.getText()));
            place = place.replaceFirst("<p dir=\"ltr\">", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            place = place.replaceAll("</p>", "");
            String txt_start = Objects.requireNonNull(binding.startdate.getText()).toString();
            String txt_end = Objects.requireNonNull(binding.enddate.getText()).toString();
            String txt_starttime = Objects.requireNonNull(binding.startTime.getText()).toString();
            String txt_endtime = Objects.requireNonNull(binding.endTime.getText()).toString();
            if (mAdapter_group.getList().size() > 0) {
                for (GroupList group : mAdapter_group.getList()) {
                    if (group != null)
                        arr_Glist.add(group.getGroup_id());
                }
            }
            if (mAdapter.getList().size() > 0) {

                for (UserList user : mAdapter.getList()) {
                    if (user != null) {
                        HashMap map = new HashMap();

                        if (user.getTeam_id().length() > 0) {

                            map.put("team_id", teamid);
                            map.put("User_id", user.getId());
                            arr_Ulist_key.add(map);
                        } else {
                            map.put("team_id", teamid);
                            map.put("User_id", user.getId());
                            arr_Ulist_key.add(map);

                        }
                        arr_Ulist.add(user.getId());
                    }
                }

            }
            for (UserList user : arr_userlist1) {
                if (user != null) {
                    HashMap map = new HashMap();

                    if (user.getTeam_id().length() > 0) {

                        map.put("team_id", user.getTeam_id());
                        map.put("User_id", user.getId());
                        arr_Ulist_key.add(map);
                    } else {
                        map.put("team_id", teamid);
                        map.put("User_id", user.getId());
                        arr_Ulist_key.add(map);

                    }
                    // arr_Ulist.add(user.getId());
                }
            }
            txt_fan = binding.fans.isChecked() ? "1" : "0";
            if (binding.otherEvent.length() > 0) {


                txt_title = Html.toHtml(new SpannableString(binding.otherEvent.getText()));
                txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                txt_title = txt_title.replaceAll("</p>", "");

                //       txt_title = Objects.requireNonNull(Html.toHtml(binding.otherEvent.getText()).replace("\n",""));
            }
            if (txt_title.length() > 0) {
                if (txt_start.length() > 0) {
                    if (txt_starttime.length() > 0) {
                        if (txt_end.length() > 0) {
                            if (txt_endtime.length() > 0) {
                                if (role.equals("3") || role.equals("2")) {
                                    String value_start = startdatestring2 + " " + txt_starttime;
                                    String value_end = enddatestring2 + " " + txt_endtime;
                                   /* if (txt_fan.equals("1")) {
                                        String date = txt_start + " " + txt_starttime;
                                        String dateend = txt_end + " " + txt_endtime;
                                        if (cmn.isOnline(context)) {
                                            if (place.length() == 0 && txt_desc.length() == 0) {
                                                alert("Vill du skapa eventet utan Plats\n" +
                                                        "Beskrivning?\n", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            } else if (place.length() == 0 && txt_desc.length() > 0) {
                                                alert("Vill du skapa eventet utan Plats?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            } else if (place.length() > 0 && txt_desc.length() == 0) {
                                                alert("Vill du skapa eventet utan Beskrivning?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);


                                            } else {
                                                EventRequest(txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            }
                                        }
                                    } else {*/
                                    if (txt_fan.equals("1") || arr_Glist.size() > 0 || arr_Ulist.size() > 0 || arr_Ulist_key.size() > 0) {

                                        String date = txt_start + " " + txt_starttime;
                                        String dateend = txt_end + " " + txt_endtime;
                                        if (cmn.isOnline(context)) {
                                            if (place.length() == 0 && txt_desc.length() == 0) {
                                                alert("Vill du skapa eventet utan Plats\n" +
                                                        "Beskrivning?\n", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            } else if (place.length() == 0 && txt_desc.length() > 0) {
                                                alert("Vill du skapa eventet utan Plats?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            } else if (place.length() > 0 && txt_desc.length() == 0) {
                                                alert("Vill du skapa eventet utan Beskrivning?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);


                                            } else {
                                                EventRequest(txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                            }
                                        } else {
                                            showToast(getString(R.string.internet));
                                        }
                                    } else {
                                        showCustomDialog1("Välj grupp eller användare");
                                        //cmn.showAlert("Välj grupp eller användare", context);
                                    }
                               // }
                                }
                                else {
                                    String value_start = startdatestring2 + " " + txt_starttime;
                                    String value_end = enddatestring2 + " " + txt_endtime;
                                    String date = txt_start + " " + txt_starttime;
                                    String dateend = txt_end + " " + txt_endtime;

                                    if (place.length() == 0 && txt_desc.length() == 0) {
                                        alert("Vill du skapa eventet utan Plats\n" +
                                                "Beskrivning?\n", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                    } else if (place.length() == 0 && txt_desc.length() > 0) {
                                        alert("Vill du skapa eventet utan Plats?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                    } else if (place.length() > 0 && txt_desc.length() == 0) {
                                        alert("Vill du skapa eventet utan Beskrivning?", txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);


                                    } else {
                                        EventRequest(txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);

                                    }

                                }
                            } else {

                                cmn.showAlert("Vänligen ange sluttid", context);
                            }
                        } else {
                            cmn.showAlert("Vänligen ange slutdatum", context);
                        }
                    } else {
                        cmn.showAlert("Vänligen ange starttid", context);
                    }
                } else {
                    cmn.showAlert("Vänligen ange startdatum", context);
                }
            } else {
                cmn.showAlert("Välj titel", context);

            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
    }

    private void updateLabelStart(AppCompatEditText editText, AppCompatEditText editTextEnd, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        Locale locale = new Locale("sv", "SE");
        Locale.setDefault(locale);
        //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);


        // int result = now.compareTo(dd);
        if (result == 0) {
            editText.setText(String.format("%s", "Idag"));
            editTextEnd.setText(String.format("%s", "Idag"));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddatestring = sdf1.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            startdate1 = dfDate.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

        } else if (result == 1) {
            editText.setText(String.format("%s", "I morgon"));
            editTextEnd.setText(String.format("%s", "I morgon"));

            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            startdate1 = dfDate.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

        } else if (result > 1 && result <= 6) {
            editText.setText(sdf.format(myCalendar.getTime()));
            editTextEnd.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddatestring = sdf1.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdate1 = dfDate.format(myCalendar.getTime());

        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            editText.setText(sdf1.format(myCalendar.getTime()));
            editTextEnd.setText(sdf1.format(myCalendar.getTime()));
            startdatestring = sdf1.format(myCalendar.getTime());
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdate1 = dfDate.format(myCalendar.getTime());

        }
    }

    private void updateLabelEnd(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        Locale locale = new Locale("sv", "SE");
        Locale.setDefault(locale);

        // int result = now.compareTo(dd);
        if (result == 0) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);

            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {
                editText.setText(String.format("%s", "Idag"));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());


            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden", this);
            }
        } else if (result == 1) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {

                editText.setText(String.format("%s", "I morgon"));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden", this);
            }

        } else if (result > 1 && result <= 6) {

            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {

                editText.setText(sdf.format(myCalendar.getTime()));
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                //old text
                //Slutdatum bör vara större än startdatumet
                cmn.showAlert("Sluttiden bör vara efter starttiden", this);
            }


        } else {
            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {
                editText.setText(sdf.format(myCalendar.getTime()));
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
                editText.setText(sdf1.format(myCalendar.getTime()));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden", this);
            }
        }
    }

    private void EventRequest(String desc, String title, String stdate, String enddate, String places, String is_publics, ArrayList<String> arr_group, ArrayList<String> arr_user, String start, String end, ArrayList<HashMap<String, String>> list_key) {
//        ProgressDialog mprogdialog = ProgressDialog.show(CreateEvent.this, "", "Vänta", true);
//        mprogdialog.setCancelable(false);
        Gson gson = new Gson();
        EventsPojo asgn = new EventsPojo();
        asgn.description = desc;
        asgn.access_key = "f76646abb2bb5408ecc6d8e36b64c9d8";
        asgn.title = title;
        asgn.start_date = start;
        String[] parts = end.split(" ");
        String part1 = parts[0];
        String part2 = parts[1];
        String part3 = parts[2];
        String part4 = parts[3];
        int edate = (Integer.parseInt(part1) + 1);
        finalEndDate = edate + " " + part2 + " " + part3 + " " + part4;
        if (hour_end.equals("24") && tomorrowDate.contains(finalEndDate)) {
            asgn.end_date = finalEndDate;
        } else {
            asgn.end_date = end;
        }

        asgn.place = places;
        asgn.is_public = is_publics;
        asgn.user_id = userid;
        asgn.users = arr_user;
        asgn.groups = arr_group;
        asgn.team_id = teamid;
        asgn.Team_users = list_key;
        if (role.equals(ConsURL.sub_admin) || role.equals(ConsURL.members) || role.equals(ConsURL.admin)) {
            asgn.coach_id = "";

        } else {
            asgn.coach_id = userid;
        }
        String tset = gson.toJson(asgn);
        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog("Eventet har skapats");

                } else {
                    cmn.showAlert(result.getMessage(), CreateEvent.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, tset).execute(ConsURL.BASE_URL_TEST + "add_event");


    }


    private void getGroupAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("team_id", teamid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }
        // {"access_key":"f76646abb2bb5408ecc6d8e36b64c9d8","limit":"1000","offset":0,"user_id":"1","team_id":""}
        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                    }
                    if (obj.length() > 0) {
                        mAdapter_group.setLoaded();
                    } else {
                        binding.llGroup.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (arr.size() > 0) {
                    mAdapter_group.notifyDataSetChanged();
                }
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupList");
    }


    private void getRecursionAPIGroup(String offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "1000");
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                int position = arr.size() - 1;
                arr.remove(position);
                mAdapter_group.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter_group.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //mAdapter.setLoaded();
                mAdapter_group.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupList");
    }


    private void getUserAPI() {
        String requestData;
        arr_user.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", 0);
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
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());
                    if (objarr.length() > 0) {
                        JSONArray obj = objarr.getJSONArray("userList");
                        for (int i = 0; i < obj.length(); i++) {
                            arr_user.add((UserList) (getObject(obj.getString(i), UserList.class)));
                        }
                        if (obj.length() > 0) {
                            mAdapter.setLoaded();
                            binding.llUser.setVisibility(View.VISIBLE);
                        } else {
                            binding.llUser.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                binding.selectAllUser.setChecked(false);
                mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
                binding.userList.setAdapter(mAdapter);

            }

            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }


    private void getRecursionAPI(String offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", Integer.parseInt(offset));
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
                int position = arr_user.size() - 1;
                arr_user.remove(position);
                mAdapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());
                    JSONArray obj = objarr.getJSONArray("userList");
                    for (int i = 0; i < obj.length(); i++) {
                        arr_user.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
                binding.userList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "userList");
    }

    public boolean checkDates(String date1, String date2) {

        boolean b = false;
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

        try {
            //If start date is after the end date
            if (Objects.requireNonNull(dfDate.parse(date1)).before(dfDate.parse(date2))) {
                b = true;//If start date is before end date
            } else
                b = Objects.equals(dfDate.parse(date1), dfDate.parse(date2));//If two dates are equal
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return b;
    }

    private void getRecursionAPICoach(String offset) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 100);
            object.put("offset", Integer.parseInt(offset));
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                int position = arr_user.size() - 1;
                arr_user.remove(position);
                mAdapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {

                        arr_user.add((UserList) (getObject(obj.getString(i), UserList.class)));
                    }

                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//              for(UserList user:arr_user) {
//                  if (userid.equals(user.getId())) {
//                    arr_user.remove(user);
//                  }
//              }
                //  mAdapter.setLoaded();
                mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
                binding.userList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }

    private void getUserAPICoach() {
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
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        String id = object.getString("id");
                        if (!userid.equals(id)) {
                            arr_user.add((UserList) (getObject(obj.getString(i), UserList.class)));

                        }

                    }
                    if (obj.length() > 0) {
                        mAdapter.setLoaded();
                    }

                    if (arr_user.size() == 0) {
                        // user_record.setVisibility(View.VISIBLE);
                        //  usergroup.setVisibility(View.GONE);
                        binding.userList.setVisibility(View.GONE);

                    } else {
                        //user_record.setVisibility(View.GONE);
                        // usergroup.setVisibility(View.VISIBLE);
                        binding.userList.setVisibility(View.VISIBLE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
                binding.userList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                //  mAdapter.setLoaded();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }

    private void showCustomDialog(String text) {
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
        create.setText(text);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();
            startActivity(new Intent(CreateEvent.this, HomePage.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialog1(String text) {
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
        create.setText(text);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();

        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void alert(String txt, String txt_desc, String txt_title, String date, String dateend, String place, String txt_fan, ArrayList<String> arr_Glist, ArrayList<String> arr_Ulist, String value_start, String value_end, ArrayList<HashMap<String, String>> arr_Ulist_key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(txt);
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(context)) {
                //if (txt_desc.length() == 0 && place.length() == 0) {
                EventRequest(txt_desc, txt_title, date, dateend, place, txt_fan, arr_Glist, arr_Ulist, value_start, value_end, arr_Ulist_key);
                //  }else{

                // }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Nej", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void getTeamAPI() {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", 0);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    // JSONObject objarr = new JSONObject(result.getData());
                    //  if (objarr.length() > 0) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arr_team.add((Teamlist) (getObject(obj.getString(i), Teamlist.class)));
                    }
                    mCustomAdapter = new CustomAdapter(CreateEvent.this, arr_team);
                    binding.team.setAdapter(mCustomAdapter);

                    //   }


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
            }
            names.setText(teams.get(i).getName());
            return view;
        }
    }


    public class User_event_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;
        private ArrayList<UserList> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        public CreateEvent activity;
        public SeachUserForEvent activitysearch;
        public String flag = "";
        private ActivityCreateEventBinding binding;
        private ActivitySearchActivityforMessageBinding bindingevent;

        public User_event_Adapter(ArrayList<UserList> students, RecyclerView recyclerView, CreateEvent activity, SeachUserForEvent activitysearch, ActivityCreateEventBinding binding, ActivitySearchActivityforMessageBinding bindingevent, String flag) {
            horizontalList = students;
            this.activity = activity;
            this.activitysearch = activitysearch;
            this.binding = binding;
            this.flag = flag;
            this.bindingevent = bindingevent;
//            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    @Override
//                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//
//                        totalItemCount = linearLayoutManager.getItemCount();
//                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                            // End has been reached
//                            // Do something
//                            if (onLoadMoreListener != null) {
//                                onLoadMoreListener.onLoadMore();
//                            }
//                            loading = true;
//                        }
//                    }
//                });
//            }
        }

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new User_event_Adapter.StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participate_user, parent, false));
            } else {
                return new User_event_Adapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof User_event_Adapter.StudentViewHolder) {
                ((User_event_Adapter.StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((User_event_Adapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView iv_selected;
            private AvatarView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);

                img = view.findViewById(R.id.iv_profile);
                iv_selected = view.findViewById(R.id.iv_selected);

            }

            void setData(UserList data) {
                txtview.setText(data.getFirstname());
                iv_selected.setVisibility(arr_userlist.contains(data) ? View.VISIBLE : View.GONE);

                itemView.setOnClickListener(view -> {
                    if (arr_userlist.contains(data)) {
                        arr_userlist.remove(data);
                    } else {
                        arr_userlist.add(data);
                    }
                    notifyItemChanged(getAdapterPosition());
                    //  notifyDataSetChanged();
                    updateUI();
                });

                if (data.getFirstname().trim().length() > 0) {
                    if (data.getFirstname().trim().length() > 0) {
                        String name = String.valueOf(data.getFirstname().trim().charAt(0));
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);
                    }
            /*String name = String.valueOf(data.getFirstname().trim().charAt(0));
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
            Glide.with(itemView)
                    .load(data.getProfile_image())
                    .fitCenter()
                    .placeholder(drawable)
                    .into(img);*/
                } else {
                    txtview.setText(data.getUsername().trim());
                    if (data.getUsername().trim().length() > 0) {
                        String name = String.valueOf(data.getUsername().trim().charAt(0));
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);
                    }
                }
            }

        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        public ArrayList<UserList> getList() {
            return arr_userlist;
        }

        public void updateAllData(boolean toAdd) {
            arr_userlist.clear();
            if (toAdd) {
                arr_userlist.addAll(horizontalList);
            }
            mAdapter = new User_event_Adapter(arr_user, binding.userList, CreateEvent.this, null, binding, null, "create");
            binding.userList.setAdapter(mAdapter);
            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (flag.equals("create")) {
                if (arr_userlist.containsAll(horizontalList)) {
                    activity.isCheckFromParentUser = true;
                    binding.selectAllUser.setChecked(true);
                } else {
                    activity.isCheckFromParentUser = false;
                    binding.selectAllUser.setChecked(false);
                }
            } else {
                if (arr_userlist.containsAll(horizontalList)) {
                    activitysearch.isCheckFromParentUser = true;
                    bindingevent.selectAllUser.setChecked(true);
                } else {
                    activitysearch.isCheckFromParentUser = false;
                    bindingevent.selectAllUser.setChecked(false);
                }
            }
        }
    }


    public class Group_Event_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;

        private ArrayList<GroupList> horizontalList;

        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        private CreateEvent activity;
        private ActivityCreateEventBinding binding;

        public Group_Event_Adapter(ArrayList<GroupList> students, RecyclerView recyclerView, CreateEvent activity, ActivityCreateEventBinding binding) {
            horizontalList = students;
            this.activity = activity;
            this.binding = binding;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new Group_Event_Adapter.StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.participent_adapter, parent, false));
            } else {
                return new Group_Event_Adapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Group_Event_Adapter.StudentViewHolder) {
                ((Group_Event_Adapter.StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((Group_Event_Adapter.ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView selecttext;
            private AvatarView img;
            private TextView txtview;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.tv_name);
                img = view.findViewById(R.id.iv_profile);
                selecttext = view.findViewById(R.id.iv_selected);
            }

            void setData(GroupList data) {
                txtview.setText(data.getName());
                selecttext.setVisibility(arr_grouplist.contains(data) ? View.VISIBLE : View.GONE);
                String name = String.valueOf(data.getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView).load(data.getImage()).centerCrop().placeholder(drawable).into(img);

                itemView.setOnClickListener(view -> {
                    if (arr_grouplist.contains(data)) {
                        arr_grouplist.remove(data);
                    } else {
                        arr_grouplist.add(data);
                    }
                    selecttext.setVisibility(View.VISIBLE);

                    notifyItemChanged(getAdapterPosition());
                    updateUI();
                });
            }
        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        public ArrayList<GroupList> getList() {
            return arr_grouplist;
        }

        public void updateAllData(boolean toAdd) {
            arr_grouplist.clear();
            if (toAdd) {
                arr_grouplist.addAll(horizontalList);
            }
            mAdapter_group = new Group_Event_Adapter(arr, binding.groupList, CreateEvent.this, binding);
            binding.groupList.setAdapter(mAdapter_group);
            notifyDataSetChanged();
            updateUI();
        }

        private void updateUI() {
            if (arr_grouplist.containsAll(horizontalList)) {
                activity.isCheckFromParent = true;
                binding.selectGroup.setChecked(true);
            } else {
                activity.isCheckFromParent = false;
                binding.selectGroup.setChecked(false);
            }
        }
    }

}

