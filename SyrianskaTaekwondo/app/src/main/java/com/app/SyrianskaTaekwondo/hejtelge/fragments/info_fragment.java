package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.InfoFragmentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class info_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final Object REQUEST_LOCATION = 101;
    private LinearLayout ll_contact, ll_website, ll_email;
    private CommonMethods cmn = new CommonMethods();

    private LinearLayout ll_create;
    private String userid, role;
    private String[] permissionsRequired = new String[]{Manifest.permission.CALL_PHONE,
    };
    private InfoFragmentBinding binding;
    private SwipeRefreshLayout swipe;

    public info_fragment() {

        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void updateUI() {
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


            }
        } else {
            binding.llAdd.setVisibility(View.GONE);

        }
    }


    private void setupViewPager(ViewPager viewPager) {

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new InfoAbout_fragment(), "Info");
        adapter.addFragment(new Link_fragment(), "Länkar");
        adapter.addFragment(new Doc_fragment(), "Dokument");
        adapter.addFragment(new Contact_fragment(), "Kontakt");
        viewPager.setAdapter(adapter);


    }


    class Adapter extends FragmentPagerAdapter {
        private final List<vk.help.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public vk.help.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(vk.help.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = InfoFragmentBinding.inflate(getLayoutInflater());
        View root = inflater.inflate(R.layout.info_fragment, container, false);
        // loadID(root);
        userid = cmn.getPrefsData(getActivity(), "id", "");
        role = cmn.getPrefsData(getActivity(), "usertype", "");


        String news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        String event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        setupViewPager(binding.viewpager);

        binding.resultTabs.setupWithViewPager(binding.viewpager);

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
        return binding.getRoot();
    }


    public void onResume() {
        super.onResume();
        updateUI();
        // ctx.registerReceiver(receiver, new IntentFilter("VK.NEW.NOTIFICATION"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            //     callIntent.setData(Uri.parse("tel:" + phone));//change the number
            startActivity(callIntent);
        } else {

            // We were not granted permission this time, so don't try to show the contact picker
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onRefresh() {
        //   getUserAPI();
        updateUI();


    }
}
