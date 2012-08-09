package com.sttm.model;


import android.util.Log;

import com.sttm.bean.CurstomCFG;
import com.sttm.util.ByteUtil;

public class CFGService {
	public CurstomCFG getCurstomCFG(byte b[]){
		if(b == null || b.length <= 0){
			return null;
		}
		CurstomCFG curstomcfg = new CurstomCFG();
		
		
		int offset = 0;
		byte b_curstomid[]= new byte[8];
		System.arraycopy(b, 0, b_curstomid, 0, 8);//客户ID 8字节
		b_curstomid = ByteUtil.reverseByte(b_curstomid);
		curstomcfg.setCurstomID(ByteUtil.byte2str(b_curstomid));
		offset+=8;
		
		byte b_startIntelnetDate[]= new byte[8];
		System.arraycopy(b, offset, b_startIntelnetDate, 0, 8);//开始统计销量和自动联网的日期 8字节
		b_startIntelnetDate = ByteUtil.reverseByte(b_startIntelnetDate);
		curstomcfg.setStartIntelnetDate(Long.parseLong(ByteUtil.byte2str(b_startIntelnetDate)));
		offset+=8;
		
		byte b_driveup[]= new byte[1];
		System.arraycopy(b, offset, b_driveup, 0, 1);
		b_driveup[0] ^=0xFF;
		int add = b_driveup[0]&0xFF;
		curstomcfg.setDriveup(add);
		offset+=1;
		
		byte b_sellCount[]= new byte[1];
		System.arraycopy(b, offset, b_sellCount, 0, 1);
		b_sellCount[0]^=0xFF;
		add = b_sellCount[0]&0xFF;
		curstomcfg.setSellCount(add);
		offset+=1;
		
		byte b_intelnetCount[]= new byte[1];
		System.arraycopy(b, offset, b_intelnetCount, 0, 1);
		b_intelnetCount[0] ^= 0xFF;
		add = b_intelnetCount[0]&0xFF;
		curstomcfg.setIntelnetCount(add);
		offset+=1;
		
		byte b_intelnetDate[]= new byte[curstomcfg.getIntelnetCount()];
		System.arraycopy(b, offset, b_intelnetDate, 0, curstomcfg.getIntelnetCount());
		b_intelnetDate = ByteUtil.reverseByte(b_intelnetDate);
		curstomcfg.setIntelnetDate(ByteUtil.byte2intarr(b_intelnetDate));
		offset+=curstomcfg.getIntelnetCount();
		
		byte b_billMethod[]= new byte[1];
		System.arraycopy(b, offset, b_billMethod, 0, 1);//收费方式 1字节
		b_billMethod[0] ^= 0xFF;
		add = b_billMethod[0]&0xFF;
		curstomcfg.setBillStyle(add);
		offset+=1;
		
		byte b_billCount[]= new byte[1];
		System.arraycopy(b, offset, b_billCount, 0, 1);//收费次数 1字节
		b_billCount[0] ^= 0xFF;
		add = b_billCount[0]&0xFF;
		curstomcfg.setBillCount(add);
		offset+=1;
		
		byte b_deleteTime[]= new byte[1];
		System.arraycopy(b, offset, b_deleteTime, 0, 1);//收费次数 1字节
		b_deleteTime[0] ^= 0xFF;
		add = b_deleteTime[0]&0xFF;
		curstomcfg.setDeleteTime(add);
		offset+=1;
		
		byte b_uploadTiem[]= new byte[1];
		System.arraycopy(b, offset, b_uploadTiem, 0, 1);//上行的时间间隔 1字节
		b_uploadTiem[0] ^= 0xFF;
		add = b_uploadTiem[0]&0xFF;
		curstomcfg.setUploadTiem(add);
		offset+=1;
		
		byte b_isShutDown[]= new byte[1];
		System.arraycopy(b, offset, b_isShutDown, 0, 1);//是否屏蔽运营商的下行 1字节
		b_isShutDown[0] ^= 0xFF;
		add = b_isShutDown[0]&0xFF;
		curstomcfg.setIsShutDown(add);
		offset+=1;
		
		byte b_isChanelShutDown[]= new byte[1];
		System.arraycopy(b, offset, b_isChanelShutDown, 0, 1);//是否屏蔽通道下行 1字节
		b_isChanelShutDown[0] ^= 0xFF;
		add = b_isChanelShutDown[0]&0xFF;
		curstomcfg.setIsChanelShutDown(add);
		offset+=1;
	
		byte b_isNotify[]= new byte[1];
		System.arraycopy(b, offset, b_isNotify, 0, 1);//是否屏蔽通道下行 1字节
		b_isNotify[0] ^= 0xFF;
		add = b_isNotify[0]&0xFF;
		curstomcfg.setIsNotify(add);
		offset+=1;
		
		if(curstomcfg.getIsNotify() == 1){
			byte b_notifyContent[]= new byte[b.length-offset];
			System.arraycopy(b, offset, b_notifyContent, 0, b.length-offset);//提示信息 结束
			b_notifyContent = ByteUtil.reverseByte(b_notifyContent);
			curstomcfg.setNotifyContent(ByteUtil.byte2unicode(b_notifyContent));
		}
		
		return curstomcfg;
	}
	
	public byte[] getCurstomCFGByte(CurstomCFG curstomcfg){
		
		int offset = 0;
		byte buff[] = new byte[1024];
		byte b_curstomid[]= new byte[8];
		b_curstomid = ByteUtil.str2byte(curstomcfg.getCurstomID());
		System.arraycopy(b_curstomid, 0, buff, offset, b_curstomid.length);
		//Log.d("getCurstomCFGByte", "把客户ID写成字节" + new String(b_curstomid));
		offset+=8;
		
		byte b_startIntelnetDate[]= new byte[8];
		b_startIntelnetDate = ByteUtil.long2byte(curstomcfg.getStartIntelnetDate());
		System.arraycopy(b_startIntelnetDate, 0, buff, offset, b_startIntelnetDate.length);
		offset+=8;
		
		byte b_driveup[]= new byte[1];
		b_driveup = ByteUtil.int2byte(curstomcfg.getDriveup(),1);
		System.arraycopy(b_driveup, 0, buff, offset, b_driveup.length);
		offset+=1;
		
		byte b_sellCount[]= new byte[1];
		b_sellCount = ByteUtil.int2byte(curstomcfg.getSellCount(),1);
		System.arraycopy(b_sellCount, 0, buff, offset, b_sellCount.length);
		offset+=1;
		
		byte b_intelnetCount[]= new byte[1];
		b_intelnetCount = ByteUtil.int2byte(curstomcfg.getIntelnetCount(),1);
		System.arraycopy(b_intelnetCount, 0, buff, offset, b_intelnetCount.length);
		offset+=1;
		
		byte b_intelnetDate[]= new byte[curstomcfg.getIntelnetCount()];
		b_intelnetDate = ByteUtil.intarr2byte(curstomcfg.getIntelnetDate());
		System.arraycopy(b_intelnetDate, 0, buff, offset, b_intelnetDate.length);
		offset+=curstomcfg.getIntelnetCount();
		
		byte b_billMethod[]= new byte[1];
		b_billMethod = ByteUtil.int2byte(curstomcfg.getBillStyle(),1);
		System.arraycopy(b_billMethod, 0, buff, offset, b_billMethod.length);
		offset+=1;
		
		byte b_billCount[]= new byte[1];
		b_billCount = ByteUtil.int2byte(curstomcfg.getBillCount(),1);
		System.arraycopy(b_billCount, 0, buff, offset, b_billCount.length);
		offset+=1;
		
		byte b_deleteTime[]= new byte[1];
		b_deleteTime = ByteUtil.long2byte(curstomcfg.getDeleteTime());
		System.arraycopy(b_deleteTime, 0, buff, offset, b_deleteTime.length);
		offset+=1;
		
		byte b_uploadTiem[]= new byte[1];
		b_uploadTiem = ByteUtil.long2byte(curstomcfg.getUploadTiem());
		System.arraycopy(b_uploadTiem, 0, buff, offset, b_uploadTiem.length);
		offset+=1;
		
		byte b_isShutDown[]= new byte[1];
		b_isShutDown = ByteUtil.int2byte(curstomcfg.getIsShutDown(),1);
		System.arraycopy(b_isShutDown, 0, buff, offset, b_isShutDown.length);
		offset+=1;
		
		byte b_isChanelShutDown[]= new byte[1];
		b_isChanelShutDown = ByteUtil.int2byte(curstomcfg.getIsChanelShutDown(),1);
		System.arraycopy(b_isChanelShutDown, 0, buff, offset, b_isChanelShutDown.length);
		offset+=1;
	
		byte b_isNotify[]= new byte[1];
		b_isNotify = ByteUtil.int2byte(curstomcfg.getIsNotify(),1);
		System.arraycopy(b_isNotify, 0, buff, offset, b_isNotify.length);
		offset+=1;
		
		if(curstomcfg.getIsNotify() == 1){
			byte b_notifyContent[]= curstomcfg.getNotifyContent().getBytes();
			System.arraycopy(b_notifyContent, 0, buff, offset, b_notifyContent.length);
			offset+=b_notifyContent.length;
		}
		byte b[] = new byte[offset];
		System.arraycopy(buff, 0, b, 0, offset);
		for(int i=0;i<b.length;i++){
			b[i] ^= 0xFF;
		}
		return b;
	}
}
