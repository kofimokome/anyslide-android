package com.example.kofi.anyslide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kofi.anyslide.SocketService;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private int id;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketService.initialiseSocket();
        sharedPreferences = getSharedPreferences("anyslide",MODE_PRIVATE);

        id = sharedPreferences.getInt("id", 0);
        username = sharedPreferences.getString("username", null);

        //System.out.println("id "+ id+"\nusername "+username);


       selectActivityToShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int id = sharedPreferences.getInt("id", 0);
        username = sharedPreferences.getString("username", null);
       // System.out.println("id "+ id+"\nusername "+username);

       selectActivityToShow();
    }

    public void onDestroy() {
        super.onDestroy();

        (SocketService.getSocket()).disconnect();
    }

    private void selectActivityToShow() {
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == 0) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!SocketService.isUserSetup) {
                        (SocketService.getSocket()).emit("register_user", id, username);
                        SocketService.isUserSetup = true;
                    }
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                }
            }
        }, 3000);
    }
}
