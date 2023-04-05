package com.app.SyrianskaTaekwondo.hejtelge.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.SyrianskaTaekwondo.hejtelge.GroupChatList.CHAT_NOTIFY_BELL;
import static com.app.SyrianskaTaekwondo.hejtelge.HomePage.CHAT_FLOAT_NOTIFY_BELL;


public class ChatPush extends BaseDbModel {

    private static final String KEY_ROWID = "_id";
    private static final String DATABASE_Table1 = "NotificationChattable";
    private static final String KEY_GROUPID = "GroupID";
    private static final String KEY_USERID = "UserID";
    private ArrayList<HashMap<String, String>> arr_data = new ArrayList<>();
    private static final String DATABASE_CREATE_SQL_Notice = "CREATE TABLE if not exists "
            + DATABASE_Table1 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY , "
            + KEY_GROUPID + " INTEGER, " + KEY_USERID + " INTEGER, " + " unique (" + KEY_ROWID + "))";
    private static final String Tag = "NotificationChattable";
    private ChatPush noticeTable;

    public ChatPush(Context mCtx) {
        super(mCtx, true);
        createTable(DATABASE_CREATE_SQL_Notice, true);
    }

    public ChatPush getInstance(Context context) {
        synchronized (ChatPush.class) {
            if (noticeTable == null) {
                noticeTable = new ChatPush(context);
            }
        }
        return noticeTable;
    }

    public void insertdata(Context context, String GroupId, String userid) {
        ContentValues values = new ContentValues();
        values.put(KEY_GROUPID, GroupId);
        values.put(KEY_USERID, userid);
        if (mDbForWrite.insert(DATABASE_Table1, null, values) > 0) {
            context.sendBroadcast(new Intent(CHAT_NOTIFY_BELL));
            context.sendBroadcast(new Intent(CHAT_FLOAT_NOTIFY_BELL));
        }
    }
    public ArrayList<HashMap<String, String>> getDataNotificationData() {
        String Query = "Select * from " + DATABASE_Table1;
        Cursor c = mDbForWrite.rawQuery(Query, null);
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                do {
                    HashMap map = new HashMap();
                    map.put("groupid", c.getString(1));
                    map.put("uid", c.getString(2));

                    arr_data.add(map);
                } while (c.moveToNext());
            }
        }
        c.close();
        return arr_data;


    }

    public void deleteAll(String date) {

        mDbForWrite.delete(DATABASE_Table1, KEY_GROUPID + " = " + "'" + date + "'", null);
    }

    public void clearTable() {
        mDbForWrite.delete(DATABASE_Table1, null, null);
    }

    public boolean getSize() {

        Cursor c = mDbForWrite
                .query(DATABASE_Table1, null, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        }
        c.close();
        return false;

    }

}
