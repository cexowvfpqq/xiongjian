package com.sttm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;

public class LogFile {
	private static File getLogFile(){
		/*
		 * 发布Release版本时不提供LOG文件
		 */
		return null;
		/*
		File sdDir = null;
		String path = "";
        boolean sdCardExist = Environment.getExternalStorageState()
        				.equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在 
        if(sdCardExist){                             
        	sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
        	////Log.d("LOGFile",sdDir.toString());
        	path = sdDir.toString() + "/kso";
        }
        else{
        	path = "kso";
        }
        //Log.d("LOGFile",path);
        File ret = new File(path);
    	if(!ret.exists()){
    		ret.mkdirs();
    	}
    	path += "/kso.log";
    	ret = new File(path);
    	if(!ret.exists()){
    		try {
				ret.createNewFile();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
    	}
        return ret;*/
	}
	
	public static void log_d(boolean isLog,String tag,String msg){
		if(isLog){
			//Log.d(tag, msg);
		}
	}
	public static void WriteLogFile(String log){
		
		/*
		 * 发布Release版本时不提供LOG文件
		 */
		return;
		/*
		File file = getLogFile();
		if(file == null){
			//Log.d("WriteLogFIle","读取文件操作失败");
			return ;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		log = sdf.format(new Date()) + "    " + log + "\r\n";
		FileWriter fw;
		try {
			fw = new FileWriter(file,true);
			fw.write(log);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}
	
	
	public static void WriteLogFile1(String log,Context context){
		/*
		 * 发布Release版本时不提供LOG文件
		 */
		return;
		/*
		try {
			boolean isWriteLog = KsoHelper.isWriteLog(context);
			if(!isWriteLog){
				return;
				
			}
		} catch (NameNotFoundException e) {
			
			e.printStackTrace();
			return ;
		}
		
		File file = getLogFile();
		if(file == null){
			//Log.d("WriteLogFIle","读取文件操作失败");
			return ;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		log = sdf.format(new Date()) + "    " + log + "\r\n";
		FileWriter fw;
		try {
			fw = new FileWriter(file,true);
			fw.write(log);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}
}
