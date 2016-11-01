package com.blikoon.rooster;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.blikoon.rooster.utils.prefUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by gakwaya on 4/28/2016.
 */
public class RoosterConnectionService extends Service {
    private static final String TAG ="RoosterService";

    public static final String UI_AUTHENTICATED = "com.blikoon.rooster.uiauthenticated";
    public static final String SEND_MESSAGE = "com.blikoon.rooster.sendmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";
    public static final String DESTROX = "com.blikoon.rooster.hancurr";
    public static final String NEW_MESSAGE = "com.blikoon.rooster.newmessage";
    public static final String BUNDLE_FROM_JID = "b_from";
    private NotificationManager notificationManager;
    public static RoosterConnection.ConnectionState sConnectionState;
    public static RoosterConnection.LoggedInState sLoggedInState;
    private boolean mActive;//Stores whether or not the thread is active
    private Thread mThread;
    private Handler mTHandler;//We use this handler to post messages to
    //the background thread.
    private RoosterConnection mConnection;

    public RoosterConnectionService() {

    }
    public static RoosterConnection.ConnectionState getState()
    {
        if (sConnectionState == null)
        {
            return RoosterConnection.ConnectionState.DISCONNECTED;
        }
        return sConnectionState;
    }

    public static RoosterConnection.LoggedInState getLoggedInState()
    {
        if (sLoggedInState == null)
        {
            return RoosterConnection.LoggedInState.LOGGED_OUT;
        }
        return sLoggedInState;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preparePref();
        Log.d(TAG,"onCreate()");
    }

    private void initConnection()
    {
        if( mConnection == null)
        {
            mConnection = new RoosterConnection(this);
        }
        try
        {
            mConnection.connect();

        }catch (IOException |SmackException |XMPPException e)
        {
            Toast.makeText(getApplicationContext(),"Something went wrong, make sure the credentials are right and try again",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }

    }

    private void preparePref(){
        if(prefUtil.getInstance(this).getString("server_name")==null){
            Log.e("server_name" , "belum ADA");
            prefUtil.getInstance(this).set("server_name",getString(R.string.default_server));
        }
        else
        {
            Log.d("server_name >> ", prefUtil.getInstance(this).getString("server_name"));
        }

        if(prefUtil.getInstance(this).getString("filter_text")==null){
            Log.e("filter_text" , "belum ADA");
            prefUtil.getInstance(this).set("filter_text",getString(R.string.default_filter));
        }
        if(prefUtil.getInstance(this).getString("filter_text2")==null){
            Log.e("filter_text2" , "belum ADA");
            prefUtil.getInstance(this).set("filter_text2",getString(R.string.default_filter2 ));
        }

        if(prefUtil.getInstance(this).getString("filter_code")==null){
            Log.e("filter_code" , "belum ADA");
            prefUtil.getInstance(this).set("filter_code",getString(R.string.default_code));
        }
        else
        {
            Log.d("filter_code >> ", prefUtil.getInstance(this).getString("filter_code"));
        }

        if(prefUtil.getInstance(this).getString("filter_number")==null){
            Log.e("filter_number" , "belum ADA");
            prefUtil.getInstance(this).set("filter_number",getString(R.string.default_number));
        }

    }


    public void start()
    {
        Log.d(TAG," Service Start() function called.");
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }

    }

    public void stop()
    {
        Log.d(TAG,"stop()");
        Intent intent = new Intent(DESTROX);
        intent.setPackage(getApplicationContext().getPackageName());
        getApplicationContext().sendBroadcast(intent);
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if( mConnection != null)
                {
                    mConnection.disconnect();
                }
            }
        });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand()");
        start();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext());
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setTicker(getString(R.string.ticker_text));
        notification.setContentTitle("XMPP-SMS Service");
        notification.setContentText("Running");
        notification.setSmallIcon(R.drawable.ic_stat_maps_layers);
        notification.setOngoing(true);
        notification.setContentIntent(pendingIntent);

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification.build());
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
        notificationManager.cancel(0);
        stopSelf();
        stop();
    }
}
