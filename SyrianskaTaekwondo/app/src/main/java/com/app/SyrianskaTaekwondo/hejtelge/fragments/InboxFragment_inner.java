package com.app.SyrianskaTaekwondo.hejtelge.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.Inbox_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.InboxFragmentInnerBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.InboxList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import vk.help.Fragment;

public class InboxFragment_inner extends Fragment {
    private Inbox_Adapter adapter;
    private CommonMethods cmn;
    private InboxFragmentInnerBinding binding;
    private String role, userid, teamid;
    private ArrayList<InboxList> arr = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = InboxFragmentInnerBinding.inflate(getLayoutInflater());
        cmn = new CommonMethods();
        role = cmn.getPrefsData(getActivity(), "usertype", "");
        userid = cmn.getPrefsData(getContext(), "id", "");
        teamid = cmn.getPrefsData(getContext(), "team_id", "");

        if (cmn.isOnline(getActivity())) {
            getUserAPI();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.internet), Toast.LENGTH_SHORT).show();
        }
        adapter = new Inbox_Adapter(arr, binding.inboxlist1, getActivity(), "inbox");
        binding.inboxlist1.setAdapter(adapter);


        adapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            adapter.notifyItemInserted(arr.size() - 1);
            binding.inboxlist1.postDelayed(() -> {
                int end = arr.size() ;
                int limit=arr.size() + 20;
                getRecursionAPI(String.valueOf(end),String.valueOf(limit));
            }, 2000);
        });
        return binding.getRoot();

    }

    private void getUserAPI() {
        arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", "100");
            object.put("offset", "0");
            object.put("user_id", userid);
            object.put("team_id", "");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(binding.ppDialog, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        if (!object.getString("senderuserid").equals(userid)) {
                            arr.add((InboxList) (getObject(obj.getString(i), InboxList.class)));
                        }
                    }
                    if (arr.size() > 0) {
                        adapter.setLoaded();
                        binding.inboxlist1.setVisibility(View.VISIBLE);
                        binding.userRecord.setVisibility(View.GONE);
                    } else {
                        binding.userRecord.setVisibility(View.VISIBLE);
                        binding.inboxlist1.setVisibility(View.GONE);


                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                adapter.notifyDataSetChanged();

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "message_list");
    }

    private void getRecursionAPI(String offset,String limit) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", limit);
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("team_id", teamid);
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
                        JSONObject object = obj.getJSONObject(i);
                        if (object.getString("senderuserid").equals(userid)) {
                            arr.add((InboxList) (getObject(obj.getString(i), InboxList.class)));
                        }
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
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "message_list");
    }


}
