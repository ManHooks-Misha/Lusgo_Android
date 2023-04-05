package com.app.SyrianskaTaekwondo.hejtelge.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

public class BaseDbModel {

    private DataBaseHelper mDbHelper;
    protected SQLiteDatabase mDbForWrite;
    private final Context mCtx;
    protected SQLiteDatabase mDbForRead;

    public BaseDbModel(Context mCtx, boolean isForwrite) {
        this.mCtx = mCtx;
        if (!isOpen()){
            try {
                if (isForwrite){
                    openForWrite();
                }else {
                    openForRead();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public BaseDbModel openForWrite() throws SQLException {
        mDbHelper = new DataBaseHelper(mCtx);
        mDbForWrite = mDbHelper.getWritableDatabase();
        return this;
    }

    public BaseDbModel openForRead() throws SQLException {
        mDbHelper = new DataBaseHelper(mCtx);
        mDbForRead = mDbHelper.getReadableDatabase();

        return this;
    }

    private boolean isOpen() {
        boolean open = false;
        if (mDbForWrite != null && mDbForWrite.isOpen())
            open = true;
        return open;
    }

    private void close() {
        if (isOpen()) {
            mDbHelper.close();
            mDbForWrite.close();

        }
    }

    public  void createTable(String cmd, boolean isForWrite){
        if (isForWrite){
            mDbHelper.createTable(cmd,mDbForWrite);
        }else {
            mDbHelper.createTable(cmd,mDbForRead);
        }
    }



}
