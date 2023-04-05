package com.app.SyrianskaTaekwondo.hejtelge.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.SyrianskaTaekwondo.hejtelge.databinding.Info2FragmentBinding;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vk.help.Fragment;

public class InfoAbout_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private Info2FragmentBinding binding;
    private List<String> arr_about = new ArrayList<>();
    private ArrayList<HashMap<String,String>> arr_doc = new ArrayList<>();
    private String userid;

    ProgressBar progressbar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = Info2FragmentBinding.inflate(getLayoutInflater());
        userid = new CommonMethods().getPrefsData(getActivity(), "id", "");
       // progressbar=new Progressbar();
        getUserAPI();
        binding.swipe.setOnRefreshListener(this);

        return (binding.getRoot());
    }

    private void getUserAPI() {
        binding.progresBar.setVisibility(View.VISIBLE);
      // progressbar.showProgress(getActivity());
//        ProgressDialog mprogdialog = ProgressDialog.show(getActivity(), "", "Vänta", true);
//        mprogdialog.setCancelable(false);
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
                    binding.progresBar.setVisibility(View.GONE);
                   // mprogdialog.dismiss();
                    JSONObject object = new JSONObject(result.getData());

                    JSONArray about_us = object.getJSONArray("about_us");
                    JSONArray document = object.getJSONArray("document");
                    for (int i = 0; i < document.length(); i++) {
                        HashMap map = new HashMap();
                        JSONObject object1 = document.getJSONObject(i);
                            map.put("id", object1.getString("id"));
                            map.put("documentUrl", object1.getString("documentUrl"));
                            map.put("documentName", object1.getString("documentName"));
                            map.put("documentoder", object1.getString("documentOrder"));
                            arr_doc.add(map);

                        }

                    arr_about.add(about_us.get(0).toString());
                     getActivity().runOnUiThread(() -> {
                        if(arr_about.size()>0){
                            binding.txtShow.setVisibility(View.GONE);
                            binding.phone.setVisibility(View.VISIBLE);
                            String title =arr_about.get(0);
//                            title = title.replaceFirst("<p dir=\"ltr\">", "");
//                            // txt_title = txt_title.replaceFirst("<p dir=\"ltr\">", "");
//                            title = title.replaceAll("</p>", "");
//                            title = title.replaceAll("</u>", "");
                          //  removeUrl(title);
                           // String cleartext = title.replaceAll("http://","").replace("http:// www.","").replace("www."," ");
                            binding.phone.setText(title);
                           // binding.phone.setPaintFlags(binding.phone.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));


                        }else{
                            binding.txtShow.setVisibility(View.VISIBLE);
                            binding.phone.setVisibility(View.GONE);
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
    private String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
            binding.phone.setText(commentstr);
            Log.d("dfakslf;l",commentstr);
        }
        return commentstr;
    }

    @Override
    public void onRefresh() {
        getUserAPI();
    }
}
