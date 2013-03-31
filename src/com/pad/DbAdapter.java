package com.pad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DbAdapter {

	public static final String DATABASE_NAME = "data";
    public static final String DATABASE_TABLE = "pad";
    public static final int DATABASE_VERSION = 12;

    public static final String COL_ROWID = "_id";      
	//public static final String COL_PATH = "path";   
	public static final String COL_TITLE = "title"; 
	public static final String COL_BODY = "body";   
	public static final String TAG = "DbAdapter";
	//public static final String COL_SAVED = "saved"; //flag

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    // DATABASE_VERSION = 5
    public static String DATABASE_CREATE =
            "create table pad ("
    				+ "_id integer primary key autoincrement"
                //    + ", path text null"
                    + ", title text not null"
                    + ", body text not null"
                    + ");";
    
    // DATABASE_VERSION = 4 - 5 UPGRADE // BADSQL
 /*   private static final String DATABASE_UPDATE =
				        "alter table "+ DATABASE_TABLE +" ("
				    + "_id integer primary key autoincrement"
				    + ", path text not null"
				    + ", title text not null"
				    + ", body text not null"
				    + ", saved int not null"
				    + ");";
*/

    private Context mContext;
 
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG,"Upgrading database from v" + oldVersion + " to v" + newVersion + ".");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE );
			db.execSQL(DATABASE_CREATE);
		}
		
    }
    
    
    public DbAdapter(Context ctx){
    	this.mContext = ctx;
    }
    
    /*public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }*/
    public DbAdapter open() throws SQLException {
    	if( mContext != null ){
    		mDbHelper = new DatabaseHelper(mContext);
    		mDb = mDbHelper.getWritableDatabase();
    	}
        return this;
    }

    
    public void close() {
        mDbHelper.close();
    }

    
    public long addEntry(String title, String body){
    	title = title.trim();
    	body = body.trim();
//    	path = path.trim();
    	
    	if(title.length() == 0 ){title = " ";}
    	if(body.length() == 0 ){body = " ";}
    	
    	ContentValues initialValues = new ContentValues();
//    	initialValues.put(COL_PATH, path);
 		initialValues.put(COL_TITLE, title);
    	initialValues.put(COL_BODY, body);
//    	initialValues.put(COL_SAVED, 0);
    	
    	return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    public boolean deleteEntry(long rowId){
    	return mDb.delete(DATABASE_TABLE, COL_ROWID + "=" + rowId, null) > 0;
    }

    public boolean updateEntry(long rowId, String title, String body) {
    	title = title.trim();
    	body = body.trim();
//    	path = path.trim();
    	//updateEntryColumn(rowId,"saved","0");
        ContentValues args = new ContentValues();
        args.put(COL_TITLE, title);
        args.put(COL_BODY, body);
//        args.put(COL_PATH, path);
        //args.put(COL_DATE, System.currentTimeMillis());
        return mDb.update(DATABASE_TABLE, args, COL_ROWID + "=" + rowId, null) > 0;
    }
    
    
    public boolean updateEntryColumn(long rowId, String column, String value) {
    	value = value.trim();
        ContentValues args = new ContentValues();
        args.put(column, value);
        return mDb.update(DATABASE_TABLE, args, COL_ROWID + "=" + rowId, null) > 0;
    }
    


    public Cursor getAllEntries() {

        Cursor lCursor = mDb.query(DATABASE_TABLE, new String[] {COL_ROWID, COL_TITLE, COL_BODY}, null, null, null, null, null);
        
        if (lCursor != null){
        	lCursor.moveToFirst();
        }
       
        return lCursor;
    }
    
    public Cursor getEntry(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {COL_ROWID,
                        COL_TITLE, COL_BODY}, COL_ROWID + "=" + rowId, null,
                        null, null, null, null);
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        
        return mCursor;
    }
       
}
