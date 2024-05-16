package com.example.sportactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes.Data;
import classes.Guest;
import helpers.FirebaseHelper;
import helpers.MenuHelper;
import classes.RecycleAdapter;

public class LeaderBoard extends AppCompatActivity {

    // Declare the username and firebaseHelper objects
    String username;
    FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        // Hide navigation bar on devices with Android KitKat or later
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
        {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        // Get the username passed from the previous activity
        Intent intent = getIntent();
        username = intent.getStringExtra("userName");

        // Initialize the firebaseHelper object
        firebaseHelper = new FirebaseHelper();

        // Create a new ArrayList to store the user data
        ArrayList<Data> arrayList = new ArrayList<>();

        // Call the getAllUsers method of firebaseHelper and pass a callback to handle the results
        firebaseHelper.getAllUsers(new FirebaseHelper.ListCall<Data>() {
            @Override
            public void onSuccess(List<Data> result) {
                // Sort the data based on level if the user is a Guest
                List<Data> sortedList = sortDataList(result);
                // Add the sorted data to the arrayList
                for (int i = 0; i < result.size(); i++) {
                    arrayList.add(sortedList.get(i));
                }
                // Display the sorted data in a RecyclerView using a RecycleAdapter
                RecyclerView recyclerView = findViewById(R.id.recycleView);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LeaderBoard.this);
                recyclerView.setLayoutManager(layoutManager);
                RecycleAdapter recycleAdapter = new RecycleAdapter(arrayList);
                recyclerView.setAdapter(recycleAdapter);
            }

            @Override
            public void onError() {
                Toast.makeText(LeaderBoard.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method sorts the dataList based on level if the user is a Guest.
     * @param dataList the list of user data to sort
     * @return the sorted list of user data
     */
    public static List<Data> sortDataList(List<Data> dataList) {
        // Sort the dataList based on the level attribute of the Guest objects
        Collections.sort(dataList, (d1, d2) -> {
            if (d1 instanceof Guest && d2 instanceof Guest) {
                return ((Guest) d2).getLvl() - ((Guest) d1).getLvl();
            }
            return 0;
        });
        // Return the sorted dataList
        return dataList;
    }

    // Inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu4,menu);
        return true;
    }

    // Handle the selected option from the menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.exit) {
            MenuHelper.exit(this);
        }
        else if (id == R.id.back) {
            Intent sign_in = new Intent(LeaderBoard.this, Main.class);
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
        return super.onOptionsItemSelected(item);
    }
}