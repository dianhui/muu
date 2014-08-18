package com.muu.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.FileLoader;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.muu.util.PropertyMgr;

public class VolleyHelper {
    private static final String LOG_TAG = VolleyHelper.class.getSimpleName();
    
    private Context mCtx;
    private  ImageLoader mImageLoader = null;
    private  FileLoader mFileLoader = null;
    private RequestQueue mRequestQueue = null;
    
    private static volatile VolleyHelper mInstanse;
    private VolleyHelper(Context ctx) {
    	if (mCtx == null) {
    		mCtx = ctx.getApplicationContext();
		}
    	
    	if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(mCtx, PropertyMgr
					.getInstance().getUserAgent());
		}
    	
    	if (mImageLoader == null) {
    		mImageLoader = new ImageLoader(mRequestQueue,
                    new BitmapCache());
		}
    	
		if (mFileLoader == null) {
			new FileLoader(mRequestQueue, new DiskFileCache(PropertyMgr
					.getInstance().getVolleyCacheDir()));
		}
    }
    
    public static VolleyHelper getInstanse(Context ctx) {
    	if (mInstanse == null) {
    		synchronized (VolleyHelper.class) {
				if (mInstanse == null) {
					mInstanse = new VolleyHelper(ctx);
				}
			}
		}
    	return mInstanse;
    }
    

    public ImageLoader getDefaultImageLoader() {
        return mImageLoader;
    }
    
    public FileLoader getDefaultFileLoader() {
        return mFileLoader;
    }

    public void add(Request<?> request) {
        mRequestQueue.add(request);
    }

    public void cancelRequests(final Object tag) {
        if (null == tag) {
            Log.w(LOG_TAG, "try cancel requests with null tag");
            return;
        }

        mRequestQueue.cancelAll(tag);
    }
}
