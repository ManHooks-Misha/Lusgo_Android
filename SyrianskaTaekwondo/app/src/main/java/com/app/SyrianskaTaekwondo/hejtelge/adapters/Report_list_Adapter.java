package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Report_ListActivity;
import com.app.SyrianskaTaekwondo.hejtelge.Report_detailsActivity;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.ReportList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Report_list_Adapter extends RecyclerView.Adapter<Report_list_Adapter.MyViewHolder> {


    private List<ReportList> horizontalList;
    private Activity context;
    String userid, timediff;
    AlertDialog alertDialog;

    public Report_list_Adapter(List<ReportList> horizontalList, Activity context) {
        this.horizontalList = horizontalList;
        this.context = context;
        userid = new CommonMethods().getPrefsData(context, "id", "");


    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView date, srno, reportto, report_by, comment, button_block, button_ok, button_remove, action_done;
        AppCompatImageView view_btn;
        LinearLayout ll_action;
        TextView txtview, pos;

        MyViewHolder(View view) {
            super(view);
            //   txtview = view.findViewById(R.id.group_name);
            // imageView = view.findViewById(R.id.img_edit);
            //   pos = view.findViewById(R.id.pos);
//
            date = view.findViewById(R.id.date);
            srno = view.findViewById(R.id.report_id);
            reportto = view.findViewById(R.id.reportto);
            report_by = view.findViewById(R.id.report_by);
            comment = view.findViewById(R.id.comment);
            button_ok = view.findViewById(R.id.buttonOk);
            button_block = view.findViewById(R.id.buttonblock);
            button_remove = view.findViewById(R.id.buttonremove);
            ll_action = view.findViewById(R.id.ll_action);
            action_done = view.findViewById(R.id.action_done);
            view_btn = view.findViewById(R.id.view_btn);

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_ist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //holder.txtview.setText("Sweta Gupta");
        // holder.txtview.setText(horizontalList.get(position).get("name"));
//        int i=position+1;
//
//        holder.pos.setText(""+i);

        holder.srno.setText(horizontalList.get(position).getReport_id());


        holder.reportto.setText("Kommentaren skrevs av "+horizontalList.get(position).getReportetoName().trim());
        holder.report_by.setText("Kommentar rapporterad av "+horizontalList.get(position).getReportebyName().trim());
      //  holder.reportto.setText("Rapporterade till: " + horizontalList.get(position).getReportetoName().trim() + "(" + horizontalList.get(position).getReportedTo() + ")");
    //   holder.report_by.setText("Kommentare skrevs av: " + horizontalList.get(position).getReportebyName().trim() + "(" + horizontalList.get(position).getReportedBY() + ")");
        String comments = horizontalList.get(position).getReport_reason();
        String report_status = horizontalList.get(position).getReportstatus();
        if (report_status == null || report_status.equals("0")) {
            holder.ll_action.setVisibility(View.VISIBLE);
            holder.action_done.setVisibility(View.GONE);
        } else {
            holder.ll_action.setVisibility(View.GONE);
            holder.action_done.setVisibility(View.VISIBLE);
        }

        if (report_status != null) {
            if (report_status.equals("1")) {
                holder.action_done.setText("Handling: Kommentar har tagits bort");
            } else if (report_status.equals("2")) {
                holder.action_done.setText("Handling: Kommentaren är ok");
            } else if (report_status.equals("3")) {
                holder.action_done.setText("Handling: Användaren blockerad");
            }
        }


        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = format.parse(horizontalList.get(position).getReport_date());
            Date now = new Date();
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
           /* if (days < 9 && days > 1) {
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

            } else {*/
            timediff = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(past);
//                timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(past);
//                timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
            //  }

        } catch (Exception j) {
            j.printStackTrace();
        }
        holder.date.setText(String.format("%s ", timediff));

      /*  comments = comments.replaceFirst("" +
                "", "");
        comments = comments.replaceFirst("<p dir=\"ltr\">", "");
        comments = comments.replaceAll("</p>", "");
        comments = comments.replace("\n", "");
        comments = comments.replaceFirst("<u>", "");
        // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
        comments = comments.replaceAll("</u>", "");*/
        holder.comment.setText("Anledning: " + Html.fromHtml(comments.trim()));
        if (horizontalList.get(position).getUsertypeTo().equals("2")) {
            holder.button_block.setVisibility(View.GONE);
        } else {
            holder.button_block.setVisibility(View.VISIBLE);

        }
        holder.button_remove.setOnClickListener(v -> {
            String report_id = horizontalList.get(position).getReport_id();
            String comment_id = horizontalList.get(position).getComments_id();
            String uid = horizontalList.get(position).getReportedToID();
            showCustomDialog(v, comment_id, report_id, uid, "1");
        });
        holder.button_ok.setOnClickListener(v -> {
            String report_id = horizontalList.get(position).getReport_id();
            String comment_id = horizontalList.get(position).getComments_id();
            String uid = horizontalList.get(position).getReportedByID();
            showCustomDialog(v, comment_id, report_id, uid, "2");

        });
        holder.view_btn.setOnClickListener(v -> {
            String reason = horizontalList.get(position).getReport_reason();
            String comment = horizontalList.get(position).getComment();
            showpopDialog(v, reason, comment);
        });
        holder.button_block.setOnClickListener(view -> {
            String id = horizontalList.get(position).getReportedToID();
            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "report"));

        }); /*holder.reportto.setOnClickListener(view -> {

            String id = horizontalList.get(position).getReportedToID();
            if(id.equals(userid)){
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "own").putExtra("flag_screen", "dkcj"));

            }else {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen", "dkcj"));
            }
        });holder.report_by.setOnClickListener(view -> {
            String id = horizontalList.get(position).getReportedByID();
            context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user").putExtra("flag_screen","dkcj"));

        });*/
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", horizontalList.get(position).getReport_id()).putExtra("type","")));
    }


    private void getReportListAPI(String comment_id, String report_id, String uid, String action, String remark,View view) {
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
                   showCustomDialog(view,result.getMessage());
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "remove_comments");
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
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
                getReportListAPI(comment_id, report_id, uid, action, remark,view);

            } else {
                getReportListAPI(comment_id, report_id, uid, action, "",view);

                // Common.INSTANCE.showToast(context, "Vänligen ange anmärkningar");
            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
    }

    private void showpopDialog(View item, String reasontxt, String commenttxt) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.popup_dialog_report, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView reason = dialogView.findViewById(R.id.reason);
        AppCompatTextView comment = dialogView.findViewById(R.id.comment_text);
        AppCompatTextView buttonOk = dialogView.findViewById(R.id.buttonOk);
        commenttxt = commenttxt.replaceFirst("<p dir=\"ltr\">", "");
        // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
        commenttxt = commenttxt.replaceAll("</p>", "");
        reason.setText(Html.fromHtml(reasontxt));
        comment.setText(Html.fromHtml(commenttxt));

        buttonOk.setOnClickListener(v -> alertDialog.dismiss());

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }


    private void showCustomDialog(View item,String message) {
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
            context.startActivity(new Intent(context, Report_ListActivity.class));
            context.finish();
//            startActivity(new Intent(CreateNews.this, HomePage.class));
//            finish();
        });

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }
}


