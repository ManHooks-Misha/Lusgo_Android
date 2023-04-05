package com.app.SyrianskaTaekwondo.hejtelge;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.GridLayoutManager;

import com.app.SyrianskaTaekwondo.hejtelge.adapters.ChatUserAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.database.MuteNotify;
import com.app.SyrianskaTaekwondo.hejtelge.databinding.ActivityGroupChatDetailsUserBinding;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.GroupList;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.UserList;
import com.app.SyrianskaTaekwondo.hejtelge.utility.CommonMethods;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import vk.help.MasterActivity;

public class GroupChatDetailsUser extends MasterActivity {
    ActivityGroupChatDetailsUserBinding binding;
    private GroupList data;
    private String groupid, groupname;
    public static List<UserList> arr = new ArrayList<>();
    CommonMethods cmn;
    private ChatUserAdapter libraryHotAdapter;
    MuteNotify muteTable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatDetailsUserBinding.inflate(getLayoutInflater());
        cmn = new CommonMethods();
        setContentView(binding.getRoot());
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        muteTable = new MuteNotify(this).getInstance(this);

        CommonMethods cmn = new CommonMethods();
        String userid = cmn.getPrefsData(GroupChatDetailsUser.this, "id", "");
        if (getIntent() != null && getIntent().hasExtra("data")) {
            data = (GroupList) getObject(getIntent().getStringExtra("data"), GroupList.class);
            groupid = getIntent().getStringExtra("id");
            groupname = getIntent().getStringExtra("name");
            binding.txt.setText(groupname);
            arr = data.getUsers();

            if (cmn.getPrefsData(this,"value","").equals("1")) {
                ArrayList<HashMap<String, String>> arr_ = muteTable.getDataNotificationDataMute();
                for (int i = 0; i < arr_.size(); i++) {
                    String mutestatus = arr_.get(i).get("mutestatus");
                    String groupidstr = arr_.get(i).get("groupid");
                    if (groupid.equals(groupidstr) && mutestatus.equals("true")) {
                        binding.mutenotification.setChecked(true);
                        break;
                    } else {
                        binding.mutenotification.setChecked(false);
                    }

                }
            } else {
                binding.mutenotification.setChecked(true);

            }
            libraryHotAdapter = new ChatUserAdapter(
                    arr, GroupChatDetailsUser.this, userid, groupid, groupname, "1", "");
            binding.listEdit.setLayoutManager(new GridLayoutManager(GroupChatDetailsUser.this, 1));
            binding.listEdit.setAdapter(libraryHotAdapter);
            Glide.with(GroupChatDetailsUser.this)
                    .load(data.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.group)
                    .into(binding.profile);
        }
        binding.infoTxtDetailsUser.setOnClickListener(view -> {
            cmn.customDialogCalander(GroupChatDetailsUser.this,
                    "Om du är admin för denna chatt och vill öppna/stänga chatten gå till Grupper om det är en gruppchatt alternativt Teamlista om det är en teamchatt");

        });

       // binding.mutenotification.setChecked(true);
        binding.mutenotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cmn.setPrefsData(GroupChatDetailsUser.this, "value", "1");
                if (!groupid.isEmpty()) {
                    cmn.setPrefsData(GroupChatDetailsUser.this, "Mute", "true");
                    //   cmn.setPrefsData(GroupChatDetailsUser.this, "Mute", "true");
                    muteTable.update("true", groupid);

                } else {
                    cmn.setPrefsData(GroupChatDetailsUser.this, "Mute", "false");
                    muteTable.update("false", groupid);

                }
            }else {
                cmn.setPrefsData(GroupChatDetailsUser.this, "value", "1");
                cmn.setPrefsData(GroupChatDetailsUser.this, "Mute", "false");
                muteTable.update("false", groupid);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}