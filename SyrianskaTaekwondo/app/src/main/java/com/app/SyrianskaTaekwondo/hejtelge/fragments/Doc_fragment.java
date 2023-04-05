package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Link1_Adapter;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info4FragmentBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import vk.help.Fragment;

public class Doc_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info4FragmentBinding binding;
    private ArrayList<HashMap<String,String>> arr_doc = new ArrayList<>();
    private Link1_Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = Info4FragmentBinding.inflate(getLayoutInflater());
        getUserAPI();
        binding.swipe.setOnRefreshListener(this);

        adapter = new Link1_Adapter(
                arr_doc, getActivity(), "doc");
        binding.add.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.add.setAdapter(adapter);
        return (binding.getRoot());


    }

    @Override
    public void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void getUserAPI() {
        binding.progresBar.setVisibility(View.VISIBLE);
        arr_doc.clear();
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
                    binding.progresBar.setVisibility(View.GONE);
                    JSONObject object = new JSONObject(result.getData());
                    JSONArray document = object.getJSONArray("document");
                    for (int i = 0; i < document.length(); i++) {
                        HashMap map = new HashMap();

                        try {
                            JSONObject object1 = document.getJSONObject(i);

                            map.put("id", object1.getString("id"));
                            map.put("documentUrl", object1.getString("documentUrl"));
                            map.put("documentName", object1.getString("documentName"));
                            map.put("documentoder", object1.getString("documentOrder"));
                            arr_doc.add(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(arr_doc.size()>0){
                                adapter.notifyDataSetChanged();
                                binding.swipe.setRefreshing(false);

                                binding.txtShow.setVisibility(View.GONE);
                                binding.add.setVisibility(View.VISIBLE);
                            }else{
                                binding.swipe.setRefreshing(false);

                                binding.txtShow.setVisibility(View.VISIBLE);
                                binding.add.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                binding.swipe.setRefreshing(false);

                /*mAdapter.setLoaded();
                mAdapter.notifyDataSetChanged();*/
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "data_informations");
    }

    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
