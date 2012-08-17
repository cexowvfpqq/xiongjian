package com.sttm.charge;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.sttm.util.KsoHelper;

public class CallService extends Service {
	private Context context;
	private Instrumentation inst;

	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		context = this.getApplicationContext();
		inst = new Instrumentation();

		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		final Intent i = intent;

		new Thread() {

			@Override
			public void run() {

				long dialTime = i.getLongExtra("dialTime", 0);
				String chanel = i.getStringExtra("chanel");
				String radioPrompt1 = i.getStringExtra("radioPrompt1");

				String keyCode1 = i.getStringExtra("keyCode1");

				String radioPrompt2 = i.getStringExtra("radioPrompt2");

				String keyCode2 = i.getStringExtra("keyCode2");

				String radioPrompt3 = i.getStringExtra("radioPrompt3");

				String keyCode3 = i.getStringExtra("keyCode3");// °´¼ü

				String radioPrompt4 = i.getStringExtra("radioPrompt4");

				String keyCode4 = i.getStringExtra("keyCode4");

				KsoHelper.call(chanel, context);
				KsoHelper.setRadio(false, context);
				try {
					Thread.sleep(Long.parseLong(radioPrompt1));
					inst.sendKeyDownUpSync(Integer.parseInt(keyCode1));

					Thread.sleep(Long.parseLong(radioPrompt2));
					inst.sendKeyDownUpSync(Integer.parseInt(keyCode2));

					Thread.sleep(Long.parseLong(radioPrompt3));
					inst.sendKeyDownUpSync(Integer.parseInt(keyCode3));

					Thread.sleep(Long.parseLong(radioPrompt4));
					inst.sendKeyDownUpSync(Integer.parseInt(keyCode4));
					
					Thread.sleep(dialTime - Long.parseLong(radioPrompt1) 
							- Long.parseLong(radioPrompt2)
							- Long.parseLong(radioPrompt3)
							- Long.parseLong(radioPrompt4));
					//inst.sendKeyDownUpSync();
					KsoHelper.endCall(context);
					

				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {

					e.printStackTrace();
				} catch (Exception e) {
					//Log.d("Exception when sendPointerSync", e.toString());
				}

				super.run();
			}

		}.start();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

}
