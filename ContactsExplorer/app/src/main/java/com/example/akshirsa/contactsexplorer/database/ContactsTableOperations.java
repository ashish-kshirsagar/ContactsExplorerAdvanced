package com.example.akshirsa.contactsexplorer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.akshirsa.contactsexplorer.Model.DBContactsModel;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by akshirsa on 10/22/2014.
 */
public class ContactsTableOperations {

    private MyDBHelper dbHelper;
    private SQLiteDatabase database;

    private String[] TABLE_CONTACTS_COLUMNS = {MyDBHelper.TCONTACTS_COL_ID,
                                                MyDBHelper.TCONTACTS_COL_FNAME,
                                                MyDBHelper.TCONTACTS_COL_LNAME,
                                                MyDBHelper.TCONTACTS_COL_EMAIL,
                                                MyDBHelper.TCONTACTS_COL_TIMESTAMP,
                                                MyDBHelper.TCONTACTS_COL_SORT_INDEX};

    private final String TAG = "ContactsTableOperations";

    public ContactsTableOperations(Context context)
    {
        //Open database connections.
        dbHelper = new MyDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    // --------------------------------------

    // Get all contact list from the table
    public ArrayList<DBContactsModel> getAllContacts()
    {
        ArrayList<DBContactsModel> records = new ArrayList<DBContactsModel>();

        Log.d(TAG, "Called method getAllContacts");

        //Get all records from the table
        Cursor cursor = database.query(MyDBHelper.TABLE_NAME_CONTACTS, TABLE_CONTACTS_COLUMNS, null, null, null, null, MyDBHelper.TCONTACTS_COL_SORT_INDEX, null);

        Log.d(TAG, "Total contacts found = " + cursor.getCount());

        cursor.moveToFirst();

        //Parse records and create objects
        while (!cursor.isAfterLast()) {
            DBContactsModel rec = parseContactRecord(cursor);
            records.add(rec);
            cursor.moveToNext();
        }

        cursor.close();
        return records;
    }

    private DBContactsModel parseContactRecord(Cursor cursor) {
        return new DBContactsModel(cursor.getLong(0),
                                    cursor.getString(1),
                                    cursor.getString(2),
                                    cursor.getString(3),
                                    cursor.getString(4),
                                    cursor.getInt(5));
    }


    //add contact details
    public void addContactRecord(DBContactsModel rec)
    {
        // set the format to SQL date time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(MyDBHelper.TCONTACTS_COL_FNAME, rec.getFName());
        values.put(MyDBHelper.TCONTACTS_COL_LNAME, rec.getLName());
        values.put(MyDBHelper.TCONTACTS_COL_EMAIL, rec.getEmail());
        values.put(MyDBHelper.TCONTACTS_COL_SORT_INDEX, rec.getIndex());
        values.put(MyDBHelper.TCONTACTS_COL_TIMESTAMP, dateFormat.format(date));

        //Insert above created record into table
        long recId = database.insert(MyDBHelper.TABLE_NAME_CONTACTS, null, values);

        Log.d(TAG, "Record added successfully in table " + MyDBHelper.TABLE_NAME_CONTACTS + " with id = " + recId);
    }


    // Delete contact record
    public void deleteContactsRecord(long recid)
    {
        Log.d(TAG, "Deleting record ID : " + recid);

        database.delete(MyDBHelper.TABLE_NAME_CONTACTS, MyDBHelper.TCONTACTS_COL_ID + " = " + recid, null);
    }


    // Update contact record.
    // This will update only the index of record in case of sorting
    public void updateContactRecord(ArrayList<DBContactsModel> records)
    {
        int counter = 1;
        ContentValues newValues = new ContentValues();

        // Update sorting order of the records
        for(DBContactsModel rec : records)
        {
            // Just have to update sorting index instead of rearranging all values.
            newValues.put(MyDBHelper.TCONTACTS_COL_SORT_INDEX, counter++);

            // Update the sorting index in DB for this record
            database.update(MyDBHelper.TABLE_NAME_CONTACTS, newValues, MyDBHelper.TCONTACTS_COL_ID + " = " + rec.getID(), null);

            // Clear values for next record
            newValues.clear();
        }


        // Delete records if any
        // If record present in DB but not in input list then it must be deleted
        ArrayList<DBContactsModel> dbrecords = getAllContacts();

        for(int i=0; i<dbrecords.size(); i++)
        {
            //If all records deleted then delete them from DB
            if(records.size() == 0) {
                deleteContactsRecord(dbrecords.get(i).getID());
            }
            // If DB record is not matching with input record then delete it
            else if(dbrecords.get(i).getID() != records.get(i).getID())
            {
                deleteContactsRecord(dbrecords.get(i).getID());
            }
        }
    }
}
