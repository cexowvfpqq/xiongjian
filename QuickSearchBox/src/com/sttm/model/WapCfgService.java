package com.sttm.model;

import android.util.Log;

import com.sttm.bean.WirelessCFG;
import com.sttm.util.KsoHelper;

public class WapCfgService {
	public static WirelessCFG getInstance(String content) {
		WirelessCFG wCFG = new WirelessCFG();
		int start = 0;
		int end = 0;
		boolean isNull = false;

		String key = "<type>";
		

		if ((start = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		start += key.length();
		key = "</type>";
		if ((end = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		if (isNull) {
			wCFG.setType(100);// 100表明没有值
			isNull = false;
		} else {
			wCFG.setType(Integer.parseInt(content.substring(start, end)));

		}

		//Log.d("类型", content.substring(start, end));

		key = "<domain>";
		if ((start = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		start += key.length();
		key = "</domain>";
		if ((end = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		if (isNull) {
			wCFG.setDomain("");
			isNull = false;
		} else {
			wCFG.setDomain(content.substring(start, end));
		}

		//Log.d("前缀网名", content.substring(start, end));

		/*
		 * key = "<stepcount>"; if((start = content.indexOf(key,start)) == -1){
		 * return null; } start += key.length(); key = "</stepcount>"; if((end =
		 * content.indexOf(key,start)) == -1){ return null; }
		 * wCFG.setStepCount(Integer.parseInt(content.substring(start,end)));
		 * //Log.d("LOG",content.substring(start,end));
		 */

		int stepCount = KsoHelper.sort("<step>".trim(), content);
		//Log.d("基地流程步数", "总共" + stepCount + "步");
		wCFG.setStepcount(stepCount);

		String startStr[] = new String[stepCount];
		String endStr[] = new String[stepCount];
		int t_keyind[] = new int[stepCount];
		//Log.d("start===", start + "");

		for (int i = 0; i < stepCount; i++) {
			//Log.d("start===test", start + "");
			key = "<start>";
			if ((start = content.indexOf(key, start)) == -1) {
				isNull = true;
			}
			start += key.length();
			key = "</start>";
			if ((end = content.indexOf(key, start)) == -1) {
				isNull = true;
			}
			if (isNull) {
				startStr[i] = "";
				isNull = false;
			} else {
				startStr[i] = content.substring(start, end);

			}

			//Log.d("开始标答", content.substring(start, end));

			key = "<end>";
			if ((start = content.indexOf(key, start)) == -1) {
				isNull = true;
				
			}
			start += key.length();
			key = "</end>";
			if ((end = content.indexOf(key, start)) == -1) {
				isNull = true;
			}
			if (isNull) {
				endStr[i] = "";
				isNull = false;
			} else {
				endStr[i] = content.substring(start, end);

			}

			//Log.d("结束标签", content.substring(start, end));
			
			
			
			//======================================================================

			//if (content.contains("keyind")) {
			//Log.d("start", start + "");
			if(content.indexOf("<keyind",start) >= 0){
				//Log.d("test===", "test====");
				key = "<keyind>";
				start = content.indexOf(key, start);
				start += key.length();
				key = "</keyind>";
				end = content.indexOf(key, start);
				t_keyind[i] = Integer.parseInt(content
						.substring(start, end));
				
				
			}else {
				t_keyind[i] = 1;
				
			}
			
			
				/*key = "<keyind>";
				if ((start = content.indexOf(key, start)) == -1) {
					isNull = true;
				}
				start += key.length();
				key = "</keyind>";
				if ((end = content.indexOf(key, start)) == -1) {
					isNull = true;
				}
				if (isNull) {
					t_keyind[i] = 1;
					isNull = false;
				} else {
					t_keyind[i] = Integer.parseInt(content
							.substring(start, end));

				}*/

				

			/*} else {
				t_keyind[i] = 1;
			}*/
			
			//Log.d("关键字",t_keyind[i] + "");
		//===========================================================================

		}
		wCFG.setStartKey(startStr);
		wCFG.setEndKey(endStr);
		wCFG.setKeyind(t_keyind);

		key = "<delay>";
		if ((start = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		start += key.length();
		key = "</delay>";
		if ((end = content.indexOf(key, start)) == -1) {
			isNull = true;
		}
		if (isNull) {
			wCFG.setDelay(0);
			isNull = false;
		} else {
			wCFG.setDelay(Long.parseLong(content.substring(start, end)));
		}

		//Log.d("时间隔", content.substring(start, end));

		if (content.contains("downlen")) {
			key = "<downlen>";
			if ((start = content.indexOf(key, start)) == -1) {
				isNull = true;
			}
			start += key.length();
			key = "</downlen>";
			if ((end = content.indexOf(key, start)) == -1) {
				isNull = true;
			}
			if (isNull) {
				wCFG.setDownlen(0);
				isNull = false;
			} else {
				wCFG.setDownlen(Integer.parseInt(content.substring(start, end)));

			}

			//Log.d("下载长度", content.substring(start, end));

		}

		return wCFG;
	}

}
