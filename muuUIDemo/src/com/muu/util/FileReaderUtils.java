package com.muu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileReaderUtils {
	public static String getFileContent(String path) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					new File(path)), "GB2312");
			in = new BufferedReader(isr);
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		return sb.toString();
	}
}
