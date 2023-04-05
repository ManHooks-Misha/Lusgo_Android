
package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.SliderImageNews;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityReportDetailsBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ReportDetail;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import vk.help.MasterActivity;

public class Report_detailsActivity extends MasterActivity {
    private String userid, reportid, type="", role;
    private List<ReportDetail> arr = new ArrayList<>();
    ActivityReportDetailsBinding binding;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
//        final ActionBar abar = getSupportActionBar();
//        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.MATCH_PARENT,
//                Gravity.CENTER);
//        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
//        textviewTitle.setText("Detaljer");
//        abar.setCustomView(viewActionBar, params);
//        abar.setDisplayShowCustomEnabled(true);
//        abar.setDisplayShowTitleEnabled(false);
//        abar.setDisplayHomeAsUpEnabled(true);
       // abar.setIcon(R.color.transparent);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        reportid = getIntent().getStringExtra("report_id");
        type = getIntent().getStringExtra("type");
        userid = new CommonMethods().getPrefsData(context, "id", "");
        role = new CommonMethods().getPrefsData(context, "usertype", "");
        getDetailsAPI();
    }


    private void showCustomDialog(View item, String comment_id, String report_id, String uid, String action) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.remark_action, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email_text = dialogView.findViewById(R.id.message_text);

        ok.setOnClickListener(view -> {
            if (email_text.getText().toString().trim().length() > 0) {
                alertDialog.dismiss();
                String remark = Html.toHtml(email_text.getText());
                remark = remark.replaceFirst("" +
                        "", "");
                remark = remark.replaceFirst("<p dir=\"ltr\">", "");
                remark = remark.replaceAll("</p>", "");
                remark = remark.replaceFirst("<u>", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                remark = remark.replaceAll("</u>", "");
                getActionAPI(comment_id, report_id, uid, action, remark, view);

            } else {
                getActionAPI(comment_id, report_id, uid, action, "", view);

//                Common.INSTANCE.showToast(context, "Vänligen ange anmärkningar");
            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getDetailsAPI() {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("report_id", reportid);
            object.put("user_id", userid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray objarr = new JSONArray(result.getData());
                    if (objarr.length() > 0) {
                        for (int i = 0; i < objarr.length(); i++) {
                            arr.add((ReportDetail) (getObject(objarr.getString(i), ReportDetail.class)));
                        }

                        Glide.with(context)
                                .load(arr.get(0).getProfile_image())
                                .fitCenter()
                                .placeholder(R.drawable.user_diff)
                                .into(binding.profile);
                        binding.docTxt.setOnClickListener(view ->
                                startActivity(new Intent(this, ShowDocumentActivity.class).putExtra("Url", arr.get(0).getDoc())));
                        binding.newsTxt.setText(Html.fromHtml(arr.get(0).getNews().trim()));
                        binding.txtName.setText(arr.get(0).getName());
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date past = format.parse(arr.get(0).getReport_date());
                        String timediff = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(past);
                        binding.time.setText(timediff);
                        if (!arr.get(0).getImages().isEmpty()) {
                            binding.imageSlider.setSliderAdapter(new SliderImageNews(Report_detailsActivity.this, arr.get(0).getImages()));
                            binding.imageSlider.setIndicatorVisibility(false);
                            binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                            binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
                            binding.imageSlider.setIndicatorSelectedColor(Color.BLUE);
                            binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
                            binding.imageSlider.setAutoCycle(false);
                            binding.imageSlider.setVisibility(View.VISIBLE);
                        } else {
                            binding.imageSlider.setVisibility(View.GONE);
                        }
                        if (arr.get(0).getDoc().isEmpty()) {
                            binding.llDoc.setVisibility(View.GONE);
                        } else {
                            binding.docTxt.setText("Dokument");
                            binding.llDoc.setVisibility(View.VISIBLE);

                        }
                        if (arr.get(0).getLink().length() > 0) {
                            binding.llLink.setVisibility(View.VISIBLE);
                            binding.linkTxt.setText(Html.fromHtml("<b>" + arr.get(0).getLink() + " " + "</b>"));
                        } else {
                            binding.llLink.setVisibility(View.GONE);

                        }
                        binding.linkTxt.setOnClickListener(v -> {
                            if (arr.get(0).getLink().contains("http")) {
                                Uri uri = Uri.parse(arr.get(0).getLink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            } else {
                                Uri uri = Uri.parse("http://" + arr.get(0).getLink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                context.startActivity(intent);
                            }
                            // startActivity(new Intent(this, show_Messege.class).putExtra("Url", arr.get(0).getLink()));
                        });
                        long now = System.currentTimeMillis() - 1000;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = null;
                        try {
                            date = dateFormat.parse(arr.get(0).getInsertdate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert date != null;
                        long result1 = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);
                        // updateLabel(binding.time, result1);
                        String comment = arr.get(0).getComment();
                        comment = comment.replaceFirst("<p dir=\"ltr\">", "");
                        // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                        comment = comment.replaceAll("</p>", "");
                        binding.comment.setText(Html.fromHtml(comment));
                        binding.reasonText.setText(arr.get(0).getReport_reason());
                        String report_status = arr.get(0).getReportstatus();
                        String remark = arr.get(0).getReport_desc();
                        remark = remark.replaceFirst("" +
                                "", "");
                        remark = remark.replaceFirst("<p dir=\"ltr\">", "");
                        remark = remark.replaceAll("</p>", "");
                        remark = remark.replaceFirst("<u>", "");
                        // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                        remark = remark.replaceAll("</u>", "");
                        if (report_status != null) {
                            if (remark.length() > 0) {
                                binding.remark.setText(Html.fromHtml(remark));
                            } else {
                                binding.remarktext.setVisibility(View.GONE);
                                binding.remark.setVisibility(View.GONE);
                            }
                            if (report_status.equals("1")) {
                                binding.action.setText("Handling: Kommentar har tagits bort");

                            } else if (report_status.equals("2")) {
                                binding.remark.setText(Html.fromHtml(remark));
                                binding.action.setText("Handling: Kommentaren är ok");
                            } else if (report_status.equals("3")) {
                                binding.remark.setText(Html.fromHtml(remark));
                                binding.action.setText("Handling: Användaren blockerad");
                            }
                            binding.action.setVisibility(View.VISIBLE);

                        } else {
                            binding.remark.setVisibility(View.GONE);
                            binding.remarktext.setVisibility(View.GONE);
                            binding.vShow.setVisibility(View.GONE);
                            binding.action.setVisibility(View.GONE);
                        }
                        binding.buttonremove.setOnClickListener(v -> {
                            String report_id = arr.get(0).getReport_id();
                            String comment_id = arr.get(0).getComments_id();
                            String uid = arr.get(0).getReportedToID();
                            showCustomDialog(v, comment_id, report_id, uid, "1");

//                            getActionAPI(comment_id, report_id, uid, "1");
                        });
                        binding.reportTo.setOnClickListener(view -> {

                            String id = arr.get(0).getReportedToID();
                            if (id.equals(userid)) {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "own").putExtra("flag_screen", "dkcj"));

                            } else {
                                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "dkcj"));
                            }
                        });
                        binding.reportBy.setOnClickListener(view -> {
                            String id = arr.get(0).getReportedByID();
                            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "dkcj"));

                        });
                        binding.buttonOk.setOnClickListener(v -> {
                            String report_id = arr.get(0).getReport_id();
                            String comment_id = arr.get(0).getComments_id();
                            String uid = arr.get(0).getReportedByID();
                            showCustomDialog(v, comment_id, report_id, uid, "2");

                        });
                        if (report_status == null || report_status.equals("0")) {
                            binding.llAction.setVisibility(View.VISIBLE);
                            binding.action.setVisibility(View.GONE);
                        } else {
                            binding.llAction.setVisibility(View.GONE);
                            binding.action.setVisibility(View.VISIBLE);
                        }
                        binding.buttonblock.setOnClickListener(view -> {
                            String id = arr.get(0).getReportedToID();
                            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "report"));

                        });
                        if (arr.get(0).getUsertypeTo().equals("2")) {
                            binding.buttonblock.setVisibility(View.GONE);
                        } else {
                            binding.buttonblock.setVisibility(View.VISIBLE);

                        }
                        String reportTo = arr.get(0).getReportetoName() + "(" + arr.get(0).getReportedTo() + ")";
                        reportTo = reportTo.replace(reportTo, "<font color='#177ff0'>" + reportTo + "</font>");
                        //  binding.reportTo.setText(Html.fromHtml("Rapporterade till: " + reportTo));

                        binding.reportTo.setText("Kommentaren skrevs av " + arr.get(0).getReportetoName().trim());
                        if (role.equals("3") || role.equals("4") || role.equals("6") && report_status.equals("1")) {
                            binding.reportBy.setVisibility(View.GONE);

                        } else {
                            String reportBy = arr.get(0).getReportebyName() + "(" + arr.get(0).getReportedBY() + ")";
                            reportBy = reportBy.replace(reportBy, "<font color='#177ff0'>" + reportBy + "</font>");
                            //binding.reportBy.setText(Html.fromHtml("Kommentare skrevs av: " + reportBy));
                            binding.reportBy.setVisibility(View.VISIBLE);
                            binding.reportBy.setText("Kommentar rapporterad av " + arr.get(0).getReportebyName().trim());

                        }

                        //    binding.reportTo.setText("Rapporterade till: "+arr.get(0).getReportetoName()+"("+arr.get(0).getReportedTo()+")");
                          /*  binding.reportList.setVisibility(View.VISIBLE);

                            binding.userRecord.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
*/

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "reported_newsDetails");
    }

    private void getActionAPI(String comment_id, String report_id, String uid, String action, String remark, View item) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("action", action);
            object.put("comment", remark);
            object.put("user_id", userid);
            object.put("comment_id", comment_id);
            object.put("report_id", report_id);
            object.put("u_id", uid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    showCustomDialog(item, "Ditt svar har skickats", report_id);
                    //   Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "remove_comments");
    }


    private void showCustomDialog(View item, String message, String report_id) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.news_alert, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.ok);
        AppCompatTextView create = dialogView.findViewById(R.id.create);

        create.setText(message);
        ok.setOnClickListener(view -> {
            context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", report_id).putExtra("type", ""));
            finish();
//            startActivity(new Intent(CreateNews.this, HomePage.class));
//            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateLabel(AppCompatTextView txt, long result) {
        if (result == 0) {
            txt.setText(String.format("%s", "Idag"));

        } else if (result == 1) {
            txt.setText(String.format("%s", "Igår"));

        } else {

            txt.setText(result + String.format("%s", " dagar sedan"));

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {
                if (role.equals(ConsURL.admin)) {
                    if (type.equals("Notfication")) {
                        finish();
                    } else {
                        startActivity(new Intent(this, Report_ListActivity.class));
                        finish();
                    }
                } else {
                    finish();

                }
            }catch (Exception e){
                e.printStackTrace();
                startActivity(new Intent(this, Report_ListActivity.class));
                finish();
            }

            return true;
        }

        return super.

                onOptionsItemSelected(item);

    }

}