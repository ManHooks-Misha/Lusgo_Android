package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.AcceptInvite;
import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.Eventshow;
import com.app.SyrianskaTaekwondo.hejtelge.MessagePriviewActivity;
import com.app.SyrianskaTaekwondo.hejtelge.NewsViewList;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Report_detailsActivity;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.OnLoadMoreListener;
import com.app.SyrianskaTaekwondo.hejtelge.database.NotificationTable;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.NotificationFragmentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Notification;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.Common;
import vk.help.Fragment;
import vk.help.views.TextDrawable;

public class Notification_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Notification_Adapter adapter;
    private String userid, usertype;
    private ArrayList<Notification> arr = new ArrayList<>();
    private NotificationTable table;
    NotificationFragmentBinding binding;
    private CommonMethods cmn;

    public Notification_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateUI(NotificationFragmentBinding binding) {
        String news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        String event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        String Is_Coach = cmn.getPrefsData(getActivity(), "Is_Coach", "");

        if (userid.length() > 0) {
            if (Is_Coach.equals("true")) {
                binding.ffNews.setVisibility(View.VISIBLE);
                binding.ffInbox.setVisibility(View.VISIBLE);
                binding.ffEvent.setVisibility(View.VISIBLE);
                binding.llAdd.setVisibility(View.VISIBLE);
            } else {
               /* if (news_per.equals("1")) {
                    binding.ffNews.setVisibility(View.VISIBLE);

                } else {
                    binding.ffNews.setVisibility(View.GONE);

                }
                if (message_per.equals("1")) {
                    binding.ffInbox.setVisibility(View.VISIBLE);

                } else {
                    binding.ffInbox.setVisibility(View.GONE);

                }
                if (event_per.equals("1")) {
                    binding.ffEvent.setVisibility(View.VISIBLE);

                } else {
                    binding.ffEvent.setVisibility(View.GONE);

                }
                if (news_per.equals("0") && message_per.equals("0") && event_per.equals("0")) {
                    binding.llAdd.setVisibility(View.GONE);

                }*/

                binding.ffNews.setVisibility(View.GONE);
                binding.ffInbox.setVisibility(View.GONE);
                binding.ffEvent.setVisibility(View.GONE);
                binding.llAdd.setVisibility(View.GONE);
                //  ll_create.setVisibility(View.GONE);

            }
        }
        else {

            binding.llAdd.setVisibility(View.GONE);

        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = NotificationFragmentBinding.inflate(inflater, container, false);
        loadID(binding);
        table = new NotificationTable(getActivity()).getInstance(getActivity());
        cmn = new CommonMethods();
        userid = cmn.getPrefsData(getContext(), "id", "");
        usertype = cmn.getPrefsData(getContext(), "usertype", "");
        if (cmn.isOnline(getActivity())) {
            arr.clear();
            getUserAPI(binding);

        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
        }
        binding.notifySwipe.setOnRefreshListener(this);

        /*if (userid.length() > 0) {
            if (!usertype.equals("4")) {
                binding.llAdd.setVisibility(View.VISIBLE);
            } else {
                binding.llAdd.setVisibility(View.GONE);

            }
        } else {
            binding.llAdd.setVisibility(View.GONE);

        }
*/
        updateUI(binding);

        String news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        String event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        if (userid.length() > 0) {
            if (!usertype.equals("4")) {
                binding.ffNews.setVisibility(View.VISIBLE);
                binding.ffInbox.setVisibility(View.VISIBLE);
                binding.ffEvent.setVisibility(View.VISIBLE);
                binding.llAdd.setVisibility(View.VISIBLE);
            } else {
                if (news_per.equals("1")) {
                    binding.ffNews.setVisibility(View.VISIBLE);

                } else {
                    binding.ffNews.setVisibility(View.GONE);

                }
                if (message_per.equals("1")) {
                    binding.ffInbox.setVisibility(View.VISIBLE);

                } else {
                    binding.ffInbox.setVisibility(View.GONE);

                }
                if (event_per.equals("1")) {
                    binding.ffEvent.setVisibility(View.VISIBLE);

                } else {
                    binding.ffEvent.setVisibility(View.GONE);

                }
                if (news_per.equals("0") && message_per.equals("0") && event_per.equals("0")) {
                    binding.llAdd.setVisibility(View.GONE);

                }
                //  ll_create.setVisibility(View.GONE)

            }
        } else {
            binding.llAdd.setVisibility(View.GONE);

        }
        binding.ffNews.setOnClickListener(view -> startActivity(new Intent(getActivity(), CreateNews.class)));
        binding.ffInbox.setOnClickListener(view -> startActivity(new Intent(getActivity(), CreateMessage.class)));
        binding.ffEvent.setOnClickListener(view -> startActivity(new Intent(getActivity(), CreateEvent.class)));


        return binding.getRoot();
    }

    public void loadID(NotificationFragmentBinding view) {
      /*  list = view.findViewById(R.id.inbox_list);
        ff_inbox = view.findViewById(R.id.ff_inbox);
        ff_news = view.findViewById(R.id.ff_news);
        ff_event = view.findViewById(R.id.ff_event);
*/
        adapter = new Notification_Adapter(arr, view.notificationList, getActivity());
        view.notificationList.setAdapter(adapter);
        adapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            adapter.notifyItemInserted(arr.size() - 1);
            view.notificationList.postDelayed(() -> {
                int end = arr.size() + 1;
                getRecursionAPI(String.valueOf(end));
            }, 2000);
        });

    }


    private void getRecursionAPI(String offset) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", offset);
            object.put("user_id", userid);
//            object.put("user_id", userid);
//            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                int position = arr.size() - 1;
                arr.remove(position);
                adapter.notifyItemRemoved(position);
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject objnotification = obj.getJSONObject(i);
                        String nid = objnotification.getString("n_id");

                        arr.add((Notification) (getObject(obj.getString(i), Notification.class)));
//                        if (!check_notif) {
//                            table.insertdataData(getActivity(), nid);
//                        }
                    }

                    if (obj.length() > 0) {
                        adapter.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //  mAdapter.setLoaded();
                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "notificationList");
    }


    public void onResume() {
        super.onResume();
        updateUI(binding);
        getUserAPI(binding);
        // ctx.registerReceiver(receiver, new IntentFilter("VK.NEW.NOTIFICATION"));
    }


    @Override
    public void onPause() {
        super.onPause();
        //    ctx.unregisterReceiver(receiver);
    }

    private void getUserAPI(NotificationFragmentBinding binding) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "1000");
            object.put("offset", "0");
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(binding.ppDialog, result -> {
            try {
                if (result.isStatus()) {
                    arr.clear();
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject objnotification = obj.getJSONObject(i);
                        String nid = objnotification.getString("n_id");
                        boolean check_notif = table.CheckIsDataAlreadyInDBorNot(nid);

                        arr.add((Notification) (getObject(obj.getString(i), Notification.class)));
                        if (!check_notif) {
                            table.insertdataData(getActivity(), nid);
                        }
                    }
                    if (obj.length() > 0) {
                        adapter.setLoaded();
                        binding.notificationList.setVisibility(View.VISIBLE);
                        binding.txtShow.setVisibility(View.GONE);
                        binding.notifySwipe.setRefreshing(false);

                    } else {
                        binding.notificationList.setVisibility(View.GONE);
                        binding.txtShow.setVisibility(View.VISIBLE);
                        binding.notifySwipe.setRefreshing(false);

                    }
                }
            } catch (Exception e) {
                binding.notifySwipe.setRefreshing(false);

                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                binding.notifySwipe.setRefreshing(false);

                binding.llData.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "notificationList");
    }

    @Override
    public void onRefresh() {
        getUserAPI(binding);
    }

    public class Notification_Adapter extends RecyclerView.Adapter {

        private final int VIEW_ITEM = 1;

        private ArrayList<Notification> horizontalList;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private AlertDialog alertDialog;
        private OnLoadMoreListener onLoadMoreListener;
        // private ArrayList<HashMap<String, String>> filtedlist;
        private Activity context;
        private String timediff = "", userid;
        // private NotificationTable table;


        public Notification_Adapter(ArrayList<Notification> students, RecyclerView recyclerView, Activity context) {
            horizontalList = students;
            this.context = context;
            userid = new CommonMethods().getPrefsData(context, "id", "");
            Locale loc = new Locale("sv", "SE");
            Locale.setDefault(loc);

            //table = new NotificationTable(context).getInstance(context);
            //filtedlist = table.getDataNotificationData();

            // filtedlist=students;
/*
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
*/
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
                return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adapter, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Notification_Adapter.StudentViewHolder) {
                ((StudentViewHolder) holder).setData(horizontalList.get(position), position);
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
            private AvatarView img;
            private LinearLayout deletenotification, btn_invite,llColor;
            private TextView txtview, time;
            private ImageView notification_show;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.message);
                time = view.findViewById(R.id.notificationtime);
                img = view.findViewById(R.id.profile_img);
                deletenotification = view.findViewById(R.id.deletenotification);
                notification_show = view.findViewById(R.id.notification_show);
                llColor = view.findViewById(R.id.ll_notification);
                btn_invite = view.findViewById(R.id.btn_invite);
            }

            void setData(Notification data, int pos) {
                //time.setText(data.getCreated());
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

                }*/ //else {
                    // timediff = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(past);//  String.format("%s-%s-%s",past.getDate(),past.getMonth(),past.getYear());
                    timediff = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(past);
                    //    }

                } catch (Exception j) {
                    j.printStackTrace();
                }

                if (data.getType().equals("message")) {
                    time.setText(String.format("%s ", timediff));
                    if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                        txtview.setText("Du skickade ett meddelande");
                    } else {
                        txtview.setText(String.format("%s %s", data.getName().trim(), " skickade ett meddelande"));
                    }

                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.round_massege);
                }
                if (data.getType().equals("event")) {
                    time.setText(String.format("%s", timediff));
                    if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                        txtview.setText("Du skickade en inbjudan");
                    } else {
                        txtview.setText(String.format("%s %s", data.getName().trim(), "bjöd in dig till ett evenemang"));
                    }
                    btn_invite.setVisibility(View.GONE);
                    notification_show.setImageResource(R.drawable.round_calendar);

                }
                if (data.getType().equals("news")) {
                    time.setText(String.format("%s", timediff));
                    if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                        txtview.setText("Du skriver ett inlägg");
                    } else {
                        txtview.setText(String.format("%s %s", data.getName().trim(), " skriva ett inlägg"));
                    }
                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.post);

                }
                if (data.getType().equals("permission")) {
                    time.setText(String.format("%s", timediff));
                    if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                        txtview.setText("Du har gett tillstånd");
                    } else {
                        txtview.setText(String.format("%s %s", data.getName().trim(), " har gett dig ledarbehörighet"));
                    }
                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.privacy);

                }
                if (data.getType().equals("delete_event")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText("Det här evenemanget är inte längre tillgängligt.");
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.round_calendar);

                }
                if (data.getType().equals("reported_words")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText(data.getMessage());
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.flag);

                }
                if (data.getType().equals("delete_comment_ok")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText(data.getMessage());
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    btn_invite.setVisibility(View.GONE);

                    notification_show.setImageResource(R.drawable.flag);

                }

                if (data.getType().equals("delete_comment")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText(data.getMessage());
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    btn_invite.setVisibility(View.GONE);
                    notification_show.setImageResource(R.drawable.flag);

                }
                if (data.getType().equals("invite_user")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText(data.getMessage());
                    btn_invite.setVisibility(View.GONE);
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    notification_show.setImageResource(R.drawable.flag_invite);

                }
                if (data.getType().equals("cron_report")) {
                    time.setText(String.format("%s", timediff));
                    // if (new CommonMethods().getPrefsData(context, "id", "").equals(data.getSender())) {
                    txtview.setText(data.getMessage());
//                } else {
//                    txtview.setText(String.format("%s %s", data.getName().trim(), " här evenemanget är inte längre tillgängligt."));
//                }
                    btn_invite.setVisibility(View.GONE);
                    notification_show.setImageResource(R.drawable.flag);

                }
         /*   itemView.setOnClickListener(view -> {
                itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                String id = data.getN_id();
                //table.update(1, id);
            });
*/

                deletenotification.setOnClickListener(v -> {
                    final BottomSheetDialog bt = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                    View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet, null);
                    view.findViewById(R.id.deleteLayout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert(data.getN_id(), pos);
                            bt.dismiss();
                        }
                    });

                    TextView name = view.findViewById(R.id.title);
                    name.setText(txtview.getText().toString());
                    AvatarView imageView = view.findViewById(R.id.img_profile);
                    String name1 = String.valueOf(horizontalList.get(pos).getName().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name1.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(horizontalList.get(pos).getProfile_image())
                            .centerCrop()
                            .placeholder(drawable)
                            .into(imageView);
                    // Glide.with(context).load(data.getProfile_image()).into(imageView);

                    bt.setContentView(view);
                    bt.show();
                });
//            deletenotification.setOnClickListener(v -> {
//
//               /* BottomSheetDialogFragment bottomSheetDialogFragment = new NotificationBottomSheet();
//                bottomSheetDialogFragment.show(((FragmentActivity)context).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());*/
//               //
//            });

                if (data.getIsRead().equals("1")){
                    llColor.setBackgroundColor(Color.parseColor("#ffffff"));

                }else {
                    llColor.setBackgroundColor(Color.parseColor("#e8f6fe"));
                }


                itemView.setOnClickListener(view -> {
                    ColorDrawable viewColor = (ColorDrawable) itemView.getBackground();
                    int colorId = viewColor.getColor();
                    String id = data.getN_id();
                    String sid = data.getSn_id();
                    String type = data.getType();

//                    if (colorId == -1509634)
//                        itemView.setBackgroundColor(Color.parseColor("#ffffff"));

                    if (new CommonMethods().isOnline(context)) {
                      //  itemView.setBackgroundColor(Color.parseColor("#ffffff"));

                        getreadAPI(id,llColor);
                    } else {
                        Common.INSTANCE.showToast(context, context.getResources().getString(R.string.internet));
                    }
                    if (type.equals("message")) {
                        context.startActivity(new Intent(context, MessagePriviewActivity.class).putExtra("id", sid).putExtra("flag", "inbox"));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else if (type.equals("event")) {

                        context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid).putExtra("type", data.getType()));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("reported_words")) {

                        context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", sid).putExtra("type","Notfication"));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("delete_comment")) {

                        context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", sid).putExtra("type","Notfication"));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("delete_comment_ok")) {

                        context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", sid).putExtra("type","Notfication"));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("cron_report")) {

                        context.startActivity(new Intent(context, Report_detailsActivity.class).putExtra("report_id", sid).putExtra("type","Notfication"));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("delete_event")) {
                        Common.INSTANCE.showToast(context, "Det här evenemanget är inte längre tillgängligt.");
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else if (type.equals("invite_user")) {
                        context.startActivity(new Intent(context, AcceptInvite.class).putExtra("team_id", sid).putExtra("coach_id", data.getSender()).putExtra("status", data.getAccepted()));

                        // Common.INSTANCE.showToast(context, "Det här evenemanget är inte längre tillgängligt.");
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        //  context.startActivity(new Intent(context, Eventshow.class).putExtra("id", sid));
                    } else {
                        context.startActivity(new Intent(context, NewsViewList.class).putExtra("nid", id).putExtra("type", data.getType()));
                        context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }
                });

                if (data.getName().trim().length() > 0) {
                    if (data.getName().trim().length() > 0) {
                        String name = String.valueOf(data.getName().trim().charAt(0));
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                        Glide.with(itemView).load(data.getProfile_image()).centerCrop().placeholder(drawable).into(img);
                    }
                }


            }
        }


        private void getreadAPI(String nid, LinearLayout linearLayout) {
            String requestData;
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Accept-Encoding", "identity"
            );
            try {
                JSONObject object = new JSONObject();
                object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
                object.put("nid", nid);
                object.put("user_id", userid);
                requestData = object.toString();
            } catch (Exception e) {
                e.printStackTrace();
                requestData = "";
            }

            new NetworkCall(null, result -> {
                try {
                    if (result.isStatus()) {
                        linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "read_notification");
        }

        private void getNDeleteAPI(String nid, int pos) {


            String requestData;
            HashMap<String, String> map1 = new HashMap<>();
            map1.put("Accept-Encoding", "identity"
            );
            try {
                JSONObject object = new JSONObject();
                object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
                object.put("notification_id", nid);
                object.put("user_id", userid);
                requestData = object.toString();
            } catch (Exception e) {
                e.printStackTrace();
                requestData = "";
            }

            new NetworkCall(null, result -> {
                try {
                    if (result.isStatus()) {
                        horizontalList.remove(pos);

                        adapter = new Notification_Adapter(horizontalList, binding.notificationList, getActivity());
                        binding.notificationList.setAdapter(adapter);

                        notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "delete_notification");
        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }

        private void alert(String nid, int pos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setMessage("Är du säker på att du vill ta bort denna avisering");
            builder.setPositiveButton("Ja", (dialogInterface, i) -> {

                if (new CommonMethods().isOnline(context)) {

                    getNDeleteAPI(nid, pos);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
                }

            });
            builder.setNegativeButton("Avbryt", (dialogInterface, i) -> {
                alertDialog.dismiss();
            });
            alertDialog = builder.create();
            alertDialog.show();
        }


    }


}
