package com.example.alex.settings;

import com.firebase.client.Firebase;

/**
 * Created by Alex on 6/5/2016.
 */
public class ApplicationClass extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
