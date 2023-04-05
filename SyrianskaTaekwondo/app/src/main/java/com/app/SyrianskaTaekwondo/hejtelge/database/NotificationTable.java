package com.app.SyrianskaTaekwondo.hejtelge.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;


import com.app.SyrianskaTaekwondo.hejtelge.pojo.Notification;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.SyrianskaTaekwondo.hejtelge.HomePage.NOTIFY_BELL;


public class NotificationTable extends BaseDbModel {

    private static final String KEY_ROWID = "_id";

    private static final String KEY_UPDATE_DATE = "UPDATE_DATE";

    private static final String DATABASE_Table1 = "Notificationtable";
    private static final String DATABASE_Table2 = "NotificationtableData";
    private static final String KEY_ISRead = "ISRead";
    private static final String KEY_NID = "N_id";
    private ArrayList<Notification> arr = new ArrayList<>();
    private ArrayList<HashMap<String, String>> arr_data = new ArrayList<>();
    private static final String DATABASE_CREATE_SQL_Notice = "CREATE TABLE if not exists "
            + DATABASE_Table1 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY , "
            + KEY_ISRead + " INTEGER, " + " unique (" + KEY_ROWID + "))";
    private static final String Tag = "Notificationtable";
    private static final String DATABASE_CREATE_SQL_Notice1 = "CREATE TABLE if not exists "
            + DATABASE_Table2 + " (" + KEY_ROWID + " INTEGER PRIMARY KEY , "
            + KEY_ISRead + " text, " + KEY_NID + " text, " + " unique (" + KEY_ROWID + "))";

    private NotificationTable noticeTable;

    public NotificationTable(Context mCtx) {
        super(mCtx, true);
        createTable(DATABASE_CREATE_SQL_Notice, true);
        createTable(DATABASE_CREATE_SQL_Notice1, true);
    }

    public NotificationTable getInstance(Context context) {
        synchronized (NotificationTable.class) {
            if (noticeTable == null) {
                noticeTable = new NotificationTable(context);
            }
        }
        return noticeTable;
    }

    public void insertdata(Context context) {
        ContentValues values = new ContentValues();
        values.put(KEY_ISRead, "0");
        if (mDbForWrite.insert(DATABASE_Table1, null, values) > 0) {
            context.sendBroadcast(new Intent(NOTIFY_BELL));
        }
    }

    public void insertdataData(Context context, String nid) {
        ContentValues values = new ContentValues();
        values.put(KEY_ISRead, "0");
        values.put(KEY_NID, nid);
        if (mDbForWrite.insert(DATABASE_Table2, null, values) > 0) {
            context.sendBroadcast(new Intent(NOTIFY_BELL));
        }
    }


    public void update(int val, String nid) {
        //Log.d(Tag, "come in update");
        ContentValues values = new ContentValues();

        values.put(KEY_ISRead, val);
        mDbForWrite.update(DATABASE_Table2, values, KEY_NID + " =" + nid,
                null);

    }

    public boolean CheckIsDataAlreadyInDBorNot(String fieldValue) {
        // SQLiteDatabase sqldb = getWritableDatabase();
        String Query = "Select * from " + DATABASE_Table2 + " where " + KEY_NID + " = '" + fieldValue + "'";
        Cursor cursor = mDbForWrite.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean Checkdate(String fieldValue) {

        // SQLiteDatabase sqldb = getWritableDatabase();
        String Query = "Select * from " + DATABASE_Table1 + " where " + KEY_UPDATE_DATE + " != " + "'" + fieldValue + "'";
        Cursor cursor = mDbForWrite.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public ArrayList<Notification> getDataNotification() {
        String Query = "Select * from " + DATABASE_Table1;
        Cursor c = mDbForWrite.rawQuery(Query, null);
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                do {
                    Notification getnotice = new Notification();


                    getnotice.setN_id(c.getString(1));


                    arr.add(getnotice);
                } while (c.moveToNext());
            }
        }
        c.close();
        return arr;


    }

    public ArrayList<HashMap<String, String>> getDataNotificationData() {
        String Query = "Select * from " + DATABASE_Table2;
        Cursor c = mDbForWrite.rawQuery(Query, null);
        if (c.getCount() > 0) {

            if (c.moveToFirst()) {

                do {
                    HashMap map = new HashMap();
//                    Notification getnotice = new Notification();
                    map.put("isRead", c.getString(1));
                    map.put("nid", c.getString(2));

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

    public boolean Deletefordata(String date) {

        String query = "Delete from " + DATABASE_Table1 + " Where " + KEY_UPDATE_DATE + " != " + "'" + date + "'";
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

        mDbForWrite.delete(DATABASE_Table1, KEY_UPDATE_DATE + " != " + "'" + date + "'", null);
    }

    public void clearTable() {
        mDbForWrite.delete(DATABASE_Table1, null, null);
    }

    public void clearTabledata() {
        mDbForWrite.delete(DATABASE_Table2, null, null);
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
