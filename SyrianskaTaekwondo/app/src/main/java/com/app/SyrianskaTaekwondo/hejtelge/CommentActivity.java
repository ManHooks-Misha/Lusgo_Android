package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCommentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CommentModel;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import vk.help.AdapterView;
import vk.help.MasterActivity;
import vk.help.MasterAdapter;
import vk.help.views.CircleImageView;

public class CommentActivity extends MasterActivity {
    private MasterAdapter adapter;
    private ArrayList<CommentModel> commentModels = new ArrayList<>();
    private CommonMethods cmn;
    private String news_id = "", userid, Comment = "", Deleteid = "", item = "";
    private AlertDialog alertDialog, alertDialog1, alertDialog2;
    private ActivityCommentBinding binding;
    private ArrayList<String> deleteditem = new ArrayList<>();
    private String timediff = "", usertype;
    String sum_report_value="";

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(context, HomePage.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cmn = new CommonMethods();
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        //     Objects.requireNonNull(getSupportActionBar()).setTitle("Lägg till kommentar");
        userid = cmn.getPrefsData(CommentActivity.this, "id", "");
        usertype = cmn.getPrefsData(CommentActivity.this, "usertype", "");
        if (userid.length() == 0) {
            binding.bottomBar.setVisibility(View.GONE);
            binding.userRecord.setText("Chatten är stängd");
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
        }
        binding.editMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    binding.editMessage.setHint("");
                else
                    binding.editMessage.setHint("Skriv en kommentar");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        assert bundle != null;
        news_id = bundle.getString("news_id");

        binding.sendMessage.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.editMessage.getText()).toString().isEmpty()) {
                cmn.showAlert("Skriv kommentar", this);
                //showToast("Skriv kommentar");
            } else {
               // Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_anim);
              //  binding.sendMessage.startAnimation(animation);
                Comment = Html.toHtml(binding.editMessage.getText());
                Comment = Comment.replaceFirst("<u>", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                Comment = Comment.replaceAll("</u>", "");
                //    Comment = binding.editMessage.getText().toString();
                AddComment();

            }
        });

        adapter = new MasterAdapter(binding.commentsList, null, new AdapterView() {
            @NotNull
            @Override
            public RecyclerView.ViewHolder createChildView(@NotNull ViewGroup viewGroup, int viewType) {
                return new CommentsViewHolder(LayoutInflater.from(CommentActivity.this).inflate(R.layout.item_comments, viewGroup, false));
            }

            @Override
            public void getChildView(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
                ((CommentsViewHolder) viewHolder).setData((CommentModel) adapter.getData().get(position));
            }
        });

        loadData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (userid.length() > 0) {
                startActivity(new Intent(context, HomePage.class));
                finish();
            } else {
                finish();

            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_key", ConsURL.accessKey);
            jsonObject.put("user_id", cmn.getPrefsData(CommentActivity.this, "id", ""));
            jsonObject.put("news_id", news_id);
            new NetworkCall(CommentActivity.this, result -> {
                try {
                    if (result.isStatus()) {
                        JSONArray jsonArray = new JSONArray(result.getData());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                commentModels.add((CommentModel) getObject(jsonArray.getJSONObject(i).toString(), CommentModel.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    } else {
                        showErrorToast(getResources().getString(R.string.internet));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (commentModels.isEmpty()) {
                        binding.userRecord.setVisibility(View.VISIBLE);
                       // binding.userRecord.setText("Chatten är stängd");
                        binding.commentsList.setVisibility(View.GONE);
//                        showErrorToast("No Comments Found");
                    } else {
                        binding.userRecord.setVisibility(View.GONE);
                        binding.commentsList.setVisibility(View.VISIBLE);
                        adapter.setData(commentModels);
                    }

                }
                return null;
            }, jsonObject.toString()).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "commentList");
        } catch (Exception e) {
            e.printStackTrace();
            adapter.setData(commentModels);
        }
    }

    private void AddComment() {
        sum_report_value="";
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_key", ConsURL.accessKey);
            jsonObject.put("user_id", cmn.getPrefsData(CommentActivity.this, "id", ""));
            jsonObject.put("news_id", news_id);
            jsonObject.put("comment", Comment);

            new NetworkCall(CommentActivity.this, result -> {
                try {
                    if (result.isStatus()) {
                        hideKeyBoard();
                        binding.editMessage.setText("");
                        //JSONObject jsonObject1 = new JSONObject(result.getData());
                        try {
                            JSONObject obj = new JSONObject(result.getData());
                            CommentModel commentModel = new CommentModel();
                            commentModel.setCreated(obj.getString("created"));
                            commentModel.setName(obj.getString("name"));
                            commentModel.setId(obj.getString("id"));
                            commentModel.setComment(obj.getString("comment"));
                            commentModel.setUser_id(obj.getString("user_id"));
                            commentModel.setProfile_image(obj.getString("profile_image"));
                            commentModels.add(commentModel);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        if(result.getData()!=null){
                            JSONArray obj = new JSONArray(result.getData());
                            for(int i=0;i<obj.length();i++) {
                                String report_value = obj.getString(i);
                                sum_report_value=sum_report_value+report_value+",";
                            }
                            int length=sum_report_value.length();
                            sum_report_value=sum_report_value.substring(0,length-1);
                            reprotMessage(result.getMessage(),sum_report_value);

                        }
                       // showErrorToast("Please Check Your internet conection");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (commentModels.isEmpty()) {
                        binding.userRecord.setVisibility(View.VISIBLE);
                        binding.commentsList.setVisibility(View.GONE);
                       // binding.userRecord.setText("Chatten är stängd");
                    } else {
                        binding.userRecord.setVisibility(View.GONE);
                        binding.commentsList.setVisibility(View.VISIBLE);
                        adapter.setData(commentModels);
                    }

                }
                return null;
            }, jsonObject.toString()).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "add_comment");
        } catch (Exception e) {
            e.printStackTrace();
            adapter.setData(commentModels);
        }
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView Name, Comment, CommentTime;
        private CircleImageView profilePic;
        private AppCompatTextView Deletecomment;
        private AppCompatImageView report;

        private CommentsViewHolder(View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            Name = itemView.findViewById(R.id.Name);
            Comment = itemView.findViewById(R.id.Comment);
            CommentTime = itemView.findViewById(R.id.CommentTime);
            Deletecomment = itemView.findViewById(R.id.Deletecomment);
            report = itemView.findViewById(R.id.report);


        }

        public void setData(CommentModel data) {

            Glide.with(CommentActivity.this).load(data.getProfile_image()).placeholder(R.drawable.user_diff).into(profilePic);
            Name.setText(data.getName().trim());

            String title = data.getComment().trim();
            title = title.replaceFirst("<p dir=\"ltr\">", "");
            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
            title = title.replaceAll("</p>", "");
            Comment.setText(Html.fromHtml(title));

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date past = format.parse(data.getCreated());
                Date now = new Date();
                assert past != null;
               /* long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
                if (days < 9 && days > 1) {
                    timediff = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " Dagar sedan";
                } else if (days == 0) {
                    timediff = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h sedan";
                    long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                    if (hours == 0) {
                        long mins = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                        timediff = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m sedan";
                        if (mins == 0) {
                            timediff = "Precis nu";
                        }
                    }

                } else {*/
                   // timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                    timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
              //  }
                CommentTime.setText(timediff);

                if (userid.equals(data.getUser_id())) {
                    Deletecomment.setVisibility(View.VISIBLE);

                } else {
                    Deletecomment.setVisibility(View.GONE);

                }
                if (usertype.equals(ConsURL.admin)) {
                    Deletecomment.setVisibility(View.VISIBLE);
                    report.setVisibility(View.GONE);
                }
                if (data.getUser_id().equals(userid)) {
                    report.setVisibility(View.GONE);
                }
                if (userid.length() == 0) {
                    Deletecomment.setVisibility(View.GONE);
                }
                report.setOnClickListener(v -> {
                    String commentid = data.getId();
                    String reportTo = data.getUser_id();
                    showCustomDialogReport(v, commentid, reportTo);
                });
                Deletecomment.setOnClickListener(v -> {
                    Deleteid = data.getId();
                    showCustomDialog();
                });

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void showCustomDialogReport(View item, String commentid, String reportTo) {
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.report_view, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        RadioGroup reason = dialogView.findViewById(R.id.reason);

        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);


        cancel.setOnClickListener(view -> {
            alertDialog2.dismiss();

        });


        ok.setOnClickListener(view -> {

            int selectedId = reason.getCheckedRadioButtonId();
            RadioButton rd_reason = reason.findViewById(selectedId);
            showCustomDialogReport(commentid, reportTo, rd_reason);
        });

        //finally creating the alert dialog and displaying it
        alertDialog2 = builder.create();
        alertDialog2.show();
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Vill du ta bort kommentaren?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (cmn.isOnline(context)) {
                getCommentDeleteAPI(Deleteid);

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());


        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogReport(String commentid, String reportTo, RadioButton rd_reason) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Är du säker på att du vill rapportera den här kommentaren?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            if (cmn.isOnline(context)) {
                alertDialog1.dismiss();
                getCommentReportAPI(commentid, reportTo, rd_reason.getText().toString(), "");

            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());


        //finally creating the alert dialog and displaying it
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    private void getCommentDeleteAPI(String Deleteid) {
        //arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("user_id", cmn.getPrefsData(CommentActivity.this, "id", ""));
            object.put("comment_id", Deleteid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    for (CommentModel s : commentModels) {

                        if (s.getId().equals(Deleteid)) {
                            commentModels.remove(s);
                            adapter.setData(commentModels);
                            adapter.notifyDataSetChanged();

                        }
                    }
                    //  commentModels.remove(pos);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_comment");
    }


    private void getCommentReportAPI(String commentid, String reportTo, String reason, String desc) {
        //arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", ConsURL.accessKey);
            object.put("reportedBy", cmn.getPrefsData(CommentActivity.this, "id", ""));
            object.put("comments_id", commentid);
            object.put("news_id", news_id);
            object.put("reportedTo", reportTo);
            object.put("report_desc", desc);
            object.put("report_reason", reason);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    //Common.INSTANCE.showToast(context, "Kommentaren har tagitsbort");
                    alertDialog2.dismiss();
                    //  commentModels.remove(pos);

                }else {
                    alertDialog2.dismiss();
                    cmn.showAlert("Du har redan rapporterat den här kommentaren",context);
                    //Common.INSTANCE.showToast(context, "Du har redan rapporterat den här kommentaren");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "reported_news");
    }


    public void hideKeyBoard() {
        View view1 = this.getCurrentFocus();
        if (view1 != null) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
    }


    public void reprotMessage(String message,String message1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message+"\n"+"Stötande ord är: \n"+"["+message1+"]");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }
}