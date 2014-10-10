package com.example.RNGD.lifebook;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.RNGD.helpers.LBProvider;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Edit extends Activity {
    private EditText Etitle;
    private EditText Ebody;
    private LinearLayout imageScrollLayout;
    private HorizontalScrollView imageScroll;
    private String imageList = "";
    private static final String KEY_IMAGES = "imageList";
    private static final int KEY_SELECT_PHOTO = 100;
    private Cursor mCursor;
    private ContentResolver mCr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Button done = (Button) findViewById(R.id.done1);
        Button addImages = (Button) findViewById(R.id.imageAdd1);
        Etitle = (EditText) findViewById(R.id.Etitle1);
        Ebody = (EditText) findViewById(R.id.Ebody1);
        imageScroll = (HorizontalScrollView) findViewById(R.id.imageScroll1);
        imageScrollLayout = (LinearLayout) findViewById(R.id.imageScrollLayout1);
        if (savedInstanceState != null) {
            imageList = (String) savedInstanceState.getSerializable(KEY_IMAGES);
            String[] temp = imageList.split(" ");
            if (temp != null && temp.length > 0) {
                for (String x : temp) {
                    try {
                        addToScroll(decodeUri(Uri.parse(x)));
                    } catch (FileNotFoundException f) {

                    }
                }
            }
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEntry();
                finish();
            }
        });

        addImages.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoAdd = new Intent(Intent.ACTION_PICK);
                photoAdd.setType("image/*");
                startActivityForResult(photoAdd, KEY_SELECT_PHOTO);
            }
        });

    }

    public void saveEntry() {
        mCr = getContentResolver();
        ContentValues vals = new ContentValues();
        if (imageList == null)
            imageList = "";
        String[] tempList = imageList.split(" ");
        vals.put(LBProvider.KEY_TITLE, Etitle.getText().toString());
        vals.put(LBProvider.KEY_BODY, Ebody.getText().toString());
        vals.put(LBProvider.KEY_DATE, getDateTime());
        vals.put(LBProvider.KEY_IMAGES, imageList);
        vals.put(LBProvider.KEY_SELECTEDIMG, tempList.length == 0 ? "" : tempList[0]);
        mCr.insert(LBProvider.CONTENT_URI, vals);
    }

    public void addToScroll(Bitmap image) {
        if (image == null)
            return;
        ImageView temp = new ImageView(this);
        temp.setScaleType(ImageView.ScaleType.CENTER_CROP);
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

                    if (imageList == null || imageList.equals(""))
                        imageList = selectedImage.toString();
                    else
                        imageList = imageList + " " + selectedImage.toString();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    public void newItem(ArrayList<String> items) {
        ContentResolver cr = getContentResolver();
        ContentValues vals = new ContentValues();
        String[] tempList = imageList.split(" ");
        vals.put(LBProvider.KEY_TITLE, items.get(0));
        vals.put(LBProvider.KEY_BODY, items.get(1));
        vals.put(LBProvider.KEY_DATE, getDateTime());
        vals.put(LBProvider.KEY_IMAGES, imageList);
        vals.put(LBProvider.KEY_SELECTEDIMG, tempList.length == 0 ? "" : tempList[0]);
        cr.insert(LBProvider.CONTENT_URI, vals);

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_IMAGES, imageList);
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
