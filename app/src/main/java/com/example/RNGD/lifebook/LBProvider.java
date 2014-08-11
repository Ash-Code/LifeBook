package com.example.RNGD.lifebook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Ashwin Kumar on 8/3/2014.
 */
public class LBProvider extends ContentProvider {
public static final Uri CONTENT_URI= Uri.parse("content://com.example.RNGD.LBProvider/elements");
    public  static final String DATABASE_NAME="lifebook.db";
    public static final String DATABASE_TABLE="DiaryEntries";
    public static final int DATABASE_VERSION=3;
    public static final String KEY_ID="_id";
    public static final String KEY_TITLE="title";
    public static final String KEY_BODY="body";
    public static final String KEY_IMAGES="images";
    public static final String KEY_DATE="dates";
    public static final String KEY_TAG="tags";
    public static final String KEY_COLOR="color";
    public static final int ALL_ROWS=1;
    public static final int SINGLE_ROW=2;

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private static final UriMatcher urimatcher;
    static {
        urimatcher=new UriMatcher(UriMatcher.NO_MATCH);
        urimatcher.addURI("com.example.RNGD.LBProvider","elements",ALL_ROWS);
        urimatcher.addURI("com.example.RNGD.LBProvider","elements/#",SINGLE_ROW);
    }
    public static final String DATABASE_CREATE="CREATE TABLE "+DATABASE_TABLE+" ("+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_DATE+" TEXT, "+KEY_TITLE+" TEXT, "+KEY_BODY+" TEXT, "+KEY_IMAGES+" TEXT, "+KEY_TAG+" TEXT);";



    private static class DBHelper extends SQLiteOpenHelper{
        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }



        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("LifeBook", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }




    @Override
    public boolean onCreate(){
dbHelper =new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db=dbHelper.getWritableDatabase();
        String groupby=null;
        String having=null;
        SQLiteQueryBuilder builder= new SQLiteQueryBuilder();
        builder.setTables(DATABASE_TABLE);
        switch(urimatcher.match(uri)){
            case SINGLE_ROW:
                String rowID=uri.getPathSegments().get(1);
                builder.appendWhere(KEY_ID+"="+rowID);
            default:break;
        }
        Cursor cursor=null;
        try {
            cursor = builder.query(db, projection, selection, selectionArgs, groupby, having, sortOrder);
        }catch(Exception e){
            e.printStackTrace();
        }
            return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch(urimatcher.match(uri)){
            case ALL_ROWS:
                return "vnd.android.cursor.dir/vnd.paad.elemental";
            case  SINGLE_ROW:
            return "vnd.android.cursor.item/vnd.paad.elemental";
            default :
                throw new IllegalArgumentException("Unsupported URI "+ uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db=dbHelper.getWritableDatabase();
        System.out.println(db.getVersion());
        long id=db.insert(DATABASE_TABLE,null,values);
    if(id>-1){
        Uri insertUri= ContentUris.withAppendedId(CONTENT_URI,id);
        getContext().getContentResolver().notifyChange(insertUri,null);
        return insertUri;
    }else
    return null;


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
db=dbHelper.getWritableDatabase();

        db.delete(DATABASE_TABLE,selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        db=dbHelper.getWritableDatabase();
        db.update(DATABASE_TABLE,values,selection,null);
        return 0;
    }


}
