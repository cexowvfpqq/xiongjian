package com.sttm.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

/**
 * ��������WML�ĵ��е����г�����
 * 
 * 
 */
@SuppressWarnings("unused")
public class HyperLinkTrace {
	/*
	 * public static void main(String[] args) throws Exception { //��ʼ��HTMLParser
	 * Parser parser = new Parser(); parser.setEncoding("8859_1"); String //
	 * parser.setInputHTML(getWmlContent());
	 * 
	 * //ע���µĽ������� PrototypicalNodeFactory factory = new PrototypicalNodeFactory
	 * (); factory.registerTag(new WmlGoTag ()); parser.setNodeFactory(factory);
	 * //�����������������нڵ� NodeList nlist =
	 * parser.extractAllNodesThatMatch(lnkFilter); for(int
	 * i=0;i<nlist.size();i++){ CompositeTag node =
	 * (CompositeTag)nlist.elementAt(i); if(node instanceof LinkTag){ LinkTag
	 * link = (LinkTag)node; System.out.println("LINK: /t" + link.getLink()); }
	 * else if(node instanceof WmlGoTag){ WmlGoTag go = (WmlGoTag)node;
	 * System.out.println("GO: /t" + go.getLink()); } } }
	 */
	/**
	 * ��ȡ���Ե�WML�ű�����
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getWmlContent(String urlStr) throws Exception {
		// URL url =
		// ParserTester.class.getResource("/demo/htmlparser/test.wml");
		URL url = new URL(urlStr);
		File f = new File(url.toURI());
		BufferedReader in = new BufferedReader(new FileReader(f));
		StringBuffer wml = new StringBuffer();
		do {
			String line = in.readLine();
			if (line == null)
				break;
			if (wml.length() > 0)
				wml.append("/r/n");
			wml.append(line);
		} while (true);
		return wml.toString();
	}

	/**
	 * ���������е����ӣ�������Ϊ<a>��<go>
	 */
	@SuppressWarnings("serial")
	public static NodeFilter lnkFilter = new NodeFilter() {
		public boolean accept(Node node) {
			if (node instanceof WmlGoTag)
				return true;
			if (node instanceof LinkTag)
				return true;
			return false;
		}
	};

	/**
	 * WML�ĵ���GO��ǩ������
	 * 
	 * @author ligm
	 */
	@SuppressWarnings("serial")
	public static class WmlGoTag extends CompositeTag {
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
}