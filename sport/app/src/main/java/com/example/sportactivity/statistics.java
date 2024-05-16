package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import classes.Data;
import classes.Guest;
import classes.User;
import helpers.FirebaseHelper;
import helpers.MenuHelper;


public class statistics extends AppCompatActivity {
    TextView[] txt = new TextView[5];
    String username;

    FirebaseHelper firebaseHelper = new FirebaseHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        txt[0] = findViewById(R.id.textView);
        txt[1] = findViewById(R.id.textView2);
        txt[2] = findViewById(R.id.textView3);
        txt[3] = findViewById(R.id.textView4);
        txt[4] = findViewById(R.id.textView6);


        Intent intent = getIntent();
        username = intent.getStringExtra("userName");
        firebaseHelper.getUser(username, new FirebaseHelper.ReturnCall<Data>() {
            @Override
            public void onSuccess(Data result) {
                if (result instanceof User || result instanceof Guest) {
                    int steps = 0;
                    double calories = 0;
                    if (((Guest) result).getSes() != null) {
                        if (((Guest) result).getSes().size()>0) {
                            for (int i = 0;i < ((Guest) result).getSes().size() && ((Guest) result).getSes().get(i) != null; i++) {
                                steps = (int) (steps + (((Guest) result).getSes().get(i).getLength()/((Guest) result).getStep_size()));
                                calories = calories + (((Guest) result).getSes().get(i).getTime() * (2.2*3.5*((Guest) result).getWeight())/200);
                            }
                        }
                    }
                    txt[0].setText("monthly steps: "+ (steps/30));
                    txt[1].setText("average steps\ntaken in a day: "+ (steps/365));
                    txt[2].setText("total caloriees\n burned: " + calories);
                    txt[3].setText("calories burned\nin a month: "+(calories/30));
                    txt[4].setText("average calories\nburned per day: " + (calories/365));

                }
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }
        else if (id == R.id.back) {
            Intent sign_in = new Intent(statistics.this, Main.class);
            sign_in.putExtra("set","1");
            sign_in.putExtra("userName",username);
            finish();
            startActivity(sign_in);
        }
        else if(id == R.id.credits){
            MenuHelper.credits(this);
        }
        else if(id == R.id.credits2){
            MenuHelper.credits2(this);
        }
        else if (id == R.id.leaderboard) {
            MenuHelper.leaderBoard(this,username);
        }
        return super.onOptionsItemSelected(item);
    }
}