package com.example.RNGD.lifebook;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.util.LruCache;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import com.example.RNGD.helpers.CustomAdapter;
import com.example.RNGD.helpers.Data;
import com.example.RNGD.helpers.LBProvider;
import com.example.RNGD.helpers.TypefaceSpan;

import java.util.Calendar;
import java.util.LinkedList;


public class Main_UI extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    LinkedList<Data> list;
    ListView mainList;
    CustomAdapter listAdapter;
    private ActionBar action;
    public LruCache<String, Bitmap> cache = null;
    private ContentResolver mCr;
    private DatePickerDialog dpd;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawer;
    private final String KEY_FROM = "from";
    private final String KEY_CACHE = "lruCache";
    private final String KEY_TO = "to";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__ui);
        initializeAll();

    }

    public void initializeAll() {
        mCr = getContentResolver();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Calendar curr = Calendar.getInstance();
        dpd = new DatePickerDialog(this, new dpdListener(), curr.get(Calendar.YEAR), curr.get(Calendar.MONTH), curr.get(Calendar.DAY_OF_MONTH));
       cache=(LruCache<String,Bitmap>)getLastNonConfigurationInstance();
        if (cache == null) {
            final int maxMem = (int) Runtime.getRuntime().maxMemory() / 1024;
            int capacity = maxMem / 8;
            cache = new LruCache<String, Bitmap>(capacity) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };
        }
        setDrawer();
        setActionBar();
        setList();

    }

    public void setDrawer() {
        String[] items = {"All pages", "Filter pages", "Settings"};
        View v = getLayoutInflater().inflate(R.layout.drawer_profile, null);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawer = (ListView) findViewById(R.id.left_drawer);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, R.id.text1,items);
        mDrawer.addHeaderView(v);
        mDrawer.setAdapter(adapter);

        mDrawer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 2:
                        dpd.show();
                        break;
                    case 3:
                        startActivity(new Intent(getBaseContext(), Preference.class));
                        break;
                }
            }
        });


    }

    public void setList() {
        LoaderManager lm = getLoaderManager();
        lm.initLoader(0, null, this);
        mainList = (ListView) findViewById(R.id.mainList);
        list = new LinkedList<Data>();
        mainList.setSmoothScrollbarEnabled(true);
        listAdapter = new CustomAdapter(this, R.layout.data_layout, list, cache);
        mainList.setAdapter(listAdapter);
        registerForContextMenu(mainList);
        mainList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), Update.class);
                Data temp = list.get(position);
                i.putExtra(LBProvider.KEY_ID, temp.dbId);
                i.putExtra(LBProvider.KEY_TITLE, temp.title);
                i.putExtra(LBProvider.KEY_BODY, temp.body);
                i.putExtra(LBProvider.KEY_DATE, temp.date);
                i.putExtra(LBProvider.KEY_IMAGES, temp.images);
                startActivity(i);
            }
        });

    }

    public void setActionBar() {
        SpannableString s = new SpannableString("LifeBook");
        s.setSpan(new TypefaceSpan(this, "Berlin.ttf", 50), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(s);
    }

    private class dpdListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String y = "" + year;
            String m = "";
            String d = "";
            if (dayOfMonth < 10)
                d = "0" + dayOfMonth;
            else
                d += dayOfMonth;
            if (monthOfYear < 10)
                m = "0" + monthOfYear;
            else
                m += monthOfYear;
            String resultfrom = y + "-" + m + "-" + d + " 00:00:00";
            String resultto = y + "-" + m + "-" + d + " 23:59:59";
            Bundle bundle = new Bundle();
            bundle.putString(KEY_FROM, resultfrom);
            bundle.putString(KEY_TO, resultto);
            Log.v("CALENDAR QUERY", resultfrom + " && " + resultto + " month: " + monthOfYear);
            queryDate(bundle);

        }
    }


    public void queryDate(Bundle bundle) {
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main__ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                startActivity(new Intent(this, Preference.class));
                break;
            }
            case R.id.action_Add: {
                startActivity(new Intent(this, Edit.class));
                break;
            }
            case R.id.datePick: {
                dpd.show();
                break;
            }

            case R.id.showAll: {
                getLoaderManager().restartLoader(0, null, this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 1, "Delete multiple");


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        try {

            if (item.getTitle() == "Delete") {
                int id = info.position;
                long rowId = list.get(id).dbId;

                mCr.delete(LBProvider.CONTENT_URI, LBProvider.KEY_ID + "=" + rowId, null);
                mCr.notifyChange(LBProvider.CONTENT_URI, null);
                getLoaderManager().restartLoader(0, null, this);

            } else {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (args != null) {
            selection = LBProvider.KEY_DATE + " BETWEEN ? AND ?";
            selectionArgs = new String[]{args.getString(KEY_FROM), args.getString(KEY_TO)};
        }
        CursorLoader loader;
        loader = new CursorLoader(this, LBProvider.CONTENT_URI, null, selection, selectionArgs, null);
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

    @Override
    public Object onRetainNonConfigurationInstance() {
        return cache;

    }

    @Override
    protected void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(0, null, this);
    }

}