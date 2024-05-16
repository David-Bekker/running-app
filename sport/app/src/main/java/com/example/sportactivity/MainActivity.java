package com.example.sportactivity;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import classes.AccountDialog;
import classes.Guest;
import classes.NotificationService;
import classes.Session;
import helpers.FirebaseHelper;
import helpers.MenuHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button[] btn = new Button[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);

        btn[0] = findViewById(R.id.login);
        btn[1] = findViewById(R.id.signUP);
        btn[2] = findViewById(R.id.guest);
        for (int i = 0; i < 3; i++) {
            btn[i].setOnClickListener(this::onClick);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn[0]){//login
            Intent sign_in = new Intent(MainActivity.this, login.class);
            sign_in.putExtra("message_key", "1");
            finish();
            startActivity(sign_in);
        }
        else if(view == btn[1]){//sign in
            Intent sign_in = new Intent(MainActivity.this, login.class);
            sign_in.putExtra("message_key", "0");
            finish();
            startActivity(sign_in);
        }
        else if(view == btn[2]){//guest
            //add random guest to data base
            ArrayList<Session> ses = new ArrayList<Session>();
            Guest guest1 = new Guest(0, 0, randomName(), ses, 0, 0);
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.addGuest(guest1);

            Intent guest = new Intent(MainActivity.this, Main.class);
            guest.putExtra("userName",guest1.getUsername());
            guest.putExtra("set","0");
            finish();
            startActivity(guest);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }

        else if(id == R.id.credits){
            MenuHelper.credits(this);
        }
        else if(id == R.id.credits2){
            MenuHelper.credits2(this);
        }
        return super.onOptionsItemSelected(item);
    }

    String randomName(){
        char[] CHARACTERS = {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        Random RANDOM = new Random();
        StringBuilder sb= new StringBuilder();
        for (int i = 0; i < 10; i++)
            sb.append(CHARACTERS[RANDOM.nextInt(CHARACTERS.length)]);
        return sb.toString();
    }
}