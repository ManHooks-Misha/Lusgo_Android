package com.app.SyrianskaTaekwondo.hejtelge.customClass;


import android.os.Environment;

public class CheckSDCard {
    //Check If SD Card is present or not method
    public boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}