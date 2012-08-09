package com.sttm.model;

import android.util.Log;

import com.sttm.bean.WirelessCFG;
import com.sttm.util.KsoHelper;

public class KsoWapCFGService {
	public static WirelessCFG getInstance(String content) {
		WirelessCFG cfg = new WirelessCFG();
		
		int stepCount = KsoHelper.sort("<step>".trim(), content);
		//Log.d("基地流程步数", "总共" + stepCount + "步");
		cfg.setStepcount(stepCount);
		String startStr[] = new String[stepCount];
		String endStr[] = new String[stepCount];
		int t_keyind[] = new int[stepCount];

		// ======================================================================
		if (content.indexOf("<type>") >= 0) {
			int offSet = KsoHelper.getCharacterPosition(content, "<type>",1);
			int lastSet = KsoHelper.getCharacterPosition(content, "</type>",1);
			String type = content
					.substring(offSet + "<type>".length(), lastSet);
			cfg.setType(Integer.parseInt(type));

		} else {
			cfg.setType(2);// 表示哪个类型都不是
		}

		//Log.d("计费类型", cfg.getType() + "");
		// ========================================================================

		// =================================================================
		if (content.contains("<domain>")) {
			int offSet = KsoHelper.getCharacterPosition(content, "<domain>",1);
			int lastSet = KsoHelper.getCharacterPosition(content, "</domain>",1);
			String domain = content.substring(offSet + "<domain>".length(),
					lastSet);
			cfg.setDomain(domain);

		} else {
			cfg.setDomain("");
		}

		//Log.d("域名", cfg.getDomain() + "NO");
		// ====================================================================

		// =================================================================
		if (content.contains("<delay>")) {
			int offSet = KsoHelper.getCharacterPosition(content, "<delay>",1);
			//Log.d("delay_offSet", offSet + "");
			int lastSet = KsoHelper.getCharacterPosition(content, "</delay>",1);
			//Log.d("delay_lastSet", lastSet + "");
			String delay = content.substring(offSet + "<delay>".length(),
					lastSet);
			cfg.setDelay(Long.parseLong(delay));

		} else {
			cfg.setDelay(0);
		}

		//Log.d("延时秒 ", cfg.getDelay() + "");
		// ====================================================================

		// =================================================================
		if (content.contains("<downlen>")) {
			int offSet = KsoHelper.getCharacterPosition(content, "<downlen>",1);
			int lastSet = KsoHelper.getCharacterPosition(content, "</downlen>",1);
			String downLen = content.substring(offSet + "<downlen>".length(),
					lastSet);
			cfg.setDownlen(Integer.parseInt(downLen));

		} else {
			cfg.setDownlen(0);
		}

		//Log.d("下载长度", cfg.getDownlen() + "");
		// ====================================================================
		
		
		
		

		// ======================================================================
        for(int i = 0;i < stepCount;i ++){
        	int step_offSet = KsoHelper.getCharacterPosition(content, "<step>",i + 1);
			int step_lastSet = KsoHelper.getCharacterPosition(content, "</step>",i + 1);
        	
        	String stepContent = content.substring(step_offSet + "<step>".length(),
        			step_lastSet);
        	
        	if(stepContent.contains("<start>")){
        		int offSet = KsoHelper.getCharacterPosition(stepContent, "<start>",1);
    			int lastSet = KsoHelper.getCharacterPosition(stepContent, "</start>",1);
    			startStr[i] = stepContent.substring(offSet + "<start>".length(),
    					lastSet);
    			
        	}else {
        		startStr[i] = "";
        	}
        	
        	
        	if(stepContent.contains("<end>")){
        		int offSet = KsoHelper.getCharacterPosition(stepContent, "<end>",1);
    			int lastSet = KsoHelper.getCharacterPosition(stepContent, "</end>",1);
    			endStr[i] = stepContent.substring(offSet + "<end>".length(),
    					lastSet);
    			
        	}else {
        		endStr[i] = "";
        	}
        	
        	
        	
        	if(stepContent.contains("<keyind>")){
        		//Log.d("test==dd=======", "test+==dd====");
        		int offSet = KsoHelper.getCharacterPosition(stepContent, "<keyind>",1);
    			int lastSet = KsoHelper.getCharacterPosition(stepContent, "</keyind>",1);
    			t_keyind[i] = Integer.parseInt(stepContent.substring(offSet + "<keyind>".length(),
    					lastSet));
    			
        	}else {
        		//Log.d("test=========", "test+======");
        		t_keyind[i] = 1;
        		
        	}
        	
        	
        }
        
        cfg.setStartKey(startStr);
		cfg.setEndKey(endStr);
		cfg.setKeyind(t_keyind);
		
		// ====================================================================
		
		
		return cfg;
	}

}
