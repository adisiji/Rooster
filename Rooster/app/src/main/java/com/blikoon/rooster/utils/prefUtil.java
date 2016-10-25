package com.blikoon.rooster.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

/**
 * Created by neobyte on 10/24/2016.
 */

public class prefUtil {

    private static final String SAVED_NAME = "default_settings";
    private static prefUtil sSharedPrefs;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean mBulkUpdate = false;
    private WeakReference<Context> mContextRef;

    private prefUtil(Context context) {
        mPref = context.getSharedPreferences(SAVED_NAME, Context.MODE_PRIVATE);
        mContextRef = new WeakReference<Context>(context);
    }


    public static prefUtil getInstance(Context context) {
        if (sSharedPrefs == null) {
            sSharedPrefs = new prefUtil(context.getApplicationContext());
        }
        return sSharedPrefs;
    }

    public static prefUtil getInstance() {
        if (sSharedPrefs != null) {
            return sSharedPrefs;
        }

        //Option 1:
        throw new IllegalArgumentException("Should use getInstance(Context) at least once before using this method.");

        //Option 2:
        // Alternatively, you can create a new instance here
        // with something like this:
        // getInstance(MyCustomApplication.getAppContext());
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }

    public void set(String key, String val) {
        doEdit();
        mEditor.putString(key, val);
        doCommit();
    }

    private void doEdit() {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mPref.edit();
        }
    }

    private void doCommit() {
        if (!mBulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
        }
    }

}
