package com.example.songchiyun.myapplication;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.sql.Date;
import java.sql.Time;

import android.content.Context;
import android.util.Log;

/**
 * Created by songchiyun on 16. 8. 8..
 */
public class DbAdapter {
    Time time;
    Date date;
    SQLiteDatabase DB;
    static final String ID = "id";
    static final String HEART_RATE = "heart_rate";
    static final String DATE = "date";
    static final String TIME = "time";
    static final String DATABASENAME = "HoM_realTime";
    static final String DATABASETABLE = "RealTime";
    static final int VERSION = 1;
    static final String DATABASE_CREATE =
            "create table " + DATABASETABLE + " (id integer primary key autoincrement, "
                    + "date text not null, time text not null,"
                    + "heart_rate text not null);";
    Context context;

    Helper DBHelper;
    SQLiteDatabase db;

    public DbAdapter(Context c) {
        this.context = c;
        Log.d("db","db adapter");
        DBHelper = new Helper(context);
    }

    public class Helper extends SQLiteOpenHelper {

        public Helper(Context c) {
            super(context, DATABASENAME, null, VERSION);
            Log.d("db","check helper");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
                Log.d("db","create");
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d("db","not create");
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS contacts");
            Log.d("db","upgrade");
            onCreate(db);
        }
    }

    public DbAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---closes the database---
    public void close() {
        DBHelper.close();
    }

    public long insertContact(int id, String heart_rate) {
        Log.d("db","insert in db");
        long now = System.currentTimeMillis();
        date = new Date(now);
        String d= String.valueOf(date.getDate());
        String time = String.valueOf(date.getTime());
        ContentValues initialValues = new ContentValues();
        initialValues.put(ID, id);
        initialValues.put(DATE, d);
        initialValues.put(TIME, time);
        initialValues.put(HEART_RATE, heart_rate);
        return db.insert(DATABASETABLE, null, initialValues);
    }

    // ---deletes a particular contact---
    public boolean deleteContact(long rowId) {
        return db.delete(DATABASETABLE, ID + "=" + rowId, null) > 0;
    }
    public Cursor getAllContacts() {
        return db.query(DATABASETABLE, new String[]{ID, DATE,
                TIME, HEART_RATE}, null, null, null, null, null);
    }

}
