package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info2FragmentBinding;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vk.help.Fragment;

public class Info2_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info2FragmentBinding binding;
    private List<String> arr_about = new ArrayList<>();
    private String userid;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = Info2FragmentBinding.inflate(getLayoutInflater());
        userid = new CommonMethods().getPrefsData(getActivity(), "id", "");
        getUserAPI();
        binding.swipe.setOnRefreshListener(this);

        return (binding.getRoot());
    }

    private void getUserAPI() {
        arr_about.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity");
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());

                    JSONArray about_us = object.getJSONArray("about_us");
                    arr_about.add(about_us.get(0).toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(() -> {
                                if(arr_about.size()>0){
                                    binding.txtShow.setVisibility(View.GONE);
                                    binding.phone.setVisibility(View.VISIBLE);
                                    String title =arr_about.get(0).trim();
                                    title = title.replaceFirst("<p dir=\"ltr\">", "");
                                    // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
                                    title = title.replaceAll("</p>", "");

                                    binding.phone.setText(Html.fromHtml(title));

                                }else{
                                    binding.txtShow.setVisibility(View.VISIBLE);
                                    binding.phone.setVisibility(View.GONE);
                                }
                            });

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //{"ClientId":"1","LocationId":"6","MacId":"6b14dbfb617d3c35","Response":"YES","DeviceDateTime":"2022-10-19T17:33:32"}

                binding.swipe.setRefreshing(false);

                /*mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();*/
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
    }

    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
