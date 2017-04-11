package org.example.coursework2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import static android.provider.BaseColumns._ID;
import static org.example.coursework2.Constants.TABLE_NAME;
import static org.example.coursework2.Constants.TIME;
import static org.example.coursework2.Constants.TITLE;
import static org.example.coursework2.Constants.DETAILS;
import static org.example.coursework2.Constants.DATE;
/**
 * Created by DAVID on 09/03/2016.
 */
public class AppointmentData extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 4;
    String databasePath;

    public AppointmentData(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);

       databasePath = ctx.getDatabasePath("appointments.db").getPath();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                 _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                 TITLE + " TEXT NOT NULL, " +
                 TIME + " INTEGER, " +
                 DETAILS + " TEXT NOT NULL, " +
                 DATE + " TEXT NOT NULL);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
    }
}
