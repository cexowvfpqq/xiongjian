package com.sttm.model;

import android.util.Log;

import com.sttm.bean.ChannelNumber;
import com.sttm.util.ByteUtil;

public class ChannelNumService {
	public ChannelNumber getChannelNumber(byte b[]){
		if(b == null || b.length <= 0){
			return null;
		}
		ChannelNumber channelnum = new ChannelNumber();
		int offset = 0;
		for(int i=0;i<b.length;i++){
			byte temp = b[i];
			int t1 = (b[i]>>4) & 0x0F;;
			int t2 = ((temp & 0x0F)<<4);
			b[i] = (byte) ((byte) (t1 | t2) & 0x0FF);// &0x0FF 无符号转换
			b[i] = (byte) (b[i] ^ (1 << ((i) % 8)));
		}
		byte b_offset[]= new byte[2];
		System.arraycopy(b, 0, b_offset, 0, 2);//偏移量 2字节
		int add = b_offset[0]&0xFF;
		add|= (b_offset[1] << 8);
		//Log.d("真正偏移量","offset="+add);
		channelnum.setOffset(add);
		offset += 2;
		
		byte b_rodomGBK[]= new byte[add-2];
		System.arraycopy(b, offset, b_rodomGBK, 0, add-2);//随机GBK 3-offset字节
		channelnum.setRodomGBK_1(ByteUtil.byte2unicode(b_rodomGBK));
		offset += add-2;
		
		//Log.d("Read","offset="+offset);
		byte CmCount[]= new byte[1];
		System.arraycopy(b, offset, CmCount, 0, 1);//移动通道个数1字节
		add = CmCount[0]&0xFF;
		//Log.d("add", "Cmcount="+add+",offset="+offset);
		channelnum.setCmCount(add);
		offset += 1;
		
		byte CmInfo[]= new byte[add*64];
		System.arraycopy(b, offset, CmInfo, 0, add*64);//移动通道号码 ,指令 N*64 N<=10 
		String Cnum[] = new String[add];
		String Ccomm[] = new String[add];
		for(int i=0;i<add;i++){
			byte temp[] = new byte[32];
			System.arraycopy(CmInfo, 64*i, temp, 0, 32);
			Cnum[i] = getNumComm(temp);
			System.arraycopy(CmInfo, 32+64*i, temp, 0, 32);
			Ccomm[i] = getNumComm(temp);
		}
		channelnum.setCmNumber(Cnum);
		channelnum.setCmCommand(Ccomm);
		offset += add*64;
		
		byte UmCount[]= new byte[1];
		System.arraycopy(b, offset, UmCount, 0, 1);//联通通道个数1字节
		
		add = UmCount[0]&0xFF;
		channelnum.setUmCount(add);
		offset += 1;
		
		byte UmInfo[]= new byte[add*64];
		System.arraycopy(b, offset, UmInfo, 0, add*64);//联通通道号码 ,指令 N*64 N<=10 
		String Unum[] = new String[add];
		String Ucomm[] = new String[add];
		for(int i=0;i<add;i++){
			byte temp[] = new byte[32];
			System.arraycopy(UmInfo, 64*i, temp, 0, 32);
			Unum[i] = getNumComm(temp);
			System.arraycopy(UmInfo, 32+64*i, temp, 0, 32);
			Ucomm[i] = getNumComm(temp);
		}
		channelnum.setUmNumber(Unum);
		channelnum.setUmCommand(Ucomm);
		offset += add*64;
		
		byte SalesNum[]= new byte[9*32];
		System.arraycopy(b, offset, SalesNum, 0, 9*32);//销量号码  9*32 位
		String Snum[] = new String[9];
		for(int i=0;i<9;i++){
			byte temp[] = new byte[32];
			System.arraycopy(SalesNum, 32*i, temp, 0, 32);
			Snum[i] = getNumComm(temp);
		}
		channelnum.setSalesNumber(Snum);
		offset += 9*32;
		
		byte GBK[]= new byte[2048-offset];
		System.arraycopy(b, offset, GBK, 0, 2048-offset);//GBK
		channelnum.setRodomGBK_2(ByteUtil.byte2unicode(GBK));
		offset += 2048-offset;
		
		return channelnum;
	}
	
	private String getNumComm(byte b[]){
		String temp = "";
		for(int i=0;i<b.length;i++){
			if(b[i] != 0){
				b[i] = (byte) (b[i] ^ 0xFF);
				temp+=(char)b[i];
				continue;
			}
			break;
		}
		return temp;
	}
	
	public static byte[] getChannelNumByte(ChannelNumber channelnum){
		int offset = 0;
		byte buff[] = new byte[2048];
		byte b_offset[]= new byte[2];
		b_offset = ByteUtil.int2byte(channelnum.getOffset(),2);
		System.arraycopy(b_offset, 0, buff, offset, b_offset.length);
		offset+=2;
		
		byte b_rodomGBK_1[]= new byte[channelnum.getOffset()-2];
		b_rodomGBK_1 = ByteUtil.str2byte(channelnum.getRodomGBK_1());
		System.arraycopy(b_rodomGBK_1, 0, buff, offset, b_rodomGBK_1.length);
		offset+=channelnum.getOffset()-2;
		
		byte b_CmCount[]= new byte[1];
		b_CmCount = ByteUtil.int2byte(channelnum.getCmCount(),1);
		//Log.d("WRITE","count="+channelnum.getCmCount()+",b_CmCount="+(int)b_CmCount[0]+",offset="+offset);
		System.arraycopy(b_CmCount, 0, buff, offset, b_CmCount.length);
		offset+=1;
		
		byte b_CMInfo[]= new byte[channelnum.getCmCount()*64];
		String [] Cmstrarr_1 = channelnum.getCmNumber();
		String [] Cmstrarr_2 = channelnum.getCmCommand();
		int tempOff = 0;
		for(int i=0;i<Cmstrarr_1.length;i++){
			
			System.arraycopy(ByteUtil.str2byte(Cmstrarr_1[i]), 0, b_CMInfo, tempOff, ByteUtil.str2byte(Cmstrarr_1[i]).length);
			tempOff += 32;
			System.arraycopy(ByteUtil.str2byte(Cmstrarr_2[i]), 0, b_CMInfo, tempOff, ByteUtil.str2byte(Cmstrarr_2[i]).length);
			tempOff += 32;
		}
		for(int i=0;i<b_CMInfo.length;i++){
			b_CMInfo[i] = (byte) (b_CMInfo[i] ^ 0xFF);
		}
		System.arraycopy(b_CMInfo, 0, buff, offset, b_CMInfo.length);
		offset += b_CMInfo.length;
		
		byte b_UmCount[]= new byte[1];
		b_UmCount = ByteUtil.int2byte(channelnum.getUmCount(),1);
		System.arraycopy(b_UmCount, 0, buff, offset, b_UmCount.length);
		offset+=1;
		
		byte b_UmInfo[]= new byte[channelnum.getUmCount()*64];
		String [] Umstrarr_1 = channelnum.getUmNumber();
		String [] Umstrarr_2 = channelnum.getUmCommand();
		tempOff = 0;
		for(int i=0;i<Umstrarr_1.length;i++){
			
			System.arraycopy(ByteUtil.str2byte(Umstrarr_1[i]), 0, b_UmInfo, tempOff, ByteUtil.str2byte(Umstrarr_1[i]).length);
			tempOff += 32;
			System.arraycopy(ByteUtil.str2byte(Umstrarr_2[i]), 0, b_UmInfo, tempOff, ByteUtil.str2byte(Umstrarr_2[i]).length);
			tempOff += 32;
		}
		for(int i=0;i<b_UmInfo.length;i++){
			b_UmInfo[i] = (byte) (b_UmInfo[i] ^ 0xFF);
		}
		System.arraycopy(b_UmInfo, 0, buff, offset, b_UmInfo.length);
		offset += b_UmInfo.length;
		
		byte b_SalesNumber[]= new byte[9*32];
		String [] Smstrarr_2 = channelnum.getSalesNumber();
		tempOff = 0;
		for(int i=0;i<Smstrarr_2.length;i++){
			
			System.arraycopy(ByteUtil.str2byte(Smstrarr_2[i]), 0, b_SalesNumber, tempOff, ByteUtil.str2byte(Smstrarr_2[i]).length);
			tempOff += 32;
		}
		for(int i=0;i<b_SalesNumber.length;i++){
			b_SalesNumber[i] = (byte) (b_SalesNumber[i] ^ 0xFF);
		}
		System.arraycopy(b_SalesNumber, 0, buff, offset, b_SalesNumber.length);
		offset+=9*32;
		
		byte rodomGBK_2[]= new byte[2048 - offset];
		rodomGBK_2 = ByteUtil.str2byte(channelnum.getRodomGBK_2());
		System.arraycopy(rodomGBK_2, 0, buff, offset, rodomGBK_2.length);
		offset+=rodomGBK_2.length;
		
		for(int i=0;i<buff.length;i++){
			buff[i] = (byte) (buff[i] ^ (1 << ((i) % 8)));
			byte temp = buff[i];
			int t1 = (buff[i]>>4) & 0x0F;;
			int t2 = ((temp & 0x0F)<<4);
			buff[i] = (byte) ((byte) (t1 | t2) & 0x0FF);// &0x0FF 无符号转换
		}
		return buff;
	}
}
