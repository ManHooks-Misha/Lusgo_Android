package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import com.app.SyrianskaTaekwondo.hejtelge.pojo.Event;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityEventshowBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import vk.help.MasterActivity;

public class Eventpublic extends MasterActivity {
    private Event data;
    private ActivityEventshowBinding binding;
    private String userid;

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
        setContentView(R.layout.activity_eventpublic);
        binding = ActivityEventshowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Evenemangsdetaljer");
        if (getIntent() != null) {
            data = (Event) getObject(Objects.requireNonNull(getIntent().getStringExtra("DATA")), Event.class);
        }
        userid = new CommonMethods().getPrefsData(context, "id", "");


        String stdate = data.getStartdate();
        String[] arrstart = stdate.split(" ");
//        String txt_stdate = arrstart[0];
        String txt_sttime = arrstart[1];
        String enddate = data.getEnddate();
        String[] arrend = enddate.split(" ");
//        String enddt = arrend[0];
        String endtime = arrend[1];
        String myFormat1 = "dd MMM, yyyy"; //In which you need put here
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //In which you need put here

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat1, Locale.US);
        try {
            Date stpast = format.parse(stdate);
            Date endpast = format.parse(enddate);
            assert stpast != null;
            stdate = sdf.format(stpast.getTime());
            assert endpast != null;
            enddate = sdf.format(endpast.getTime());

            if (System.currentTimeMillis() > stpast.getTime() && System.currentTimeMillis() < endpast.getTime()) {
                binding.endEvent.setVisibility(View.GONE);


            } else {
                binding.endEvent.setVisibility(View.VISIBLE);
                binding.endEvent.setText("Avslutat");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        binding.date.setText(stdate + " - " + enddate);
        binding.title.setText(Html.fromHtml(data.getTitle()));
        binding.desc.setText(Html.fromHtml(data.getDetail()));
        binding.loc.setText(Html.fromHtml(data.getLocation()));
        String[] sttime_ = txt_sttime.split(":");
        txt_sttime = sttime_[0] + ":" + sttime_[1];
        String[] endtime_ = endtime.split(":");
        endtime = endtime_[0] + ":" + endtime_[1];
        binding.sttime.setText(txt_sttime + " - " + endtime);

        if (data.getDetail().length() > 0) {
            binding.desc.setVisibility(View.VISIBLE);
        } else {
            binding.desc.setVisibility(View.GONE);
        }
        if (userid.length() == 0) {
            binding.llPublic.setVisibility(View.VISIBLE);
            binding.coachAdminBlock.setVisibility(View.GONE);
            binding.llUser.setVisibility(View.GONE);
            binding.delete.setVisibility(View.GONE);

        }


    }
}
