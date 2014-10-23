package com.example.akshirsa.contactsexplorer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by akshirsa on 10/22/2014.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "DB_CONTACTS_EXPLORER";
    // TAG
    private static final String TAG = "DB Helper";
    // Contacts table name
    protected static final String TABLE_NAME_CONTACTS = "table_contacts";


    // Table columns
    public static final String TCONTACTS_COL_ID = "contact_id";
    public static final String TCONTACTS_COL_FNAME = "first_name";
    public static final String TCONTACTS_COL_LNAME = "last_name";
    public static final String TCONTACTS_COL_EMAIL = "contact_email";
    public static final String TCONTACTS_COL_TIMESTAMP = "timestamp";
    public static final String TCONTACTS_COL_SORT_INDEX = "sort_index";


    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CTContactsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // create fresh tables
        this.onCreate(db);
    }


    private void CTContactsTable(SQLiteDatabase db)
    {
        // SQL statement to create flash card table
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CONTACTS + " ( " +
                TCONTACTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TCONTACTS_COL_FNAME + " TEXT, " +
                TCONTACTS_COL_LNAME + " TEXT, "+
                TCONTACTS_COL_EMAIL + " TEXT, "+
                TCONTACTS_COL_TIMESTAMP + " TEXT, " +
                TCONTACTS_COL_SORT_INDEX + " INTEGER )";

        // create the table
        db.execSQL(CREATE_TABLE);

        Log.d(TAG, "Created Table : " + TABLE_NAME_CONTACTS);
    }
}
