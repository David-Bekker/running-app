package classes;

import static classes.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.sportactivity.MainActivity;
import com.example.sportactivity.R;

import java.util.Calendar;

public class NotificationService extends Service {

    // onCreate is called when the service is created
    @Override
    public void onCreate(){
        super.onCreate();
    }

    // onDestroy is called when the service is destroyed
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    // onStartCommand is called when the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check if the app was not opened for 2 days
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long lastOpenedTime = prefs.getLong("last_opened_time", 0);
        long currentTime = System.currentTimeMillis();
        long twoDaysInMillis = 2 * 24 * 60 * 60 * 1000; // 2 days in milliseconds
        if (currentTime - lastOpenedTime >= twoDaysInMillis) {
            // If the app was not opened for 2 days, create and show the notification
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sport Activity")
                    .setContentText("Don't forget to do sport sessions this week")
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentIntent(pendingIntent).build();
            startForeground(1, notification);

            // Stop the foreground service when the notification is tapped
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent stopIntent = PendingIntent.getService(this, 0, new Intent(this, NotificationService.class), PendingIntent.FLAG_CANCEL_CURRENT);
            notification.contentIntent = PendingIntent.getBroadcast(this, 0, new Intent("dummy_action"), PendingIntent.FLAG_UPDATE_CURRENT);
            notification.deleteIntent = stopIntent;
        } else {
            // If the app was opened within 2 days, remove the foreground state
            stopForeground(true);
        }
        // Update the last opened time in the preferences
        prefs.edit().putLong("last_opened_time", currentTime).apply();
        return START_STICKY;
    }

    // onBind is called when a client binds to the service with bindService()
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
