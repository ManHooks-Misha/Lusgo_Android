package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.GridViewAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.EditCampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

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

import vk.help.MasterActivity;

public class EditCampaign extends MasterActivity {
    // private ArrayList<HashMap<String, String>> arr = new ArrayList<>();
    private GridViewScroll gridView;
    private AppCompatEditText startdate, starttime, enddate, endtime;
    private AppCompatImageView edit;
    private AlertDialog alertDialog;

    private ArrayList<CampaignPojo> arr_campaign1 = new ArrayList<>();
    private String startdatestring, startdatestring2, enddatestring, userid, enddatestring2;
    private AppCompatTextView add_sponsers, edit_campaign,txt;
    GridViewAdapter adapter;
    CommonMethods cmn;
    private String togglevalue = "";
    private ArrayList<String> arr_sponser = new ArrayList<>();
    private AppCompatTextView toggle;
    Calendar mycalendar, mycalendar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_campaign);
        loadID();
        cmn = new CommonMethods();
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        txt.setText("Redigera kampanj");
        userid = cmn.getPrefsData(EditCampaign.this, "id", "");
        mycalendar = Calendar.getInstance();
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);

        mycalendar2 = Calendar.getInstance();
        if (getIntent() != null && getIntent().hasExtra(DATA)) {
            arr_campaign1.addAll((ArrayList<CampaignPojo>) getObject(Objects.requireNonNull(getIntent().getByteArrayExtra(DATA))));
        }
        String startdate_txt = arr_campaign1.get(0).getStart_date();
        //String[] st_date = startdate_txt.split(" ");
       /* String start = st_date[0];
        String time = st_date[1];
        String[] time_value = time.split(":");
*/
        String enddate_txt = arr_campaign1.get(0).getEnd_date();
     /*   String[] end_date = enddate_txt.split(" ");
        String end = end_date[0];
        String end_time = end_date[1];
        String[] end_value = end_time.split(":");
*/
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = format.parse(startdate_txt);
            Date date_end = format.parse(enddate_txt);
            long dt_start = Objects.requireNonNull(date).getTime();
            long dt_end = Objects.requireNonNull(date_end).getTime();
            mycalendar.setTimeInMillis(dt_start);
            Locale loc11 = new Locale("sv","SE");
            Locale.setDefault(loc11);

            mycalendar2.setTimeInMillis(dt_end);
            String myFormat1 = "dd MMM yyyy"; //In which you need put here

            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(mycalendar.getTime());
            enddatestring2 = sdf2.format(mycalendar2.getTime());

            startdatestring = sdf1.format(mycalendar.getTime());
            enddatestring = sdf1.format(mycalendar2.getTime());
            startdate.setText(startdatestring);
            enddate.setText(enddatestring);

        } catch (ParseException e) {
            e.printStackTrace();
        }


      /*  String time_hour = time_value[0];
        String time_min = time_value[1];

        String end_time_hour = end_value[0];
        String end_time_min = end_value[1];*/
        AddCampaign.img = arr_campaign1.get(0).getSponsers();

//        starttime.setText(time_hour + ":" + time_min);
//        //startdate.setText(start);
//        endtime.setText(end_time_hour + ":" + end_time_min);
        adapter = new GridViewAdapter(EditCampaign.this, AddCampaign.img);
        gridView.setAdapter(adapter);
        add_sponsers.setOnClickListener(view -> startActivity(new Intent(EditCampaign.this, ChooseSponser.class)));


        toggle.setOnClickListener(view -> {

            alert();
        });

        startdate.setOnClickListener(v -> {
            Calendar instance = Calendar.getInstance();
            Locale loc11 = new Locale("sv","SE");
            Locale.setDefault(loc11);
            Configuration configuration = context.getResources().getConfiguration();
            configuration.setLocale(loc11);
            configuration.setLayoutDirection(loc11);
            createConfigurationContext(configuration);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.MONTH, monthOfYear);
                temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                long now = System.currentTimeMillis() - 1000;

                long result = TimeUnit.DAYS.convert((temp.getTime().getTime() - now), TimeUnit.MILLISECONDS);

                Log.d("Get Result", "The result id " + result);
                updateLabelStart(startdate, result, temp);


            }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
            datePickerDialog.show();
        });
        enddate.setOnClickListener(v -> {

            Calendar instance = Calendar.getInstance();
            Locale loc11 = new Locale("sv","SE");
            Locale.setDefault(loc11);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.MONTH, monthOfYear);
                temp.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                long now = System.currentTimeMillis() - 1000;

                long result = TimeUnit.DAYS.convert((temp.getTime().getTime() - now), TimeUnit.MILLISECONDS);

                Log.d("Get Result", "The result id " + result);
                updateLabelEnd(enddate, result, temp);


            }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ja", datePickerDialog);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
            datePickerDialog.show();
        });


        starttime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;

            mTimePicker = new TimePickerDialog(EditCampaign.this, (timePicker, selectedHour, selectedMinute) -> {
                String hours = String.valueOf(selectedHour);

                String min = String.valueOf(selectedMinute);
                if (min.length() == 1) {
                    min = "0" + min;
                }
                if (hours.length() == 1) {
                    hours = "0" + hours;
                }
                starttime.setText(hours + ":" + min);


            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });
        endtime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(EditCampaign.this, (timePicker, selectedHour, selectedMinute) -> {
                String min = String.valueOf(selectedMinute);
                String hours = String.valueOf(selectedHour);
                if (min.length() == 1) {
                    min = "0" + min;
                }
                if (hours.length() == 1) {
                    hours = "0" + hours;
                }
                endtime.setText(hours + ":" + min);


            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });

    }

    public void loadID() {
        gridView = findViewById(R.id.grid);
        starttime = findViewById(R.id.starttime);
        edit = findViewById(R.id.img_edit);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        endtime = findViewById(R.id.endtime);
        edit_campaign = findViewById(R.id.edit_campaign);
        add_sponsers = findViewById(R.id.add_sponser);
        toggle = findViewById(R.id.toggle);
        txt = findViewById(R.id.txt);


    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save:


          /*      if (!toggle.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Finns det någon organisation eller företag som vill ska synas extra för dina användare. Här kan du nämnligen hantera dina sponsorkampanjer. Välj en eller flera sponsorer och välj hur lång tid du kampanjen varar.");
                    builder.setPositiveButton("OK", (dialogInterface, i) -> {
                        arr_sponser.clear();
                        String starttime_text = Objects.requireNonNull(starttime.getText()).toString();
                        String endtime_text = Objects.requireNonNull(endtime.getText()).toString();
                        for (HashMap map : AddCampaign.img) {
                            String id = Objects.requireNonNull(map.get("id")).toString();
//                HashMap map1 = new HashMap();
//                map1.put("id", id);
                            arr_sponser.add(id);
                        }
                        String value_start = startdatestring + " " + starttime_text;
                        String value_end = enddatestring + " " + endtime_text;
                        if (arr_sponser.size() > 0) {
                            if (cmn.isOnline(EditCampaign.this)) {
                                getUserAPI(arr_sponser, value_start, value_end);
                            } else {
                                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Välj någon sponsor", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
                        alertDialog.dismiss();
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
*/
                //  } else {
                arr_sponser.clear();
                String starttime_text = Objects.requireNonNull(starttime.getText()).toString();
                String endtime_text = Objects.requireNonNull(endtime.getText()).toString();
                for (HashMap map : AddCampaign.img) {
                    String id = Objects.requireNonNull(map.get("id")).toString();
//                HashMap map1 = new HashMap();
//                map1.put("id", id);
                    arr_sponser.add(id);
                }
                String value_start = startdatestring2 + " " + starttime_text;
                String value_end = enddatestring2 + " " + endtime_text;
                if (arr_sponser.size() > 0) {
                    if (cmn.isOnline(EditCampaign.this)) {
                        alert(arr_sponser, value_start, value_end);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Välj någon sponsor", Toast.LENGTH_SHORT).show();
                }
                //}

        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.save); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
        mColorFullMenuBtn.setTitle(s);
        return true;
    }

    private void updateLabelStart(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);


        // int result = now.compareTo(dd);
        if (result == 0) {
            editText.setText("Idag");
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
        } else if (result == 1) {
            editText.setText("Imorgon");
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());

        } else if (result > 1 && result <= 6) {
            editText.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());

        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            editText.setText(sdf1.format(myCalendar.getTime()));
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());

        }

    }

    private void updateLabelEnd(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd, MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);


        // int result = now.compareTo(dd);
        if (result == 0) {
            editText.setText("Idag");
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

            enddatestring = sdf1.format(myCalendar.getTime());
        } else if (result == 1) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

            editText.setText("Imorgon");
            enddatestring = sdf1.format(myCalendar.getTime());

        } else if (result > 1 && result <= 6) {
            editText.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            editText.setText(sdf1.format(myCalendar.getTime()));
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

        }

    }


    private void getUserAPI(ArrayList<String> arr, String startdate, String enddate) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            EditCampaignPojo pojo = new EditCampaignPojo();
            pojo.access_key = ConsURL.accessKey;
            pojo.start_date = startdate;
            pojo.end_date = enddate;
            pojo.user_id = userid;
            pojo.sponsers = arr;
            pojo.campaign_id = arr_campaign1.get(0).getId();
            pojo.status = "1";
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

//            requestData = pojo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(EditCampaign.this, result -> {
            try {
            if (result.isStatus()) {
//                    JSONArray obj = new JSONArray(result.getData());
//                    for (int i = 0; i < obj.length(); i++) {
//                        arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
//                    }
                    AddCampaign.img.clear();
                  showCustomDialog();


            }else{

                    JSONObject object=new JSONObject(result.getData());
                    Toast.makeText(this,object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this,result.getMessage(), Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_Campaign");

    }

    private void getDeleteAPI() {
        //  arr.clear();
        String requestData;HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );

        try {
            EditCampaignPojo pojo = new EditCampaignPojo();
            pojo.access_key = ConsURL.accessKey;

            pojo.user_id = userid;
            pojo.campaign_id = arr_campaign1.get(0).getId();
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

//            requestData = pojo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(EditCampaign.this, result -> {
            try {
                if (result.isStatus()) {

                    AddCampaign.img.clear();
                    startActivity(new Intent(EditCampaign.this, HomePage.class));
                   // finish();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_Campaign");

    }


    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill ta bort kampanjen.");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {


            if (cmn.isOnline(EditCampaign.this)) {
                getDeleteAPI();
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCampaign.this);
        builder.setCancelable(false);
        builder.setMessage("Finns det någon organisation eller företag som vill ska synas extra för dina användare. Här kan du nämnligen hantera dina sponsorkampanjer. Välj en eller flera sponsorer och välj hur lång tid du kampanjen varar.");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            startActivity(new Intent(EditCampaign.this, ChooseSponser.class));
        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void alert(ArrayList<String> arr, String startdate, String enddate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditCampaign.this);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du uppdaterar den här kampanjen?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            if (new CommonMethods().isOnline(EditCampaign.this)) {
                getUserAPI(arr, startdate, enddate);
            } else {
                Toast.makeText(EditCampaign.this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
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
        create.setText("Kampanjen uppdaterades.");
        ok.setOnClickListener(view -> {

            startActivity(new Intent(EditCampaign.this, HomePage.class));
            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}

























































