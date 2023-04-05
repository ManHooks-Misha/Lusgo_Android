package com.app.SyrianskaTaekwondo.hejtelge.utils;
import vk.help.HelpApp;

public class AppApplication extends HelpApp {


    @Override
    public void onCreate() {
        setLogLevel(Level.NONE);
        setTimeout(60);

        super.onCreate();
        setToastMessageStyle(ToastMessageStyle.FIRST_WORD_CAPITAL);

    }
}
