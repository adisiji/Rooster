package com.blikoon.rooster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by neobyte on 10/31/2016.
 */

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /***** For start Service  ****/
        Intent myIntent = new Intent(context, RoosterConnectionService.class);
        context.startService(myIntent);
    }
}
