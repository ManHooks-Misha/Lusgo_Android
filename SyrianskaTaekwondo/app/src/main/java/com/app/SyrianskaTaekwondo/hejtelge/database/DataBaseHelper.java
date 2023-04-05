package com.app.SyrianskaTaekwondo.hejtelge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
   // protected SQLiteDatabase mDbForRead;
    private static final String DATABASE_NAME = "Lusgo.db";
    private static final int DATABASE_VERSION = 2;
//    private static final String TAG = "DATABASEHELPER";

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    public void  createTable(String cmd, SQLiteDatabase db) {
        db.execSQL(cmd);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       // db.needUpgrade(DATABASE_VERSION);
  /*      db.execSQL("DROP TABLE IF EXISTS " + Videotable.TABLE_VIDEO);
        db.execSQL("DROP TABLE IF EXISTS " + MotivationalTable.DATABASE_Table1);
        db.execSQL("DROP TABLE IF EXISTS " + EventTable.DATABASE_Table2);
        db.execSQL("DROP TABLE IF EXISTS " + LayoutTable.DATABASE_Table1);
        db.execSQL("DROP TABLE IF EXISTS " + GalleryTable.DATABASE_Table1);
        db.execSQL("DROP TABLE IF EXISTS " + SettingTable.DATABASE_Table1);
        db.execSQL("DROP TABLE IF EXISTS "
                + OnlineurlAndMarqueeTable.DATABASE_Table1);
        db.execSQL("DROP TABLE IF EXISTS " + NoticeTable.DATABASE_Table1);
        createTable(Videotable.DATABASE_CREATE_SQL_Video, db);*/

    }
}
