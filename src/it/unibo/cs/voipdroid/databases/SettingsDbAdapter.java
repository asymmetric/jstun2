/* 
*  Copyright 2007, 2008, 2009 Luca Bonora, Luca Bedogni, Lorenzo Manacorda
*  
*  This file is part of VOIPDroid.
*
*  VOIPDroid is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  VOIPDroid is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with VOIPDroid.  If not, see <http://www.gnu.org/licenses/>.
*/
package it.unibo.cs.voipdroid.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class SettingsDbAdapter {

		public static final String KEY_NICKNAME="nickname";
		public static final String KEY_USERNAME="username";
	    public static final String KEY_PASSWORD="password";
	    public static final String KEY_REGISTRAR="registrar";
	    public static final String KEY_STUN="stun";
	    public static final String KEY_CHECKREG="checkreg";
	    public static final String KEY_ROWID="_id";
	    
	    /**
	     * Database creation sql statement
	     */
	    private static final String DATABASE_CREATE =
	        "create table settings (_id integer primary key autoincrement, "
	        + "nickname text not null,username text not null,password text not null, " + 
	        "registrar text not null, stun text not null,checkreg text not null );";
	    
	    private static final String TAG = "SettingsDbAdapter";
	    private static final String DATABASE_NAME = "settings.db";
	    private static final String DATABASE_TABLE = "settings";
	    private static final int DATABASE_VERSION = 2;
	    
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
	            db.execSQL("DROP TABLE IF EXISTS settings");
	            onCreate(db);
	        }
	    }

	    
	    /**
	     * Constructor - takes the context to allow the database to be opened/created
	     * @param ctx the Context within which to work
	     */
	    public SettingsDbAdapter(Context ctx) {
	        this.mCtx = ctx;
	    }
	    
	    /**
	     * Open the settings database. If it cannot be opened, try to create a new instance of
	     * the database. If it cannot be created, throw an exception to signal the failure
	     * @return this (self reference, allowing this to be chained in an initialization call)
	     * @throws SQLException if the database could be neither opened or created
	     */
	    public SettingsDbAdapter open() throws SQLException {
	        mDbHelper = new DatabaseHelper(mCtx);
	        mDb = mDbHelper.getWritableDatabase();
	        return this;
	    }
	    
	    /**
	     * Close the settings database 
	     */
	    public void close() {
	        mDbHelper.close();
	    }
	    
	    /**
	     * Create a new setting using the username, the password 
	     * and the registrar provided. 
	     * If the contact setting is successfully created
	     * return the new rowId for that setting,
	     * otherwise return a -1 to indicate failure.
	     * @param nickname the nickname of the contact
	     * @param username the username of the contact
	     * @param password the password of the contact
	     * @param registrar the realm of the contact
	     * @param check true if register on startup
	     * @return rowId or -1 if failed
	     */
	    public long createSetting(String nickname, String username, String password, String registrar, String stun, String checkreg) {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_NICKNAME, nickname);
	        initialValues.put(KEY_USERNAME, username);
	        initialValues.put(KEY_PASSWORD, password);
	        initialValues.put(KEY_REGISTRAR, registrar);
	        initialValues.put(KEY_STUN, stun);
	        initialValues.put(KEY_CHECKREG, checkreg);
	        return mDb.insert(DATABASE_TABLE, null, initialValues);
	    }
	    
	    /**
	     * Return a Cursor positioned at the setting that matches the given rowId
	     * @param rowId id of the setting to retrieve
	     * @return Cursor positioned to matching setting, if found
	     * @throws SQLException if setting could not be found/retrieved
	     */
	    public Cursor fetchSetting(long rowId) throws SQLException {
	        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
	                KEY_ROWID, KEY_NICKNAME, KEY_USERNAME, KEY_PASSWORD, KEY_REGISTRAR, KEY_STUN, KEY_CHECKREG},
	                KEY_ROWID + "=" + rowId, null, null, null, null, null);
	        if ((result.getCount() == 0) || !result.moveToFirst()) {
	            throw new SQLException("No setting matching ID: " + rowId);
	        }
	        return result;
	    }
	    
	    
	    /**
	     * Return a Cursor over the list of all contacts in the database
	     * @return Cursor over all contacts
	     */
	    public Cursor fetchAllSettings() {
		        return mDb.query(DATABASE_TABLE, new String[] {
		                KEY_ROWID, KEY_NICKNAME, KEY_USERNAME, KEY_PASSWORD, KEY_REGISTRAR, KEY_CHECKREG}, null, null, null, null, null);
	    }
	    
	    /**
	     * Update the contact settings using the details provided.
	     * The setting to be updated is specified using	the rowId, 
	     * and it is altered to use the username, password and registrar values passed in.
	     * @param rowId id of contact to update
	     * @param nickname value to set contact nickname to
	     * @param username value to set contact name to
	     * @param password value to set contact password to
	     * @param registrar value to set contact registrar to
	     * @param check true if register on startup
	     * @return true if the note was successfully updated, false otherwise
	     */
	    public boolean updateSetting(long rowId,
	    		String nickname, String username, String password, String registrar, String stun, String checkreg) 
	    {
	        ContentValues args = new ContentValues();
	        args.put(KEY_NICKNAME, nickname);
	        args.put(KEY_USERNAME, username);
	        args.put(KEY_PASSWORD, password);
	        args.put(KEY_REGISTRAR, registrar);
	        args.put(KEY_STUN, stun);
	        args.put(KEY_CHECKREG, checkreg);
	        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	    }
}
