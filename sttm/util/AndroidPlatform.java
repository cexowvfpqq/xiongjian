package com.sttm.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.util.Log;






public class AndroidPlatform {

	public static final String KEYSTRING_USER_AGENT = "user_agent_key";

	public static String getUAFromProperties() {
		try {
			FileInputStream is = getPropertyStream();
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			for (int k = 0; -1 != (k = is.read(buf));)
				bytearrayoutputstream.write(buf, 0, k);

			String fileString = new String(bytearrayoutputstream.toByteArray(),
					"UTF-8");
			//Log.d("fileString=========", fileString);

			return getProperties(KEYSTRING_USER_AGENT, fileString);

			// System.out.println("IS FILE Android Platform  " +
			// bytearrayoutputstream.size() + "  "+());

		} catch (Exception e) {
			// TODO: handle exception

			System.out.println("IS FILE erororo");
			e.printStackTrace();
		}
		return null;
	}

	public static FileInputStream getPropertyStream() {
		try {

			File property = new java.io.File("/opl/etc/properties.xml");
			if (property.exists()) {
				return new FileInputStream(new java.io.File(
						"/opl/etc/properties.xml"));
			} else {
				property = new java.io.File("/opl/etc/product_properties.xml");
				if (property.exists()) {
					return new FileInputStream(new java.io.File(
							"/opl/etc/product_properties.xml"));
				} else {
					return null;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static String getProperties(String key, String content) {
		String STARTKEY = "<" + key + ">";
		String ENDKEY = "</" + key + ">";
		content = content.replace("\r", "");
		content = content.replace("\n", "");

		int startIndex = content.indexOf(STARTKEY) + STARTKEY.length();
		int endIndex = content.indexOf(ENDKEY);
		if (startIndex > -1 && endIndex > -1) {
			return content.substring(startIndex, endIndex);
		} else
			return null;
	}
	
	
	
	
	

}