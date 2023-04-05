package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.NewsListAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info1FragmentBinding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vk.help.Fragment;

public class Contact_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info1FragmentBinding binding;
    int index = 0;
    private List<HashMap<String, String>> arr_list = new ArrayList<>();
    private NewsListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = Info1FragmentBinding.inflate(getLayoutInflater());
        arr_list.clear();

        getUserAPI();
       // checkPermission1();
        binding.swipe.setOnRefreshListener(this);


        adapter = new NewsListAdapter(
                arr_list, getActivity());
        binding.add.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.add.setAdapter(adapter);


        return (binding.getRoot());
    }


    private void getUserAPI() {
        binding.progresBar.setVisibility(View.VISIBLE);
        arr_list.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
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
                    JSONArray contactlist = object.getJSONArray("contactList");

                    for (int i = 0; i < contactlist.length(); i++) {
                        JSONObject obj = contactlist.getJSONObject(i);
                        HashMap map = new HashMap();
                        String id = obj.getString("id");
                        String email = obj.getString("email");
                        String mobile_no = obj.getString("mobile_no");
                        String name = obj.getString("name");
                        String role = obj.getString("website");
                        String contactOrder = obj.getString("contactOrder");
                        String image = obj.getString("image");
                        map.put("email", email);
                        map.put("mobile_no", mobile_no);
                        map.put("name", name);
                        map.put("role", role);
                        map.put("contactOrder", contactOrder);
                        map.put("image", image);
                        map.put("id", id);
                        arr_list.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    getActivity().runOnUiThread(() -> {
                        if(arr_list.size()>0){
                            binding.txtShow.setVisibility(View.GONE);
                            binding.add.setVisibility(View.VISIBLE);
                        }else{
                            binding.txtShow.setVisibility(View.VISIBLE);
                            binding.add.setVisibility(View.GONE);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                binding.swipe.setRefreshing(false);

                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST2 + "data_informations");
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
        getUserAPI();
    }
}
