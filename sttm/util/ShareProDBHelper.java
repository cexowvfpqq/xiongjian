package com.sttm.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 存储工具类（xml）
 * 
 * @author ligm
 * 
 */


public class ShareProDBHelper {
	private Context context;

	// private String dbName = "";
	public ShareProDBHelper(Context context) {
		this.context = context;
	}

	public Editor writer(String dbName) {
		// this.dbName = dbName;
		SharedPreferences settings = context.getSharedPreferences(dbName, 0);
		SharedPreferences.Editor editor = settings.edit();
		// editor.putBoolean("silentMode", mSilentMode);

		// editor.commit();
		return editor;
	}

	public SharedPreferences getSharedPreferences(String dbName) {
		// this.dbName = dbName;
		// boolean silent = settings.getBoolean("silentMode", false);
		return context.getSharedPreferences(dbName, 0);
	}

	public void writerMsg(String key, String value, int flag) {

		SharedPreferences sp = context.getSharedPreferences("dataCenter",Context.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = sp.edit();
		try{
			switch (flag) {
				case 1: // int
					editor.putInt(key, Integer.parseInt(value));
					break;
				case 2:// String
					editor.putString(key, value);
					break;
				case 3: // boolean
					editor.putBoolean(key, Boolean.parseBoolean(value));
					break;
				case 4:// long
					editor.putLong(key, Long.parseLong(value));
					break;
					
		
			}
			editor.commit();
		}catch(Exception e){
			e.printStackTrace();
			LogFile.WriteLogFile("写入SHARE 文件失败：flag="+flag+"  value="+value);
		}

	}

	public String readMsg(String key, int flag,String defaltValue) {
		String result = "";
		SharedPreferences sp = context.getSharedPreferences("dataCenter", 0);
		try{
			switch (flag) {
				case 1: // int
					result = String.valueOf(sp.getInt(key, Integer.parseInt(defaltValue)));
					break;
				case 2:// String
					result = sp.getString(key, defaltValue);
					break;
				case 3: // boolean
					result = String.valueOf(sp.getBoolean(key, Boolean.parseBoolean(defaltValue)));
					break;
				case 4:// long
					result = String.valueOf(sp.getLong(key, Long.parseLong(defaltValue)));
					break;
	
			}
		}catch(Exception e){
			e.printStackTrace();
			LogFile.WriteLogFile("读出SHARE 文件失败：flag="+flag+"  key="+key);
		}
		return result;
		

	}




}
