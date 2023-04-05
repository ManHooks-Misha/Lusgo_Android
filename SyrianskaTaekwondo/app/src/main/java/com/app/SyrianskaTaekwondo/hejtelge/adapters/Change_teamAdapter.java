package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.CreateMessage;
import com.app.SyrianskaTaekwondo.hejtelge.ProfileActivity;
import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.Remove_assign;
import com.app.SyrianskaTaekwondo.hejtelge.Rolechange_assign;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Item;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.RecyclerPopupWindow;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vk.help.Common;

public class Change_teamAdapter extends RecyclerView.Adapter<Change_teamAdapter.MyViewHolder> implements RecyclerPopupWindowAdapter.OnItemClickListener {
    int temp;
    private ArrayList<Teamlist> teamArrayList = new ArrayList<>();
    private CreateMessage.TeamListAdapter teamListAdapter;
    private AlertDialog alertDialog;
    private List<HashMap<String, String>> horizontalList;
    private Activity context;
    private ArrayList<HashMap<String, String>> arr_output = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_listrole = new ArrayList<>();
    CommonMethods cmn = new CommonMethods();
    List<Teamlist> arr_teams = new ArrayList<>();
    String userid, id, role, usertype;
    private Team_list_alertbox adapter_team;
    private Link_Adapter adapter;
    private RecyclerPopupWindowAdapter recyclerPopupWindowAdapter;
    String p_coach, teamid, roleid;
    View view_team;
    private String searchValue = "";
    BottomSheetDialog bt;

    public Change_teamAdapter(List<HashMap<String, String>> horizontalList, Activity context, String id, String role, String usertype) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.id = id;
        this.role = role;
        this.usertype = usertype;
        userid = cmn.getPrefsData(context, "id", "");
        arr_output.clear();


    }

    @Override
    public void onItemClick(int position) {
        if (roleid.equals(ConsURL.members) || roleid.equals(ConsURL.sub_coach)) {
            if (position == 0) {
                showCustomDialogTeam(view_team, teamid, id, horizontalList);

              //  Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
            if (position == 1) {
                showCustomDialogRole(view_team, id, teamid, roleid);

            }
            if (position == 2) {
                if (horizontalList.size() > 1) {
                    // Remove User from team
                    showCustomDialogRemoveUser(view_team, id, teamid, roleid);
                } else {
                    if (bt != null) {
                        bt.dismiss();
                    }
                    new CommonMethods().showAlert("Användare är ensam teamleader för ett team och därför går det inte att ta bort denna användare.", context);
                }
            }
        } else {
            if (position == 0) {
                showCustomDialogRole(view_team, id, teamid, roleid);

            }
            if (position == 1) {
                if (horizontalList.size() > 1) {
                    // Remove User from team
                    showCustomDialogRemoveUser(view_team, id, teamid, roleid);
                } else {
                    if (bt != null) {
                        bt.dismiss();
                    }
                    new CommonMethods().showAlert("Användare är ensam teamleader för ett team och därför går det inte att ta bort denna användare.", context);
                }
            }
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        //  AppCompatImageView flBottomRight, flTopLeft, flBottomLeft, flTopRight;
        AppCompatImageView select;
        AppCompatTextView team_name, role;

        MyViewHolder(View view) {
            super(view);
//            flTopLeft = view.findViewById(R.id.flTopLeft);
//            flBottomLeft = view.findViewById(R.id.flBottomLeft);
//            flTopRight = view.findViewById(R.id.flTopRight);
//            flBottomRight = view.findViewById(R.id.flBottomRight);
            team_name = view.findViewById(R.id.teamname);
            role = view.findViewById(R.id.role);
            select = view.findViewById(R.id.change);


        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.change_teamrole, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

      /*  String desc = Objects.requireNonNull(horizontalList.get(position).get("description")).toString();
        String link = Objects.requireNonNull(horizontalList.get(position).get("link")).toString();
        String name = Objects.requireNonNull(horizontalList.get(position).get("name")).toString();
        String doc = Objects.requireNonNull(horizontalList.get(position).get("doc")).toString();
        String profile_image = Objects.requireNonNull(horizontalList.get(position).get("profile_image")).toString();
*/

        String team = horizontalList.get(position).get("team_name");
        String role_ = horizontalList.get(position).get("role");
        String team_id = horizontalList.get(position).get("teamid");
        String roleids = horizontalList.get(position).get("roleid");
        if (roleids.equals(ConsURL.members) || roleids.equals(ConsURL.sub_coach)) {
            List<Item> arr = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                Item item = new Item();

                if (i == 0) {
                    item.setTitle("Byt team");
                } else if (i == 1) {

                    item.setTitle("Ändra roll");

                } else {
                    item.setTitle("Ta bort användare");

                }

                arr.add(item);
            }
            View.OnClickListener handler = v -> {
                p_coach = horizontalList.get(position).get("coach_id");
                teamid = horizontalList.get(position).get("teamid");
                roleid = horizontalList.get(position).get("roleid");
                bt = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                view_team = LayoutInflater.from(context).inflate(R.layout.bottomsheet_changeadapter, null);


                RecyclerView recyclerView = view_team.findViewById(R.id.list);
                //name.setText(txtview.getText().toString());
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerPopupWindowAdapter = new RecyclerPopupWindowAdapter(arr, context);
                recyclerPopupWindowAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(recyclerPopupWindowAdapter);
                bt.setContentView(view_team);
                bt.show();
                //To do Call popup to change team and role and remove user
                //      popupWindow(arr, holder.select, v, teamid, id, roleid);
                // getCoachAPI(v, holder.select, position,p_coach);
            };
            holder.select.setOnClickListener(handler);
        } else {
            List<Item> arr = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Item item = new Item();
                if (i == 0) {
                    item.setTitle("Ändra roll");
                } else {
                    item.setTitle("Ta bort användare");
                }
                arr.add(item);
            }
            View.OnClickListener handler = v -> {
                p_coach = horizontalList.get(position).get("coach_id");
                teamid = horizontalList.get(position).get("teamid");
                roleid = horizontalList.get(position).get("roleid");
//                popupWindow(arr, holder.select, holder.itemView, teamid, id, roleid);


                bt = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                view_team = LayoutInflater.from(context).inflate(R.layout.bottomsheet_changeadapter, null);
//                view.findViewById(R.id.del_notification).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                       // alert(data.getN_id(), pos);
//                        bt.dismiss();
//                    }
//                });

                RecyclerView recyclerView = view_team.findViewById(R.id.list);
                //name.setText(txtview.getText().toString());
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerPopupWindowAdapter = new RecyclerPopupWindowAdapter(arr, context);
                recyclerPopupWindowAdapter.setOnItemClickListener(this);
                recyclerView.setAdapter(recyclerPopupWindowAdapter);
               /* String name1 = String.valueOf(horizontalList.get(pos).getName().trim().charAt(0));
                TextDrawable drawable = TextDrawable.builder()
                        .buildRect(name1.toUpperCase(), Color.parseColor("#1da0fc"));
                Glide.with(context)
                        .load(horizontalList.get(pos).getProfile_image())
                        .centerCrop()
                        .placeholder(drawable)
                        .into(imageView);*/
                // Glide.with(context).load(data.getProfile_image()).into(imageView);

                bt.setContentView(view_team);
                bt.show();

                // getCoachAPI(v, holder.select, position,p_coach);
            };


            holder.select.setOnClickListener(handler);

        }
        holder.team_name.setText(team);
        holder.role.setText(role_);
        if (usertype.equals(ConsURL.admin)) {
            holder.select.setVisibility(View.VISIBLE);
        } else {
            holder.role.setGravity(Gravity.END);
            holder.select.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public ArrayList<HashMap<String, String>> getList() {
        return arr_output;
    }

    public void popupWindow(List<Item> items, AppCompatTextView textView, View pos, String p_team, String p_member, String roleid) {
        // initialize a pop up window type

        RecyclerPopupWindow recyclerPopupWindow = new RecyclerPopupWindow(items);
        recyclerPopupWindow.showPopupWindow(context, textView, 500, 400, false);
        recyclerPopupWindow.setCallBack((value, coach) -> {
            if (!"-1".equals(value)) {
                if (value.equals("Byt team")) {
                    // change team
                    showCustomDialogTeam(pos, p_team, p_member, horizontalList);
                } else if (value.equals("Ändra roll")) {

                    // Change role
                    showCustomDialogRole(pos, p_member, p_team, roleid);
                } else {
                    if (horizontalList.size() > 1) {
                        // Remove User from team
                        showCustomDialogRemoveUser(pos, p_member, p_team, roleid);
                    } else {
                        new CommonMethods().showAlert("Användare är ensam teamleader för ett team och därför går det inte att ta bort denna användare.", context);
                    }
                }
            }

        });



    }


    private void showCustomDialogTeam(View item, String previousteam, String member, List<HashMap<String, String>> arr_team) {
        ViewGroup viewGroup = item.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.change_teampopup, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatEditText email_text = dialogView.findViewById(R.id.email_text);
        email_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchValue = s.toString();
                adapter_team.getFilter().filter(s);
//                adapter_team.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        coach.setVisibility(View.VISIBLE);
        getTeamAPI(email_text, coach, arr_team);

        email_text.setOnClickListener(view -> {
            coach.setVisibility(View.VISIBLE);
        });
        adapter_team = new Team_list_alertbox(coach, userid, context, email_text, cmn.getPrefsData(context, "team_id_invite", ""), "2");
        coach.setAdapter(adapter_team);
//        adapter_team.setUpdatedData(teamArrayList);
        adapter_team.setOnLoadMoreListener(() -> {
            adapter_team.addItem();
            coach.postDelayed(() -> {
                int end = adapter_team.getItemCount() + 1;
                getRecursionTeamAPI((end), horizontalList);
            }, 2000);
        });


        ok.setOnClickListener(view -> {
            String teamID = adapter_team.getTeamID();
            if (teamID != null) {
                getchangeTeamAPI(teamID, previousteam, member);

            } else {
                Toast.makeText(context, "Välj lag", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogRole(View item, String member, String teamid, String role) {
        arr_listrole.clear();
        if (role.equals(ConsURL.coach)) {
            for (int i = 0; i < 2; i++) {
                HashMap map = new HashMap();
               /* if (i == 0) {
                    map.put("role", "Tränare");
                    map.put("roleid", "3");
                }*/
                if (i == 0) {
                    map.put("role", "Användare");
                    map.put("roleid", "4");
                }
                if (i == 1) {
                    map.put("role", "Teamleader hjälpare");
                    map.put("roleid", "6");
                }
                arr_listrole.add(map);

            }
        } else if (role.equals(ConsURL.members)) {
            for (int i = 0; i < 2; i++) {
                HashMap map = new HashMap();
                if (i == 0) {
                    map.put("role", "Teamleader");
                    map.put("roleid", "3");
                }

                if (i == 1) {
                    map.put("role", "Teamleader hjälpare");
                    map.put("roleid", "6");
                }
                arr_listrole.add(map);

            }
        } else {
            for (int i = 0; i < 2; i++) {
                HashMap map = new HashMap();
                if (i == 0) {
                    map.put("role", "Teamleader");
                    map.put("roleid", "3");
                }
                if (i == 1) {
                    map.put("role", "Användare");
                    map.put("roleid", "4");
                }

                arr_listrole.add(map);

            }

        }
        ViewGroup viewGroup = item.findViewById(android.R.id.content);
        List<HashMap<String, String>> arr_role = new ArrayList<>();
        View dialogView = LayoutInflater.from(context).inflate(R.layout.teamchangerole, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView txt = dialogView.findViewById(R.id.txt);
        txt.setText(Html.fromHtml("Välj vilken behörighet som denna användare ska ha"));
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        AppCompatTextView email_text = dialogView.findViewById(R.id.email_text);
        email_text.setHint("--Byt roll--");
        RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        coach.setVisibility(View.VISIBLE);

        email_text.setOnClickListener(view -> {
            coach.setVisibility(View.VISIBLE);
        });
        adapter = new Link_Adapter(arr_listrole, context, email_text, coach);
        coach.setAdapter(adapter);


        ok.setOnClickListener(view -> {
            String roleID = adapter.getroleID();
            String rolename = adapter.getroleName();
            if (roleID != null) {
                getchangeRoleAPI(roleID, member, teamid, view, alertDialog);
            } else {
                Toast.makeText(context, "Välj roll", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogRemoveUser(View item, String member, String teamid, String roleID) {
        // ViewGroup viewGroup = item.findViewById(android.R.id.content);
        // List<HashMap<String, String>> arr_role = new ArrayList<>();
        // View dialogView = LayoutInflater.from(context).inflate(R.layout.team_invite_show, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage("Vill du ta bort användare från teamet?");
        // builder.setView(dialogView);
        //   AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        //   AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        // AppCompatTextView email_text = dialogView.findViewById(R.id.email_text);
        // email_text.setHint("--Byt roll--");
        //   RecyclerView coach = dialogView.findViewById(R.id.list_coach);
        //   coach.setVisibility(View.VISIBLE);

        builder.setPositiveButton("Ja", (dialog, which) -> {

            //String roleID = adapter.getroleID();
            getchangeRoleAPIRemove(roleID, member, teamid, item);
        });
        builder.setNegativeButton("Avbryt", (dialog, which) -> {
            alertDialog.dismiss();

        });


        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    public class Link_Adapter extends RecyclerView.Adapter<Link_Adapter.MyViewHolder> {


        private List<HashMap<String, String>> horizontalList;
        private String flag, roleid, rolename, usertype;
        private Context context;
        private AppCompatTextView textView;
        private RecyclerView coach;
        int row_index;

        public Link_Adapter(List<HashMap<String, String>> horizontalList, Context context, AppCompatTextView textView, RecyclerView coach) {
            this.horizontalList = horizontalList;
            this.flag = flag;
            this.textView = textView;
            this.coach = coach;
            this.context = context;

        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            private AppCompatTextView name;
            private CardView card_view;

            MyViewHolder(View view) {
                super(view);
                //   txtview = view.findViewById(R.id.group_name);
                // imageView = view.findViewById(R.id.img_edit);

                //   pos = view.findViewById(R.id.pos);
//
                name = view.findViewById(R.id.link);
                card_view = view.findViewById(R.id.card_view);

            }
        }


        public String getroleID() {

            return roleid;
        }

        public String getroleName() {
            return rolename;
        }

        public String setroleid(String id) {
            roleid = id;
            return roleid;
        }

        public String setroleName(String name) {
            rolename = name;
            return rolename;
        }

        @NotNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.role, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.name.setText(horizontalList.get(position).get("role"));
            //setroleid(horizontalList.get(position).get("roleid"));
//            textView.setText(horizontalList.get(position).get("role"));
//            coach.setVisibility(View.GONE);
            if (row_index == position) {
                setroleid(horizontalList.get(position).get("roleid"));


                holder.card_view.setCardBackgroundColor(Color.parseColor("#c0c0c0"));
            } else {

                holder.card_view.setCardBackgroundColor(Color.parseColor("#ffffff"));
            }
            holder.itemView.setOnClickListener(view -> {
                setroleid(horizontalList.get(position).get("roleid"));
                setroleName(horizontalList.get(position).get("role"));
                textView.setText(horizontalList.get(position).get("role"));
                holder.itemView.setBackgroundColor(Color.parseColor("#c0c0c0"));
                coach.setVisibility(View.VISIBLE);
                row_index = position;
                refreshRecyclerView(coach);

            });

        }

        void refreshRecyclerView(RecyclerView recyclerView) {
            RecyclerView.Adapter adapterRef = recyclerView.getAdapter();
            recyclerView.setAdapter(null);
            recyclerView.setAdapter(adapterRef);
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }


    private void getchangeTeamAPI(String newteam, String previousteam, String member) {
        teamArrayList.clear();
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("member_id", member);
            object.put("new_team", newteam);
            object.put("user_id", userid);
            object.put("previous_team", previousteam);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", id).putExtra("flag", "user"));
                    context.finish();//jb activity hi finish ho gyi toh notify kisko kroge?

                } else {
                    JSONObject obj = new JSONObject(result.getData());
                    if (obj.getString("flag").equals("3")) {
                        alertDialog.dismiss();
                        new CommonMethods().showAlert(obj.getString("message"), context);
                    }
                    if (obj.getString("flag").equals("4")) {
                        alertDialog.dismiss();
                        new CommonMethods().showAlert(obj.getString("message"), context);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "change_team");
    }


    private void getchangeRoleAPI(String role, String member, String teamid, View view, AlertDialog alertDialog) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("u_id", member);
            object.put("role", role);
            object.put("user_id", userid);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", member).putExtra("flag", "user"));
                    context.finish();

                } else {
                    JSONObject obj = new JSONObject(result.getData());
                    if (obj.has("flag")) {
                        if (obj.getString("flag").equals("3")) {
                            alertDialog.dismiss();
                            new CommonMethods().showAlert(obj.getString("message"), context);
                        }
                        if (obj.getString("flag").equals("4")) {
                            alertDialog.dismiss();
                            new CommonMethods().showAlert(obj.getString("message"), context);
                        } else {

                        }
                    } else {
                        arr_teams.clear();
                        alertDialog.dismiss();
                        Teamlist list = new Teamlist();
                        list.setName(obj.getString("team_name"));
                        list.setUsername(obj.getString("username"));
                        list.setCoach_id(obj.getString("coach_id"));
                        list.setId(obj.getString("team_id"));
                        list.setRole_id(obj.getString("role_id"));
                        arr_teams.add(list);
                        showCustomDialog(view, arr_teams, member);
                        new CommonMethods().showAlert(obj.getString("message"), context);
                    }

                }
            } catch (Exception e) {
                alertDialog.dismiss();
                new CommonMethods().showAlert("Den här användaren har flera lag, tilldela Teamleader för de första.", context);
                e.printStackTrace();
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "updateRole");
    }

    private void getchangeRoleAPIRemove(String role, String member, String teamid, View view) {
        String requestData;
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("u_id", member);
            object.put("role", role);
            object.put("user_id", userid);
            object.put("team_id", teamid);

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    context.startActivity(new Intent(context, ProfileActivity.class).putExtra("id", member).putExtra("flag", "user"));
                    context.finish();

                } else {
                    JSONObject obj = new JSONObject(result.getData());
                    if (obj.has("flag")) {
                        if (obj.getString("flag").equals("3")) {
                            alertDialog.dismiss();
                            new CommonMethods().showAlert(obj.getString("message"), context);
                        }
                        if (obj.getString("flag").equals("4")) {
                            alertDialog.dismiss();
                            new CommonMethods().showAlert(obj.getString("message"), context);
                        }

                    } else {
                        Teamlist list = new Teamlist();
                        list.setName(obj.getString("team_name"));
                        list.setUsername(obj.getString("username"));
                        list.setCoach_id(obj.getString("coach_id"));
                        list.setId(obj.getString("team_id"));
                        list.setRole_id(obj.getString("role_id"));
                        arr_teams.add(list);

                        showCustomDialogRemove(view, arr_teams, member, role);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                new CommonMethods().showAlert("Den här användaren har flera lag, tilldela Teamleader för de första.", context);
            }
            return null;
        }, requestData).execute(ConsURL.BASE_URL_TEST + "delete_user");
    }

    private void showCustomDialog(View view1, List<Teamlist> arr_team, String p_coach) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = view1.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.team_assign, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        ok.setOnClickListener(view -> {
            String roleid = adapter.getroleID();
            if (cmn.isOnline(context)) {
                alertDialog.dismiss();

                context.startActivity(new Intent(context, Rolechange_assign.class).putExtra("data", Common.INSTANCE.getBytes(arr_team)).putExtra("coach", p_coach).putExtra("roleid", roleid));
                context.finish();


                //     startActivity(new Intent(ProfileActivity.this,Teamshow_array.class).putExtra("data",Common.INSTANCE.getJSON(arr_team)));
             /*   String email_text = Objects.requireNonNull(email.getText()).toString();
                if (email_text.length() > 0) {
                  //  getforgotAPI(email_text);
                } else {
                    Toast.makeText(this, "Skriv in ditt användarnamn", Toast.LENGTH_SHORT).show();
                }*/
             /*   if (cmn.isEmailValid(email_text)) {

                } else {
                    Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
                }*/

            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showCustomDialogRemove(View view1, List<Teamlist> arr_team, String p_coach, String roleid) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = view1.findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.team_assign, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        AppCompatTextView ok = dialogView.findViewById(R.id.buttonOk);
        AppCompatTextView cancel = dialogView.findViewById(R.id.buttoncancel);
        ok.setOnClickListener(view -> {
            if (cmn.isOnline(context)) {

                context.startActivity(new Intent(context, Remove_assign.class).putExtra("data", Common.INSTANCE.getBytes(arr_team)).putExtra("coach", p_coach).putExtra("roleid", roleid));


            }

        });
        cancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void getTeamAPI(AppCompatEditText textView, RecyclerView coachlist, List<HashMap<String, String>> arr) {
        teamArrayList.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 30);
            object.put("offset", 0);
            object.put("user_id", userid);
            object.put("coach_id", "");

            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                if (result.isStatus()) {

                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        String id = object.getString("id");
                        temp = 0;

                        for (HashMap list : arr) {

                            if ((id.equals(list.get("teamid")))) {
                                //teamArrayList.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                                temp = 1;
                                break;
                            }

                        }
                        if (temp == 0) {
                            teamArrayList.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                        }
                    }
                    if (obj.length() > 0) {
                        if (obj.length() == 1) {
                            textView.setText(teamArrayList.get(0).getName());
//                            cmn.setPrefsData(context, "team_id_invite", teamArrayList.get(0).getId());
//                            cmn.setPrefsData(context, "team_name_invite", teamArrayList.get(0).getName());
                            coachlist.setVisibility(View.GONE);
                        } else {
                            adapter_team.setLoaded();
                            for (Teamlist list : teamArrayList) {
                                String id = cmn.getPrefsData(context, "team_id_invite", "");
                                if (id.equals(list.getId())) {
                                    textView.setText(cmn.getPrefsData(context, "team_name_invite", ""));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                adapter_team.setUpdatedData(teamArrayList);

                //adapter_team.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "teamList");
    }


    private void getRecursionTeamAPI(int offset, List<HashMap<String, String>> arr) {
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 30);
            object.put("offset", offset);
            object.put("user_id", userid);
            object.put("coach_id", "");
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(null, result -> {
            try {
                adapter_team.removeLastData();
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        String id = object.getString("id");
                        temp = 0;

                        for (HashMap list : arr) {

                            if ((id.equals(list.get("teamid")))) {
                                //teamArrayList.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                                temp = 1;
                                break;
                            }

                        }
                        if (temp == 0) {
                            teamArrayList.add((Teamlist) (Common.INSTANCE.getObject(obj.getString(i), Teamlist.class)));
                        }
                    }

                    if (obj.length() > 0) {
                        adapter_team.setLoaded();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //mAdapter.setLoaded();
                adapter_team.setUpdatedData(teamArrayList, searchValue);

//                adapter_team.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "teamList");
    }

}

