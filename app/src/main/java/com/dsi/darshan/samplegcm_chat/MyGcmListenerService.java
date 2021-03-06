package com.dsi.darshan.samplegcm_chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by darshan on 12/11/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    String msg,receiverId;
    DbHelper dbHelper;
    String to;

    @Override
    public void onCreate() {
        super.onCreate();

    }


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("TEST",data+"");
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        dbHelper = new DbHelper(getApplicationContext());
        dbHelper.getWritableDatabase();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        to = sharedPreferences.getString(Constants.REG_ID,"not available");

        try {
            JSONObject object = new JSONObject(message);
            msg = object.getString("message");
            receiverId = object.getString("senderId");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dbHelper.insertMessage(msg,receiverId,to,0);



        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(msg);
        notifyBroadcast(msg);
        // [END_EXCLUDE]
    }

    private void notifyBroadcast(String message) {

        MessageData messageData = new MessageData();
        messageData.message = message;
        messageData.me = 0;
        ChatActivityListView.messages.add(messageData);
        Intent mes = new Intent(Constants.MESSAGE_ARRIVED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mes);


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, ChatActivityListView.class);
        intent.putExtra(Constants.RECEIVED_REG_ID,receiverId);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
