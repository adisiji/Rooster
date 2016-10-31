package com.blikoon.rooster;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blikoon.rooster.utils.prefUtil;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;

import static com.blikoon.rooster.RoosterConnectionService.sLoggedInState;

/**
 * Created by gakwaya on 4/28/2016.
 */

public class RoosterConnection implements ConnectionListener,ChatMessageListener {

    private static final String TAG = "RoosterConnection";
    private static final String tesID = "asneiya31@xmpp.jp";
    private  final Context mApplicationContext;
    private  final String mUsername, mServerHost, mPort;
    private  final String mPassword;
    private  final String mServiceName;
    private  final String jid;
    private XMPPTCPConnection mConnection;
    private BroadcastReceiver uiThreadMessageReceiver;//Receives messages from the ui thread.
    private NotificationManager mNotification;
    private int notifyID = 21;
    private int numMsg = 0;
    private NotificationCompat.Builder mNotifyBuilder;


    public static enum ConnectionState
    {
        CONNECTED ,AUTHENTICATED, CONNECTING ,DISCONNECTING ,DISCONNECTED;
    }

    public static enum LoggedInState
    {
        LOGGED_IN , LOGGED_OUT;
    }


    public RoosterConnection(Context context)
    {
        Log.d(TAG,"RoosterConnection Constructor called.");
        mApplicationContext = context.getApplicationContext();
        mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder =  new NotificationCompat.Builder(context)
                        .setContentTitle("SMS XMPP")
                        .setContentText("You've received new messages.")
                        .setSmallIcon(R.mipmap.ic_launcher);

        mServerHost = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_server",null);
        jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_jid",null);
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_password",null);
        mPort = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_port",null);

        if( jid != null)
        {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        }else
        {
            mUsername ="";
            mServiceName="";
        }
    }

    private XMPPTCPConnectionConfiguration.Builder builder(int port, String domain, String server){
        XMPPTCPConnectionConfiguration.Builder buildoz =
                XMPPTCPConnectionConfiguration.builder();
        buildoz.setHost(server)
                .setPort(port)
                .setServiceName(domain)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        return buildoz;
    }

    private XMPPTCPConnectionConfiguration.Builder builder(int port, String domain){
        XMPPTCPConnectionConfiguration.Builder buildoz =
                XMPPTCPConnectionConfiguration.builder();
        buildoz .setPort(port)
                .setServiceName(domain);
        return buildoz;
    }


    public void connect() throws IOException, XMPPException,SmackException
    {
        Log.d(TAG, "Connecting to server " + mServiceName);
        XMPPTCPConnectionConfiguration.Builder builderx;
        if(!TextUtils.isEmpty(mServerHost) && !TextUtils.isEmpty(mPort)){ //servernya di custom
            builderx = builder(Integer.parseInt(mPort),mServiceName,mServerHost);
        }
        else {
            builderx = builder(5222,mServiceName);
        }
        builderx.setUsernameAndPassword(mUsername, mPassword);
        //builder.setRosterLoadedAtLogin(true);
        builderx.setResource("Rooster");
        try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                MemorizingTrustManager mtm = new MemorizingTrustManager(mApplicationContext);
                sslContext.init(null, new X509TrustManager[]{mtm}, new java.security.SecureRandom());
                builderx.setCustomSSLContext(sslContext);
                builderx.setHostnameVerifier(
                        mtm.wrapHostnameVerifier(new org.apache.http.conn.ssl.StrictHostnameVerifier()));
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        //Set up the ui thread broadcast message receiver.
        setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(builderx.build());
        mConnection.addConnectionListener(this);
        mConnection.setPacketReplyTimeout(15000);
        setSASL();
        mConnection.connect();
        mConnection.login();

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();
        Roster roster = Roster.getInstanceFor(mConnection);
        Presence presence = roster.getPresence(tesID);
        if(presence.isAvailable()){
            Log.d(tesID," available");
        }
        else {
            Log.e(tesID,"ga ada cuii");
        }
    }

    private void setSASL(){
        final Map<String, String> registeredSASLMechanisms = SASLAuthentication.getRegisterdSASLMechanisms();
        for (String mechanism : registeredSASLMechanisms.values()) {
            SASLAuthentication.unBlacklistSASLMechanism(mechanism);
        }
    }

    private void setupUiThreadBroadCastMessageReceiver() {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    //Check if the Intents purpose is to send the message.
                    String action = intent.getAction();
                    if (action.equals(RoosterConnectionService.SEND_MESSAGE)) {
                        Bundle bundle = intent.getExtras();
                        if(bundle!=null){
                            //Send the message.
                            sendMessage(intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY),
                                    intent.getStringExtra(RoosterConnectionService.BUNDLE_TO));
                        }
                    }
                    else if(action.equals("android.provider.Telephony.SMS_RECEIVED")){
                        final String filter_sms = prefUtil.getInstance().getString("filter_text",null);
                        final String batas = prefUtil.getInstance().getString("filter_text2",null);
                        final String kode = prefUtil.getInstance().getString("filter_code",null);
                        final String server = prefUtil.getInstance().getString("server_name",null);
                        Bundle myBundle = intent.getExtras();
                        SmsMessage [] messages = null;
                        String strMessage = "";

                        if (myBundle != null)
                        {
                            Object [] pdus = (Object[]) myBundle.get("pdus");

                            messages = new SmsMessage[pdus.length];

                            for (int i = 0; i < messages.length; i++)
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    String format = myBundle.getString("format");
                                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                                }
                                else {
                                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                                }
                                strMessage += "SMS From: " + messages[i].getOriginatingAddress();
                                strMessage += " : ";
                                String isiSMS =  messages[i].getMessageBody();
                                strMessage += messages[i].getMessageBody();
                                int ind = isiSMS.indexOf(filter_sms);
                                int bts = isiSMS.indexOf(batas);
                                if(ind>-1 && bts >-1){
                                    String deposit = isiSMS.substring(ind+3,bts);
                                    deposit = deposit.replaceAll("[^\\d]", "");
                                    deposit += "."+kode;
                                    deposit += isiSMS.substring(bts-3,bts);
                                    //show notification
                                    mNotifyBuilder.setContentText(messages[i].getMessageBody())
                                            .setNumber(numMsg++);
                                    mNotification.notify(notifyID,mNotifyBuilder.build());
                                    sendMessage("Forward Ouput : "+deposit,server);
                                }
                            }
                            Log.e(" SMS >>", strMessage);
                            Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(RoosterConnectionService.SEND_MESSAGE);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mApplicationContext.registerReceiver(uiThreadMessageReceiver, filter);
    }


    private void sendMessage ( String body ,String toJid)
    {
        Log.d(TAG,"Sending message to :"+ toJid);
        Chat chat = ChatManager.getInstanceFor(mConnection)
                .createChat(toJid,this);
        try
        {
            if(mConnection.isAuthenticated()){
                chat.sendMessage(body);
            }
        }catch (SmackException.NotConnectedException e)
        {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void processMessage(Chat chat, Message message) {

        if(message.getBody()!=null){

            Log.d(TAG,"message.getBody() :"+message.getBody());
            Log.d(TAG,"message.getFrom() :"+message.getFrom());

            String from = message.getFrom();
            String contactJid="";
            if ( from.contains("/"))
            {
                contactJid = from.split("/")[0];
                Log.d(TAG,"The real jid is :" +contactJid);
            }else
            {
                contactJid=from;
            }

            //Bundle up the intent and send the broadcast.
            Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
            intent.setPackage(mApplicationContext.getPackageName());
            intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID,contactJid);
            intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY,message.getBody());
            mApplicationContext.sendBroadcast(intent);
            Log.d(TAG,"Received message from :"+contactJid+" broadcast sent.");
        }

    }


    public void disconnect()
    {
        try
        {
            if (mConnection.isAuthenticated())
            {
                mConnection.disconnect();
            }

        }catch (Exception e)
        {
            RoosterConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
            e.printStackTrace();
        }
        mConnection = null;
        // Unregister the message broadcast receiver.
        if( uiThreadMessageReceiver != null)
        {
            mApplicationContext.unregisterReceiver(uiThreadMessageReceiver);
            uiThreadMessageReceiver = null;
        }
    }


    @Override
    public void connected(XMPPConnection connection) {
        RoosterConnectionService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Connected Successfully");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean arg0) {
        RoosterConnectionService.sConnectionState=ConnectionState.AUTHENTICATED;
        ChatManager.getInstanceFor(mConnection).createChat(tesID,this);
        Log.d(TAG,"Authenticated Successfully");
        showContactListActivityWhenAuthenticated();
    }

    @Override
    public void connectionClosed() {
        RoosterConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        RoosterConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }

    @Override
    public void reconnectingIn(int seconds) {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG,"ReconnectingIn() ");

    }

    @Override
    public void reconnectionSuccessful() {
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED;
        Log.d(TAG,"ReconnectionSuccessful()");

    }

    @Override
    public void reconnectionFailed(Exception e) {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG,"ReconnectionFailed()");

    }

    private void showContactListActivityWhenAuthenticated()
    {
        Intent i = new Intent();
        i.setAction(RoosterConnectionService.UI_AUTHENTICATED);
        i.setPackage(mApplicationContext.getPackageName());
        sLoggedInState = RoosterConnection.LoggedInState.LOGGED_IN;
        mApplicationContext.sendBroadcast(i);
        Log.d(TAG,"Sent the broadcast that we are good");
    }
}
