package classes;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "notificationServiceChannel";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannel();
    }

    /**
     * Creates a notification channel for the application.
     * This is required for versions of Android Oreo (8.0) and higher.
     * Channels are used for grouping notifications together and giving the user control over them.
     */
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Sport Activity", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
