package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.InboxFragmentBinding;
import com.google.android.material.tabs.TabLayout;
import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.IOnBackPressed;
import com.app.SyrianskaTaekwondo.hejtelge.SliderViewDemo;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Inbox_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.CampaignPojo;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.mipl.autoimageslider.IndicatorAnimations;
import com.mipl.autoimageslider.SliderAnimations;
import com.mipl.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vk.help.Common;
import vk.help.Fragment;

public class Inbox_Fragment extends Fragment implements IOnBackPressed {
    private CommonMethods cmn = new CommonMethods();
    private ArrayList<HashMap<String, String>> arr_img = new ArrayList<>();

    private ArrayList<InboxList> arr = new ArrayList<>();
    private Inbox_Adapter adapter;
    private String role, userid, teamid;
    private SliderViewDemo Imageadapter;
    private List<CampaignPojo> list_campaign = new ArrayList<>();
    private InboxFragmentBinding binding;

    public Inbox_Fragment() {
        // Required empty public constructor
    }

    public void onResume() {
        super.onResume();
        updateUI(binding);
        // ctx.registerReceiver(receiver, new IntentFilter("VK.NEW.NOTIFICATION"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateUI(InboxFragmentBinding binding) {
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
                binding.ffNews.setVisibility(View.GONE);
                binding.ffInbox.setVisibility(View.GONE);
                binding.ffEvent.setVisibility(View.GONE);
                binding.llAdd.setVisibility(View.GONE);
                //  ll_create.setVisibility(View.GONE);

            }
        } else {
            binding.llAdd.setVisibility(View.GONE);

        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = InboxFragmentBinding.inflate(inflater, container, false);
        role = cmn.getPrefsData(getActivity(), "usertype", "");
        userid = cmn.getPrefsData(getContext(), "id", "");
        teamid = cmn.getPrefsData(getContext(), "team_id", "");

        setupViewPager(binding.viewpager);
        // Set Tabs inside Toolbar
        binding.resultTabs.setupWithViewPager(binding.viewpager);
        if(role.equals(ConsURL.members)){
            String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");

            if(message_per.equals("1")) {
                for (int i = 0; i < binding.resultTabs.getTabCount(); i++) {
                    TabLayout.Tab tab = binding.resultTabs.getTabAt(0);
                    RelativeLayout relativeLayout = (RelativeLayout)
                            LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, binding.resultTabs, false);

                    TextView tabTextView = relativeLayout.findViewById(R.id.tab_title);
                    tabTextView.setText(tab.getText());
                    tabTextView.setAllCaps(false);
                    tab.setCustomView(relativeLayout);
                    tab.select();
                }

            }
        }else {
            for (int i = 0; i < binding.resultTabs.getTabCount(); i++) {
                TabLayout.Tab tab = binding.resultTabs.getTabAt(0);
                RelativeLayout relativeLayout = (RelativeLayout)
                        LayoutInflater.from(getActivity()).inflate(R.layout.tab_layout, binding.resultTabs, false);

                TextView tabTextView = relativeLayout.findViewById(R.id.tab_title);

                assert tab != null;
                tabTextView.setText(tab.getText());
                tabTextView.setAllCaps(false);
                tab.setCustomView(relativeLayout);
                tab.select();
            }
        }

        if (cmn.isOnline(getActivity())) {
            getCampaignAPI(binding);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
        }

        Imageadapter = new SliderViewDemo(getActivity(), arr_img);
        binding.imageSlider.setSliderAdapter(Imageadapter);

        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.NONE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        binding.imageSlider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        binding.imageSlider.setIndicatorSelectedColor(Color.BLUE);
        binding.imageSlider.setIndicatorUnselectedColor(Color.GRAY);
        binding.imageSlider.setAutoCycle(true);
    /*    if (userid.length() > 0) {

            if (!role.equals("4")) {
                binding.llAdd.setVisibility(View.VISIBLE);
            } else {
                binding.llAdd.setVisibility(View.GONE);

            }
        } else {
            binding.llAdd.setVisibility(View.GONE);

        }*/
        updateUI(binding);

        return binding.getRoot();
    }


    private void getCampaignAPI(InboxFragmentBinding binding) {
        list_campaign.clear();
        //     AddCampaign.img.clear();
        //arr.clear();
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String requestData;
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
                if (list_campaign.size() > 0) {
                    arr_img.addAll(list_campaign.get(0).getSponsers());
                } else {
                    binding.imageSlider.setVisibility(View.GONE);
                }
                Imageadapter.setCount(arr_img.size());
                Imageadapter.notifyDataSetChanged();
                binding.ffNews.setOnClickListener(view ->
                {
                    startActivity(new Intent(getActivity(), CreateNews.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                });
                binding.ffInbox.setOnClickListener(view -> {

                    startActivity(new Intent(getActivity(), CreateMessage.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
                binding.ffEvent.setOnClickListener(view -> {

                    startActivity(new Intent(getActivity(), CreateEvent.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });


                ;
    /* if (arr.size() > 0) {

                } else {
                    ll_norecord.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);

                }*/
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "Campaigns");
    }


    @Override
    public boolean onBackPressed() {

        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");

        if (role.equals(ConsURL.members)) {
            if (message_per.equals("1")) {
                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new SentInboxFragment(), "Skickat");
                adapter.addFragment(new InboxFragment_inner(), "Inkorg");

                viewPager.setAdapter(adapter);
            }else{
                Adapter adapter = new Adapter(getChildFragmentManager());
                adapter.addFragment(new InboxFragment_inner(), "Inkorg");

                viewPager.setAdapter(adapter);
            }
        } else {
            Adapter adapter = new Adapter(getChildFragmentManager());
            adapter.addFragment(new SentInboxFragment(), "Skickat");
            adapter.addFragment(new InboxFragment_inner(), "Inkorg");

            viewPager.setAdapter(adapter);
        }


    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
