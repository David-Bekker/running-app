package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import classes.Data;
import classes.ForgotDialog;
import classes.Guest;
import classes.Session;
import classes.StartSessionDialog;
import classes.User;
import helpers.FirebaseHelper;
import helpers.MenuHelper;

public class Main extends AppCompatActivity implements View.OnClickListener{

    Button btn;
    ProgressBar[] progressBar = new ProgressBar[3];
    TextView[] textViews = new TextView[3];
    boolean flag = false;
    String user_name;
    String sign;
    TextView name;

    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        name = findViewById(R.id.name);
        btn = findViewById(R.id.session);//session

        progressBar[0] = findViewById(R.id.steps);//steps
        progressBar[1] = findViewById(R.id.burn_progress);//burned cal
        progressBar[2] = findViewById(R.id.level);//lvl

        textViews[0] = findViewById(R.id.stepCounter);//steps
        textViews[1] = findViewById(R.id.calBurn);//calories
        textViews[2] = findViewById(R.id.lvlMeter);//level




        Intent intent = getIntent();
        user_name = intent.getStringExtra("userName");
        if (user_name != null) {
            name.setText("welcome " + user_name);
        }

        firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUser(user_name, new FirebaseHelper.ReturnCall<Data>() {
            @Override
            public void onSuccess(Data result) {
                
                if (result instanceof User) {
                    ArrayList<Session> ses = ((User) result).getSes();
                    double calories = 0;
                    int steps = 0;
                    // create a SimpleDateFormat object with the desired format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // create a Date object representing the current date
                    Date currentDate = new Date();
                    // format the date using the SimpleDateFormat object
                    String formattedDate = dateFormat.format(currentDate);
                    for (int i = 0; i < ses.size() - 1; i++) {
                        if (ses.get(i).getDate().equals(formattedDate)) {
                            calories += 0.75 * ((User) result).getWeight() * ses.get(i).getLength();
                            double len = ses.get(i).getLength()*1000;
                            double stepLen = ((User) result).getStep_size()/100;
                            steps += (int) len / stepLen;
                        }
                    }
                    progressBar[0].setProgress((steps/10000)*100);
                    textViews[0].setText("steps: \n" + steps);

                    double cal1 = calories/3000;
                    double cal2 = cal1*100;
                    progressBar[1].setProgress((int) cal2);
                    DecimalFormat cf = new DecimalFormat("#.#");
                    String formattedCalories = cf.format(calories);
                    textViews[1].setText("calories \nburned: \n"+formattedCalories);

                    if (((User) result).getXp() >= 10000) {
                        int xp = 0;
                        xp = ((User) result).getXp()/10000;
                        int lvl = ((User) result).getLvl() + xp;
                        ((User) result).setLvl(lvl);
                        textViews[2].setText("level: \n" + lvl);
                        double xp1 = ((User) result).getXp();
                        double xp2 = xp1%10000;
                        double progress1 = xp2/10000;
                        double progress2 =  progress1*100;
                        progressBar[2].setProgress((int) progress2);
                        ((User) result).setXp((int )xp2);
                        firebaseHelper.addUser(((User) result));
                    }
                    else{
                        int lvl = ((User) result).getLvl();
                        textViews[2].setText("level: \n" + lvl);
                        double xp = ((User) result).getXp();
                        double progress1 = xp/10000;
                        double progress2 =  progress1*100;
                        progressBar[2].setProgress((int) progress2);
                    }
                }
                else if(result instanceof Guest){
                    ArrayList<Session> ses = ((Guest) result).getSes();
                    double calories = 0;
                    int steps = 0;
                    // create a SimpleDateFormat object with the desired format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // create a Date object representing the current date
                    Date currentDate = new Date();
                    // format the date using the SimpleDateFormat object
                    String formattedDate = dateFormat.format(currentDate);
                    for (int i = 0; i < ses.size() - 1; i++) {
                        if (ses.get(i).getDate().equals(formattedDate)) {
                            calories += 0.75 * ((Guest) result).getWeight() * ses.get(i).getLength();
                            double len = ses.get(i).getLength()*1000;
                            double stepLen = ((Guest) result).getStep_size()/100;
                            steps += (int) len / stepLen;
                        }
                    }
                    progressBar[0].setProgress((steps/10000)*100);
                    textViews[0].setText("steps: \n" + steps);

                    double cal1 = calories/3000;
                    double cal2 = cal1*100;
                    progressBar[1].setProgress((int) cal2);
                    DecimalFormat cf = new DecimalFormat("#.#");
                    String formattedCalories = cf.format(calories);
                    textViews[1].setText("calories \nburned: \n"+formattedCalories);

                    if (((Guest) result).getXp() >= 10000) {
                        int xp = 0;
                        xp = ((Guest) result).getXp()/10000;
                        int lvl = ((Guest) result).getLvl() + xp;
                        ((Guest) result).setLvl(lvl);
                        textViews[2].setText("level: \n" + lvl);
                        double xp1 = ((Guest) result).getXp();
                        double xp2 = xp1%10000;
                        double progress1 = xp2/10000;
                        double progress2 =  progress1*100;
                        progressBar[2].setProgress((int) progress2);
                        ((Guest) result).setXp((int )xp2);
                        firebaseHelper.addGuest(((Guest) result));
                    }
                    else{
                        int lvl = ((Guest) result).getLvl();
                        textViews[2].setText("level: \n" + lvl);
                        double xp = ((Guest) result).getXp();
                        double progress1 = xp/10000;
                        double progress2 =  progress1*100;
                        progressBar[2].setProgress((int) progress2);
                    }
                }
            }

            @Override
            public void onError() {
                Toast.makeText(Main.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        sign = intent.getStringExtra("set");
        if (sign != null) {
            if (sign.equals("0")) {//go to settings or not to go
                Intent main = new Intent(Main.this, settings.class);
                main.putExtra("userName",user_name);
                startActivity(main);
            }
        }

        btn.setOnClickListener(this::onClick);


    }

    @Override
    public void onClick(View view) {
        if(view == btn){//start session
            StartSessionDialog dialog = new StartSessionDialog();
            Bundle args = new Bundle();
            args.putString("userName",user_name);
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(),"start session");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3,menu);
        menu.getItem(4).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }
        else if (id == R.id.back) {
            Intent intent = new Intent(Main.this, MainActivity.class);
            finish();
            startActivity(intent);
        }
        else if (id == R.id.statistics) {
            Intent intent = new Intent(Main.this, statistics.class);
            intent.putExtra("userName",user_name);
            finish();
            startActivity(intent);
        }
        else if(id == R.id.credits){
            MenuHelper.credits(this);
        }
        else if(id == R.id.credits2){
            MenuHelper.credits2(this);
        }
        else if (id == R.id.leaderboard) {
            Intent intent = new Intent(Main.this, LeaderBoard.class);
            intent.putExtra("userName",user_name);
            finish();
            MenuHelper.leaderBoard(this,user_name);
        }
        else if(id == R.id.settings){
            Intent intent = new Intent(Main.this, settings.class);
            intent.putExtra("userName",user_name);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}