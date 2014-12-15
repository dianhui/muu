package com.android.volley.toolbox;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import com.android.volley.VolleyLog;

public final class VolleyUtil {
    /**
     * Cast a uri String to a 'safe' String that can be used as file name, or other
     * purpose
     * 
     * @param uri
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月11日 下午4:07:47
     */
    public static String uri2CacheKey(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return uri;
        }

        return Base64.encodeToString(uri.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }
    
    /**
     * Cast a cacheKey to a uri
     * 
     * @param cacheKey
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月11日 下午4:07:44
     */
    public static String cacheKey2Uri(String cacheKey) {
        if (TextUtils.isEmpty(cacheKey)) {
            return cacheKey;
        }
        
        return new String(Base64.decode(cacheKey, Base64.URL_SAFE | Base64.NO_WRAP));
    }
    
    /**
     * Read File into byte array
     * 
     * @param file
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月11日 下午5:23:19
     */
    public static byte[] readFile(File file) {
        FileInputStream inputStream = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int cnt = 0;
            while ((cnt = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, cnt);
            }
            
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(inputStream);
            safeClose(baos);
        }
        
        return null;
    }
    
    /**
     * Close a Closeable object safely
     * 
     * @param object
     *
     * @author xuegang
     * @version Created: 2014年9月11日 下午4:14:11
     */
    public static void safeClose(Closeable object) {
        if (null != object) {
            try {
                object.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Decode a new Bitmap from a file.
     * 
     * @param fileName
     * @param maxWidth max width limitation of Bitmap, 0 for unlimited
     * @param maxHeight max height limitation of Bitmap, 0 for unlimited
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月25日 下午5:52:28
     */
    public static Bitmap decodeFile(String fileName, int maxWidth, int maxHeight) {
        if (TextUtils.isEmpty(fileName)) {
            VolleyLog.e("decode file name is empty");
            return null;
        }

        return decodeFile(new File(fileName), maxWidth, maxHeight);
    }

    /**
     * Decode a new Bitmap from a file.
     * 
     * @param file
     * @param maxWidth maxWidth max width limitation of Bitmap, 0 for unlimited
     * @param maxHeight maxHeight max height limitation of Bitmap, 0 for unlimited
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月25日 下午5:54:21
     */
    public static Bitmap decodeFile(File file, int maxWidth, int maxHeight) {
        if (!file.exists()) {
            VolleyLog.e("decode file does not exist");
            return null;
        }

        try {
            return decodeStream(new FileInputStream(file), maxWidth, maxHeight);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decode a new Bitmap from an InputStream.
     * 
     * @param inputStream
     * @param maxWidth maxWidth max width limitation of Bitmap, 0 for unlimited
     * @param maxHeight maxHeight max height limitation of Bitmap, 0 for unlimited
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月25日 下午5:54:52
     */
    public static Bitmap decodeStream(InputStream inputStream, int maxWidth, int maxHeight) {
        byte[] data = readStream(inputStream);
        if (null == data) {
            VolleyLog.e("read stream failed");
            return null;
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap = null;
        if (0 == maxWidth && 0 == maxHeight) {
            return decodeByteArray(data, decodeOptions);
        }

        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
        int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);

        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth,
                desiredHeight);

        bitmap = decodeByteArray(data, decodeOptions);

        if (VolleyLog.DEBUG) {
            VolleyLog
                    .d("maxWidth: %d, maxHeight: %d, actualWidth: %d, actualHeight: %d, desiredWidth: %d, desiredHeight: %d, inSampleSize: %d",
                            maxWidth, maxHeight, actualWidth, actualHeight, desiredWidth,
                            desiredHeight, decodeOptions.inSampleSize);
        }
        return bitmap;
    }

    /**
     * Decode a new Bitmap from byte array.
     * 
     * @param data
     * @param options
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月25日 下午5:56:04
     */
    public static Bitmap decodeByteArray(byte[] data, BitmapFactory.Options options) {
        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }

        return null;
    }

    /** Default I/O buffer size */
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static byte[] readStream(InputStream inputStream) {
        if (inputStream == null)
            return null;

        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int cnt = 0;
            while ((cnt = inputStream.read(buffer)) > -1) {
                baos.write(buffer, 0, cnt);
            }
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            safeClose(inputStream);
            safeClose(baos);
        }
        return null;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
            int actualSecondary) {
        // If no dominant value at all, just return the actual.
        if (maxPrimary == 0 && maxSecondary == 0) {
            return actualPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;
        if (resized * ratio > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    private static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth,
            int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}
