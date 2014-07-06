package com.muu.util;

public class FileFormatUtil {
	public static final String TXT_POSTFIX = ".txt";
	public static final String PNG_POSTFIX = ".png";
	public static final String JPG_POSTFIX = ".jpg";
	public static final String GIF_POSTFIX = ".gif";
	
	public static String getFileSuffixByUrl(String url) {
		if (url.endsWith(JPG_POSTFIX)) return JPG_POSTFIX;
		if (url.endsWith(PNG_POSTFIX)) return PNG_POSTFIX;
		if (url.endsWith(GIF_POSTFIX)) return GIF_POSTFIX;
		if (url.endsWith(TXT_POSTFIX)) return TXT_POSTFIX;
		return "";
	}

}
