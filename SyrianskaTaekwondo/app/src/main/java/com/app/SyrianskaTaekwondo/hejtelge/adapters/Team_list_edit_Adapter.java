package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.EditGroup;
import com.app.SyrianskaTaekwondo.hejtelge.EditTeam;
import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateTeam;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UpdateUser;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
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


public class Team_list_edit_Adapter extends RecyclerView.Adapter<Team_list_edit_Adapter.MyViewHolder> {
    String role, role_id;
    private String msg = "Du är inte auktoriserad för den här åtgärden eftersom du är en användare i detta team";
    private AlertDialog alertDialog;
    private List<UserList> horizontalList;
    private Activity context;
    private String userid, groupid, group_name, flag, teamid;
    private ArrayList<String> arr_user = new ArrayList<>();
    private CommonMethods cmn = new CommonMethods();
    //private GetRoll getRoll;
    private AppCompatImageView imagePrfile, titleImg;
    private RelativeLayout rllImg;
    private AppCompatTextView addUserTXT, addUserInviteTXT;
    private AppCompatEditText titleEDT;
    private AppCompatTextView groupDeleteBTN;
    private SwitchCompat switchCompat;
    private View view1,view2;

    public Team_list_edit_Adapter(List<UserList> horizontalList,
                                  Activity context, String userid, String groupid,
                                  String group_name,
                                  String flag, String teamid,
                                  AppCompatImageView imagePrfile,
                                  AppCompatTextView addUserTXT,
                                  AppCompatTextView addUserInviteTXT,
                                  AppCompatEditText titleEDT,
                                  SwitchCompat switchCompat,
                                  AppCompatTextView groupDeleteBTN,
                                  AppCompatImageView titleImg,
                                  RelativeLayout rllImg,View view1,View view2) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.userid = userid;
        this.groupid = groupid;
        this.group_name = group_name;
        this.flag = flag;
        this.teamid = teamid;
        this.imagePrfile = imagePrfile;
        this.addUserTXT = addUserTXT;
        this.addUserInviteTXT = addUserInviteTXT;
        this.titleEDT = titleEDT;
        this.switchCompat = switchCompat;
        this.groupDeleteBTN = groupDeleteBTN;
        this.titleImg = titleImg;
        this.rllImg = rllImg;
        this.view1 = view1;
        this.view2 = view2;

        // this.getRoll = getRoll;
    }

    @NotNull
    @Override
    public Team_list_edit_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_edit_list_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        role = cmn.getPrefsData(context, "role", "");
        role_id = cmn.getPrefsData(context, "role_id", "");

        if (!horizontalList.get(position).getFirstname().isEmpty()) {

            holder.txtview.setText(horizontalList.get(position).getFirstname());
        } else {
            holder.txtview.setText(horizontalList.get(position).getUsername());

        }

        for (int i = 0; i < horizontalList.size(); i++) {
            if (horizontalList.get(i).getId().equals(userid)) {
                if (horizontalList.get(i).getRole_id().equals("4")) {
                    imagePrfile.setOnClickListener(view -> cmn.showAlert(msg, context));
                    titleEDT.setClickable(false);
                    titleEDT.setEnabled(false);
//                    addUserInviteTXT.setOnClickListener(view -> cmn.showAlert(msg, context));
//                    addUserTXT.setOnClickListener(view -> cmn.showAlert(msg, context));
//                    switchCompat.setClickable(false);
//                    switchCompat.setEnabled(false);
//                    groupDeleteBTN.setClickable(false);
//                    groupDeleteBTN.setEnabled(false);

                    holder.img_del.setVisibility(View.GONE);
                    holder.img_del.setClickable(false);
                    holder.img_del.setEnabled(false);
                    holder.itemView.setClickable(false);
                    holder.itemView.setEnabled(false);

                    titleImg.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    rllImg.setVisibility(View.GONE);
                    addUserInviteTXT.setVisibility(View.GONE);
                    addUserTXT.setVisibility(View.GONE);
                    switchCompat.setVisibility(View.GONE);
                    groupDeleteBTN.setVisibility(View.GONE);
                    break;
                }
            }
        }
        String img = horizontalList.get(position).getImagepath();
        if (img != null)
            if (img.length() > 0 && img.contains("http")) {
                if (horizontalList.get(position).getProfile_image().length() > 0) {
                    Glide.with(context)
                            .load(horizontalList.get(position).getImagepath())
                            .centerCrop()
                            .into(holder.img);
                } else if (horizontalList.get(position).getFirstname().trim().length() > 0) {

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

            if (horizontalList.get(position).getRole_id().equals(ConsURL.coach)) {
                holder.txt.setText("Teamleader");
                holder.img_del.setVisibility(View.GONE);
                holder.txt.setVisibility(View.VISIBLE);

            } else {
                holder.img_del.setVisibility(View.VISIBLE);
                holder.txt.setVisibility(View.GONE);
            }

        holder.img_del.setOnClickListener(view -> {
            arr_user.add(horizontalList.get(position).getId());
            showCustomDialogTeam(position, group_name);

        });


        holder.itemView.setOnClickListener(v -> {
            role = cmn.getPrefsData(context, "role", "");
            //  getRoll.rollId(horizontalList.get(position).getRole_id());
            //  Toast.makeText(context, "edit", Toast.LENGTH_SHORT).show();
            if (role.equals("Admin") || role.equals("Teamleader") || role.equals("Subadmin")) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", horizontalList.get(position).getId()).putExtra("flag_screen", "dkcj").putExtra("flag", "user"));
            } else {
                 context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", horizontalList.get(position).getId()).putExtra("flag", "user").putExtra("flag_screen", "team"));

            }
        });


    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    private void showCustomDialog(int pos, String group_name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Vill du ta bort " + Objects.requireNonNull(horizontalList.get(pos).getFirstname()).trim() + " " + horizontalList.get(pos).getLastname() + " från denna grupp?");
        builder.setPositiveButton("Ja", (dialogInterface, i) ->
                addAddAPI(arr_user, groupid, pos));
        builder.setNegativeButton("Avbryt", (dialogInterface, i) -> alertDialog.dismiss());
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    private void showCustomDialogTeam(int pos, String group_name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setMessage("Avlägsna " + Objects.requireNonNull(horizontalList.get(pos).getFirstname()).trim() + " " + horizontalList.get(pos).getLastname() + " från '" + group_name + "' Team?");

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

    public interface GetRoll {
        void rollId(String id);
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
}

