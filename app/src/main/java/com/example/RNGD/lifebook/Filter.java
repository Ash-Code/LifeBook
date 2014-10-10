package com.example.RNGD.lifebook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

import com.example.RNGD.helpers.CustomAdapter;
import com.example.RNGD.helpers.Data;
import com.example.RNGD.helpers.LBProvider;

import java.util.Calendar;
import java.util.LinkedList;

public class Filter extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Calendar curr;
    private final String KEY_CAL = "curr cal";
    private DatePickerDialog dpd;
    private CustomAdapter listAdapter;
    private LinkedList<Data> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        if (savedInstanceState == null) {
            curr = Calendar.getInstance();
        } else {
            curr = (Calendar) savedInstanceState.getSerializable(KEY_CAL);
        }

       dpd = new DatePickerDialog(this, new dpdListener(), curr.get(Calendar.YEAR), curr.get(Calendar.MONTH), curr.get(Calendar.DAY_OF_MONTH));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader;

        loader = new CursorLoader(this, LBProvider.CONTENT_URI, null, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null)
            return;
        int iID = data.getColumnIndexOrThrow(LBProvider.KEY_ID);
        int iTitle = data.getColumnIndexOrThrow(LBProvider.KEY_TITLE);
        int iBody = data.getColumnIndexOrThrow(LBProvider.KEY_BODY);
        int iImages = data.getColumnIndexOrThrow(LBProvider.KEY_IMAGES);
        int iCover = data.getColumnIndexOrThrow(LBProvider.KEY_SELECTEDIMG);
        int iDate = data.getColumnIndexOrThrow(LBProvider.KEY_DATE);
        list.clear();
        while (data.moveToNext()) {


            String finalUri = "";
            String[] stringuri = data.getString(iImages).split(" ");
            for (int i = 0; i < stringuri.length; i++) {
                if (stringuri[i].length() > 5) {
                    finalUri = stringuri[i];
                }
            }



            Data dd = new Data(data.getInt(iID), data.getString(iTitle), data.getString(iBody), data.getString(iImages), null, finalUri, data.getString(iDate));
            list.addFirst(dd);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }



    private class dpdListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            dpd.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
