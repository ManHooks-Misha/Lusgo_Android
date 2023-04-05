package com.app.SyrianskaTaekwondo.hejtelge.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class HelpingClass {

    private static ProgressDialog dialog = null;


    public static void showProgress(Context context) {
        dialog = ProgressDialog.show(context, "", "Vänta", true);
        dialog.setCancelable(false);
    }

    public static void hideProgress() {
        if (dialog != null) {
            dialog.hide();
        }
    }
}
