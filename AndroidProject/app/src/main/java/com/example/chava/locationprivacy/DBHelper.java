package com.example.chava.locationprivacy;

/**
 * Created by vikramchava on 6/28/17.
 */


//This class helps with the local database operations for storing the random locations

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Locations.db";
    public static final String Locations_TABLE_NAME = "previousLocations";
    public static final String Locations_COLUMN_lat = "lat";
    public static final String Locations_COLUMN_lon = "lon";
    public static final String Locations_COLUMN_condChoice = "condChoice";
    public static final String Locations_COLUMN_num = "num";
    public static final String Locations_COLUMN_results = "results";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table previousLocations (lat REAL, lon REAL, num REAL,results TEXT, condChoice num)");
        db.execSQL("CREATE TRIGGER WipeOnOverflow\n" +
                "AFTER INSERT ON previousLocations\n" +
                "WHEN (SELECT COUNT(*) FROM previousLocations) > 1000\n" +
                "BEGIN\n" +
                "    DELETE FROM previousLocations;\n" +
                "END;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS previousLocations");
        onCreate(db);
    }


    public boolean insertLocations (Double lat, Double lon, Double num, String results, int condChoice, boolean deleteCol) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(deleteCol){
            Log.d("Detailed Analysis","Going to delete previous location values in sqllite db");
            deleteColumn(lat, lon);
        }
        //db.execSQL("Delete from previousLocations");
        ContentValues contentValues = new ContentValues();
        contentValues.put("lat", lat);
        contentValues.put("lon", lon);
        contentValues.put("num", num);
        contentValues.put("results", results);
        contentValues.put("condChoice",condChoice);
        db.insert("previousLocations", null, contentValues);
        return true;
    }

    public Cursor getData(double lat, double lon, double num) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from previousLocations where lat="+Math.round(lat * 10000.0) / 10000.0+" and lon="+Math.round(lon * 10000.0) / 10000.0+"", null );
        Log.d("Detailed analysis","executing the query");
        //Cursor res =  db.rawQuery( "select * from previousLocations", null );
        return res;
    }

    public Cursor getLocationData() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor res =  db.rawQuery( "select * from previousLocations where lat="+lat+" and lon="+lon+"", null );
        Log.d("Detailed analysis","executing the query");
        Cursor res =  db.rawQuery( "select * from previousLocations", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Locations_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Double lat, Double lon, double num, String results) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("lat", lat);
        contentValues.put("lon", lon);
        contentValues.put("num", num);
        contentValues.put("results", results);
        db.update("previousLocations", contentValues, "lat = ? , lon = ? ", new String[] { Double.toString(lat), Double.toHexString(lon) } );
        return true;
    }

    public Integer deleteColumn (Double lat, Double lon) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("previousLocations",
                "lat = ? AND lon = ? ",
                new String[] { Double.toString(lat) , Double.toString(lon) });
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            //array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }
}
