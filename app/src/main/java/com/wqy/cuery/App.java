package com.wqy.cuery;

import android.app.Application;

/**
 * Created by wqy on 16-12-22.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBHelper.terminate();
    }
}
