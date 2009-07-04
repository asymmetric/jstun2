package it.unibo.cs.voipdroid.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ProfileDbAdapter {
	public static final String KEY_FULL="fullname";
    public static final String KEY_GIZMO="gizmoname";
    public static final String KEY_CITY="city";
    public static final String KEY_STATE="state";
    public static final String KEY_COUNTRY="country";
    public static final String KEY_HOME="homepage";
    public static final String KEY_SIP_URI="sip_uri";
    public static final String KEY_LANGUAGE="language";
    public static final String KEY_SEX="sex";
    public static final String KEY_BIRTH="birthday";
    public static final String KEY_DESCRIPTION="description";
    public static final String KEY_MD5="md5";
    public static final String KEY_ROWID="_id";
    
	/**
     * Database creation sql statement
     */
    private static String DATABASE_CREATE = null;

    private static final String DATABASE_NAME = "profiles.db";
    private static String DATABASE_TABLE = null;
    private static final int DATABASE_VERSION = 2;
    
    private static final String TAG = "ProfileDbAdapter";
    
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
            db.execSQL("DROP TABLE IF EXISTS profiles");
            onCreate(db);
        }
    }


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public ProfileDbAdapter(Context ctx, String table) {
        this.mCtx = ctx;
        setDatabaseTable(table);
        DATABASE_CREATE = "create table " +  getDatabaseTable() + " (_id integer primary key autoincrement, "
            + "fullname text not null, gizmoname text not null, city text, state text, " +
            "country text, homepage text, sip_uri text not null, language text, " +
            "sex text, birthday text, description text, md5 text not null);";
    }

	/**
     * Open the profiles database. If it cannot be opened, try to create a new instance of
     * the database. If it cannot be created, throw an exception to signal the failure
     * @return this (self reference, allowing this to be chained in an initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public ProfileDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    /**
     * Close the profile database 
     */
    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new profile using the strings provided. If the profile 
     * is successfully created return the new rowId for that profile,
     * otherwise return a -1 to indicate failure.
     * @param fullname the full name of the user
     * @param gizmoname the gizmo username of the user
     * @param city the city of the user
     * @param state the state of the user
     * @param country the country of the user
     * @param homepage the homepage URL of the user
     * @param sip_uri the sip URI of the user
     * @param language the language of the user
     * @param sex the sex of the user
     * @param birthday the birthday date of the user
     * @param description the description of the user
     * @param md5 the md5 checksum of the profile
     * @return rowId or -1 if failed
     */
    public long createProfile(String fullname, String gizmoname, String city,
    		String state, String country, String homepage, String sip_uri,
    		String language, String sex, String birthday, String description,
    		String md5) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FULL, fullname);
        initialValues.put(KEY_GIZMO, gizmoname);
        initialValues.put(KEY_CITY, city);
        initialValues.put(KEY_STATE, state);
        initialValues.put(KEY_COUNTRY, country);
        initialValues.put(KEY_HOME, homepage);
        initialValues.put(KEY_SIP_URI, sip_uri);
        initialValues.put(KEY_LANGUAGE, language);
        initialValues.put(KEY_SEX, sex);
        initialValues.put(KEY_BIRTH, birthday);
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_MD5, md5);
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Delete the profile with the given rowId
     * @param rowId id of the profile to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteProfile(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the profile that matches the given rowId
     * @param rowId id of the profile to retrieve
     * @return Cursor positioned to matching profile, if found
     * @throws SQLException if profile could not be found/retrieved
     */
    public Cursor fetchProfile(long rowId) throws SQLException {
        Cursor result = mDb.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_FULL, KEY_GIZMO, KEY_CITY, KEY_STATE, KEY_COUNTRY,
                KEY_HOME, KEY_SIP_URI, KEY_LANGUAGE, KEY_SEX, KEY_BIRTH,
                KEY_DESCRIPTION, KEY_MD5}, KEY_ROWID + "=" + rowId, null, null,
                null, null,null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No profile matching ID: " + rowId);
        }
        return result;
    }

    /**
     * Update the profile using the details provided. The profile to be updated is specified using
     * the rowId, and it is altered to use the name and sip address values passed in
     * @param rowId id of profile to update
     * @param fullname value to set profile user fullname
     * @param gizmoname value to set profile user gizmoname
     * @param city value to set profile user city
     * @param state value to set profile user state
     * @param country value to set profile user country
     * @param homepage value to set profile user homepage url
     * @param sip_uri value to set profile user sip uri
     * @param language value to set profile user language
     * @param sex value to set profile user sex
     * @param birthday value to set profile user birthday
     * @param description value to set profile user description
     * @param md5 value to set profile user md5 sum
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateProfile(long rowId, String fullname, String gizmoname, String city,
    		String state, String country, String homepage, String sip_uri,
    		String language, String sex, String birthday, String description,
    		String md5) {
        ContentValues args = new ContentValues();
        args.put(KEY_FULL, fullname);
        args.put(KEY_GIZMO, gizmoname);
        args.put(KEY_CITY, city);
        args.put(KEY_STATE, state);
        args.put(KEY_COUNTRY, country);
        args.put(KEY_HOME, homepage);
        args.put(KEY_SIP_URI, sip_uri);
        args.put(KEY_LANGUAGE, language);
        args.put(KEY_SEX, sex);
        args.put(KEY_BIRTH, birthday);
        args.put(KEY_DESCRIPTION, description);
        args.put(KEY_MD5, md5);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor positioned at the profile that matches the given query name
     * @param queryName gizmoname of profile we are searching for
     * @return Cursor positioned to matching profile, if found
     */
    public Cursor fetchName(String queryName){
        Cursor result = mDb.query(false, DATABASE_TABLE, new String[] {
        		 KEY_ROWID, KEY_FULL, KEY_GIZMO, KEY_CITY, KEY_STATE, KEY_COUNTRY,
                 KEY_HOME, KEY_SIP_URI, KEY_LANGUAGE, KEY_SEX, KEY_BIRTH,
                 KEY_DESCRIPTION, KEY_MD5}, 
                KEY_GIZMO + " like '%" + queryName + "%'" ,
                null, null, null, KEY_GIZMO, null);
        return result;
    }
    
    /**
     * Return a Cursor positioned at the profile that matches the given query name
     * @param queryName gizmoname of profile we are searching for
     * @return Cursor positioned to matching profile, if found
     */
    public Cursor fetchMd5(String queryName){
        Cursor result = mDb.query(false, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_GIZMO,KEY_MD5}, 
                KEY_GIZMO + " like '%" + queryName + "%'" ,
                null, null, null, KEY_GIZMO, null);
        return result;
    }
    
    private void setDatabaseTable(String table) {
		DATABASE_TABLE = table;
	}
    
    private String getDatabaseTable() {
    	return DATABASE_TABLE;
    }
}
