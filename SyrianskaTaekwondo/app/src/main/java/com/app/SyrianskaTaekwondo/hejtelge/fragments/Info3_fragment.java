package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Link_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info3FragmentBinding;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import vk.help.Fragment;

public class Info3_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info3FragmentBinding binding;
    List<String> arr_link = new ArrayList<>();
    private Link_Adapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = Info3FragmentBinding.inflate(getLayoutInflater());

        getUserAPI();
        binding.swipe.setOnRefreshListener(this);
        adapter = new Link_Adapter(
                arr_link, getActivity(), "link");
        binding.add.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.add.setAdapter(adapter);
        return (binding.getRoot());
    }


    private void getUserAPI() {
        arr_link.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("user_id", new CommonMethods().getPrefsData(getActivity(), "id", ""));
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject object = new JSONObject(result.getData());

                    JSONArray links = object.getJSONArray("links");
                    for (int i = 0; i < links.length(); i++) {
                        arr_link.add(links.get(i).toString());
                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/


                    }


                    getActivity().runOnUiThread(() -> {
                        if(arr_link.size()>0){
                            Collections.reverse(arr_link);
                            adapter.notifyDataSetChanged();
                            binding.swipe.setRefreshing(false);

                            binding.txtShow.setVisibility(View.GONE);
                            binding.add.setVisibility(View.VISIBLE);
                        }else{
                            binding.swipe.setRefreshing(false);

                            binding.txtShow.setVisibility(View.VISIBLE);
                            binding.add.setVisibility(View.GONE);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
    }

    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
