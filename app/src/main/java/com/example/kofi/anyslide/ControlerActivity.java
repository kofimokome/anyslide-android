package com.example.kofi.anyslide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ControlerActivity extends AppCompatActivity {

    private int indexh = 0, indexv = 0, indexf = 0, presentation_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controler);
        Intent intent = getIntent();
        final String link = intent.getStringExtra("link");

        SocketService.getSocket().emit("join_presentation", link, false);

        Emitter.Listener onJoinPresentation = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                ControlerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        System.out.println(args[0]);
                        try {
                            if (data.getBoolean("status")) {
                                presentation_id = data.getInt("id");

                                TextView controller_info = (TextView) findViewById(R.id.controller_info);
                                controller_info.setText("You Are Now Controlling Presentation " + presentation_id + "\n\nClick On The Buttons Below To Control Your Slide");

                                Toast.makeText(ControlerActivity.this, "You Can Now Control This Slide", Toast.LENGTH_SHORT).show();
                                // Show the controller view layout
                                Button prev_slide = (Button) findViewById(R.id.prev_slide);
                                Button next_slide = (Button) findViewById(R.id.next_slide);

                                prev_slide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (indexh > 0) {
                                            indexh--;
                                            SocketService.getSocket().emit("update_presentation", link, indexh, indexv, indexf);
                                        }
                                    }
                                });

                                next_slide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        indexh++;
                                        SocketService.getSocket().emit("update_presentation", link, indexh, indexv, indexf);
                                    }
                                });


                            } else {
                                Toast.makeText(ControlerActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ControlerActivity.this, DashboardActivity.class));
                            }
                        } catch (JSONException e) {
                            // do sometjing
                        }

                    }
                });
            }
        };
        SocketService.getSocket().on("joinPresentation", onJoinPresentation);

        Emitter.Listener onUpdateSlide = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                ControlerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ControlerActivity.this, "Slide Has Been Updated", Toast.LENGTH_SHORT).show();
                        JSONObject data = (JSONObject) args[0];
                        //System.out.println(args[0]);
                        try {

                            // Show the controller view layout
                            indexh = data.getInt("indexh");
                            indexv = data.getInt("indexv");
                            indexf = data.getInt("indexf");

                        } catch (JSONException e) {
                            // do sometjing
                        }

                    }
                });
            }
        };
        SocketService.getSocket().on("update_slide", onUpdateSlide);

        Emitter.Listener onLeavePresentation = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                ControlerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ControlerActivity.this, "Presentation Has Been Deleted", Toast.LENGTH_SHORT).show();
                        //System.out.println(args[0]);

                        startActivity(new Intent(ControlerActivity.this, DashboardActivity.class));

                    }
                });
            }
        };

        SocketService.getSocket().on("leavePresentation", onLeavePresentation);
        //System.out.println("link is " + link);
    }
}
