package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.SliderViewDemo;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.NewsloadingAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.database.MuteNotify;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.News;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NewsApi;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import vk.help.Common;
import vk.help.Fragment;

public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView list;
    private FrameLayout ff_news, ff_inbox, ff_event;
    private CommonMethods cmn = new CommonMethods();
    private LinearLayout ll_create, ll_add;
    private ProgressBar dialog;
    private String teamid, userid, role, news_per, message_per, event_per, enableads, Is_Coach;
    private SliderView sliderView;
    // private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    private static int NUMBER_OF_ADS = 0;
    private List<News> mRecyclerViewItems = new ArrayList();
    private List<News> mRecyclerViewItems1 = new ArrayList();
    private ProgressBar progress_dialog;
    //  private AdLoader adLoader;
    private SwipeRefreshLayout swipe;
    LinearLayout llNoInternet;
    View viewline_news;
    NewsApi api;
    int index = 0, callApi = 0;
    private ArrayList<HashMap<String, String>> arr_img = new ArrayList<>();
    private SliderViewDemo adapter;
    NewsloadingAdapter adapterAds;
    private NestedScrollView nestedScroll;
    private List<CampaignPojo> list_campaign = new ArrayList<>();
    ArrayList<GroupList> arrGruopList = new ArrayList<>();
    MuteNotify muteTable;
    @Override
    public void onResume() {
        super.onResume();
        //  load(0);

        updateUI();

        // ctx.registerReceiver(receiver, new IntentFilter("VK.NEW.NOTIFICATION"));
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.news_fragment, container, false);

        userid = cmn.getPrefsData(getActivity(), "id", "");
        teamid = cmn.getPrefsData(getActivity(), "team_id", "");
        role = cmn.getPrefsData(getActivity(), "usertype", "");
        news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        enableads = cmn.getPrefsData(getActivity(), "enableads", "");
        muteTable = new MuteNotify(getActivity()).getInstance(getActivity());
        loadID(root);
        getGroupListAPI();
        updateUI();
        load(0);
        swipe.setOnRefreshListener(this);
        //     mNativeAds.clear();
        if (cmn.isOnline(getActivity())) {
            getCampaignAPI();

            nestedScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    try {
                        index = index + 1;
                        load1(index);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });

            //    list.setHasFixedSize(true);
            //load(index);

            //    api = ServiceGenerator.createService(NewsApi.class);

        }

        ff_news.setOnClickListener(view ->
        {
            startActivity(new Intent(getActivity(), CreateNews.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        });
        ff_inbox.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), CreateMessage.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        ff_event.setOnClickListener(view -> {

            startActivity(new Intent(getActivity(), CreateEvent.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        list.setHasFixedSize(true);
        assert getArguments() != null;
        String flag = getArguments().getString("flag");
        assert flag != null;

        ll_add.setVisibility(View.VISIBLE);


        return root;
    }


    public void loadID(View view) {
        list = view.findViewById(R.id.news_list);
        ff_inbox = view.findViewById(R.id.ff_inbox);
        ff_news = view.findViewById(R.id.ff_news);
        ff_event = view.findViewById(R.id.ff_event);
        ll_create = view.findViewById(R.id.ll_create);
        ll_add = view.findViewById(R.id.ll_show);
        ff_news = view.findViewById(R.id.ff_news);
        ff_event = view.findViewById(R.id.ff_event);
        ff_inbox = view.findViewById(R.id.ff_inbox);
        //    progressBar = view.findViewById(R.id.pp_dialog);
        progress_dialog = view.findViewById(R.id.progress_dialog);
        sliderView = view.findViewById(R.id.imageSlider);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        swipe = view.findViewById(R.id.swipe);
        llNoInternet = view.findViewById(R.id.llNoInternet);
        viewline_news = view.findViewById(R.id.viewline_news);

        adapter = new SliderViewDemo(getActivity(), arr_img);
        adapter.setCount(arr_img.size());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorVisibility(false);
        sliderView.setAutoCycle(true);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        list.setLayoutManager(layoutManager);
//        list.setAdapter(adapterAds);
    }

    private void load(int offset) {
        progress_dialog.setVisibility(View.VISIBLE);
        // ProgressDialog mprogdialog = ProgressDialog.show(getActivity(), "", "Vänta", true);
        // mprogdialog.setCancelable(false);
        String url = ConsURL.BASE_URL_TEST + "newsListGet?access_key=f76646abb2bb5408ecc6d8e36b64c9d8&user_id=" + userid + "&offset=" + offset + "&team_id=" + teamid;
        okhttp3.Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).get().build());
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        llNoInternet.setVisibility(View.VISIBLE));
                try {
                    viewline_news.setVisibility(View.GONE);
                    progress_dialog.setVisibility(View.GONE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.code() == 200) {

                        mRecyclerViewItems.clear();
                        res = Objects.requireNonNull(response.body()).string();

                        JSONObject objvalue = new JSONObject(res);
                        String status = objvalue.getString("status");
                        String message = objvalue.getString("message");
                        if (status.equals("true")) {
                            JSONArray obj = objvalue.getJSONArray("data");
                            for (int i = 0; i < obj.length(); i++) {
                                mRecyclerViewItems.add((News) (getObject(obj.getString(i), News.class)));

                            }
                        } else {
                            cmn.showAlert(message, getActivity());
                        }


                    } else {
                        cmn.showAlert("Serverfel", getActivity());

                    }

                } catch (Exception e) {
                    //  progress_dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }


                new Handler(Looper.getMainLooper()).post(() -> {

                    swipe.setRefreshing(false);
                    //progressBar.setVisibility(View.GONE);
                    // adapterAds.notifyDataChanged();
                    adapterAds = new NewsloadingAdapter(mRecyclerViewItems, userid, getActivity(), role);
                    list.setLayoutManager(new LinearLayoutManager(getActivity()));
                    list.setAdapter(adapterAds);
                    progress_dialog.setVisibility(View.GONE);
                    //    callApi++;
                });
            }
        });


    }

    private void load1(int offset) {
        progress_dialog.setVisibility(View.VISIBLE);
        String url = ConsURL.BASE_URL_TEST + "newsListGet?access_key=f76646abb2bb5408ecc6d8e36b64c9d8&user_id=" + userid + "&offset=" + offset + "&team_id=" + teamid;
        okhttp3.Call call = new OkHttpClient.Builder().build().newCall(new okhttp3.Request.Builder().addHeader("Accept-Encoding", "identity").url(url).get().build());
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
                progress_dialog.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) {
                try {
                    String res = "";
                    if (response.code() == 200) {
                        res = Objects.requireNonNull(response.body()).string();
                        JSONObject objvalue = new JSONObject(res);
                        JSONArray obj = objvalue.getJSONArray("data");
                        for (int i = 0; i < obj.length(); i++) {

                            mRecyclerViewItems.add((News) (getObject(obj.getString(i), News.class)));
                            new Handler(Looper.getMainLooper()).post(() -> {
                                swipe.setRefreshing(false);
                                adapterAds = new NewsloadingAdapter(mRecyclerViewItems, userid, getActivity(), role);
                                list.setLayoutManager(new LinearLayoutManager(getActivity()));
                                list.setAdapter(adapterAds);
                                progress_dialog.setVisibility(View.GONE);
                            });

                        }

                    }

                } catch (Exception e) {
                    progress_dialog.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    progress_dialog.setVisibility(View.GONE);
                });


            }
        });


    }

    private void getCampaignAPI() {
        list_campaign.clear();
        //     AddCampaign.img.clear();
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        list_campaign.add((CampaignPojo) (Common.INSTANCE.getObject(obj.getString(i), CampaignPojo.class)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                arr_img.clear();
                if (!list_campaign.isEmpty()) {
                    if (list_campaign.get(0) != null && list_campaign.get(0).getSponsers() != null) {
                        arr_img.addAll(list_campaign.get(0).getSponsers());
                    }
                } else {
                    sliderView.setVisibility(View.GONE);
                }
                adapter.setCount(arr_img.size());
                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Campaigns");
    }

    private void getGroupListAPI() {
        String requestData;
        arrGruopList.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 1000);
            object.put("offset", 0);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        arrGruopList.add((GroupList) (getObject(obj.getString(i), GroupList.class)));
                        if (!muteTable.CheckIsDataAlreadyInDBorNot(arrGruopList.get(i).getGroup_id())) {
                            muteTable.insertdata(getActivity(), arrGruopList.get(i).getGroup_id(), userid, "true");

                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "groupListMember");
    }


    public void updateUI() {
        news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        Is_Coach = cmn.getPrefsData(getActivity(), "Is_Coach", "");
        if (userid.length() > 0) {
            if (Is_Coach.equals("true")) {
                ff_news.setVisibility(View.VISIBLE);
                ff_inbox.setVisibility(View.VISIBLE);
                ff_event.setVisibility(View.VISIBLE);
                ll_create.setVisibility(View.VISIBLE);
            } else {
                ff_news.setVisibility(View.GONE);
                ff_inbox.setVisibility(View.GONE);
                ff_event.setVisibility(View.GONE);
                ll_create.setVisibility(View.GONE);

            }/*if (!role.equals("4")) {
                ff_news.setVisibility(View.VISIBLE);
                ff_inbox.setVisibility(View.VISIBLE);
                ff_event.setVisibility(View.VISIBLE);
                ll_create.setVisibility(View.VISIBLE);
            } else {
                ff_news.setVisibility(View.GONE);
                ff_inbox.setVisibility(View.GONE);
                ff_event.setVisibility(View.GONE);
                ll_create.setVisibility(View.GONE);

            }*/
        } else {
            ll_create.setVisibility(View.GONE);

        }
    }

    @Override
    public void onRefresh() {
        updateUI();
        getGroupListAPI();
        callApi = 0;
        load(0);

    }
}
