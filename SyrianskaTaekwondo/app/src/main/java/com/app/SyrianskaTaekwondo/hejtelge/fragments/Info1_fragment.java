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

public class Info1_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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
       /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(20);
        params.setMarginEnd(20);
        params.setMargins(0,10,0,10);


        LinearLayout ll_main = new LinearLayout(getActivity());
        ll_main.setOrientation(LinearLayout.VERTICAL);
        ll_main.setBackgroundResource(R.drawable.layout_info);
        ll_main.setPadding(20, 20, 20, 20);
        ll_main.setLayoutParams(params);
        AppCompatEditText et_1 = new AppCompatEditText(getActivity());
        et_1.setBackgroundResource(R.drawable.edittext);
        et_1.setHint("Ange mobil");
        et_1.setPadding(30, 30, 30, 30);

        AppCompatEditText et_2 = new AppCompatEditText(getActivity());
        et_2.setBackgroundResource(R.drawable.edittext);
        et_2.setHint("Skriv in e-mail");
        et_2.setPadding(30, 30, 30, 30);

        AppCompatEditText et_3 = new AppCompatEditText(getActivity());
        et_3.setBackgroundResource(R.drawable.edittext);
        et_3.setHint("Gå in på webbplatsen");
        et_3.setPadding(30, 30, 30, 30);

        et_1.setLayoutParams(params);
        et_2.setLayoutParams(params);
        et_3.setLayoutParams(params);
        // et_1.setId(i);
        ll_main.addView(et_1);
        ll_main.addView(et_2);
        ll_main.addView(et_3);
        binding.ll.addView(ll_main);*/

        return (binding.getRoot());
    }


    private void getUserAPI() {
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
                    JSONObject object = new JSONObject(result.getData());
                    JSONArray contactlist = object.getJSONArray("contactlist");
                    for (int i = 0; i < contactlist.length(); i++) {
                        JSONObject obj = contactlist.getJSONObject(i);
                        HashMap map = new HashMap();
                        String email = obj.getString("email");
                        String mobile_no = obj.getString("mobile_no");
                        String website = obj.getString("name");
                        String role = obj.getString("role");
                        map.put("email", email);
                        map.put("mobile_no", mobile_no);
                        map.put("name", website);
                        map.put("role", role);
                        arr_list.add(map);
                    }
                    adapter.notifyDataSetChanged();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(arr_list.size()>0){
                                binding.txtShow.setVisibility(View.GONE);
                                binding.add.setVisibility(View.VISIBLE);
                            }else{
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

                adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "data_informations");
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
