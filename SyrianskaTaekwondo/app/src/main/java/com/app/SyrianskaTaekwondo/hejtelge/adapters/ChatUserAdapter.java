package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.EditGroup;
import com.app.SyrianskaTaekwondo.hejtelge.EditTeam;
import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateTeam;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateUser;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import agency.tango.android.avatarview.views.AvatarView;
import vk.help.views.TextDrawable;


public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.MyViewHolder> {

    private AlertDialog alertDialog;
    private List<UserList> horizontalList;
    private Activity context;
    private String userid, groupid, group_name, flag, teamid;
    private ArrayList<String> arr_user = new ArrayList<>();

    public ChatUserAdapter(List<UserList> horizontalList, Activity context, String userid, String groupid, String group_name, String flag, String teamid) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.userid = userid;
        this.groupid = groupid;
        this.group_name = group_name;
        this.flag = flag;
        this.teamid = teamid;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        AvatarView img;
        AppCompatTextView txtview, pos, txt;
        AppCompatImageView img_del;

        MyViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.group_name);
            img = view.findViewById(R.id.pos);
            img_del = view.findViewById(R.id.img_del);
            txt = view.findViewById(R.id.txt);
        }
    }


    @NotNull
    @Override
    public ChatUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_edit_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.txt.setVisibility(View.GONE);
        holder.txtview.setText(horizontalList.get(position).getFirstname());
        String img = horizontalList.get(position).getImagepath();
        if (img != null)
            if (img.length() > 0 && img.contains("http")) {
                if (horizontalList.get(position).getFirstname().trim().length() > 0) {

                    String name = String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(img)
                            .centerCrop()
                            .placeholder(drawable)
                            .into(holder.img);
                }

            } else {
                if (horizontalList.get(position).getFirstname().trim().length() > 0) {
                    String name = String.valueOf(horizontalList.get(position).getFirstname().trim().charAt(0));
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(name.toUpperCase(), Color.parseColor("#1da0fc"));
                    Glide.with(context)
                            .load(horizontalList.get(position).getProfile_image())
                            .centerCrop()
                            .placeholder(drawable)
                            .into(holder.img);
                }
            }
        holder.img_del.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(view -> {
                context.startActivity(new Intent(context, ProfileActivity.class)
                        .putExtra("id", horizontalList.get(position).getId())
                        .putExtra("flag", "user"));


            });

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    private void showCustomDialog(int pos, String group_name) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        //then we will inflate the custom alert dialog xml that we created
        //Now we need an AlertDialog.Builder object

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Remove " + Objects.requireNonNull(horizontalList.get(pos).getFirstname()).trim() + " " + horizontalList.get(pos).getLastname() + " from '" + group_name + "' group?");
        builder.setPositiveButton("Ja", (dialogInterface, i) ->
                addAddAPI(arr_user, groupid, pos));
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogTeam(int pos, String group_name) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup

        //then we will inflate the custom alert dialog xml that we created


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Remove " + Objects.requireNonNull(horizontalList.get(pos).getFirstname()).trim() + " " + horizontalList.get(pos).getLastname() + " from '" + group_name + "' Team?");

        builder.setPositiveButton("Ja", (dialogInterface, i) ->

                addTeamAPI(arr_user, groupid, pos, teamid));

        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();

        alertDialog.show();
    }


    private void addAddAPI(ArrayList<String> arr, String groupid, int pos) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            UpdateUser pojo = new UpdateUser();
            pojo.access_key = ConsURL.accessKey;
            pojo.action = "Delete";
            pojo.group_id = groupid;
            pojo.user_id = userid;
            pojo.users = arr;
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    EditGroup.arr.remove(pos);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_groupUser");
    }

    private void addTeamAPI(ArrayList<String> arr, String coachid, int pos, String teamid) {
        //  arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            UpdateTeam pojo = new UpdateTeam();
            pojo.access_key = ConsURL.accessKey;
            pojo.action = "Delete";
            pojo.coach_id = coachid;
            pojo.user_id = userid;
            pojo.members = arr;
            pojo.team_id = teamid;
            Gson gson = new Gson();

            requestData = gson.toJson(pojo);

        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {

                    EditTeam.arr.remove(pos);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "update_teamUser");
    }


}

