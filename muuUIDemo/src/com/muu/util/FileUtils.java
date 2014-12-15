package com.muu.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

public class FileUtils {
    /** Default I/O buffer size */
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    /** Name of download directory */
    private static final String DOWNLOAD_DIRECTORY = "download";
    /** Download directory */
    private static File sDownloadDirectory = null;
    /** Cache directory */
    private static File sCacheDirectory = null;

    /**
     * 获取下载目录
     * 
     * @return
     */
    public static File getDownloadDirectory(Context ctx) {
        if (null == sDownloadDirectory) {
            synchronized (FileUtils.class) {
				if (null == sDownloadDirectory) {
					if (Environment.MEDIA_MOUNTED.equals(Environment
							.getExternalStorageState())) {
						sDownloadDirectory = ctx
								.getExternalFilesDir(DOWNLOAD_DIRECTORY);
					} else {
						sDownloadDirectory = ctx.getDir(DOWNLOAD_DIRECTORY,
								Context.MODE_PRIVATE);
					}
                }
            }
        }

        return sDownloadDirectory;
    }

    /**
     * 获取缓存目录
     * 
     * @return
     */
    public static File getCacheDirectory(Context ctx) {
        if (null == sCacheDirectory) {
            synchronized (FileUtils.class) {
                if (null == sCacheDirectory) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        sCacheDirectory = ctx.getExternalCacheDir();
                    } else {
                        sCacheDirectory = ctx.getCacheDir();
                    }
                }
            }
        }

        return sCacheDirectory;
    }

    /**
     * Read data from input stream and return as a String
     * 
     * @param in
     * @return
     * @throws IOException
     */
    public static String readStream(InputStream in) throws IOException {
        if (in == null)
            return "";

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int cnt = 0;
        while ((cnt = in.read(buffer)) > -1) {
            baos.write(buffer, 0, cnt);
        }
        baos.flush();

        in.close();
        baos.close();

        return baos.toString();
    }

    /**
     *
     * @param file: file object
     * @return file contents as string
     */
    public static String readFileToString(File file) {
        try {
            InputStream fileInputStream = new FileInputStream(file);
            return readStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Copy data from input stream to output stream
     * 
     * @param input
     * @param out
     * @throws IOException
     */
    public static void copyStream(InputStream input, OutputStream out) throws IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int temp = -1;
        while ((temp = input.read(buffer)) != -1) {
            out.write(buffer, 0, temp);
        }
        out.flush();
    }

    /**
     * Copy stream data without closing stream
     * 
     * @param input
     * @param out
     * @throws IOException
     *
     * @author xuegang
     * @version Created: 2014年9月23日 下午5:45:06
     */
    public static void copyWithoutOutputClosing(InputStream input, OutputStream out)
            throws IOException {
        try {
            final byte[] buffer = new byte[512];
            int temp = -1;
            while ((temp = input.read(buffer)) != -1) {
                out.write(buffer, 0, temp);
                out.flush();
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            input.close();
        }
    }
    
    /**
     * 缩放图片，参数不合法则返回null
     * 
     * @param bitmap
     * @param dstWidth
     *            缩放后宽度
     * @param dstHeight
     *            缩放后高度
     * @param needRecycle
     *            是否需要回收bitmap
     * @return 图片ByteArray
     */
    public static byte[] getThumbBitmapByteArray(Bitmap bitmap, int dstWidth,
            int dstHeight, boolean needRecycle) {
        if (null == bitmap || dstWidth <= 0 || dstHeight <= 0) {
            return null;
        }
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, dstWidth,
                dstHeight, true);
        if (needRecycle && thumbBmp != bitmap) {
            bitmap.recycle();
        }
        return FileReaderUtil.bmpToByteArray(thumbBmp, true);
    }
}
