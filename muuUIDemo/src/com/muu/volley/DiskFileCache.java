package com.muu.volley;

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

public class DiskFileCache implements FileCache {
    private static final float HYSTERESIS_FACTOR = 0.8f;
    private static final int DEFAULT_DISK_USAGE_BYTES = 5 * 1024 * 1024;
    
    private final File mRootDirectory;
    private final long mMaxCacheSizeInBytes;
    private long mTotalSize = 0;
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
        File oldFile = mEntries.get(fileName);
        if (null != oldFile) {
            mEntries.remove(fileName);
            mTotalSize -= oldFile.length();
            oldFile.delete();
            oldFile = null;
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
