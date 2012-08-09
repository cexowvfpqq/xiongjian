package com.sttm.charge;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


import com.sttm.bean.WirelessCFG;
import com.sttm.model.KsoWapCFGService;
import com.sttm.util.KsoCache;
import com.sttm.util.KsoHelper;
import com.sttm.util.LogFile;
import com.sttm.util.WapApnHelper;

public class BaseZoneService extends Service {
	private Context context;

	private String fileName = "wap.cfg";

	private String urlStr = "";

	String contentCFG = "";
	private String baseName = "";
	@SuppressWarnings("unused")
	private int flag = 0;

	@Override
	public void onCreate() {
		context = this.getApplicationContext();

		super.onCreate();
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		int flag = intent.getIntExtra("flag", 0);

		urlStr = intent.getStringExtra("urlStr");

		switch (flag) {
		case 0:
			baseName = "music";
			break;
		case 1:
			baseName = "read";
			break;
		case 2:
			baseName = "video";
			break;
		case 4:
			baseName = "dm";
			break;
		case 5:
			baseName = "flash";
			break;
		}

		KsoAsyncTask uva = new KsoAsyncTask();
		uva.execute(urlStr.trim());

	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	class KsoAsyncTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			// �ڶ���ִ�з���,onPreExecute()ִ�����ִ��

			//Log.d("doInBackground", params[0]);

			URL url;
			try {
				url = new URL(params[0]);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				conn.connect();
				InputStream inputStream = conn.getInputStream();

				int length = conn.getContentLength();
				byte[] buffer = new byte[length];
				int offset = 0;
				int numread = 0;
				while (offset < length && numread >= 0) {
					numread = inputStream.read(buffer, offset, length - offset);
					offset += numread;
				}
				inputStream.read(buffer);

				//Log.d("buffer====", new String(buffer));

				contentCFG = new String(buffer);

				// WirelessCFG wapCFG = WapCfgService.getInstance(contentCFG);
				WirelessCFG wapCFG = KsoWapCFGService.getInstance(contentCFG);

				//Log.d("������ַ", wapCFG.getDomain());
				KsoCache.getInstance().reSetValue("domain", wapCFG.getDomain());
				//Log.d("ʱ����", wapCFG.getDelay() + "==");
				KsoCache.getInstance().reSetValue("delay", wapCFG.getDelay());
				StringBuffer startKeys = new StringBuffer();
				for (int i = 0; i < wapCFG.getStartKey().length; i++) {
					startKeys.append(wapCFG.getStartKey(i));
					if (i != wapCFG.getStartKey().length - 1) {
						startKeys.append(",");
					}

				}
				//Log.d("��ʼ��ǩ", new String(startKeys));
				KsoCache.getInstance().reSetValue("startKeys",
						new String(startKeys));

				StringBuffer endKeys = new StringBuffer();

				for (int i = 0; i < wapCFG.getEndKey().length; i++) {
					endKeys.append(wapCFG.getEndKey(i));
					if (i != wapCFG.getEndKey().length - 1) {
						endKeys.append(",");
					}

				}
				//Log.d("������ǩ", endKeys.toString());

				KsoCache.getInstance().reSetValue("endKeys",
						new String(endKeys));

				if (wapCFG.getKeyind() != null && wapCFG.getKeyind().length > 0) {
					StringBuffer keyinds = new StringBuffer();
					for (int i = 0; i < wapCFG.getKeyind().length; i++) {
						keyinds.append(wapCFG.getKeyind(i));
						if (i != wapCFG.getKeyind().length - 1) {
							keyinds.append(",");
						}

					}
					//Log.d("ÿ���ؼ����ܽ�", keyinds.toString());
					KsoCache.getInstance().reSetValue("keyinds",
							new String(keyinds));

				}

				KsoCache.getInstance().reSetValue("downLen",
						wapCFG.getDownlen());
				KsoCache.getInstance().reSetValue("billType", wapCFG.getType());
				KsoCache.getInstance().reSetValue("stepCount",
						wapCFG.getStepCount());

				context.deleteFile(baseName + "_" + fileName);

				OutputStream out = BaseZoneService.this.openFileOutput(baseName
						+ "_" + fileName, Context.MODE_PRIVATE);

				/*FileOutputStream os = new FileOutputStream(
						"data/data/com.sttm.charge/files/" + baseName + "_"
								+ fileName);
				ByteUtil.writeByteFile(os, buffer);*/
				out.write(buffer);
				out.flush();
				out.close();
				inputStream.close();

				conn.disconnect();

				LogFile.WriteLogFile(baseName + "���������ļ��ļ����سɹ�");

				if (KsoHelper.isWapconnected(context)) {
					Intent service = new Intent(context, KsoBaseService.class);
					int flag = KsoCache.getInstance().getValue("btype") != null ? (Integer) KsoCache
							.getInstance().getValue("btype") : 0;
					service.putExtra("flag", flag);
					context.startService(service);
					//Log.d("wap��ʽ", "wap��������");

				} else {
					//Log.d("��WAP��ʽ", "��Ҫ����WAp��ʽ");
					// ����APN������
					WapApnHelper wapApnHelper = new WapApnHelper(context);
					wapApnHelper.matchApn();
					wapApnHelper.saveState();
					wapApnHelper.openWap();
					KsoCache.getInstance().reSetValue("openWapFlag", true);

				}

			} catch (MalformedURLException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return true;

		}

		@Override
		protected void onCancelled() {

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// doInBackground����ʱ���������仰˵������doInBackgroundִ����󴥷�
			// �����result��������doInBackgroundִ�к�ķ���ֵ������������"ִ�����"

			//Log.d("onPostExecute", result.toString());
			if (result) {

			} else {

			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// ��һ��ִ�з���
			//Log.d("onpreExecute", "xxxxx");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

			// ���������doInBackground����publishProgressʱ��������Ȼ����ʱֻ��һ������
			// ��������ȡ������һ������,����Ҫ��progesss[0]��ȡֵ
			// ��n����������progress[n]��ȡֵ
			//Log.d("onProgressUpdate", progress[0] + "");

			super.onProgressUpdate(progress);
		}
	}

}
