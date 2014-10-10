package com.example.RNGD.lifebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Welcome extends Activity {

    private Button prefButton;
    private EditText pass;
    private TextView desc;
    private Button accessButton;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        TextView title=(TextView)findViewById(R.id.LIFEBOOKTITLE);
        Typeface tf=Typeface.createFromAsset(getAssets(),String.format("fonts/%s", "Berlin.ttf"));
        title.setTypeface(tf);

        prefButton = (Button) findViewById(R.id.prefButton);
        accessButton = (Button) findViewById(R.id.accessButton);
        pass = (EditText) findViewById(R.id.pass2);
        desc = (TextView) findViewById(R.id.desc);


        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.equals(pass.getText().toString())) {
                    finish();
                    Intent i = new Intent(getBaseContext(), Main_UI.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        prefButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Preference.class));
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        password = pref.getString("newpass1", "DEFAULT");
        boolean noPass = pref.getBoolean("noPass", true);
        if (noPass) {
            desc.setText(R.string.welcome_remind);
            desc.setVisibility(View.VISIBLE);
            accessButton.setVisibility(View.INVISIBLE);
            prefButton.setVisibility(View.INVISIBLE);
            pass.setVisibility(View.INVISIBLE);
            Launcher launch = new Launcher();
            launch.start();
        } else {
            if (password != null && password.length() != 0) {
                Toast.makeText(this, password + " works" + password.length(), Toast.LENGTH_SHORT).show();
                pass.setVisibility(View.VISIBLE);
                desc.setText("Please enter the password to access database");
                desc.setVisibility(View.VISIBLE);
                accessButton.setVisibility(View.VISIBLE);
                prefButton.setVisibility(View.GONE);

            } else {
                accessButton.setVisibility(View.GONE);
                prefButton.setVisibility(View.VISIBLE);
                desc.setText(R.string.welcome_remind);
                desc.setVisibility(View.VISIBLE);
            }
        }
    }

    public class Launcher extends Thread {
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (Exception c) {

            }
            finish();
            startActivity(new Intent(getBaseContext(), Main_UI.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
