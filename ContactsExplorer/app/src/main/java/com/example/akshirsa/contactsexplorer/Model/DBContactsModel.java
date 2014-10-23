package com.example.akshirsa.contactsexplorer.Model;

/**
 * Created by akshirsa on 10/22/2014.
 */
public class DBContactsModel {
    private long contactid;
    private String fname;
    private String lname;
    private String email;
    private String timestamp;
    private int index;


    public DBContactsModel(){}

    public DBContactsModel(String fname, String lname, String email, int index) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.index = index;
    }

    public DBContactsModel(long id, String fname, String lname, String email, String timestamp, int index) {
        this.contactid = id;
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.timestamp = timestamp;
        this.index = index;
    }

    public String getFName()
    {
        return fname;
    }

    public String getLName()
    {
        return lname;
    }

    public String getEmail()
    {
        return email;
    }

    public long getID()
    {
        return contactid;
    }

    public int getIndex()
    {
        return index;
    }
}
