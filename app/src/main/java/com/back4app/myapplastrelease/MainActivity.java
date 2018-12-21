package com.back4app.myapplastrelease;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final EditText et_username;
        final EditText et_password;
        final Button bt_login;
        final Button bt_register;
        final ProgressDialog progressDialog;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);

        bt_register = findViewById(R.id.bt_register);
        bt_login = findViewById(R.id.bt_login);

        progressDialog = new ProgressDialog(MainActivity.this);

        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Registering");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String user = (et_username.getText().toString());
                            final String pwd  = (et_password.getText().toString());
                            userRegister(user, pwd, progressDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait");
                progressDialog.setTitle("Login in");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String user = (et_username.getText().toString());
                            final String pwd = (et_password.getText().toString());
                            userLogin(user, pwd, progressDialog);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    void alertDisplayer(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    void userRegister(final String username, final String password, final ProgressDialog progressDialog){
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().logOut();
        }
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    alertDisplayer("Register Successful", "User: " + username);

                } else {
                    progressDialog.dismiss();
                    alertDisplayer("Register Fail", e.getMessage()+" Please Try Again");
                }
            }
        });
    }

    void userLogin(String username, String password, final ProgressDialog progressDialog) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    progressDialog.dismiss();
                    alertDisplayer("Login Successful", "Welcome " + user.getUsername());
                    Intent myIntent = new Intent(MainActivity.this, LiveQuery.class);
                    myIntent.putExtra("userId", user.getObjectId()); //Optional parameters
                    MainActivity.this.startActivity(myIntent);
                } else {
                    progressDialog.dismiss();
                    alertDisplayer("Login Failed", e.getMessage() + " Please Try Again");
                }
            }
        });
    }
}
