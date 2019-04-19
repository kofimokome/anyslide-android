package com.example.kofi.anyslide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kofi.anyslide.SocketService;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketService.initialiseSocket();
        sharedPreferences = getSharedPreferences("anyslide",MODE_PRIVATE);

        int id = sharedPreferences.getInt("id", 0);
        String username = sharedPreferences.getString("username", null);

        //System.out.println("id "+ id+"\nusername "+username);

        if (id == 0) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            if (!SocketService.isUserSetup) {
                (SocketService.getSocket()).emit("register_user", id, username);
                SocketService.isUserSetup = true;
            }
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int id = sharedPreferences.getInt("id", 0);
        String username = sharedPreferences.getString("username", null);
       // System.out.println("id "+ id+"\nusername "+username);

        if (id == 0) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    public void onDestroy() {
        super.onDestroy();

        (SocketService.getSocket()).disconnect();
    }
}
