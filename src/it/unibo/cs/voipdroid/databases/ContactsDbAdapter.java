package it.unibo.cs.voipdroid.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ContactsDbAdapter {
	
	
    public static final String KEY_NAME="name";
    public static final String KEY_JID="jid";
    public static final String KEY_ROWID="_id";
    
	/**
     * Database creation sql statement
     */
    private static String DATABASE_CREATE = null;

    private static final String DATABASE_NAME = "contacts.db";
    private static String DATABASE_TABLE = null;
    private static final int DATABASE_VERSION = 2;
    
    private static final String TAG = "ContactsDbAdapter";
    
    private SQLiteDatabase mDb;
    private final Context mCtx;
    private DatabaseHelper mDbHelper;
    
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public ContactsDbAdapter(Context ctx, String table) {
        this.mCtx = ctx;
        setDatabaseTable(table);
        DATABASE_CREATE = "create table " +  getDatabaseTable() + " (_id integer primary key autoincrement, "
            + "name text not null, jid text not null);";
    }

	/**
     * Open the contacts database. If it cannot be opened, try to create a new instance of
     * the database. If it cannot be created, throw an exception to signal the failure
     * @return this (self reference, allowing this to be chained in an initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ContactsDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    /**
     * Close the contacts database 
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new contact using the name and sip address provided. If the contact 
     * is successfully created return the new rowId for that contact,
     * otherwise return a -1 to indicate failure.
     * @param name the name of the contact
     * @param sipAddress the sip address of the contact
     * @return rowId or -1 if failed
     */
    public long createContact(String name, String sipAddress) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_JID, sipAddress);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Delete the contact with the given rowId
     * @param rowId id of the contact to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteContact(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all contacts in the database
     * @return Cursor over all contacts
     */
    public Cursor fetchAllContacts() {
	        return mDb.query(DATABASE_TABLE, new String[] {
	                KEY_ROWID, KEY_NAME, KEY_JID}, null, null, null, null, KEY_NAME);
    }
    
    /**
     * Return a Cursor positioned at the contact that matches the given rowId
     * @param rowId id of the contact to retrieve
     * @return Cursor positioned to matching contact, if found
     * @throws SQLException if contact could not be found/retrieved
     */
    public Cursor fetchContact(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_NAME, KEY_JID}, KEY_ROWID + "=" + rowId, null, null,
                null, null,null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No contact matching ID: " + rowId);
        }
        return result;
    }

    /**
     * Update the contact using the details provided. The contact to be updated is specified using
     * the rowId, and it is altered to use the name and sip address values passed in
     * @param rowId id of contact to update
     * @param name value to set contact name to
     * @param sipAddress value to set contact sip address to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateContact(long rowId, String name, String sipAddress) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_JID, sipAddress);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the contact that matches the given query name
     * @param queryName name of contact we are searching for
     * @return Cursor positioned to matching contact, if found
     */
    public Cursor fetchName(String queryName){
        Cursor result = mDb.query(false, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_NAME, KEY_JID}, 
                KEY_NAME + " like '%" + queryName + "%' or "
                + KEY_JID + " like '%" + queryName + "%'" ,
                null, null, null, KEY_NAME, null);
        return result;
    }

    private void setDatabaseTable(String table) {
		DATABASE_TABLE = table;
	}
    
    private String getDatabaseTable() {
    	return DATABASE_TABLE;
    }
}
