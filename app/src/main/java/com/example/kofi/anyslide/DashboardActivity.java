package com.example.kofi.anyslide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DashboardActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("anyslide", MODE_PRIVATE);
        int id = sharedPreferences.getInt("id", 0);

        final LinearLayout parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
        final TextView dashboard_info = (TextView) findViewById(R.id.dashboard_info);

        final TextView username = findViewById(R.id.username);
        username.setText("Welcome " + sharedPreferences.getString("username", null));

        SocketService.getSocket().emit("get_presentations", id);
        Emitter.Listener onGetPresentations = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                DashboardActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray presentations = (JSONArray) args[0];
                        //System.out.println(args[0]);
                        if(presentations.length() <=0){
                            dashboard_info.setVisibility(View.VISIBLE);
                        }
                        try {
                            for (int i = 0; i < presentations.length(); i++) {
                                final JSONObject presentation = presentations.getJSONObject(i);
                                final String link = presentation.getString("link");
                                int id = presentation.getInt("id");
                                LinearLayout linearLayout = new LinearLayout(DashboardActivity.this);
                                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                                final TextView presentation_name = new TextView(DashboardActivity.this);
                                presentation_name.setText("Presentation " + id);
                                //presentation_name.setLayoutParams(new ViewGroup.LayoutParams(880, 60));

                                final Button control_btn = new Button(DashboardActivity.this);
                                control_btn.setText("CONTROL");

                                control_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent controller = new Intent(DashboardActivity.this, ControlerActivity.class);
                                        controller.putExtra("link", link);
                                        startActivity(controller);
                                        //System.out.println("You will control " + presentation_name.getText());
                                    }
                                });

                                linearLayout.addView(presentation_name);
                                linearLayout.addView(control_btn);

                                parent_layout.addView(linearLayout);
                                //System.out.println("id " + presentation.getInt("id") + " link " + presentation.getString("link"));
                            }
                        } catch (JSONException e) {
                            // do sometjing
                        }

                    }
                });
            }
        };
        SocketService.getSocket().on("presentations", onGetPresentations);
    }

    public void onDestroy() {
        super.onDestroy();

        SocketService.disconnect();
    }

    public void Logout(View view) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt("id", 0);
        prefsEditor.putString("username", null);
        prefsEditor.apply();

        SocketService.disconnect();
        startActivity(new Intent(this, LoginActivity.class));

    }
}
