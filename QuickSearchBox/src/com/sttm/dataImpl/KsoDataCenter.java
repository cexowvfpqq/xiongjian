package com.sttm.dataImpl;

import android.net.Uri;
import android.provider.BaseColumns;

public final class KsoDataCenter {
	public static final String AUTHORITY = "com.kso.provider.KsoDataCenter";
	private KsoDataCenter() {};
	
	public static final class DataCenter implements BaseColumns {
		
		private DataCenter(){};
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				 + "/dataCenter");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.kso.ksoDataCenter";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.kso.ksoDataCenter";
		
		public static final String DEFAULT_SORT_ORDER = "ksokey DESC";
		
		public static final String KSOKEY = "ksokey";
		
		public static final String KSOVALUE = "ksovalue";
		
		
		
	}
	

}
