package com.sttm.dataImpl;

import java.util.HashMap;




import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.sttm.dataImpl.KsoDataCenter.DataCenter;

public class KsoContentProvider extends ContentProvider {
	private KsoDBHelper dbHelper;
	private static final UriMatcher sUriMatcher;
	private static final int DATACENTER = 1;
	private static final int DATACENTER_ID = 2;

	private static HashMap<String, String> dataParams;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(KsoDataCenter.AUTHORITY, "dataCenter", DATACENTER);
		sUriMatcher.addURI(KsoDataCenter.AUTHORITY, "dataCenter/#",
				DATACENTER_ID);

		dataParams = new HashMap<String, String>();

		dataParams.put(DataCenter._ID, DataCenter._ID);
		dataParams.put(DataCenter.KSOKEY, DataCenter.KSOKEY);
		dataParams.put(DataCenter.KSOVALUE, DataCenter.KSOVALUE);

	}

	@Override
	public boolean onCreate() {
		dbHelper = new KsoDBHelper(getContext());

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder gb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case DATACENTER:
			gb.setTables(KsoDBHelper.TABLE_NAME);
			gb.setProjectionMap(dataParams);
			break;
		case DATACENTER_ID:

			gb.setTables(KsoDBHelper.TABLE_NAME);
			gb.setProjectionMap(dataParams);
			gb.appendWhere(DataCenter._ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("´íÎóµÄURL" + uri);
		}
		
		
		String orderBy;
		if(TextUtils.isEmpty(sortOrder)){
			orderBy = DataCenter.DEFAULT_SORT_ORDER;
		}else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = gb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);


		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case DATACENTER:
			return DataCenter.CONTENT_TYPE;

		case DATACENTER_ID:
			return DataCenter.CONTENT_ITEM_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long rowID = db.insert(KsoDBHelper.TABLE_NAME, DataCenter.KSOKEY,
				values);

		if (rowID > 0) {
			Uri ksoUri = ContentUris.withAppendedId(DataCenter.CONTENT_URI,
					rowID);
			this.getContext().getContentResolver().notifyChange(ksoUri, null);
			return ksoUri;

		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		int count;
		switch (sUriMatcher.match(uri)) {
		case DATACENTER:
			count = db.delete(KsoDBHelper.TABLE_NAME, selection, selectionArgs);
			break;
		case DATACENTER_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(KsoDBHelper.TABLE_NAME, DataCenter._ID
					+ "="
					+ noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("´íÎóµÄURL" + uri);

		}
		this.getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		int count;
		switch (sUriMatcher.match(uri)) {
		case DATACENTER:
			count = db.update(KsoDBHelper.TABLE_NAME, values,selection, selectionArgs);
			break;
		case DATACENTER_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(KsoDBHelper.TABLE_NAME,values, DataCenter._ID
					+ "="
					+ noteId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("´íÎóµÄURL" + uri);

		}
		this.getContext().getContentResolver().notifyChange(uri, null);


		return count;
	}

}
