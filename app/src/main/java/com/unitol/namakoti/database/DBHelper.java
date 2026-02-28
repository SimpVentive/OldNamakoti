package com.unitol.namakoti.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.model.CountModel;

public class DBHelper extends SQLiteOpenHelper{

	public static String DATABASE_NAME = "NAMAKOTI";
	public static String COUNT_TABLE_NAME = "count_table";

	// Count Table Columns
	public static String USER_ID = "user_id";
	public static String NAMAM_ID = "namam_id";

	public static int DATABASE_VERSION = 3;

	private SQLiteDatabase db;
	public static DBHelper instance = null;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBHelper() {
		super(MyApplication.getInstance().getApplicationContext(), DATABASE_NAME,
				null, DATABASE_VERSION);
	}

	public static DBHelper getInstance() {
		if (instance != null) {
			return instance;
		} else {
			return new DBHelper();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		//create a counting table..
		String new_count_table = "CREATE TABLE "+ COUNT_TABLE_NAME+"("
				+"internal_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
				+USER_ID+" TEXT,"
				+NAMAM_ID+" TEXT NOT NULL);";
		db.execSQL(new_count_table);
	}

	public void insertIntoCountTable(CountModel countModel)
	{
		db = getWritableDatabase();

		ContentValues cv = new ContentValues();

		try
		{
			cv.put(USER_ID, countModel.getUser_id());
			cv.put(NAMAM_ID, countModel.getNamam_id());


			db.insert(COUNT_TABLE_NAME, null, cv);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
