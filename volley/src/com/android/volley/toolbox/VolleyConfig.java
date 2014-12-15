package com.android.volley.toolbox;

import java.io.File;

import com.android.volley.toolbox.FileLoader.FileCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

public final class VolleyConfig {
    /** 本地图片目录。如果目录中有所需图片，则直接从该目录中获取，而不需要经过网络 */
    private static File mLocalImageDirectory = null;
    /** memory cache for Bitmap */
    private static ImageCache mImageCache = null;
    /** file cache */
    private static FileCache mFileCache = null;
    
    public static void setLocalImageDirectory(File directory) {
        mLocalImageDirectory = directory;
    }
    
    public static File getLocalImageDirectory() {
        return mLocalImageDirectory;
    }

    public static ImageCache getImageCache() {
        return mImageCache;
    }

    public static void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public static FileCache getFileCache() {
        return mFileCache;
    }

    public static void setFileCache(FileCache fileCache) {
        mFileCache = fileCache;
    }
}
