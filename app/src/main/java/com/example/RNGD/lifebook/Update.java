package com.example.RNGD.lifebook;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.RNGD.helpers.LBProvider;

import java.io.FileNotFoundException;

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
    private HorizontalScrollView imageScroll;
    private LinearLayout imageScrollLayout;
    private Cursor mCursor;

    private ActionBar bar;
    private static final int KEY_SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update);

        Ttitle = (TextView) (findViewById(R.id.Ttitle));
        Tbody = (TextView) (findViewById(R.id.Tbody));
        Tdate = (TextView) (findViewById(R.id.date));
        Etitle = (EditText) findViewById(R.id.Etitle);
        Ebody = (EditText) findViewById(R.id.Ebody);
        imageScroll = (HorizontalScrollView) findViewById(R.id.imageScroll);
        imageScrollLayout = (LinearLayout) findViewById(R.id.imageScrollLayout);
        imageScrollLayout.setDividerPadding(10);
        flipperTB = (ViewFlipper) findViewById(R.id.flipperTB);
        flipperButtons = (ViewFlipper) findViewById(R.id.flipperButtons);
        Intent i = getIntent();


        Bundle bundle = i.getExtras();
        dbID = bundle.getInt(LBProvider.KEY_ID);
        populateTexts();
        populateImages();
        Button edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Etitle.setText(Ttitle.getText().toString());
                Ebody.setText(Tbody.getText().toString());
                flipperTB.showNext();
                flipperButtons.showNext();

            }
        });
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {


            @Override
            public void onClick(View v) {
                flipperTB.showPrevious();
                flipperButtons.showPrevious();

            }
        });
        Button addImage = (Button) findViewById(R.id.imageAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent photoAdd = new Intent(Intent.ACTION_PICK);
                photoAdd.setType("image/*");
                startActivityForResult(photoAdd, KEY_SELECT_PHOTO);
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



            }
        });

    }

    public void populateImages() {
        mCr = getContentResolver();
        mCursor = mCr.query(LBProvider.CONTENT_URI, null, LBProvider.KEY_ID + "=" + dbID, null, null);
        startManagingCursor(mCursor);
        int iImage = mCursor.getColumnIndexOrThrow(LBProvider.KEY_IMAGES);
        mCursor.moveToFirst();
        String imageList = mCursor.getString(iImage);
        String[] uris = null;
        if (imageList == null || imageList.length() < 1)
            return;
        else
            uris = imageList.split(" ");

        for (String x : uris) {
            Uri temp = Uri.parse(x);
            Bitmap image = null;
            try {
                image = decodeUri(temp);
            } catch (FileNotFoundException f) {

            }
            addToScroll(image);
        }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update, menu);
        return true;
    }

    public void addToScroll(Bitmap image) {
        if (image == null)
            return;
        ImageView temp = new ImageView(this);
        temp.setImageBitmap(image);
        imageScrollLayout.addView(temp);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case KEY_SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent == null)
                        return;
                    Uri selectedImage = imageReturnedIntent.getData();

                    Bitmap image = null;
                    try {

                        image = decodeUri(selectedImage);
                    } catch (FileNotFoundException f) {

                    }
                    mCr = getContentResolver();
                    mCursor = mCr.query(LBProvider.CONTENT_URI, null, LBProvider.KEY_ID + "=" + dbID, null, null);
                    startManagingCursor(mCursor);
                    int iImage = mCursor.getColumnIndexOrThrow(LBProvider.KEY_IMAGES);
                    mCursor.moveToFirst();
                    String imageList = mCursor.getString(iImage);
                    if (imageList == null)
                        imageList = selectedImage.toString();
                    else
                        imageList = imageList + " " + selectedImage.toString();
                    ContentValues vals = new ContentValues();
                    vals.put(LBProvider.KEY_IMAGES, imageList);
                    mCr.update(LBProvider.CONTENT_URI, vals, LBProvider.KEY_ID + "=" + dbID, null);
                    addToScroll(image);
                }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 100;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

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
