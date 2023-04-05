package com.app.SyrianskaTaekwondo.hejtelge.adapters;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Item;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Teamlist;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.app.SyrianskaTaekwondo.hejtelge.utility.ConsURL;
import com.app.SyrianskaTaekwondo.hejtelge.utility.RecyclerPopupWindow;
import com.app.SyrianskaTaekwondo.hejtelge.utils.NetworkCall;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssignTeam_Coach extends RecyclerView.Adapter<AssignTeam_Coach.MyViewHolder> {


    private List<Teamlist> horizontalList;
    private Activity context;
    private ArrayList<HashMap<String, String>> arr_output = new ArrayList<>();
    private List<Item> arr = new ArrayList<>();
    CommonMethods cmn = new CommonMethods();
    String userid;
    // private Show_Team_Adapter adapter;

    public AssignTeam_Coach(List<Teamlist> horizontalList, Activity context) {
        this.horizontalList = horizontalList;
        this.context = context;
        userid = cmn.getPrefsData(context, "id", "");
        arr_output.clear();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        //  AppCompatImageView flBottomRight, flTopLeft, flBottomLeft, flTopRight;

        AppCompatTextView team_name, select;

        MyViewHolder(View view) {
            super(view);
//            flTopLeft = view.findViewById(R.id.flTopLeft);
//            flBottomLeft = view.findViewById(R.id.flBottomLeft);
//            flTopRight = view.findViewById(R.id.flTopRight);
//            flBottomRight = view.findViewById(R.id.flBottomRight);
            team_name = view.findViewById(R.id.teamname);
            select = view.findViewById(R.id.txt);


        }
    }


    @NotNull
    @Override
    public AssignTeam_Coach.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_teamassign, parent, false);

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

        String team = horizontalList.get(position).getName();
        holder.team_name.setText(team);
        View.OnClickListener handler = v -> {
            String p_coach = horizontalList.get(position).getCoach_id();

            getCoachAPI(v, holder.select, position, p_coach);


        };
        holder.select.setOnClickListener(handler);


    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    public ArrayList<HashMap<String, String>> getList() {
        return arr_output;
    }

    public void popupWindowDogs(List<Item> items, AppCompatTextView textView, int pos) {
        // initialize a pop up window type
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        RecyclerPopupWindow recyclerPopupWindow = new RecyclerPopupWindow(items);
        recyclerPopupWindow.showPopupWindow(context, textView, textView.getWidth(), height - 600, false);
        recyclerPopupWindow.setCallBack((value, coachid) -> {
            if (!"-1".equals(value)) {
                textView.setText(value);
                for (int i = 0; i < arr_output.size(); i++) {
                    String team = arr_output.get(i).get("team_id");
                    if (team.equals(horizontalList.get(pos).getId())) {
                        arr_output.remove(i);
                        break;
                    }
                }


                HashMap map = new HashMap();
                map.put("Coach_id", coachid);
                map.put("team_id", horizontalList.get(pos).getId());
                map.put("previous_coach", horizontalList.get(pos).getCoach_id());
                arr_output.add(map);
            }
            //  recyclerPopupWindow = null;
        });


      /*  popupWindow.setWindowLayoutMode(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

*/
//        adapter = new Show_Team_Adapter(arr, coach,textView);
//        coach.setLayoutManager(new LinearLayoutManager(context));
//
//        coach.setAdapter(adapter);

    /*    adapter.setOnLoadMoreListener(() -> {
            arr.add(null);
            adapter.notifyItemInserted(arr.size() - 1);
            coach.postDelayed(() -> {
                int end = arr.size() + 1;
                getRecursionAPI(String.valueOf(end));
            }, 2000);
        });*/
        // some other visual settings
        // popupWindow.setFocusable(true);

        // set the list view as pop up window content
        //  popupWindow.setContentView(popupView);

    }

    private void getCoachAPI(View v, AppCompatTextView textView, int pos, String p_coach) {
        arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 500);
            object.put("offset", 0);
            object.put("user_id", userid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONArray obj = new JSONArray(result.getData());
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject object = obj.getJSONObject(i);
                        if (!p_coach.equals(object.getString("id"))) {
                            Item map = new Item();
                            map.setTitle(object.getString("firstname"));
                            map.setUser_id(object.getString("id"));
                            map.setRole(object.getString("role"));
                            map.setEmail(object.getString("email"));
                            map.setUsername(object.getString("username"));
                            map.setProfile_image(object.getString("profile_image"));
                            arr.add(map);
                        }
                    }
                    if (obj.length() > 0) {

                        //adapter.setLoaded();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getUserAPI(pos, textView, p_coach);


               /* if (v.getId() == R.id.txt) {
                    // show the list view as dropdown
                    popupWindowDogs.showAsDropDown(v, -5, 0);
                }*/
                // mAdapter.setLoaded();
                // adapter.notifyDataSetChanged();
            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "coachList");
    }


    public class Show_Team_Adapter extends RecyclerView.Adapter {
        private final int VIEW_ITEM = 1;

        private ArrayList<HashMap<String, String>> horizontalList;

        // The minimum amount of items to have below your current scroll position
// before loading more.
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        AppCompatTextView textView_item;
        private OnLoadMoreListener onLoadMoreListener;
        private RecyclerView recyclerView;

        public Show_Team_Adapter(ArrayList<HashMap<String, String>> students, RecyclerView recyclerView, AppCompatTextView txt) {
            textView_item = txt;
            this.recyclerView = recyclerView;
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            int VIEW_PROG = 0;
            return horizontalList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
        }

        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_ITEM) {
                return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.team_show_adapter, parent, false));
            } else {
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof StudentViewHolder) {
                ((StudentViewHolder) holder).setData(horizontalList.get(position));
            } else {
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        //
        public class StudentViewHolder extends RecyclerView.ViewHolder {
            private AppCompatImageView edit;
            private CircleImageView img;
            private TextView txtview, role, email, mob;

            StudentViewHolder(View view) {
                super(view);
                txtview = view.findViewById(R.id.team);
                role = view.findViewById(R.id.role);
                img = view.findViewById(R.id.profile_img);

            }

            void setData(HashMap data) {
                if (Objects.requireNonNull(data.get("name")).toString().length() > 0) {
                    txtview.setText(Objects.requireNonNull(data.get("name")).toString().trim());
                    role.setText(Objects.requireNonNull(data.get("role")).toString().trim());
                } else {
                    txtview.setText(data.get("username").toString());
                    role.setText(Objects.requireNonNull(data.get("role")).toString().trim());

                }
                itemView.setOnClickListener(view -> {

                    //  String  coachid = data.get("id").toString();
                    textView_item.setText(data.get("name").toString());
                    recyclerView.setVisibility(View.GONE);
                });
            }

        }

        public class ProgressViewHolder extends RecyclerView.ViewHolder {
            ProgressBar progressBar;

            ProgressViewHolder(View v) {
                super(v);
                progressBar = v.findViewById(R.id.progressBar1);
            }
        }
    }


    private void getUserAPI(int pos, AppCompatTextView textView, String p_coach) {
        //arr.clear();
        String requestData;
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("Accept-Encoding", "identity"
        );
        String teamid = horizontalList.get(pos).getId();
        try {
            JSONObject object = new JSONObject();
            object.put("access_key", "f76646abb2bb5408ecc6d8e36b64c9d8");
            object.put("limit", 500);
            object.put("offset", 0);
            object.put("user_id", p_coach);
            object.put("block_users", "1");

            object.put("team_id", teamid);
            requestData = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
            requestData = "";
        }

        new NetworkCall(context, result -> {
            try {
                if (result.isStatus()) {
                    JSONObject objarr = new JSONObject(result.getData());
                    if (objarr.length() > 0) {

                        JSONArray obj = objarr.getJSONArray("userList");
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject object = obj.getJSONObject(i);
                            if (!userid.equals(object.getString("id"))) {
                                Item map = new Item();
                                map.setTitle(object.getString("firstname"));
                                map.setUser_id(object.getString("id"));
                                map.setRole(object.getString("role"));
                                map.setEmail(object.getString("email"));
                                map.setUsername(object.getString("username"));
                                arr.add(map);
                            }

                            //    arr.add((UserList) (getObject(obj.getString(i), UserList.class)));
                        }
                        if (obj.length() > 0) {
                            //adapter.setLoaded();
                        }
                       /* if (arr.size() == 0) {
                            user_record.setVisibility(View.VISIBLE);
                        } else {
                            //Objects.requireNonNull(getSupportActionBar()).setTitle(arr.size()+" Användare");
                            users.setText(arr.size() + " Användare");
                            if (role.equals(ConsURL.admin) || role.equals(ConsURL.sub_admin)) {
                                publicuser.setText(count + " Offentliga användare");
                                publicuser.setVisibility(View.VISIBLE);
                                users.setVisibility(View.VISIBLE);
                                llpublic.setVisibility(View.VISIBLE);
                            } else {
                                llpublic.setVisibility(View.GONE);

                                publicuser.setVisibility(View.GONE);
                                users.setVisibility(View.GONE);
                                Objects.requireNonNull(getSupportActionBar()).setTitle(arr.size() + " Användare");
                            }
                            user_record.setVisibility(View.GONE);
                        }
                    }else{
                        user_record.setVisibility(View.VISIBLE);

                    }*/

                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // mAdapter.setLoaded();
                popupWindowDogs(arr, textView, pos);

            }
            return null;
        }, requestData).setHeader(map1).execute(ConsURL.BASE_URL_TEST + "TeamuserList");
    }

}

