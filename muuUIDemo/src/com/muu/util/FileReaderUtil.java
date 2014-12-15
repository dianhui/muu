package com.muu.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class FileReaderUtil {
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
	
	public static ArrayList<String> getLines(String path) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader in = null;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					new File(path)), "GB2312");
			in = new BufferedReader(isr);
			String line = null;
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		return lines;
	}
	
	public static void deleteFiles(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	        	deleteFiles(child);

	    fileOrDirectory.delete();
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
