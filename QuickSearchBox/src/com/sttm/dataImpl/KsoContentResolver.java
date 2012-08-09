package com.sttm.dataImpl;

import com.sttm.dataImpl.KsoDataCenter.DataCenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class KsoContentResolver {
	private Context context;

	public KsoContentResolver(Context context) {
		this.context = context;

	}

	public void insert(String key, String value) {

		Uri uri = DataCenter.CONTENT_URI;

		ContentValues values = new ContentValues();

		values.put(DataCenter.KSOKEY, key);

		values.put(DataCenter.KSOVALUE, value);

		context.getContentResolver().insert(uri, values);

	}

	public void insert(String[] key, String[] value, int length) {

		Uri uri = DataCenter.CONTENT_URI;

		ContentValues values = new ContentValues();
		for (int i = 0; i < length; i++) {
			values.put(DataCenter.KSOKEY, key[i]);

			values.put(DataCenter.KSOVALUE, value[i]);

			context.getContentResolver().insert(uri, values);
			values.clear();

		}

	}

	public void delete(String key) {
		Uri uri = DataCenter.CONTENT_URI;
		String where = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { key };
		context.getContentResolver().delete(uri, where, selectionArgs);
	}

	public void delete() {
		Uri uri = DataCenter.CONTENT_URI;

		context.getContentResolver().delete(uri, null, null);
	}

	public void update(String key, String value) {

		Uri uri = DataCenter.CONTENT_URI;
		ContentValues values = new ContentValues();

		values.put(DataCenter.KSOVALUE, value);

		String where = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { key };

		context.getContentResolver().update(uri, values, where, selectionArgs);
	}

	public String getCurstomId() {
		String curstomID = "KS000102";
		Uri uri = DataCenter.CONTENT_URI;
		String[] projection = { DataCenter.KSOVALUE };// 查询结果（哪些列查出来)
		String selection = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { "curstomID" };

		Cursor c = context.getContentResolver().query(uri, projection,
				selection, selectionArgs, null);

		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				curstomID = c.getString(c.getColumnIndexOrThrow(DataCenter.KSOVALUE));
				
			}
		}
		return curstomID;

	}
	
	public String getSmsCenterNumber() {
		String smsCenterNumber = "755";
		Uri uri = DataCenter.CONTENT_URI;
		String[] projection = { DataCenter.KSOVALUE };// 查询结果（哪些列查出来)
		String selection = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { "smsCenterNumber" };

		Cursor c = context.getContentResolver().query(uri, projection,
				selection, selectionArgs, null);

		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				smsCenterNumber = c.getString(c.getColumnIndexOrThrow(DataCenter.KSOVALUE));
				
			}
		}
		return smsCenterNumber;

	}
	
	
	public String query(String key) {
		String result = "";
		
		Uri uri = DataCenter.CONTENT_URI;
		String[] projection = { DataCenter.KSOVALUE };// 查询结果（哪些列查出来)
		String selection = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { key };

		Cursor c = context.getContentResolver().query(uri, projection,
				selection, selectionArgs, null);

		if (c.moveToFirst()) {
			for (int i = 0; i < c.getCount(); i++) {
				c.moveToPosition(i);
				result = c.getString(c.getColumnIndexOrThrow(DataCenter.KSOVALUE));
				
			}
		}
		return result;

	}
	
	public boolean isSave(String key) {
		boolean result = false;
		
		Uri uri = DataCenter.CONTENT_URI;
		String[] projection = { DataCenter.KSOVALUE };// 查询结果（哪些列查出来)
		String selection = DataCenter.KSOKEY + "=?";
		String[] selectionArgs = { key };

		Cursor c = context.getContentResolver().query(uri, projection,
				selection, selectionArgs, null);
        if(c == null){
        	return false;
        }
		if(c.getCount() > 0){
			result = true;
		}
		return result;

	}
	
	

	
    public Cursor query1(String key) {  
        // 获得ContentResolver对象   
        ContentResolver cr = context.getContentResolver();  
        Uri uri = DataCenter.CONTENT_URI;  
        // 查询对象   
        String[] projection = { DataCenter._ID, DataCenter.KSOKEY, DataCenter.KSOVALUE };  
        // 设置查询条件，这里我把selection和selectionArgs参数都设为null，表示查询全部数据   
        String selection = null;  
        String[] selectionArgs = null;  
        if (!"".equals(key)) {  
            selection = DataCenter.KSOKEY + "=?";  
            selectionArgs = new String[] { key };  
        }  
        // 设置排序条件   
        String sortOrder = DataCenter._ID;  
        Cursor c = cr.query(uri, projection, selection, selectionArgs,  
                sortOrder);  
       
        return c;  
    }  

}
