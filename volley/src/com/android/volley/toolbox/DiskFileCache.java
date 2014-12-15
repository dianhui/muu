package com.android.volley.toolbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.SystemClock;

import com.android.volley.VolleyLog;
import com.android.volley.toolbox.FileLoader.FileCache;

/**
 * 文件下载的磁盘缓存
 * 
 * @author xuegang
 *
 */
public class DiskFileCache implements FileCache {
    /** 磁盘空间平衡系数 */
    private static final float HYSTERESIS_FACTOR = 0.8f;
    /** 默认最大磁盘空间大小 */
    private static final int DEFAULT_DISK_USAGE_BYTES = 100 * 1024 * 1024;
    
    /** 磁盘缓存根目录 */
    private final File mRootDirectory;
    /** 最大缓存空间 */
    private final long mMaxCacheSizeInBytes;
    /** 缓存当前大小 */
    private long mTotalSize = 0;
    /** 缓存列表 */
    private final Map<String, File> mEntries = new LinkedHashMap<String, File>(16, .75f, true);

    public DiskFileCache(File rootDirectory, long maxSize) {
        mRootDirectory = rootDirectory;
        mMaxCacheSizeInBytes = maxSize;
    }

    public DiskFileCache(File rootDirectory) {
        this(rootDirectory, DEFAULT_DISK_USAGE_BYTES);
    }
    
    @Override
    public synchronized void init() {
        if (null == mRootDirectory) {
            VolleyLog.e("DiskFileCache's root directory is null");
            return;
        }

        if (!mRootDirectory.exists()) {
            if (!mRootDirectory.mkdirs()) {
                VolleyLog.e("Unable to create cache dir %s", mRootDirectory.getAbsolutePath());
            }
            return;
        }
        
        File[] files = mRootDirectory.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        
        mEntries.clear();
        mTotalSize = 0;
        for (File file : files) {
            mEntries.put(file.getName(), file);
            mTotalSize += file.length();
        }
    }
    
    @Override
    public synchronized void clear() {
        if (null == mRootDirectory) {
            VolleyLog.e("DiskFileCache's root directory is null");
            return;
        }

        File[] files = mRootDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        mEntries.clear();
        mTotalSize = 0;
        VolleyLog.d("Cache cleared.");
    }

    @Override
    public synchronized String getFile(String fileName) {
        File file = mEntries.get(fileName);
        if (null == file) {
            return null;
        }
        
        return file.getAbsolutePath();
    }

    @Override
    public synchronized String putFile(String fileName, byte[] data) {
        pruneIfNeeded(data.length);
        /** 如果缓存存在，则清除 */
        File oldFile = mEntries.get(fileName);
        if (null != oldFile) {
            mEntries.remove(fileName);
            mTotalSize -= oldFile.length();
            oldFile.delete();
            oldFile = null;
        }
        
        if (null == mRootDirectory) {
            VolleyLog.e("DiskFileCache's root directory is null");
            return null;
        }

        try {
            File file = new File(mRootDirectory, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            mEntries.put(fileName, file);
            mTotalSize += file.length();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public synchronized void remove(String fileName) {
        File oldFile = mEntries.get(fileName);
        if (null != oldFile) {
            mEntries.remove(fileName);
            mTotalSize -= oldFile.length();
            oldFile.delete();
        }
    }

    private void pruneIfNeeded(int neededSpace) {
        if (mTotalSize + neededSpace < mMaxCacheSizeInBytes) {
            return;
        }
        
        if (VolleyLog.DEBUG) {
            VolleyLog.v("Pruning old cache entries.");
        }
        
        long before = mTotalSize;
        int prunedFiles = 0;
        long startTime = SystemClock.elapsedRealtime();
        
        Iterator<Map.Entry<String, File>> iterator = mEntries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, File> entry = iterator.next();
            File file = entry.getValue();
            long fileSize = file.length();
            if (file.delete()) {
                mTotalSize -= fileSize;
            } else {
                VolleyLog.d("Could not delete cache entry for key=%s", entry.getKey());
            }
            
            iterator.remove();
            ++prunedFiles;
            
            if (mTotalSize + neededSpace < mMaxCacheSizeInBytes * HYSTERESIS_FACTOR) {
                break;
            }
        }
        
        if (VolleyLog.DEBUG) {
            VolleyLog.v("pruned %d files, %d bytes, %d ms", prunedFiles, (mTotalSize - before), SystemClock.elapsedRealtime() - startTime);
        }
    }
}
