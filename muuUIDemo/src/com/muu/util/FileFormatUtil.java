package com.muu.util;

public class FileFormatUtil {
	public static final String TXT_POSTFIX = ".txt";
	public static final String CAPS_TXT_POSTFIX = ".TXT";
	public static final String PNG_POSTFIX = ".png";
	public static final String CAPS_PNG_POSTFIX = ".PNG";
	public static final String JPG_POSTFIX = ".jpg";
	public static final String CAPS_JPG_POSTFIX = ".JPG";
	public static final String GIF_POSTFIX = ".gif";
	public static final String CAPS_GIF_POSTFIX = ".GIF";
	
	public static String getFileSuffixByUrl(String url) {
		if (url.endsWith(JPG_POSTFIX) || url.endsWith(CAPS_JPG_POSTFIX)) return JPG_POSTFIX;
		if (url.endsWith(PNG_POSTFIX) || url.endsWith(CAPS_JPG_POSTFIX)) return PNG_POSTFIX;
		if (url.endsWith(GIF_POSTFIX) || url.endsWith(CAPS_GIF_POSTFIX)) return GIF_POSTFIX;
		if (url.endsWith(TXT_POSTFIX) || url.endsWith(CAPS_TXT_POSTFIX)) return TXT_POSTFIX;
		return "";
	}

}
