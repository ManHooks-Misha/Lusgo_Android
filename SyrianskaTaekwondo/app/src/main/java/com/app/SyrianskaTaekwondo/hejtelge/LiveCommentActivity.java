package com.app.SyrianskaTaekwondo.hejtelge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityCommentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CommentModel;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnection;
import com.smartarmenia.dotnetcoresignalrclientjava.HubConnectionListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubEventListener;
import com.smartarmenia.dotnetcoresignalrclientjava.HubMessage;
import com.smartarmenia.dotnetcoresignalrclientjava.WebSocketHubConnectionP2;

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

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.AdapterView;
import vk.help.MasterActivity;
import vk.help.MasterAdapter;
import vk.help.views.TextDrawable;

public class LiveCommentActivity extends MasterActivity implements HubConnectionListener, HubEventListener {
    ActivityCommentBinding binding;
    HubConnection connection;
    String sum_report_value = "";
    int index = 0;
    private MasterAdapter adapter;
    private ArrayList<CommentModel> arrCommentList = new ArrayList<>();
    private CommonMethods cmn = new CommonMethods();
    private String news_id = "", userid, Comment = "", Deleteid = "", item = "";
    private AlertDialog alertDialog, alertDialog1, alertDialog2;
    private String timediff = "", usertype;
    private String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1bmlxdWVfbmFtZSI6Ijc5NzhjMjI3LWViMGItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTYxYzMxYmIiLCJuYmYiOjE1MDYyODQ4NzMsImV4cCI6NDY2MTk1ODQ3MywiaWF0IjoxNTA2Mjg0ODczLCJpc3MiOiJCbGVuZCIsImF1ZCI6IkJsZW5kIn0.QUh241IB7g3axLcfmKR2899Kt1xrTInwT6BBszf6aP4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //     Objects.requireNonNull(getSupportActionBar()).setTitle("Lägg till kommentar");
        userid = cmn.getPrefsData(LiveCommentActivity.this, "id", "");
        usertype = cmn.getPrefsData(LiveCommentActivity.this, "usertype", "");
        if (userid.length() == 0) {
            binding.bottomBar.setVisibility(View.GONE);
            binding.userRecord.setText("Chatten är stängd");
        } else {
            binding.bottomBar.setVisibility(View.VISIBLE);
        }

        connection = new WebSocketHubConnectionP2("https://web.lusgo.se/NotificationHub?UserId=" + userid, authHeader);
        connection.addListener(LiveCommentActivity.this);
        connection.subscribeToEvent("SendComment", LiveCommentActivity.this);
        connection.connect();

        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        assert bundle != null;
        news_id = bundle.getString("news_id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new MasterAdapter(binding.commentsList, null, new AdapterView() {
            @NotNull
            @Override
            public RecyclerView.ViewHolder createChildView(@NotNull ViewGroup viewGroup, int viewType) {
                return new CommentsViewHolder(LayoutInflater.from(LiveCommentActivity.this).inflate(R.layout.item_comments, viewGroup, false));
            }

            @Override
            public void getChildView(@NotNull RecyclerView.ViewHolder viewHolder, int position) {
                ((CommentsViewHolder) viewHolder).setData((CommentModel) adapter.getData().get(position));
            }
        });
//        binding.commentsList.post(() -> {
//            try {
//                float y = binding.commentsList.getY() + binding.commentsList.getChildAt(9).getY();
//                binding.nestedScroll.smoothScrollTo(0, (int) y);
//                adapter.notifyDataSetChanged();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

        runOnUiThread(() ->
                loadData());

        binding.editMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                binding.editMessage.setHint("");
            else
                binding.editMessage.setHint("Skriv en kommentar");
        });

        binding.nestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                try {
                    index = index + 1;
                    loadDataPagination(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        binding.sendMessage.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.editMessage.getText()).toString().isEmpty()) {
                showToast("Skriv kommentar");
            } else {
                // Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_anim);
                // binding.sendMessage.startAnimation(animation);
                Comment = Html.toHtml(binding.editMessage.getText());
                Comment = Comment.replaceFirst("<u>", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                Comment = Comment.replaceAll("</u>", "");
                //    Comment = binding.editMessage.getText().toString();
                try {
                    connection.invoke("SendComment", userid, Comment, news_id);
                    binding.editMessage.setText("");
                } catch (Exception e) {
                    Toast.makeText(LiveCommentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void loadData() {
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_key", ConsURL.accessKey);
            jsonObject.put("user_id", cmn.getPrefsData(LiveCommentActivity.this, "id", ""));
            jsonObject.put("news_id", news_id);
            new NetworkCall(null, result -> {
                try {
                    if (result.isStatus()) {
                        arrCommentList.clear();
                        JSONArray jsonArray = new JSONArray(result.getData());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                arrCommentList.add((CommentModel) getObject(jsonArray.getJSONObject(i).toString(), CommentModel.class));
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
                    if (arrCommentList.isEmpty()) {
                        binding.userRecord.setVisibility(View.VISIBLE);
                        binding.commentsList.setVisibility(View.GONE);
                    } else {

                        binding.userRecord.setVisibility(View.GONE);
                        binding.commentsList.setVisibility(View.VISIBLE);
                        adapter.setData(arrCommentList);

                        //  binding.commentsList.scrollToPosition(arrCommentList.size() - 1);
                    }

                }
                return null;
            }, jsonObject.toString()).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "commentList");
        } catch (Exception e) {
            e.printStackTrace();
            adapter.setData(arrCommentList);
        }
    }

    private void loadDataPagination(int pageNo) {
        binding.progresBar.setVisibility(View.VISIBLE);
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("access_key", ConsURL.accessKey);
            jsonObject.put("user_id", cmn.getPrefsData(LiveCommentActivity.this, "id", ""));
            jsonObject.put("news_id", news_id);
            jsonObject.put("offset", String.valueOf(pageNo));
            new NetworkCall(null, result -> {
                try {
                    if (result.isStatus()) {
                        binding.progresBar.setVisibility(View.GONE);
                        JSONArray jsonArray = new JSONArray(result.getData());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                arrCommentList.add((CommentModel) getObject(jsonArray.getJSONObject(i).toString(), CommentModel.class));
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
                    if (arrCommentList.isEmpty()) {
                        binding.userRecord.setVisibility(View.VISIBLE);
                        binding.commentsList.setVisibility(View.GONE);
                    } else {

                        binding.userRecord.setVisibility(View.GONE);
                        binding.commentsList.setVisibility(View.VISIBLE);
                        adapter.setData(arrCommentList);
                        binding.commentsList.scrollToPosition(arrCommentList.size() - 1);
                    }
                }
                return null;
            }, jsonObject.toString()).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "commentList");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onMessage(HubMessage message) {
        hideKeyBoard();
          //loadData();
        if (message.getTarget().equals("ReceiveMessage")) {
            JsonElement[] object = message.getArguments();
            JsonElement o = object[0];

            JsonObject datObj = o.getAsJsonObject();
            String flag = datObj.get("flag").getAsString();
            if (flag.equals("Delete")) {
                String id = datObj.get("data").getAsString();
                for (CommentModel s : arrCommentList) {
                    if (s.getId().equals(id)) {
                        runOnUiThread(() -> {
                            arrCommentList.remove(s);
                            // Collections.reverse(arrCommentList);
                            adapter.setData(arrCommentList);
                            adapter.notifyDataSetChanged();
                            // binding.commentsList.smoothScrollToPosition(arrCommentList.size());
                        });


                    }
                }
            } else {
                runOnUiThread(() -> binding.editMessage.setText(""));

                JsonObject obj = datObj.getAsJsonObject("data");
                String created = obj.get("created").getAsString();
                String newsId = obj.get("news_id").getAsString();
                // JSONObject obj = new JSONObject(result.getData());
                CommentModel commentModel = new CommentModel();
                commentModel.setCreated(created);
                commentModel.setName(obj.get("name").getAsString());
                commentModel.setId(obj.get("id").getAsString());
                commentModel.setComment(obj.get("comment").getAsString());
                commentModel.setUser_id(obj.get("user_id").getAsString());
                commentModel.setProfile_image(obj.get("profile_image").getAsString());
                if (newsId.equals(news_id)) {
                    // arrCommentList.add(commentModel);
                    arrCommentList.add(0,commentModel);

                }
                runOnUiThread(() -> {
                    binding.userRecord.setVisibility(View.GONE);
                    binding.commentsList.setVisibility(View.VISIBLE);
                    adapter.setData(arrCommentList);
                    adapter.notifyDataSetChanged();
                   // loadData();
                    //binding.commentsList.smoothScrollToPosition(arrCommentList.size());
                });

            }

        }
    }

    @Override
    public void onError(Exception exception) {
        Log.d("checkmessges", exception.getMessage());
    }

    @Override
    public void onEventMessage(HubMessage message) {
        Log.d("checkmessges", message.getTarget());
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(context, HomePage.class));
        finish();
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
                connection.invoke("DeleteComment", Deleteid);
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
                if (rd_reason == null) {
                    cmn.showAlert("Välj på", this);
                } else {
                    getCommentReportAPI(commentid, reportTo, rd_reason.getText().toString(), "");

                }


            } else {
                Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton("Avbryt", (dialogInterface, i) ->
                alertDialog1.dismiss());
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
            object.put("user_id", cmn.getPrefsData(LiveCommentActivity.this, "id", ""));
            object.put("comment_id", Deleteid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    for (CommentModel s : arrCommentList) {
                        if (s.getId().equals(Deleteid)) {
                            arrCommentList.remove(s);
                            adapter.setData(arrCommentList);
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
            object.put("reportedBy", cmn.getPrefsData(LiveCommentActivity.this, "id", ""));
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
                    // commentModels.remove(pos);


                } else {
                    alertDialog2.dismiss();
                    cmn.showAlert("Du har redan rapporterat den här kommentaren",context);
                  //  Common.INSTANCE.showToast(context, "Du har redan rapporterat den här kommentaren");


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

    public void reprotMessage(String message, String message1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(message + "\n" + "Stötande ord är: \n" + "[" + message1 + "]");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {

            alertDialog.dismiss();

        });
        // builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        alertDialog = builder.create();
        alertDialog.show();


    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private TextView Name, Comment, CommentTime;
        private AvatarView profilePic;
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
            if (data.getName().trim().length() > 0) {
                String name = String.valueOf(data.getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(profilePic);
            }

            // Glide.with(LiveCommentActivity.this).load(data.getProfile_image()).placeholder(R.drawable.user_diff).into(profilePic);
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
                timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());


                CommentTime.setText(timediff);

                if (userid.equals(data.getUser_id())) {
                    Deletecomment.setVisibility(View.VISIBLE);

                } else {
                    Deletecomment.setVisibility(View.GONE);

                }
                if (usertype.equals(ConsURL.admin) || usertype.equals(ConsURL.sub_admin)) {
                    if (userid.equals(data.getUser_id())) {
                        Deletecomment.setVisibility(View.VISIBLE);

                    } else {
                        Deletecomment.setVisibility(View.GONE);
                    }

                    report.setVisibility(View.GONE);
                }
                if (usertype.equals(ConsURL.members) || usertype.equals(ConsURL.coach)) {
                    if (data.getUser_id().equals(userid)) {
                        report.setVisibility(View.GONE);
                    }else {
                        report.setVisibility(View.VISIBLE);

                    }
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
}