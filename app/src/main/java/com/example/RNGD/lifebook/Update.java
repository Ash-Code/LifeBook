package com.example.RNGD.lifebook;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class Update extends Activity {
    private TextView Ttitle;
    private TextView Tbody;
    private TextView Tdate;
    private TextView Edate;
    private int dbID;
    private EditText Etitle;
    private EditText Ebody;
    private ViewFlipper flipperTB;
    private ViewFlipper flipperButtons;
    private ContentResolver mCr;
    private Cursor mCursor;
    private boolean isFlipped = false;
    private String KEY_ISFLIPPED = "isFlipped";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Ttitle = (TextView) (findViewById(R.id.Ttitle));
        Tbody = (TextView) (findViewById(R.id.Tbody));
        Tdate = (TextView) (findViewById(R.id.date1));
        Etitle = (EditText) findViewById(R.id.Etitle);
        Ebody = (EditText) findViewById(R.id.Ebody);
        flipperTB = (ViewFlipper) findViewById(R.id.flipperTB);
        flipperButtons = (ViewFlipper) findViewById(R.id.flipperButtons);
        Intent i = getIntent();
        if (savedInstanceState != null) {
            isFlipped = savedInstanceState.getBoolean(KEY_ISFLIPPED);
            if (isFlipped) {
                flipperTB.showNext();
                flipperButtons.showNext();
            }
        }

        Bundle bundle = i.getExtras();
        dbID = bundle.getInt(LBProvider.KEY_ID);
        populateTexts();
        Button edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Etitle.setText(Ttitle.getText().toString());
                Ebody.setText(Tbody.getText().toString());
                flipperTB.showNext();
                flipperButtons.showNext();
                isFlipped = true;
            }
        });
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View v) {
                flipperTB.showPrevious();
                flipperButtons.showPrevious();
                isFlipped = false;
            }
        });
        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCr = getContentResolver();
                ContentValues vals = new ContentValues();
                vals.put(LBProvider.KEY_TITLE, Etitle.getText().toString());
                vals.put(LBProvider.KEY_BODY, Ebody.getText().toString());
                mCr.update(LBProvider.CONTENT_URI, vals, LBProvider.KEY_ID + "=" + dbID, null);
                populateTexts();
                flipperButtons.showPrevious();
                flipperTB.showPrevious();

                isFlipped = false;

            }
        });

    }


    public void populateTexts() {
        mCr = getContentResolver();

        mCursor = mCr.query(LBProvider.CONTENT_URI, null, LBProvider.KEY_ID + "=" + dbID, null, null);
        startManagingCursor(mCursor);
        mCursor.moveToFirst();

        int ititle = mCursor.getColumnIndexOrThrow(LBProvider.KEY_TITLE);
        int ibody = mCursor.getColumnIndexOrThrow(LBProvider.KEY_BODY);
        int idate = mCursor.getColumnIndexOrThrow(LBProvider.KEY_DATE);
        Ttitle.setText(mCursor.getString(ititle));
        Tbody.setText(mCursor.getString(ibody));
        Tdate.setText(mCursor.getString(idate));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ISFLIPPED, isFlipped);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update, menu);
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
