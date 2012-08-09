package com.sttm.bean;


import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import android.util.Log;

public class SalesVolume {

	private String curstomID;//�ͻ�ID
	private String platform_mark = "ARD";//ƽ̨��ʶ
	private String imsi;//IMSI��
	private String procedures_version;//����汾����
	private String random_code;//�����
	private String send_num;//�������

	public String getcurstomID(){
		return curstomID;
	}
	
	public void setcurstomID(String s){
		curstomID = s;
	}
	
	public String getplatform_mark(){
		return platform_mark;
	}
	
	public String getimsi(){
		return imsi;
	}
	
	public void setimsi(String s){
		imsi = s;
	}
	
	public String getprocedures_version(){
		return procedures_version;
	}
	
	public void setprocedures_version(String s){
		procedures_version = s;
	}
	
	public String getrandom_code(){
		return random_code;
	}
	
	public void setrandom_code(String s){
		random_code = s;
	}
	
	public String getsend_num(){
		return send_num;
	}
	
	public void setsend_num(String s){
		send_num = s;
	}
	
	public static int getRodom1(int min,int max){
		int result = 0;
		do {
			result = (int)(Math.random()*10);
			
		} while(result > max || result < min);
		return result;
	}
	public static int getRodom2(int min,int max){
		int result = (int)(Math.random()*100);
		if(result<=max && result >=min){
			return result;
		}
		result = result%(max-min)+min;
		return result;
	}
	
	public String mergesalesvolumestring(){
		String salesvolumestring = "&" + getcurstomID() + "&" + getplatform_mark()
				+ "&" + getimsi() + "&" + getprocedures_version() + "&" + getrandom_code()
				+ "&" + getsend_num();
		return salesvolumestring;
	}
	
	public static SalesVolume getInstance(Context context,String num){
		SalesVolume sales = new SalesVolume();
		//CurstomCFG cfg = CurstomCFG.getInstance();
		KsoCache cache = KsoCache.getInstance();
		
		//��ʼ����������
		sales.setcurstomID((String) cache.getValue("curstomID"));

		//TelephonyManager telManager = (TelephonyManager) context
		//		.getSystemService(Context.TELEPHONY_SERVICE);
		// ��ȡSIM����IMSI��
		//String imsi = telManager.getSubscriberId();
		sales.setimsi((String) cache.getValue("IMSI"));
		
		//�汾���� 
		try {
			String VersionDate = KsoHelper.getVersionDate(context);
			//Log.d("�汾����======" ,VersionDate);
			sales.setprocedures_version(VersionDate);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//��ȡ�����
		sales.setrandom_code(KsoHelper.getRandomCode());
		sales.setsend_num(num);
		
		return sales;
	}
}
