/**
* Created By: Godfrey Oguike Copyright 2014
* 
* the database adapter creates the table and store the necessary methods to access
* and modify the data stored in the database.
**/

package com.example.myimchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
   
	static final String KEY_ROWID = "_id"; //Column user in DB.
    static final String KEY_MESSAGE = "message"; //Column user in DB.
    static final String KEY_USER = "user"; //Column user in DB.
    static final String KEY_FRIEND = "friend"; //Column friend in DB.
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "T";
    static final String DATABASE_TABLE = "TheMessages2";
    static final int DATABASE_VERSION = 1;
   
    // Put all column references into one array for later use.
    public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_MESSAGE, KEY_USER, KEY_FRIEND};
    
    // Create DB statement.
    private static final String DATABASE_CREATE_SQL = "create table " + DATABASE_TABLE 
    		+ " (" + KEY_ROWID + " integer primary key autoincrement, "+ KEY_MESSAGE + " text not null, "+ KEY_USER + " text, "+ KEY_FRIEND + " text "
			+ ");";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    
    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE_SQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS message");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() 
    {
        DBHelper.close();
    }

    //insert message into the DB
    public long insertMessage(String message, String user, String friend) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MESSAGE, message);
        initialValues.put(KEY_USER, user);
        initialValues.put(KEY_FRIEND, friend);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));				
			} while (c.moveToNext());
		}
		c.close();
	}
    
    public boolean deleteRow(long rowId) {
//      int	 delete(String table, String whereClause, String[] whereArgs)
//      Convenience method for deleting rows in the database.
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}

    public Cursor getAllRows() {
//    	Cursor	 query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal)
//    	Query the given URL, returning a Cursor over the result set.
		String where = "user = ? AND friend = ?"; //Define the columns to search.
		String[] whereArgs = new String[] {""+Logi.parsedUser, ""+FriendsList.theFriend}; //Define the value to search for.
		String orderBy = null;
		
		Cursor c = 	db.query(DATABASE_TABLE, ALL_KEYS, where, whereArgs, null, null, orderBy, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
}
