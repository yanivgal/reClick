package com.reclick.reclick;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you
        // received in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        
        if (!extras.isEmpty()) {
        	Log.e("msg", extras.toString());
            /*
             * Filter messages based on message type. Since it is likely that
             * GCM will be extended in the future with new message types, just
             * ignore any message types you're not interested in, or that you
             * don't recognize.
             */
            if (messageType.equals(
            		GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR)) {
                sendNotification("Send error: " + extras.toString(), "", "");
            } else if (messageType.equals(
            		GoogleCloudMessaging.MESSAGE_TYPE_DELETED)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString(), "", "");
            // If it's a regular GCM message, do some work.
            } else if (messageType.equals(
            		GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
            	
            	// TODO do some work here
            	
                // Post notification of received message.
            	String message = extras.getString("message");
            	String gameId = extras.getString("gameId");
            	String sequence = extras.getString("sequence");
                sendNotification(message, gameId, sequence);
                
                Log.i("GcmIntentService", "Received: " + extras.toString());
            }
        }
        
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

	}
	
	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String gameId, String sequence) {
    	mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Intent intent = new Intent(this, GameActivity.class);
    	intent.putExtra("gameId", gameId);
    	intent.putExtra("sequence", sequence);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
    	
    	NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("reClick")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg)
        .setAutoCancel(true);
    	
    	mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
