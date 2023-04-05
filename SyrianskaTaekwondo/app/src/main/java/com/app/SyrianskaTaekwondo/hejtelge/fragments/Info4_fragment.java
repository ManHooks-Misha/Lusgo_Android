package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.Doc_adapter;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info4FragmentBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.Fragment;

public class Info4_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info4FragmentBinding binding;
    private List<HashMap<String,String>> arr_doc = new ArrayList<>();
    private Doc_adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = Info4FragmentBinding.inflate(getLayoutInflater());
        getUserAPI();
        binding.swipe.setOnRefreshListener(this);

        adapter = new Doc_adapter(
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
                    JSONObject object = new JSONObject(result.getData());

                    JSONArray links = object.getJSONArray("document");
                    for (int i = 0; i < links.length(); i++) {
                        HashMap map=new HashMap();
                        JSONObject object1=links.getJSONObject(i);
                        map.put("name",object1.getString("document_name"));
                        map.put("path",object1.getString("data"));
                        arr_doc.add(map);

                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> adapter.notifyDataSetChanged());
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
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
    }

    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
