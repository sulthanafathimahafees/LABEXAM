
package com.example.busfarecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BusFare.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE fares (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "distance REAL, rate REAL, " +
                "passengers INTEGER, total REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS fares");
        onCreate(db);
    }

    public boolean insertFare(double distance, double rate,
                              int passengers, double total) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("distance", distance);
        values.put("rate", rate);
        values.put("passengers", passengers);
        values.put("total", total);

        long result = db.insert("fares", null, values);
        db.close();

        return result != -1;
    }

    public Cursor getAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM fares ORDER BY id DESC", null);
    }

    public boolean deleteRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(
                "fares", "id=?", new String[]{String.valueOf(id)});

        db.close();
        return rowsDeleted > 0;
    }
}
