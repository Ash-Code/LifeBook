package com.example.RNGD.lifebook;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Main_UI extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    ArrayList<Data> list;


    ListView mainList;
    CustomAdapter listAdapter;
    private ActionBar action;
    public static final int REQUEST_EDIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__ui);
        action = getActionBar();
        action.hide();
        getLoaderManager().initLoader(0,null,this);
        mainList = (ListView) findViewById(R.id.mainList);
        list = new ArrayList<Data>();
        mainList.setSmoothScrollbarEnabled(true);
        listAdapter = new CustomAdapter(this, R.layout.data_layout, list);
        mainList.setAdapter(listAdapter);
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_Add) {
            startActivityForResult(new Intent(this, Edit.class), REQUEST_EDIT);
        }
        return super.onOptionsItemSelected(item);
    }

    public void newItem(ArrayList<String> items){
        ContentResolver cr=getContentResolver();
        ContentValues vals=new ContentValues();
        vals.put(LBProvider.KEY_TITLE,items.get(0));
        vals.put(LBProvider.KEY_BODY,items.get(1));
        vals.put(LBProvider.KEY_DATE,getDateTime());
        cr.insert(LBProvider.CONTENT_URI,vals);
        getLoaderManager().restartLoader(0,null,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_EDIT:
                ArrayList<String> res=intent.getStringArrayListExtra("res");
                if(!res.isEmpty()){
newItem(res);
                }
                break;


        }

    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader=new CursorLoader(this,LBProvider.CONTENT_URI,null,null,null,null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data==null)
            return;
        int iTitle=data.getColumnIndexOrThrow(LBProvider.KEY_TITLE);
        int iBody=data.getColumnIndexOrThrow(LBProvider.KEY_BODY);
        int iDate=data.getColumnIndexOrThrow(LBProvider.KEY_DATE);
        list.clear();
        while(data.moveToNext()){
            Data dd=new Data(data.getString(iTitle),data.getString(iBody),"",data.getString(iDate));
            list.add(dd);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        getLoaderManager().restartLoader(0,null,this);
    }

}