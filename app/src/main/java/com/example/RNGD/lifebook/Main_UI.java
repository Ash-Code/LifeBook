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
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
    private ContentResolver mCr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action = getActionBar();
        action.hide();
        setContentView(R.layout.activity_main__ui);
mCr=getContentResolver();
        getLoaderManager().initLoader(0,null,this);
        mainList = (ListView) findViewById(R.id.mainList);
        list = new ArrayList<Data>();
        mainList.setSmoothScrollbarEnabled(true);
        listAdapter = new CustomAdapter(this, R.layout.data_layout, list);
        mainList.setAdapter(listAdapter);
        registerForContextMenu(mainList);
        mainList.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent i= new Intent(getBaseContext(), Update.class);
                Data temp=list.get(position);
                i.putExtra(LBProvider.KEY_ID,temp.dbId);
                i.putExtra(LBProvider.KEY_TITLE,temp.title);
                i.putExtra(LBProvider.KEY_BODY,temp.body);
                i.putExtra(LBProvider.KEY_DATE,temp.date);
                i.putExtra(LBProvider.KEY_IMAGES,temp.images);
                startActivity(i);
            }
        });
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
            startActivity(new Intent(this, Edit.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v, ContextMenuInfo info){
        super.onCreateContextMenu(menu, v, info);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Delete");



    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();


        try
        {

            if(item.getTitle()=="Delete")
            {
int id=info.position;
                long rowId=list.get(id).dbId;

                mCr.delete(LBProvider.CONTENT_URI,LBProvider.KEY_ID+"="+rowId,null);
mCr.notifyChange(LBProvider.CONTENT_URI,null);
                getLoaderManager().restartLoader(0,null,this);

            }
            else
            {return false;}
            return true;
        }
        catch(Exception e)
        {
            return true;
        }
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
        int iID=data.getColumnIndexOrThrow(LBProvider.KEY_ID);
        int iTitle=data.getColumnIndexOrThrow(LBProvider.KEY_TITLE);
        int iBody=data.getColumnIndexOrThrow(LBProvider.KEY_BODY);
        int iImages=data.getColumnIndexOrThrow(LBProvider.KEY_IMAGES);
        int iDate=data.getColumnIndexOrThrow(LBProvider.KEY_DATE);
        list.clear();
        while(data.moveToNext()){
            Data dd=new Data(data.getInt(iID),data.getString(iTitle),data.getString(iBody),data.getString(iImages),data.getString(iDate));
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