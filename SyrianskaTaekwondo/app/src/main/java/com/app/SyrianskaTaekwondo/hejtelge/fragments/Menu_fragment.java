package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.CreateEvent;
import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.CreateNews;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Menu_adapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Menu_model;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu_fragment extends Fragment {
    private RecyclerView list;
    private String usertype, userid;
    private CircleImageView profile;
    private AppCompatTextView txt_name;
    private CommonMethods cmn = new CommonMethods();
    private FrameLayout ff_news, ff_inbox, ff_event;
    private LinearLayout ll_create;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.menu_fragment, container, false);
        loadID(root);
        usertype = cmn.getPrefsData(getActivity(), "usertype", "");
        userid = cmn.getPrefsData(getActivity(), "id", "");

        List<Menu_model> libraryHotModels = LibraryHotMethod();
        Menu_adapter libraryHotAdapter = new Menu_adapter(libraryHotModels, getActivity(), usertype);
        list.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        list.setAdapter(libraryHotAdapter);
        String imgpath = cmn.getPrefsData(getActivity(), "imagepath", "");
        String fst_name = cmn.getPrefsData(getActivity(), "firstname", "");
        String last = cmn.getPrefsData(getActivity(), "lastname", "");
        txt_name.setText(fst_name + " " + last);

        Glide.with(Objects.requireNonNull(getActivity()))
                .load(imgpath)
                .centerCrop()
                .placeholder(R.drawable.user_profile)
                .into(profile);

        updateUI();
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
        return root;


    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
//        getActivity().sendBroadcast(new Intent(HomePage.NOTIFY_Permission));

        // ctx.registerReceiver(receiver, new IntentFilter("VK.NEW.NOTIFICATION"));
    }

    private void updateUI() {
        String news_per = cmn.getPrefsData(getActivity(), "news_per", "");
        String message_per = cmn.getPrefsData(getActivity(), "msg_per", "");
        String event_per = cmn.getPrefsData(getActivity(), "event_per", "");
        String Is_Coach = cmn.getPrefsData(getActivity(), "Is_Coach", "");

        if (userid.length() > 0) {
            if (Is_Coach.equals("true")) {
                ff_news.setVisibility(View.VISIBLE);
                ff_inbox.setVisibility(View.VISIBLE);
                ff_event.setVisibility(View.VISIBLE);
                ll_create.setVisibility(View.VISIBLE);
            } else {
               /* if (news_per.equals("1")) {
                    ff_news.setVisibility(View.VISIBLE);

                } else {
                    ff_news.setVisibility(View.GONE);
                }
                if (message_per.equals("1")) {
                    ff_inbox.setVisibility(View.VISIBLE);

                } else {
                    ff_inbox.setVisibility(View.GONE);

                }
                if (event_per.equals("1")) {
                    ff_event.setVisibility(View.VISIBLE);

                } else {
                    ff_event.setVisibility(View.GONE);

                }
                if (news_per.equals("0") && message_per.equals("0") && event_per.equals("0")) {
                    ll_create.setVisibility(View.GONE);

                }*/
                ff_news.setVisibility(View.GONE);
                ff_inbox.setVisibility(View.GONE);
                ff_event.setVisibility(View.GONE);
                ll_create.setVisibility(View.GONE);


            }
        } else {
            ll_create.setVisibility(View.GONE);

        }
    }

    public void loadID(View view) {
        list = view.findViewById(R.id.list_menu);
        txt_name = view.findViewById(R.id.txt_name);
        profile = view.findViewById(R.id.profile_img);
        ff_inbox = view.findViewById(R.id.ff_inbox);
        ff_news = view.findViewById(R.id.ff_news);
        ff_event = view.findViewById(R.id.ff_event);
        ll_create = view.findViewById(R.id.ll_create);

    }


    private List<Menu_model> LibraryHotMethod() {

        List<Menu_model> data = new ArrayList<>();
        if (usertype.equals("4")) {
//            data.add(new Menu_model(R.drawable.group_list, "Grupper"));
            // data.add(new Menu_model(R.drawable.password_change, "Ändra lösenord"));
          //  data.add(new Menu_model(R.drawable.team, "Byt team"));

            data.add(new Menu_model(R.drawable.logout, "Logga ut"));

        } else if (usertype.equals("3")) {
            data.add(new Menu_model(R.drawable.invite, "Skicka inbjudan"));
            data.add(new Menu_model(R.drawable.team, "Teamlista"));
            data.add(new Menu_model(R.drawable.invitation_list, "Inbjudningslista"));
            data.add(new Menu_model(R.drawable.group_list, "Grupper"));

            data.add(new Menu_model(R.drawable.user_list, "Användare"));
//            data.add(new Menu_model(R.drawable.password_change, "Ändra lösenord"));
        //    data.add(new Menu_model(R.drawable.team, "Byt team"));

            data.add(new Menu_model(R.drawable.logout, "Logga ut"));

        } else if (usertype.equals("6")) {
            data.add(new Menu_model(R.drawable.group_list, "Grupper"));

            data.add(new Menu_model(R.drawable.user_list, "Användare"));
       //     data.add(new Menu_model(R.drawable.team, "Byt team"));

            data.add(new Menu_model(R.drawable.logout, "Logga ut"));

        } else if (usertype.equals("5")) {
            data.add(new Menu_model(R.drawable.information_new, "Uppdatera information"));

            data.add(new Menu_model(R.drawable.invite, "Skicka inbjudan"));
            data.add(new Menu_model(R.drawable.team, "Teamlista"));

            data.add(new Menu_model(R.drawable.user_list, "Användare"));

            data.add(new Menu_model(R.drawable.esponser, "Skapa sponsor"));

            data.add(new Menu_model(R.drawable.group_list, "Grupper"));
            data.add(new Menu_model(R.drawable.invitation_list, "Inbjudningslista"));
            data.add(new Menu_model(R.drawable.add_campaign, "Sponsorkampanj"));
//            data.add(new Menu_model(R.drawable.campaign_list, "Kampanjlista"));
              data.add(new Menu_model(R.drawable.report, "Rapporter"));
            data.add(new Menu_model(R.drawable.terms, "Villkor för app"));

            //   data.add(new Menu_model(R.drawable.password_change, "Ändra lösenord"));

            data.add(new Menu_model(R.drawable.logout, "Logga ut"));
        } else {
            data.add(new Menu_model(R.drawable.information_new, "Uppdatera information"));

            data.add(new Menu_model(R.drawable.invite, "Skicka inbjudan"));
            data.add(new Menu_model(R.drawable.team, "Teamlista"));

            data.add(new Menu_model(R.drawable.user_list, "Användare"));

            data.add(new Menu_model(R.drawable.esponser, "Skapa sponsor"));

            data.add(new Menu_model(R.drawable.group_list, "Grupper"));

            data.add(new Menu_model(R.drawable.invitation_list, "Inbjudningslista"));
            data.add(new Menu_model(R.drawable.add_campaign, "Sponsorkampanj"));
            data.add(new Menu_model(R.drawable.report, "Rapporter"));
            data.add(new Menu_model(R.drawable.terms, "Villkor för app"));
//            data.add(new Menu_model(R.drawable.campaign_list, "Kampanjlista"));

            //   data.add(new Menu_model(R.drawable.password_change, "Ändra lösenord"));

            data.add(new Menu_model(R.drawable.logout, "Logga ut"));
        }
        return data;
    }
}
