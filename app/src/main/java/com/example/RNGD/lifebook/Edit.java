package com.example.RNGD.lifebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.RNGD.lifebook.R;

import java.util.ArrayList;

public class Edit extends Activity {
EditText title;
    EditText body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Button b=(Button)findViewById(R.id.b);
         title=(EditText)findViewById(R.id.Etitle);
         body=(EditText)findViewById(R.id.Ebody);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent();
                ArrayList<String> res=new ArrayList<String>();
                res.add(title.getText().toString());
                res.add(body.getText().toString());
                i.putExtra("res",res);
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
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

    public void onClix(View v){

    }
}
