package com.example.ashwinkumar.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ashwin Kumar on 7/6/2014.
 */
public class DatabaseAdapter {

    final Context context;
    DatabaseHelper helper;
    SQLiteDatabase db;

    public DatabaseAdapter(Context context){
        this.context=context;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context){
            super(context, "MyDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
