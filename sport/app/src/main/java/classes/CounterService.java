package classes;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.sportactivity.map_activity;

public class CounterService extends Service {
    private int i = 0;
    private CountDownTimer countDownTimer;
    public static final String ACTION_RESUME = "com.example.sportactivity.RESUME";

    /**
     * Called when binding to the service is requested, but this service does not support binding,
     * so null is returned.
     * @param intent The intent that was used to bind to this service
     * @return null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when the service is started. If the intent has action ACTION_RESUME, the timer is resumed
     * from the value of 'i' passed in the intent, otherwise it starts from 0.
     * @param intent The intent that was used to start this service
     * @param flags Additional data about this start request
     * @param startId A unique integer representing this specific request to start
     * @return An integer representing the behavior of the service after the onStartCommand() method has run.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null && action.equals(ACTION_RESUME)) {
            // If the action is to resume the timer, start the timer from the last value of 'i'
            i = intent.getIntExtra("TIME", 0);
            TextView textView = intent.getParcelableExtra("TEXT_VIEW");
            if (textView != null) {
                map_activity.txt[0] = textView;
            }
            if (countDownTimer == null) {
                countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        i += 1;
                        int seconds = i % 60;
                        int minutes = (i / 60) % 60;
                        int hours = i / 3600;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        map_activity.txt[0].setText(timeString);
                    }

                    @Override
                    public void onFinish() {

                    }
                };
            }
        } else {
            // If the action is not to resume, start the timer from 0
            i = 0;

            if (countDownTimer == null) {
                countDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        i += 1;
                        int seconds = i % 60;
                        int minutes = (i / 60) % 60;
                        int hours = i / 3600;
                        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        map_activity.txt[0].setText(timeString);
                    }

                    @Override
                    public void onFinish() {

                    }
                };
            }
        }
        countDownTimer.start();
        return START_STICKY;
    }

    /**
     * Called when the service is destroyed. It sends a broadcast intent to notify the activity
     * of the final value of 'i', and cancels the timer.
     */
    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.example.sportactivity.TIMER");
        broadcastIntent.putExtra("TIME", i * 1000);
        sendBroadcast(broadcastIntent);

        countDownTimer.cancel();
        super.onDestroy();
    }
}
