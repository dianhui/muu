package com.muu.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapCache implements ImageCache {
    private LruCache<String, Bitmap> mCache = null;
    /** Cache size */
    private static final int MAX_SIZE = 5 * 1024 * 1024;

    public BitmapCache() {
        mCache = new LruCache<String, Bitmap>(MAX_SIZE) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                int size = value.getRowBytes() * value.getHeight();
                return size;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }

}
