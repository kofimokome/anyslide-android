package com.example.kofi.anyslide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.example.kofi.anyslide.SocketService;


import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("anyslide", MODE_PRIVATE);
        SocketService.initialiseSocket();

        int id = sharedPreferences.getInt("id", 0);

        if (id > 0) {
            startActivity(new Intent(this, DashboardActivity.class));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        int id = sharedPreferences.getInt("id", 0);

        System.out.println("id "+ id);
        if (id > 0) {
            //startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    public void onDestroy() {
        super.onDestroy();

        SocketService.disconnect();
    }

    public void Login(View view) {

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        String username_text = (username.getText()).toString();
        String password_text = (password.getText()).toString();

        if ((username_text.trim()).length() <= 1) {
            Toast.makeText(this, "Enter a valid user name", Toast.LENGTH_SHORT).show();
        } else if ((password_text.trim()).length() <= 2) {
            Toast.makeText(this, "Enter a valid password", Toast.LENGTH_SHORT).show();
        } else {
            attemptLogin(username_text, password_text);
        }
    }

    private void attemptLogin(String username, String password) {
        System.out.println(R.string.api_root);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://anyslide.000webhostapp.com/api/login.php";

        HashMap<String, String> userdata = new HashMap<>();
        userdata.put("username", username);
        userdata.put("password", password);

        JSONObject parameters = new JSONObject(userdata);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        try {
                            if (response.getBoolean("success")) {

                                SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                prefsEditor.putInt("id", response.getInt("id"));
                                prefsEditor.putString("username", response.getString("username"));
                                prefsEditor.apply();

                                if (SocketService.isIsSocketInitialised()) {
                                    Socket socket = SocketService.getSocket();
                                    socket.emit("register_user", response.getInt("id"), response.getString("username"));
                                    SocketService.isUserSetup = true;
                                }
                                Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            } else {
                                //System.out.println(response.get("message"));
                                Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "An Error Occurred While Communicating With The Server. Please Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "An Error Has Occurred. Please Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                });

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}

