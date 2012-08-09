package com.sttm.dataImpl;

import com.sttm.dataImpl.KsoDataCenter.DataCenter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

public class KsoDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ksoDataCenter.db";
	private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME  = "dataCenter";

	public KsoDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
		        + DataCenter._ID + " INTEGER PRIMARY KEY,"
		        + DataCenter.KSOKEY + " TEXT,"
		        + DataCenter.KSOVALUE + " TEXT"
		        + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS DATACENTER");
		onCreate(db);
		

	}

}
