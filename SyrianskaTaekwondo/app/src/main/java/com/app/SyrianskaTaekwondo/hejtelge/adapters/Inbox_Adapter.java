package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.ImageShowDetails;
import com.app.SyrianskaTaekwondo.hejtelge.MessagePriviewActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.IsReadUserArr;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.views.TextDrawable;

public class Inbox_Adapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;

    private ArrayList<InboxList> horizontalList;
    private CommonMethods cmn;
    private String userid, usertype;
    private AlertDialog alertDialog;
    // The minimum amount of items to have below your current scroll position
// before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Activity context;
    private String timediff = "", flag;
    private List<IsReadUserArr> arr_readusers = new ArrayList<>();

    public Inbox_Adapter(ArrayList<InboxList> students, RecyclerView recyclerView, Activity context, String flag) {
        horizontalList = students;
        this.context = context;
        this.flag = flag;
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(context, "id", "");
        usertype = cmn.getPrefsData(context, "usertype", "");
        Locale loc = new Locale("sv", "SE");
        Locale.setDefault(loc);

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
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_adapter, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            ((StudentViewHolder) holder).setData(horizontalList.get(position));
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
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

    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_menu;
        ImageView imageView, doc, img, link, attach, img1;
        AvatarView profile;
        AppCompatTextView txtview, user, role, created_date, total_users, read_user, sentmsg, team_name;

        StudentViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.image);
            img = view.findViewById(R.id.img);
            doc = view.findViewById(R.id.doc);
            link = view.findViewById(R.id.link);
            sentmsg = view.findViewById(R.id.sentmsg);
            txtview = view.findViewById(R.id.message);
            user = view.findViewById(R.id.txt_user);
            role = view.findViewById(R.id.role_user);
            created_date = view.findViewById(R.id.created_date);
            ll_menu = view.findViewById(R.id.ll_inbox);
            total_users = view.findViewById(R.id.total_users);
            read_user = view.findViewById(R.id.read_user);
            profile = view.findViewById(R.id.profile);
            attach = view.findViewById(R.id.attach);
            team_name = view.findViewById(R.id.team_name);
            img1 = view.findViewById(R.id.img1);
        }

        void setData(InboxList data) {


            if (flag.equals("sent")) {
                if (data.getImages().size() > 0) {
                    attach.setVisibility(View.VISIBLE);
                } else {
                    attach.setVisibility(View.GONE);

                }
                String title = data.getMessage();
                if (title != null)
                    title = title.replaceAll("<p dir=\"ltr\">", "");
                title = title.replaceAll("<br>", "");
                // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                title = title.replaceAll("</p>", "");
                user.setTypeface(user.getTypeface(), Typeface.NORMAL);
        /*        if (title.length() > 22) {
                    title = title.substring(0, 22);
                    title = title.replaceFirst("<p dir=\"ltr\">", "");

                    txtview.setVisibility(View.GONE);

                    //   role.setPadding(10,10,10,10);
                    //  role.setGravity(Gravity.CENTER_VERTICAL);
                    role.setVisibility(View.VISIBLE);
                    user.setVisibility(View.VISIBLE);
                    role.setText(Html.fromHtml(title.trim() + "..."));
                    user.setText(Html.fromHtml(data.getName()));

                } else {
                    txtview.setVisibility(View.GONE);
                    //w user.setPadding(10, 10, 10, 10);
                    user.setGravity(Gravity.CENTER_VERTICAL);
                    role.setVisibility(View.VISIBLE);
                    user.setVisibility(View.VISIBLE);
                    role.setText(Html.fromHtml(title + "..."));
                    user.setText(Html.fromHtml(data.getName()));
                }*/
                user.setText(Html.fromHtml(data.getName()));

            } else {

                if (data.getImages().size() > 0) {
                    attach.setVisibility(View.VISIBLE);
                } else {
                    attach.setVisibility(View.GONE);

                }
                String team_namestr= data.getTeamName();
                if(team_namestr!=null) {
                    if (team_namestr.length() > 0) {
                        team_name.setText(team_namestr);
                    } else {
                        img1.setVisibility(View.GONE);
                        team_name.setVisibility(View.GONE);
                    }
                }else{
                    img1.setVisibility(View.GONE);
                    team_name.setVisibility(View.GONE);
                }
                txtview.setVisibility(View.GONE);
                role.setVisibility(View.VISIBLE);
                user.setVisibility(View.VISIBLE);
                user.setText(data.getName().trim());
                user.setTypeface(user.getTypeface(), Typeface.BOLD);
                String title = data.getMessage();
                if (title != null) {
                    title = title.replaceAll("<p dir=\"ltr\">", "");
                    // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                    title = title.replaceAll("<br>", "");
                    title = title.replaceAll("</p>", "");
                    title = title.replaceAll("</span>", "");
                    title = title.replaceAll("<span style=\"background-color:#669DF6;\">", "");
                  /*  if (title.length() > 22) {
                        title = title.substring(0, 22);
                        title = title.replaceFirst("<p dir=\"ltr\">", "");
                        role.setText(Html.fromHtml(title + "..."));


                    } else {*/
                        role.setText(Html.fromHtml(title));

                  //  }
                }
            }

            if (data.getName().length() > 0) {
                String name = String.valueOf((data.getName().trim().charAt(0)));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(data.getProfile_image())
                        .fitCenter()
                        .placeholder(drawable)
                        .into(profile);
            }


            if (userid.equals(data.getSender_id())) {
                sentmsg.setText("Skickat meddelande");
                sentmsg.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                sentmsg.setText("Inkorgmeddelande");
                sentmsg.setTextColor(context.getResources().getColor(R.color.yellow));

            }
            ArrayList<HashMap<String, String>> images = data.getImages();
            if (images.size() > 0) {
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.GONE);

            }
            if (data.getLink() != null) {
                if (data.getLink().length() > 0) {
                    link.setVisibility(View.VISIBLE);

                } else {
                    link.setVisibility(View.GONE);

                }
            } else {
                link.setVisibility(View.GONE);

            }
            //   read_user.setText("Läsa: " + data.getReadCount());
            //  total_users.setText("Totalt antal: " + data.getRead_users().size());

//            total_users.setOnClickListener(view -> {
//                if (data.getRead_users().size() > 0) {
//                    context.startActivity(new Intent(context, ReadUser_Message.class).putExtra("DATA", Common.INSTANCE.getBytes(data.getRead_users())));
//
//                }
//            });
            if (data.getDocument() != null) {
                if (data.getDocument().length() > 0) {
                    doc.setVisibility(View.VISIBLE);

                } else {
                    doc.setVisibility(View.GONE);

                }
            } else {
                doc.setVisibility(View.GONE);

            }

            img.setOnClickListener(view -> {
                ArrayList<String> arr_images = new ArrayList<>();
                for (HashMap map : images) {
                    arr_images.add(Objects.requireNonNull(map.get("name")).toString());
                }
                context.startActivity(new Intent(context, ImageShowDetails.class).putExtra("image", Common.INSTANCE.getJSON(images)).putExtra("position", "" + getPosition()));
                context.overridePendingTransition(R.anim.slide_in_up, 0);

            });
            //  link.setOnClickListener(view -> context.startActivity(new Intent(context, show_Messege.class).putExtra("Url", data.getLink())));
            //  doc.setOnClickListener(view -> context.startActivity(new Intent(context, ShowDocumentActivity.class).putExtra("Url", data.getDocument())));
      /*      String[] time_arr = data.getCreated().split(" ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = null;
            try {
                date = dateFormat.parse(time_arr[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long now = System.currentTimeMillis() - 1000;

            long result = TimeUnit.DAYS.convert((now - date.getTime()), TimeUnit.MILLISECONDS);

            updateLabel(created_date, result);*/

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date past = format.parse(data.getCreated());
                Date now = new Date();
                long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
              /*  if (days < 9 && days > 1) {
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
//                timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(past);

                //  timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                //    }

            } catch (Exception j) {
                j.printStackTrace();
            }
            created_date.setText(timediff);
            itemView.setOnClickListener(view -> {
                String id = data.getMessage_id();
                context.startActivity(new Intent(context, MessagePriviewActivity.class).putExtra("DATA", Common.INSTANCE.getJSON(data)).putExtra("id", id).putExtra("flag", flag));
                context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            });
         /*   if (!usertype.equals("2") || !usertype.equals("5")) {
                if (data.getIsRead().equals("1")||userid.equals(data.getSender_id())) {
                    ll_menu.setBackgroundColor(Color.parseColor("#ffffff"));

                } else {
                    ll_menu.setBackgroundColor(Color.parseColor("#e8f6fe"));
                }
            } else {
                ll_menu.setBackgroundColor(Color.parseColor("#ffffff"));

            }*/
        }

    }

    private void getreadAPI(String msgid, LinearLayout linearLayout) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("message_id", msgid);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "read_message");
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

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    private void openFile(String url) {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }


}




