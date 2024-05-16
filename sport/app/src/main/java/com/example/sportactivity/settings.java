package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import classes.AccountDialog;
import classes.Data;
import classes.Guest;
import classes.User;
import helpers.FirebaseHelper;
import helpers.MenuHelper;

public class settings extends AppCompatActivity {
    Button account_setting;
    Button save;
    EditText weight;
    EditText step_len;
    Boolean flag = false;
    String username;
    TextView name;

    FirebaseHelper firebaseHelper = new FirebaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        weight = findViewById(R.id.weight);
        step_len = findViewById(R.id.step);
        save = findViewById(R.id.save);
        account_setting = findViewById(R.id.account);
        name = findViewById(R.id.name);

        Intent intent = getIntent();
        username = intent.getStringExtra("userName");
        name.setText("welcome " + username);
        firebaseHelper.getUser(username, new FirebaseHelper.ReturnCall<Data>() {
            @Override
            public void onSuccess(Data result) {
                if (result instanceof User) {
                    if (result == null) {
                        Toast.makeText(settings.this, "Failed to retrieve user data. Please try again later.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if (((User) result).getWeight() != 0 && result.getStep_size() != 0) {
                        step_len.setText("" + result.getStep_size());
                        weight.setText("" + result.getWeight());
                        flag = true;
                    }
                }
                else {
                    if (((Guest) result).getWeight() != 0 && result.getStep_size() != 0) {
                        step_len.setText("" + result.getStep_size());
                        weight.setText("" + result.getWeight());
                        flag = true;
                    }
                }
            }

            @Override
            public void onError() {
                Toast.makeText(settings.this, "error", Toast.LENGTH_LONG).show();
            }
        });

        account_setting.setOnClickListener(this::onClick);
        save.setOnClickListener(this::onClick);
    }

    void onClick(View  view){
        if (view == save) {
            if (!weight.getText().toString().equals("")&&!step_len.getText().toString().equals("")) {
                flag = true;
                firebaseHelper.getUser(username, new FirebaseHelper.ReturnCall<Data>() {
                    @Override
                    public void onSuccess(Data result) {
                        if((Double.parseDouble(weight.getText().toString())<150 && Double.parseDouble(weight.getText().toString())>40)&&
                            (Double.parseDouble(step_len.getText().toString())<100 && Double.parseDouble(step_len.getText().toString())>40)){
                            if (result instanceof User) {
                                result.setWeight(Double. parseDouble(weight.getText().toString()));
                                result.setStep_size(Double. parseDouble(step_len.getText().toString()));
                                firebaseHelper.addUser((User) result);
                                Toast.makeText(settings.this, "saved", Toast.LENGTH_LONG).show();
                            }
                            else{
                                result.setWeight(Double.parseDouble(weight.getText().toString()));
                                result.setStep_size(Double.parseDouble(step_len.getText().toString()));
                                firebaseHelper.addGuest((Guest) result);
                                Toast.makeText(settings.this, "saved", Toast.LENGTH_LONG).show();
                            }
                        }
                        else Toast.makeText(getApplicationContext(), "must enter valid input", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
        else if (view == account_setting) {
            openAccountDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu3,menu);
        menu.getItem(4).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }
        else if (id == R.id.back) {
            Intent intent = new Intent(settings.this, Main.class);
            if (!weight.getText().toString().equals("")&&!step_len.getText().toString().equals("")) {
                double weightValue = Double.parseDouble(weight.getText().toString());
                double stepLenValue = Double.parseDouble(step_len.getText().toString());
                if((weightValue<150 && weightValue>40) && (stepLenValue<100 && stepLenValue>40)){
                    if (flag) {
                        intent.putExtra("userName",username);
                        intent.putExtra("set","1");
                        finish();
                        startActivity(intent);
                    }
                    else Toast.makeText(this, "must save", Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(this, "must enter valid input", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(this, "must enter all fields", Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.statistics) {
            Intent intent = new Intent(settings.this, statistics.class);
            if (!weight.getText().toString().equals("")&&!step_len.getText().toString().equals("")) {
                double weightValue = Double.parseDouble(weight.getText().toString());
                double stepLenValue = Double.parseDouble(step_len.getText().toString());
                if((weightValue<150 && weightValue>40) && (stepLenValue<100 && stepLenValue>40)){
                    if (flag) {
                        intent.putExtra("userName",username);
                        intent.putExtra("set","1");
                        finish();
                        startActivity(intent);
                    }
                    else Toast.makeText(this, "must save", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(this, "must enter valid input", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "must enter all fields", Toast.LENGTH_SHORT).show();
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

    public void openAccountDialog(){
        AccountDialog dialog = new AccountDialog();
        Bundle bundle = new Bundle();
        firebaseHelper.getUser(username, new FirebaseHelper.ReturnCall<Data>() {
            @Override
            public void onSuccess(Data result) {
                if (result instanceof User) {
                    bundle.putString("mode","1");
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(),"account dialog");
                }
                else{
                    bundle.putString("mode","0");
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(),"account dialog");
                }
            }

            @Override
            public void onError() {

            }
        });
    }
}