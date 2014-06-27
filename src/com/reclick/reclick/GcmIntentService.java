package com.reclick.reclick;

import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
	
	private Map<String,Command> gcmCommands;
	
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");
		
		gcmCommands = new HashMap<String, GcmIntentService.Command>();
		
		gcmCommands.put("gameCreatedCommand", new GameCreatedCommand());
		gcmCommands.put("gameCreatedCreatorCommand", new GameCreatedCreatorCommand());
		gcmCommands.put("playerMadeHisMoveCommand", new PlayerMadeHisMoveCommand());
		gcmCommands.put("playerFailedCommand", new PlayerFailedCommand());
	}

	/**
	 * This method gets the GCM messages from the BrocastReceiver.
	 * Here we decides what to do with the received GCM message.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("onHandleIntent", "GCM message");
		Bundle extras = intent.getExtras();
		try {
			gcmCommands.get(extras.getString("type")).exec(extras);
		} catch (NullPointerException e) { }
        
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private interface Command {
		void exec(Bundle extras);
	}
	
	private class GameCreatedCommand implements Command {
		@Override
		public void exec(Bundle extras) {			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(GcmUiUpdateReceiver.ACTION_GAME_CREATED);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtras(extras);
			sendBroadcast(broadcastIntent);
		}
	}
	
	private class GameCreatedCreatorCommand implements Command {
		@Override
		public void exec(Bundle extras) {			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(GcmUiUpdateReceiver.ACTION_GAME_CREATED_CREATOR);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtras(extras);
			sendBroadcast(broadcastIntent);
		}
	}
	
	private class PlayerMadeHisMoveCommand implements Command {
		@Override
		public void exec(Bundle extras) {
			String message = extras.getString("message");
        	String gameId = extras.getString("gameId");
        	String sequence = extras.getString("sequence");
        	sendPlayerMadeHisMoveNotification(message, gameId, sequence);
		}
	}
	
	private class PlayerFailedCommand implements Command {
		@Override
		public void exec(Bundle extras) {
			String message = extras.getString("message");
    		sendPlayerFailedNotification(message);
		}
	}
	
    private void sendPlayerMadeHisMoveNotification(String msg, String gameId, String sequence) {
    	mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Intent intent = new Intent(this, GameActivity.class);
    	intent.putExtra("gameId", gameId);
    	intent.putExtra("sequence", sequence);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
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
    
    private void sendPlayerFailedNotification(String msg) {
    	mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
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
