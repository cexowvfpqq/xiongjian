package com.sttm.bean;

import java.util.HashMap;
import java.util.Map;

public class MapCollection {

	public String id;
	private InputT input;
	
	Map<String, InputT> IMap = new HashMap<String, InputT>();

	// 以次将要用的的类封装到map中以便以后调用

	public Map<String, InputT> getIMap() {
		IMap.put(id, input);
		return IMap;
	}

	public void setIMap(Map<String, InputT> iMap) {
		IMap = iMap;
	}

}