package com.app.SyrianskaTaekwondo.hejtelge;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.ChooseCampaignAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.AddCompaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AddCampaign extends AppCompatActivity {
    private AppCompatTextView add_sponsers, add_campaign;
    public static ArrayList<HashMap<String, String>> img = new ArrayList<>();
    public ArrayList<HashMap<String, String>> arr_sponser = new ArrayList<>();
    private AppCompatEditText startdate;
    private AppCompatEditText enddate;
    private AppCompatEditText endtime;
    private AppCompatEditText starttime;
    private RecyclerView grid;
    private String userid, startdatestring, startdatestring2, enddatestring, enddatestring2;
    CommonMethods cmn;
    AlertDialog alertDialog;
    Calendar mycalendar, endcalendar;
    ChooseCampaignAdapter libraryHotAdapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.save) {
            arr_sponser.clear();
            String starttime_text = Objects.requireNonNull(starttime.getText()).toString();
            String endtime_text = Objects.requireNonNull(endtime.getText()).toString();
            for (HashMap map : img) {
                String id = Objects.requireNonNull(map.get("id")).toString();
                HashMap map1 = new HashMap();
                map1.put("id", id);
                arr_sponser.add(map1);
            }
            String value_start = startdatestring2;
            String value_end = enddatestring2;


            if (arr_sponser.size() > 0) {
                if (cmn.isOnline(AddCampaign.this)) {
                    getUserAPI(arr_sponser, value_start, value_end);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddCampaign.this, "Välj någon sponsor", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campaign);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Sponsorkampanj");
        loadID();
        cmn = new CommonMethods();
        Locale locale = new Locale("sv", "SE"); // ( language code, country code );

        //  Locale locale = new Locale("SE");
        Locale.setDefault(locale);
        userid = cmn.getPrefsData(AddCampaign.this, "id", "");
        mycalendar = Calendar.getInstance();
        endcalendar = Calendar.getInstance();
        updateLabelStart(startdate, 0, mycalendar);
        updateLabelEnd(enddate, 0, endcalendar);
        Date stdate = mycalendar.getTime();
        int h = stdate.getHours();
        int m = stdate.getMinutes();
        String hours = String.valueOf(h);

        String min = String.valueOf(m);
        if (min.length() == 1) {
            min = "0" + min;
        }
        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        starttime.setText(hours + ":" + min);
        endtime.setText(hours + ":" + min);
        add_sponsers.setOnClickListener(view -> {
            startActivity(new Intent(AddCampaign.this, ChooseSponser.class));
        });
        libraryHotAdapter = new ChooseCampaignAdapter(img, AddCampaign.this);
        grid.setLayoutManager(new LinearLayoutManager(AddCampaign.this, RecyclerView.HORIZONTAL, false));
        grid.setAdapter(libraryHotAdapter);
        if (img.size() > 0) {
            libraryHotAdapter.notifyDataSetChanged();


        }


        startdate.setOnClickListener(v -> {

            Calendar instance = Calendar.getInstance();
            Locale loc11 = new Locale("sv", "SE");
            Locale.setDefault(loc11);
            Configuration configuration = getResources().getConfiguration();
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
            Locale loc11 = new Locale("sv", "SE");
            Locale.setDefault(loc11);
            Configuration configuration = getResources().getConfiguration();
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
                updateLabelEnd(enddate, result, temp);


            }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);

            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
            datePickerDialog.show();
        });

      /*  starttime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;

            mTimePicker = new TimePickerDialog(AddCampaign.this, (timePicker, selectedHour, selectedMinute) -> {
                String hours1 = String.valueOf(selectedHour);

                String min1 = String.valueOf(selectedMinute);
                if (min1.length() == 1) {
                    min1 = "0" + min1;
                }
                if (hours1.length() == 1) {
                    hours1 = "0" + hours1;
                }
                starttime.setText(hours1 + ":" + min1);


            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });*/
        /*endtime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddCampaign.this, (timePicker, selectedHour, selectedMinute) -> {
                String min1 = String.valueOf(selectedMinute);
                String hours1 = String.valueOf(selectedHour);
                if (min1.length() == 1) {
                    min1 = "0" + min1;
                }
                if (hours1.length() == 1) {
                    hours1 = "0" + hours1;
                }
                endtime.setText(hours1 + ":" + min1);


            }, hour, minute, true);//Yes 24 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        });*/

    }

    public void loadID() {
        add_sponsers = findViewById(R.id.add_sponser);
        startdate = findViewById(R.id.startdate);
        enddate = findViewById(R.id.enddate);
        starttime = findViewById(R.id.starttime);
        endtime = findViewById(R.id.endtime);
        grid = findViewById(R.id.sponser);
        add_campaign = findViewById(R.id.add_campaign);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (img.size() > 0) {
            libraryHotAdapter.notifyDataSetChanged();
/*

            ChooseCampaignAdapter libraryHotAdapter = new ChooseCampaignAdapter(img, AddCampaign.this);
            grid.setLayoutManager(new LinearLayoutManager(AddCampaign.this, RecyclerView.HORIZONTAL, false));
            grid.setAdapter(libraryHotAdapter);
*/

        }
    }

   /* private void updateLabelStart(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Locale loc = new Locale("sv","SE");
        Locale.setDefault(loc);


        // int result = now.compareTo(dd);
        if (result == 0) {
            editText.setText("Idag");
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);

            startdatestring = sdf1.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());
        } else if (result == 1) {
            editText.setText("Imorgon");
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);

            startdatestring = sdf1.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

        } else if (result > 1 && result <= 6) {
            editText.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);

            startdatestring = sdf1.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);

            editText.setText(sdf1.format(myCalendar.getTime()));
            startdatestring = sdf1.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

        }
        //    Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();

    }
*/

    private void updateLabelStart(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        Locale loc = new Locale("sv", "SE");
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
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);

            editText.setText("Imorgon");
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
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            startdatestring2 = sdf2.format(myCalendar.getTime());

            editText.setText(sdf1.format(myCalendar.getTime()));
            startdatestring = sdf1.format(myCalendar.getTime());

        }

    }

    private void updateLabelEnd(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        Locale loc = new Locale("sv", "SE");
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

            editText.setText("Imorgon");
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

            enddatestring = sdf1.format(myCalendar.getTime());

        } else if (result > 1 && result <= 6) {
            editText.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

            enddatestring = sdf1.format(myCalendar.getTime());

        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());

            editText.setText(sdf1.format(myCalendar.getTime()));
            enddatestring = sdf1.format(myCalendar.getTime());

        }

    }

    private void getUserAPI(ArrayList<HashMap<String, String>> arr, String startdate, String enddate) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            AddCompaignPojo pojo = new AddCompaignPojo();
            pojo.access_key = ConsURL.accessKey;
            pojo.start_date = startdate;
            pojo.end_date = enddate;
            pojo.user_id = userid;
            pojo.sponsers = arr;
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

//            requestData = pojo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(AddCampaign.this, result -> {
            try {
                if (result.isStatus()) {
                    startActivity(new Intent(AddCampaign.this, HomePage.class));
                    finish();

                } else {
                    if (!result.isStatus()) {
                        cmn.showAlert(result.getMessage(),this);

                    } else {
                        cmn.showAlert(result.getMessage(),this);

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "add_Campaign");
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

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCampaign.this);
        builder.setCancelable(false);
        builder.setMessage("Finns det någon organisation eller företag som vill ska synas extra för dina användare. Här kan du nämnligen hantera dina sponsorkampanjer. Välj en eller flera sponsorer och välj hur lång tid du kampanjen varar.");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
        });
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
            alertDialog.dismiss();
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
}
