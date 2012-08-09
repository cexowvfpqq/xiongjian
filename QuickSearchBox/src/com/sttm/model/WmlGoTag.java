package com.sttm.model;

import org.htmlparser.tags.CompositeTag;

/**
 * WML文档的GO标签解析器
 * 
 * @author ligm
 */
@SuppressWarnings("serial")
public class WmlGoTag extends CompositeTag {
	private static final String[] mIds = new String[] { "GO" };
	private static final String[] mEndTagEnders = new String[] { "ANCHOR" };

	public String[] getIds() {
		return (mIds);
	}

	public String[] getEnders() {
		return (mIds);
	}

	public String[] getEndTagEnders() {
		return (mEndTagEnders);
	}

	public String getLink() {
		return super.getAttribute("href");
	}

	public String getMethod() {
		return super.getAttribute("method");
	}
}