package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import classes.Data;
import classes.ForgotDialog;
import classes.Session;
import classes.User;
import helpers.FirebaseHelper;
import helpers.MenuHelper;

import java.util.ArrayList;
import java.util.List;

public class login extends AppCompatActivity implements View.OnClickListener{
    Switch remember;
    Button login;
    ImageButton forgot;
    EditText user_name,password,phone;
    TextInputLayout textInputLayout,passwordTextInputLayout;
    String flag;

    // First, get a reference to the SharedPreferences object
    SharedPreferences sharedPref;

    FirebaseHelper firebaseHelper = new FirebaseHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        // Initialize the SharedPreferences object after setContentView()
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        remember = findViewById(R.id.remember);
        login = findViewById(R.id.button);
        forgot = findViewById(R.id.forgot);
        user_name =findViewById(R.id.userName);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        textInputLayout = findViewById(R.id.phone1);
        passwordTextInputLayout = findViewById(R.id.password1);

        Intent intent = getIntent();
        flag = intent.getStringExtra("message_key");
        if (flag.equals("1")) {//login screen
            forgot.setVisibility(View.VISIBLE);
            remember.setVisibility(View.VISIBLE);
            phone.setVisibility(View.GONE);
            textInputLayout.setVisibility(View.GONE);

            // To retrieve values from SharedPreferences
            boolean boolValue = sharedPref.getBoolean("key_bool", false);
            String passwordValue = sharedPref.getString("key_password", null);
            String usernameValue = sharedPref.getString("key_username",null);

            if (boolValue) {
                if (usernameValue != null && passwordValue != null) {
                    //check firebase if exists and then enter
                    firebaseHelper.userExists(usernameValue, new FirebaseHelper.Callback() {
                        @Override
                        public void onSuccess(boolean exists) {
                            firebaseHelper.getUser(usernameValue, new FirebaseHelper.ReturnCall<Data>() {
                                @Override
                                public void onSuccess(Data result) {
                                    if (result instanceof User) {
                                        user_name.setText(((User) result).getUsername());
                                        password.setText(((User) result).getPassword());
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }
                        @Override
                        public void onError() {
                            Toast.makeText(login.this, "wrong user name or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        forgot.setOnClickListener(this::onClick);
        login.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View view) {
        if (view == forgot) {
            //open custom dialog enter new password
            openForgotDialog();
        }
        else if (view == login) {
            if (!user_name.getText().toString().equals("") && !password.getText().toString().equals("")) {
                if (true_password(password.getText().toString())) {
                    if(flag.equals("1")){//login screen check firebase
                        firebaseHelper.userExists(user_name.getText().toString(), new FirebaseHelper.Callback() {
                            @Override
                            public void onSuccess(boolean exists) {
                                if (exists) {
                                    firebaseHelper.getUser(user_name.getText().toString(), new FirebaseHelper.ReturnCall<Data>() {
                                        @Override
                                        public void onSuccess(Data result) {
                                            if (result instanceof User) {
                                                if (((User) result).getPassword().equals(password.getText().toString())) {
                                                    save();
                                                    Intent main = new Intent(login.this, Main.class);
                                                    main.putExtra("userName",user_name.getText().toString());
                                                    main.putExtra("set","1");
                                                    finish();
                                                    startActivity(main);
                                                }
                                                else Toast.makeText(login.this, "wrong user name or password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onError() {

                                        }
                                    });

                                }
                                else Toast.makeText(login.this, "wrong user name or password", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onError() {

                            }
                        });

                    }
                    else if(flag.equals("0")){//sign up enter firebase and sql
                        firebaseHelper.userExists(user_name.getText().toString(), new FirebaseHelper.Callback() {
                            @Override
                            public void onSuccess(boolean exists) {
                                if (exists) Toast.makeText(login.this, "user name taken", Toast.LENGTH_SHORT).show();
                                else {
                                    if (phone.getText().toString().length() != 10) {
                                        Toast.makeText(login.this, "must enter valid phone number", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        ArrayList<Session> ses = new ArrayList<Session>();
                                        User user = new User(0,0,user_name.getText().toString(),ses,0,0, password.getText().toString(),phone.getText().toString());
                                        firebaseHelper.addUser(user);
                                        Intent main = new Intent(login.this, Main.class);
                                        main.putExtra("userName",user_name.getText().toString());
                                        main.putExtra("set","0");
                                        finish();
                                        startActivity(main);
                                        }
                                    }
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }
                }
                else Toast.makeText(this, "must enter valid password that is at least 12 char and has upper and lower case and numbers", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(this, "must enter all fields", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu4,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }
        else if (id == R.id.back) {
            Intent sign_in = new Intent(login.this, MainActivity.class);
            finish();
            startActivity(sign_in);
        }
        else if(id == R.id.credits){
            MenuHelper.credits(this);
        }
        else if(id == R.id.credits2){
            MenuHelper.credits2(this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Saves user login credentials to SharedPreferences if the "remember" checkbox is checked
     */
    void save(){
        if (remember.isChecked()) {
            //enter the user to the sql table
            // To write data to SharedPreferences, use the edit() method
            SharedPreferences.Editor editor = sharedPref.edit();

            // Use the putXXX() methods to add values to the editor
            editor.putBoolean("key_bool", true);

            editor.putString("key_username",user_name.getText().toString());
            editor.putString("key_password",password.getText().toString());

            // Don't forget to call apply() to save the changes
            editor.apply();
        }

    }

    /**
     * Opens a ForgotDialog fragment
     */
    public void openForgotDialog(){
        ForgotDialog dialog = new ForgotDialog();
        dialog.show(getSupportFragmentManager(),"forgot dialog");
    }

    /**
     * What it does: Checks if a given password meets the required criteria of being at least 12 characters long with at least one uppercase letter, one lowercase letter, and one digit
     * @param password String
     * @return A Boolean value indicating whether the password meets the criteria (true) or not (false)
     */
    public Boolean true_password(String password){
        //checks if password is good
        if (password.length() < 12) {
            return false;
        }
        int upperCount = 0;
        int lowerCount = 0;
        int numberCount = 0;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                upperCount++;
            }
            else if (Character.isLowerCase(c)) {
                lowerCount++;
            }
            else if (Character.isDigit(c)) {
                numberCount++;
            }
        }
        if (upperCount > 0 && lowerCount > 0 && numberCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}