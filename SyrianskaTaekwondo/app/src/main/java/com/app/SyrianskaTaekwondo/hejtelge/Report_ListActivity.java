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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Report_list_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityReportListBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ReportList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

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

import vk.help.MasterActivity;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Report_ListActivity extends MasterActivity {
    CommonMethods cmn = new CommonMethods();
    String userid;
    private ArrayList<ReportList> arr = new ArrayList<>();
    Report_list_Adapter mAdapter;
    ActivityReportListBinding binding;
    String status, msg, txt_fan, startdatestring, startdatestring2, startdate1, enddate1, enddatestring, enddatestring2, txt_title = "";
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.arkiv) {
        startActivity(new Intent(this,ArkivActivity.class));
        finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
//        final ActionBar abar = getSupportActionBar();
//        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.MATCH_PARENT,
//                Gravity.CENTER);
//        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
//        textviewTitle.setText("Rapportlista");
//        abar.setCustomView(viewActionBar, params);
//        abar.setDisplayShowCustomEnabled(true);
//        abar.setDisplayShowTitleEnabled(false);
//        abar.setDisplayHomeAsUpEnabled(true);
//      //  abar.setIcon(R.color.transparent);
//        abar.setHomeButtonEnabled(true);
     //   getSupportActionBar().setTitle("Rapportlista");

        userid = cmn.getPrefsData(context, "id", "");

//        Locale locale = new Locale("sv", "SE");
//        Locale.setDefault(locale);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.MONTH, -1); // to get previous year add -1
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here


        //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat1);
        startdatestring2 = sdf.format(cal.getTime());
        enddatestring2 = sdf.format(today);
        binding.startdate.setText(startdatestring2);
        binding.enddate.setText(enddatestring2);
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        startdate1 = dfDate.format(cal.getTime());
        enddate1 = dfDate.format(today);
        mAdapter = new Report_list_Adapter(
                arr, Report_ListActivity.this);
        binding.reportList.setLayoutManager(new GridLayoutManager(context, 1));
        binding.reportList.setAdapter(mAdapter);
      /*  mAdapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            mAdapter.notifyItemInserted(arr.size() - 1);
            binding.reportList.postDelayed(() -> {
                int end = arr.size();
                //getRecursionAPI(String.valueOf(end));
            }, 2000);
        });*/
        String start = binding.startdate.getText().toString();
        String end = binding.enddate.getText().toString();
        getReportListAPI(start, end);


        binding.startdate.setOnClickListener(v -> {
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

                Log.d("Get Result", "The result id " + result);
          updateLabelStart(binding.startdate, binding.enddate, result, temp);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            //  datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });
        binding.enddate.setOnClickListener(v -> {
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
//                Locale.setDefault(locale);
                long now = System.currentTimeMillis() - 1000;
                long result = TimeUnit.DAYS.convert((temp.getTime().getTime() - now), MILLISECONDS);
                Log.d("Get Result", "The result id " + result);
                updateLabelEnd(binding.enddate, result, temp);
            }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", datePickerDialog);
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Avbryt", datePickerDialog);
            datePickerDialog.show();

        });

    }


    private void updateLabelStart(AppCompatEditText editText, AppCompatEditText editText1, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
//        Locale locale = new Locale("sv", "SE");
//        Locale.setDefault(locale);
        //In which you need put here



        // int result = now.compareTo(dd);
        if (result == 0) {
           // editText1.setText(String.format("%s", "Idag"));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddatestring = sdf1.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            startdate1 = dfDate.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());
            if(enddate1!=null) {
                if (checkDates(startdate1, enddate1)) {

                    editText.setText(sdf1.format(myCalendar.getTime()));
//                    editText.setText(sdf1.format(myCalendar.getTime()));
                    enddatestring = sdf1.format(myCalendar.getTime());
                    enddatestring2 = sdf2.format(myCalendar.getTime());
                } else {
                    startdate1=binding.startdate.getText().toString();
                    showToast("Startdatum bör vara mindre än slutdatum");
                    return;
                }
            }
        } else if (result == 1) {
        //    editText.setText(String.format("%s", "I morgon"));
          //  editText1.setText(String.format("%s", "I morgon"));

            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            startdate1 = dfDate.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());
            if(enddate1!=null) {
                if (checkDates(startdate1, enddate1)) {

                    editText.setText(sdf1.format(myCalendar.getTime()));
                    enddatestring = sdf1.format(myCalendar.getTime());
                    enddatestring2 = sdf2.format(myCalendar.getTime());

                } else {
                    startdate1=binding.startdate.getText().toString();

                    showToast("Startdatum bör vara mindre än slutdatum");
                    return;
                }
            }
        } else if (result > 1 && result <= 6) {
          //  editText1.setText(sdf.format(myCalendar.getTime()));
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddatestring = sdf1.format(myCalendar.getTime());

            startdatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdate1 = dfDate.format(myCalendar.getTime());
            if(enddate1!=null) {
                if (checkDates(startdate1, enddate1)) {

                    editText.setText(sdf1.format(myCalendar.getTime()));
                    enddatestring = sdf1.format(myCalendar.getTime());
                    enddatestring2 = sdf2.format(myCalendar.getTime());

                } else {
                   // startdate1=binding.startdate.getText().toString();

                    showToast("Startdatum bör vara mindre än slutdatum");
                    return;
                }
            }
        } else {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
         //   editText1.setText(sdf1.format(myCalendar.getTime()));
            startdatestring = sdf1.format(myCalendar.getTime());
            enddatestring = sdf1.format(myCalendar.getTime());
            SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
            enddatestring2 = sdf2.format(myCalendar.getTime());
            startdatestring2 = sdf2.format(myCalendar.getTime());

            startdate1 = dfDate.format(myCalendar.getTime());
            if(enddate1!=null) {
                if (checkDates(startdate1, enddate1)) {
                    editText.setText(sdf1.format(myCalendar.getTime()));

                    enddatestring = sdf1.format(myCalendar.getTime());
                    enddatestring2 = sdf2.format(myCalendar.getTime());

                } else {
                    showToast("Startdatum bör vara mindre än slutdatum");
                    return;
                }
            }
        }
        String starts =editText.getText().toString();
        String ends = editText1.getText().toString();
        getReportListAPI(starts, ends);

    }

    private void updateLabelEnd(AppCompatEditText editText, long result, Calendar myCalendar) {
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        String myFormat = "EEEE";
        //In which you need put here
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
//        Locale locale = new Locale("sv", "SE");
//        Locale.setDefault(locale);

        // int result = now.compareTo(dd);
        if (result == 0) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {
                editText.setText(sdf1.format(myCalendar.getTime()));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());
            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden",this);            }
        } else if (result == 1) {
            SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);

            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {

                editText.setText(sdf1.format(myCalendar.getTime()));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden",this);
            }

        } else if (result > 1 && result <= 6) {

            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {

                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
                editText.setText(sdf1.format(myCalendar.getTime()));

                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden",this);
            }


        } else {
            enddate1 = dfDate.format(myCalendar.getTime());
            if (checkDates(startdate1, enddate1)) {

//                editText.setText(sdf.format(myCalendar.getTime()));
                SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1);
                editText.setText(sdf1.format(myCalendar.getTime()));
                enddatestring = sdf1.format(myCalendar.getTime());
                SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat1, Locale.US);
                enddatestring2 = sdf2.format(myCalendar.getTime());

            } else {
                cmn.showAlert("Sluttiden bör vara efter starttiden",this);
                return;
            }
        }
        String starts = binding.startdate.getText().toString();
        String ends = binding.enddate.getText().toString();
        getReportListAPI(starts, ends);
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


    private void getReportListAPI(String start, String end) {
        arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("end_date", end);
            object.put("start_date", start);
            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray objarr = new JSONArray(result.getData());
                    if (objarr.length() > 0) {

                        for (int i = 0; i < objarr.length(); i++) {
                            JSONObject obj=objarr.getJSONObject(i);
                            String reportstatus=obj.getString("reportstatus");
                            if(reportstatus.equals("null")||reportstatus.equals("0")) {
                                arr.add((ReportList) (getObject(objarr.getString(i), ReportList.class)));
                            }
                        }
                        if (arr.size() == 0) {
                            binding.reportList.setVisibility(View.GONE);

                            binding.userRecord.setVisibility(View.VISIBLE);

                        } else {
                            binding.reportList.setVisibility(View.VISIBLE);

                            binding.userRecord.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();

                        }
                    } else {
                        mAdapter.notifyDataSetChanged();
                         binding.reportList.setVisibility(View.GONE);
                        binding.userRecord.setVisibility(View.VISIBLE);


                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "reported_newsFilter");
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arkiv, menu);
        MenuItem mColorFullMenuBtn = menu.findItem(R.id.arkiv); // extract the menu item here

        String title = mColorFullMenuBtn.getTitle().toString();
        if (title != null) {
            SpannableString s = new SpannableString(title);
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // provide whatever color you want here.
            mColorFullMenuBtn.setTitle(s);
        }
        return true;
    }


}