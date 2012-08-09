package com.sttm.model;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import android.content.Context;

import android.util.Log;

import com.sttm.util.HttpUtil;
import com.sttm.util.LogFile;
import com.sttm.util.WapNetUtil;

public class KsoProcessTemplate {
	private String baseName = "";
	private String content = "";
	private String stepUrl = "";
	private String[] startKeys, endKeys;
	private int[] keyinds;
	@SuppressWarnings("unused")
	private String lastName = "";

	private String startKey;
	private String endKey;
	private String keyind;
	private String doMain;
	private int btype;
	private int type;
	private int stepCount;
	private long delay;
	private int downLen;
	private String accessUrl;
	private Context context;

	public KsoProcessTemplate(String startKey, String endKey, String keyind,
			String doMain, int btype, int type, int stepCount, long delay,
			int downLen, String accessUrl, Context context) {
		this.startKey = startKey;
		this.endKey = endKey;
		this.keyind = keyind;
		this.doMain = doMain;
		this.btype = btype;
		this.type = type;
		this.stepCount = stepCount;
		this.delay = delay;
		this.downLen = downLen;
		this.accessUrl = accessUrl;
		this.context = context;

	}

	public void program() {
		//Log.d("test==", "���ؿ۷����̳���ʼ��");
		startKeys = new String[stepCount];
		endKeys = new String[stepCount];

		if (startKey != null && !"".equals(startKey)) {
			startKeys = startKey.split(",");
		}
		if (endKey != null && !"".equals(endKey)) {
			endKeys = endKey.split(",");
		}
		if (keyind != null && !"".equals(keyind)) {

			String[] keyindsStr = keyind.split(",");
			//Log.d("�ڼ����ؼ��ֳ���", keyindsStr.length + "dd");
			keyinds = new int[keyindsStr.length];
			if (keyindsStr.length > 0) {

				for (int i = 0; i < keyindsStr.length; i++) {

					keyinds[i] = Integer.parseInt(keyindsStr[i]);
					if (keyinds[i] == 0) {
						keyinds[i] = 1;
						//Log.d("�ؼ���", keyinds[i] + "dd");
					}
				}
			}

		}

		switch (btype) {
		case 0:
			baseName = "music";
			lastName = "mp3";
			break;
		case 1:
			baseName = "read";
			lastName = "txt";
			break;
		case 2:
			baseName = "video";
			lastName = "mp4";
			break;
		case 4:
			baseName = "dm";
			lastName = "gif";
			break;
		case 5:
			baseName = "flash";
			lastName = "flv";
			break;
		}

		new Thread() {

			@Override
			public void run() {
				LogFile.WriteLogFile(baseName + "���ؼƷ����̿�ʼ����");
				//Log.d("���URL", accessUrl);
			    LogFile.WriteLogFile("���URL:" + accessUrl);
				//Log.d("basename", baseName);
				content = HttpUtil.getContentApacheGet(accessUrl, context,
						baseName + "_access.wml");
				//content = HttpUtil.getWapContent(accessUrl);
				//Log.d("���content", content.trim());
				LogFile.WriteLogFile("���content" + content.trim());
				
				if ("".equals(content)) {
					//Log.d("���content", "������");
					return;
				}
			//	boolean isCookieInHeader = HttpUtil.getCookieInHeader(accessUrl);

				if (!content.contains(startKeys[0]) && content.contains("<go") && !startKeys[0].contains("<go")) {
					LogFile.WriteLogFile("�������Ϊ�ƶ���ʾҳ�棬��Ҫ�ض����ȡ���������ҳ��");
				//if ( isCookieInHeader && !content.contains(startKeys[0]) && content.contains("<go")) {
					Parser parser = new Parser();
					try {
						parser.setEncoding("GBK");
						parser.setInputHTML(content.trim());

						// ע���µĽ�������
						PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
						factory.registerTag(new WmlGoTag());
						parser.setNodeFactory(factory);

						// �����������������нڵ�
						NodeList nlist = parser
								.extractAllNodesThatMatch(lnkFilter);
						for (int i = 0; i < nlist.size(); i++) {
							CompositeTag node = (CompositeTag) nlist
									.elementAt(i);
							if (node instanceof WmlGoTag) {
								WmlGoTag go = (WmlGoTag) node;
								//Log.d("GO: /t", go.getLink());
								stepUrl = go.getLink().replaceAll("&amp;", "&");
								break;
							}
						}
						
						//Log.d("��������ڵ�ַ", stepUrl);
						LogFile.WriteLogFile("��������ڵ�ַ" + stepUrl);
						content = HttpUtil.getContentApacheGet(stepUrl,
								context, baseName + "_access_readly.wml");
						//Log.d("�������������", content.trim());
						LogFile.WriteLogFile("�������������" + content.trim());

					} catch (ParserException e) {

						e.printStackTrace();
					}

				}

				//Log.d("�����ܲ���", stepCount + "");
				//Log.d("���ʱ��", delay + "");

				for (int i = 0; i < stepCount; i++) {
					//Log.d("keyinds[i]dddddddd",keyinds[i] + "dkd");
					if (i == (stepCount - 1)) {
						//Log.d("���һ��", "test<><><><>");
						LogFile.WriteLogFile("�������һ��");
						if (type == 0) {
							//Log.d("test<><><><>type == 0", "test<><><><>");
							if (downLen == 0) {
								//Log.d("test<><><><>downLen == 0","test<><><><>");
								try {
									Thread.sleep(delay * 1000);
								} catch (Exception e) {

								}

								if (startKeys[i].contains("action")) {
									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("ACTION·��", "�Ҳ���·��");
										return;
									}
									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("��" + (i + 1) + "��action_URL",stepUrl);
									LogFile.WriteLogFile("��" + (i + 1) + "��action_URL" +
											stepUrl);
									HashMap<String, String> params = getInputs(getFormContent(
											content, keyinds[i] - 1));
									if (params != null) {
										//Log.d("�����б�FORM", params.isEmpty() + "");
										content = HttpUtil.postRequest(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params);
										//Log.d("��" + (i + 1) + "������ʽconten",content.trim());
										LogFile.WriteLogFile("��" + (i + 1) + "������ʽconten" +
												content.trim());
									}

								} else {
									//Log.d("��" + (i + 1) + "���ؼ���44444", keyinds[i]+ "");

									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("��" + (i + 1) + "��", "��������ֵ");
										return;
									}

									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("��" + (i + 1) + "��URL", stepUrl);
									LogFile.WriteLogFile("��" + (i + 1) + "��URL" + stepUrl);
									content = HttpUtil.getContentApacheGet(
											stepUrl, context, baseName
													+ (i + 1) + ".wml");
									//Log.d("��" + (i + 1) + "��content",content.trim());
									LogFile.WriteLogFile("��" + (i + 1) + "��content" +
											content.trim());

								}

							} else {

								try {
									Thread.sleep(delay * 1000);
								} catch (Exception e) {

								}

								if (startKeys[i].contains("action")) {
									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("ACTION·��", "�Ҳ���·��");
										return;
									}
									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("��" + (i + 1) + "��action_URL",stepUrl);
									LogFile.WriteLogFile("��" + (i + 1) + "��action_URL" +
											stepUrl);
									HashMap<String, String> params = getInputs(getFormContent(
											content, keyinds[i] - 1));
									if (params != null) {
										//Log.d("�����б�FORM", params.isEmpty() + "");
										/*content = HttpUtil.postRequest(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params);
*/										
										
										content = HttpUtil.postRequest1(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params,downLen);
										//Log.d("��" + (i + 1) + "������ʽconten",content.trim());
										LogFile.WriteLogFile("��" + (i + 1) + "������ʽconten" +
												content.trim());
									}

								} else {
									//Log.d("��" + (i + 1) + "���ؼ��֡�3����3����3��", keyinds[i]+ "");

									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("��" + (i + 1) + "��", "��������ֵ");
										return;
									}

									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("��" + (i + 1) + "��URL", stepUrl);
									LogFile.WriteLogFile("��" + (i + 1) + "��URL" + stepUrl);
								/*	content = HttpUtil.getContentApacheGet(
											stepUrl, context, baseName
													+ (i + 1) + ".wml");*/
									
									content = HttpUtil.getContentApacheGet1(
											stepUrl, context, baseName
													+ (i + 1) + ".wml",downLen);
									//Log.d("��" + (i + 1) + "��content",content.trim());
									LogFile.WriteLogFile("��" + (i + 1) + "��content" + content.trim());
								}

							}

						} else if (type == 1) {

						}

					} else {

						//Log.d("�м䲽��", "���ǵ�" + (i + 1) + "��");
						LogFile.WriteLogFile("���ǵ�" + (i + 1) + "��");

						try {
							Thread.sleep(delay * 1000);
						} catch (Exception e) {

						}

						if (btype == 1) {
							String imageUrl = doMain + getImageSrc(content);
							//Log.d("ͼƬ����·��", imageUrl);
							LogFile.WriteLogFile("�����Ķ����ؼƷ����̣���Ҫ����ͼƬ��ͼƬ����·����" + imageUrl);
							WapNetUtil.getImage2(imageUrl, context);

							try {
								Thread.sleep(delay * 1000);
							} catch (Exception e) {

							}
						}

						if (startKeys[i].contains("action")) {

							stepUrl = getStepUrl(doMain, keyinds, startKeys,
									endKeys, content.trim(), i);

							if (stepUrl == null || "".equals(stepUrl)) {
								//Log.d("ACTION·��", "�Ҳ���·��");
								return;
							}
							stepUrl = stepUrl.replaceAll("&amp;", "&");
							//Log.d("��" + (i + 1) + "��action_URL", stepUrl);
							LogFile.WriteLogFile("��" + (i + 1) + "��action_URL" + stepUrl);
							HashMap<String, String> params = getInputs(getFormContent(
									content, keyinds[i] - 1));
							if (params != null) {
								//Log.d("�����б�FORM", params.isEmpty() + "");
								content = HttpUtil.postRequest(context,
										baseName + "_" + (i + 1) + ".wml",
										stepUrl, params);
								//Log.d("��" + (i + 1) + "������ʽcontent",content.trim());
								LogFile.WriteLogFile("��" + (i + 1) + "������ʽcontent"+
										content.trim());
							}

						} else {
							//Log.d("��" + (i + 1) + "���ؼ���6666666", keyinds[i] + "");

							stepUrl = getStepUrl(doMain, keyinds, startKeys,
									endKeys, content.trim(), i);

							if (stepUrl == null || "".equals(stepUrl)) {
								//Log.d("��" + (i + 1) + "��", "��������ֵ");
								return;
							}

							stepUrl = stepUrl.replaceAll("&amp;", "&");
							//Log.d("��" + (i + 1) + "��URL", stepUrl);
							LogFile.WriteLogFile("��" + (i + 1) + "��URL" + stepUrl);
							content = HttpUtil.getContentApacheGet(stepUrl,
									context, baseName + (i + 1) + ".wml");
							//Log.d("��" + (i + 1) + "��content", content.trim());
							LogFile.WriteLogFile("��" + (i + 1) + "��content" + content.trim());

						}

					}

				}

				super.run();
			}

		}.start();

	}

	/**
	 * 
	 * @param string����
	 * @param params�ָ��
	 * @param keying�ڼ���
	 * @return�ָ��ַ�������λ��
	 */

	public int getCharacterPosition(String string, String params, int keyind) {
		// �����ǻ�ȡ"/"���ŵ�λ��Pattern.CASE_INSENSITIVE)����ʱ���Բ����ִ�Сд�ز���

		//Log.d("����keyind", keyind + "");
		Matcher slashMatcher = Pattern
				.compile(params, Pattern.CASE_INSENSITIVE).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// ��"/"���ŵ����γ��ֵ�λ��
			if (mIdx == keyind) {
				break;
			}
		}

		return slashMatcher.start();
	}

	public int getCharacterPosition2(String string, String params, int keyind) {
		// �����ǻ�ȡ"/"���ŵ�λ��Pattern.CASE_INSENSITIVE)����ʱ���Բ����ִ�Сд�ز���
		Matcher slashMatcher = Pattern
				.compile(params, Pattern.CASE_INSENSITIVE).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// ��"/"���ŵ����γ��ֵ�λ��
			if (mIdx == keyind) {
				return slashMatcher.start();
			}
		}
		return -1;

	}

	public String getImageSrc(String inputHtml) {
		Parser myParser;
		try {
			myParser = new Parser(inputHtml);
			// ���ñ���
			myParser.setEncoding("GBK");
			String filterStr = "img";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			ImageTag imageTag = (ImageTag) nodeList.elementAt(0);
			if (imageTag != null) {
				if (imageTag.getAttribute("src") != null) {
					//Log.d("ͼƬSRC", imageTag.getAttribute("src"));
					return imageTag.getAttribute("src");
				}

			}

		} catch (ParserException e) {
			return "";

		}
		return "";

	}

	public String getLink_A(String resource, int index) {
		Parser myParser;
		try {
			myParser = new Parser(resource);
			// ���ñ���
			myParser.setEncoding("GBK");
			String filterStr = "a";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			LinkTag tabletag = (LinkTag) nodeList.elementAt(index);
			if (tabletag != null) {
				//Log.d("href·��", tabletag.getAttribute("href"));

				return tabletag.getAttribute("href");

			}

		} catch (ParserException e) {

			e.printStackTrace();
		}
		return "";

		// System.out.println("==============");

	}

	public String getLink_text(String resource, int index) {
		Parser myParser;
		try {
			myParser = new Parser(resource);
			// ���ñ���
			myParser.setEncoding("GBK");
			String filterStr = "a";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			LinkTag aTag = (LinkTag) nodeList.elementAt(index);
			if (aTag != null) {
				//Log.d("<a>�ı�", aTag.toHtml());

				return aTag.toHtml();

			}

		} catch (ParserException e) {

			e.printStackTrace();
		}
		return "";

		// System.out.println("==============");

	}

	public String getFormContent(String resource, int index) {
		Parser myParser;
		try {
			myParser = new Parser(resource);
			// ���ñ���
			myParser.setEncoding("GBK");
			String filterStr = "form";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			FormTag tabletag = (FormTag) nodeList.elementAt(index);

			//Log.d("FOrm������", tabletag.toHtml());

			return tabletag.toHtml();

		} catch (ParserException e) {

			return "";
		}

	}

	public HashMap<String, String> getInputs(String formContent) {
		if ("".equals(formContent) || formContent == null) {
			return null;
		}
		HashMap<String, String> inputs = new HashMap<String, String>();
		Parser myParser;
		try {
			myParser = new Parser(formContent);
			// ���ñ���
			myParser.setEncoding("GBK");
			String filterStr = "input";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			InputTag tabletag = null;

			for (int i = 0; i < nodeList.size(); i++) {
				tabletag = (InputTag) nodeList.elementAt(i);
				String type = tabletag.getAttribute("type");
				if (type.equalsIgnoreCase("hidden")) {
					String value = tabletag.getAttribute("value");
					String name = tabletag.getAttribute("name");
					//Log.d("��" + i + "hidden����","type=" + type + ";value=" + value + ";name=" + name);
					inputs.put(name, value);

				}
			}

			return inputs;
		} catch (ParserException e) {
			return null;

		}


	}

	/**
	 * �õ�ÿһ����URL��ַ
	 * 
	 * @param content
	 * @param stepIndex
	 * @return
	 */

	public String getStepUrl(String doMain, int[] keyinds, String[] startKeys,
			String[] endKeys, String content, int stepIndex) {
		// //Log.d("getCharacterPosition����", content);

		String stepUrl = "";

		//Log.d("startKeys[stepIndex]", startKeys[stepIndex]);
		//Log.d("keyinds[stepIndex]", keyinds[stepIndex] + "");
		//Log.d("endKeys[stepIndex]", endKeys[stepIndex]);

		if (content.contains(startKeys[stepIndex])) {
			//Log.d("content.contains(startKeys[stepIndex]","" + content.contains(startKeys[stepIndex]));
			int offSet = getCharacterPosition2(content, startKeys[stepIndex],
					keyinds[stepIndex]);
			if (offSet < 0) {
				//Log.d("�Ҳ���getCharacterPosition2", "�Ҳ���offSet");
				return "";
			}
			String tempStr = content.substring(offSet
					+ startKeys[stepIndex].length());
			//Log.d("�м�����", tempStr);

			int lastSet = getCharacterPosition2(tempStr, endKeys[stepIndex], 1);
			if (lastSet < 0) {
				//Log.d("�Ҳ���getCharacterPosition2", "�Ҳ���lastSet");
				return "";
			}

			// //Log.d("lastSet", lastSet + "");
			// //Log.d("startKeys.length",startKeys[stepIndex].length() + "");

			stepUrl = tempStr.substring(0, lastSet);
			if("".equals(stepUrl)){
				return "";
			}

			//Log.d("getCharacterPosition2��ַ", stepUrl + "xxx");

			if (!stepUrl.startsWith("http")) {

				stepUrl = doMain.trim() + stepUrl.substring(1);
			}

		}

		return stepUrl;

	}

	public String getStepUrl2(int[] keyinds, String[] startKeys,
			String[] endKeys, String content, int stepIndex) {

		//Log.d("startKeys[stepIndex]", startKeys[stepIndex]);
		//Log.d("endKeys[stepIndex]", endKeys[stepIndex]);

		//Log.d("keyinds[stepIndex]", keyinds[stepIndex] + "");

		int offSet = getCharacterPosition2(content, startKeys[stepIndex],
				keyinds[stepIndex] - 1);
		if (offSet < 0) {
			//Log.d("�Ҳ���ACTION", "�Ҳ���ACTION");
			return "";
		}
		int lastSet = getCharacterPosition2(content, endKeys[stepIndex],
				keyinds[stepIndex] - 1);

		String stepUrl = content.substring(offSet + startKeys.length, lastSet);

		return stepUrl;

	}

	/**
	 * ���������е����ӣ�������Ϊ<a>��<go>
	 */

	@SuppressWarnings("serial")
	public NodeFilter lnkFilter1 = new NodeFilter() {
		public boolean accept(Node node) {
			if (node instanceof WmlGoTag)
				return true;
			if (node instanceof LinkTag)
				return true;
			return false;
		}
	};

	/**
	 * ���������е����ӣ�������Ϊ<go>
	 */
	@SuppressWarnings("serial")
	public NodeFilter lnkFilter = new NodeFilter() {
		public boolean accept(Node node) {
			if (node instanceof WmlGoTag) {
				return true;

			}

			return false;
		}
	};

}
