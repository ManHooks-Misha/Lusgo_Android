package com.app.SyrianskaTaekwondo.hejtelge.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.app.SyrianskaTaekwondo.hejtelge.pojo.Notification;

import java.util.ArrayList;
import java.util.HashMap;


public class Chatfloatmessage extends BaseDbModel {

    private static final String KEY_ROWID = "_id";

    private static final String KEY_UPDATE_DATE = "UPDATE_DATE";

    private static final String DATABASE_Table1 = "NotificationChattable1";
    // private static final String DATABASE_Table2 = "NotificationCgattableData";
    private static final String KEY_GROUPID = "GroupID";
    private static final String KEY_USERID = "UserID";
    private ArrayList<Notification> arr = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_data = new ArrayList<>();
    private static final String DATABASE_CREATE_SQL_Notice = "CREATE TABLE if not exists "
            + DATABASE_Table1 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY , "
            + KEY_GROUPID + " INTEGER, " + KEY_USERID + " INTEGER, " + " unique (" + KEY_ROWID + "))";
    private static final String Tag = "NotificationChattable";
//    private static final String DATABASE_CREATE_SQL_Notice1 = "CREATE TABLE if not exists "
//            + DATABASE_Table2 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY , "
//            + KEY_ISRead + " text, " + KEY_NID + " text, " + " unique (" + KEY_ROWID + "))";

    private Chatfloatmessage noticeTable;

    public Chatfloatmessage(Context mCtx) {
        super(mCtx, true);
        createTable(DATABASE_CREATE_SQL_Notice, true);
        // createTable(DATABASE_CREATE_SQL_Notice1, true);
    }

    public Chatfloatmessage getInstance(Context context) {
        synchronized (ChatPush.class) {
            if (noticeTable == null) {
                noticeTable = new Chatfloatmessage(context);
            }
        }
        return noticeTable;
    }

    public void insertdata(Context context, String GroupId, String userid) {
        ContentValues values = new ContentValues();
        values.put(KEY_GROUPID, GroupId);
        values.put(KEY_USERID, userid);
        if (mDbForWrite.insert(DATABASE_Table1, null, values) > 0) {
         //   context.sendBroadcast(new Intent(CHAT_FLOAT_NOTIFY_BELL));
        }
    }


    public ArrayList<HashMap<String, String>> getDataNotificationData() {
        String Query = "Select * from " + DATABASE_Table1;
        Cursor c = mDbForWrite.rawQuery(Query, null);
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                do {
                    HashMap map = new HashMap();
//                    Notification getnotice = new Notification();
                    map.put("groupid", c.getString(1));
                    map.put("uid", c.getString(2));

                    // getnotice(c.getString(1));


                    arr_data.add(map);
                } while (c.moveToNext());
            }
        }
        c.close();
        return arr_data;


    }

    public String getMaxUpdatedate() {
        String maxdate = "2015-01-01T01:01:00.000";
        if (checkfordata()) {
            Cursor c = mDbForWrite.rawQuery("SELECT MAX(" + KEY_UPDATE_DATE + ")"
                    + " FROM " + DATABASE_Table1, null);
            if (c.getCount() > 0) {

                if (c.moveToFirst()) {

                    maxdate = c.getString(0);
                    Log.d("Max Date of Notice", maxdate);
                }

            }
            c.close();
        }
        return maxdate;

    }

    private boolean checkfordata() {

        String query = "select " + KEY_UPDATE_DATE + " from " + DATABASE_Table1;
        Cursor c = mDbForWrite.rawQuery(query, null);
        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }

    }

    public boolean Deletefordata() {

        String query = "Delete from " + DATABASE_Table1;
        Cursor c = mDbForWrite.rawQuery(query, null);
        return true;

    }

    public boolean checkId(Integer id) {

        Cursor mCursor = mDbForWrite.rawQuery("SELECT " + KEY_ROWID + " FROM "
                + DATABASE_Table1 + " where " + KEY_ROWID + " = " + id, null);

        if (mCursor.getCount() > 0) {
            mCursor.close();
            return true;
        }
        mCursor.close();
        return false;
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
