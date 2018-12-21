package com.back4app.myapplastrelease;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;


import org.json.JSONObject;

import java.util.HashMap;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryClient;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class LiveQuery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_query);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("userId");

        Button btn_register = (Button) findViewById(R.id.bt_registerLikes);
        Button btn_callFunction = (Button) findViewById(R.id.bt_callFunctionLikes);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin(id);
            }
        });
        btn_callFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFunction(id);
            }
        });


        LiveQueryClient.init("wss://" + "newapptest.back4app.io/", "lOnJ1j6c5OHzz7RFmlXATbEqGvKA3BuT9KXGyWdO", true);
        LiveQueryClient.connect();

        final Subscription sub = new BaseQuery.Builder("Likes")
            .where("userId", id)
            .addField("totalLikes")
            .build()
            .subscribe();

        sub.on(LiveQueryEvent.UPDATE, new OnListener() {

            @Override
            public void on(JSONObject object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText pokeText = (EditText) findViewById(R.id.et_likes);
                        numPokes++;
                        if(numPokes == 1) {
                            pokeText.setText("Poked " + numPokes + " time.");
                        }
                        else {
                            pokeText.setText("Poked " + numPokes + " times.");
                        }
                    }



                });
            }
        });
        sub.on(LiveQueryEvent.CREATE, new OnListener() {

            @Override
            public void on(JSONObject object) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText pokeText = (EditText) findViewById(R.id.et_likes);
                        numPokes++;
                        if(numPokes == 1) {
                            pokeText.setText("Poked " + numPokes + " time.");
                        }
                    }
                });
            }
        });


    }

    public void userLogin(final String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Likes");
        query.whereEqualTo("userId", id);
        query.setLimit(1);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if(object.isDataAvailable() == true){
                        object.increment("totalLikes");
                        object.put("userId", id);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "1 " + e.getMessage().toString(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        });
                    } else {
                        object.increment("totalLikes");
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "2 " + e.getMessage().toString(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            e.toString(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

    }

    public void callFunction(final String id){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("poked", true);
        params.put("userId", id);
        ParseCloud.callFunctionInBackground("updateLike", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {

                if (e == null) {
                    // Update object successfully

                    Toast.makeText(
                            getApplicationContext(),
                            e.toString(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
    }
    int numPokes = 0;
}
