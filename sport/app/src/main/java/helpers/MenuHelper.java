package helpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.sportactivity.LeaderBoard;
import com.example.sportactivity.Main;
import com.example.sportactivity.MainActivity;

import org.checkerframework.checker.units.qual.C;

import classes.Credits2Dialog;
import classes.CreditsDialog;

public class MenuHelper {
    public static void exit(Activity activity) {
        // Displays an alert dialog with confirmation message and finishes the activity if user confirms
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.finishAndRemoveTask();
                activity.finishAffinity();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void credits(Activity activity){
        // Displays a credits dialog fragment
        CreditsDialog dialog = new CreditsDialog();
        dialog.show(((FragmentActivity) activity).getSupportFragmentManager(),"credits dialog");
    }
    public static void credits2(Activity activity){
        // Displays a credits dialog fragment
        Credits2Dialog dialog = new Credits2Dialog();
        dialog.show(((FragmentActivity) activity).getSupportFragmentManager(),"credits dialog");
    }

    public static void leaderBoard(Activity activity,String user_name){
        // Starts LeaderBoard activity and passes the user's name as an extra
        Intent intent = new Intent(activity, LeaderBoard.class);
        intent.putExtra("userName",user_name);
        activity.startActivity(intent);
    }
    
}
