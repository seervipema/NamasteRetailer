package com.pemaseervi.myretailer.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.HomeActivity;
import com.pemaseervi.myretailer.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.pemaseervi.myretailer.utility.MyBroadCastReceiver.ACTION_SNOOZE;
import static com.pemaseervi.myretailer.utility.MyBroadCastReceiver.EXTRA_NOTIFICATION_ID;

public class FcmMessagingService  extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String TAG = "Notify:";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title= remoteMessage.getData().get("title");
        String message= remoteMessage.getData().get("body");
        createNotificationChannel();
        sendNotification(title,message);
      for(int count=0;count<(DBQueries.ordersItemModelList.size());count++){
          if(DBQueries.ordersItemModelList.get(count).getOrderId().equals(title)){
              DBQueries.ordersItemModelList.get(count).setOrderStatus(message);
          }
//          Log.d("OrderId", DBQueries.ordersItemModelList.get(count).getOrderId());
//          Log.d("OrderStatus", DBQueries.ordersItemModelList.get(count).getOrderStatus());
      }

       super.onMessageReceived(remoteMessage);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void sendNotification(String title, String message) {
        Intent snoozeIntent = new Intent(getBaseContext(), MyBroadCastReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 12);

        Log.e(TAG, snoozeIntent.getExtras().toString());

        Log.e(TAG, "snoozeIntent id: " + snoozeIntent.getIntExtra(EXTRA_NOTIFICATION_ID, -1));

        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(getBaseContext(), 12, snoozeIntent, 0);
        Intent intent =new Intent(this, HomeActivity.class);
        intent.putExtra("orderFragment",2);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(String.format("%s", title))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(12, builder.build());
    }

    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String recent_token=s;
        SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString(getString(R.string.FCM_TOKEN),recent_token);
        editor.commit();

    }
}
