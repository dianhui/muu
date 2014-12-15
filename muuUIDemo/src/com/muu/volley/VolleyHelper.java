package com.muu.volley;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BitmapCache;
import com.android.volley.toolbox.DiskFileCache;
import com.android.volley.toolbox.FileLoader;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.VolleyConfig;
import com.muu.util.FileUtils;

/**
 * @brief this class is wrapper for Volley library. Normally, it's not suggested to
 *        refer to call Volley operation directly, every Volley operation request should
 *        come from this class.
 */

public class VolleyHelper {
    private RequestQueue mRequestQueue;

    private static VolleyHelper _instance = null;
    private ImageLoader mImageLoader = null;
    private FileLoader mFileLoader = null;

    public static VolleyHelper getInstance(Context ctx) {
        if (null == _instance) {
            synchronized (VolleyHelper.class) {
                if (null == _instance) {
                    _instance = new VolleyHelper(ctx);
                }
            }
        }

        return _instance;
    }

    private VolleyHelper(Context ctx) {
        VolleyConfig.setLocalImageDirectory(FileUtils.getCacheDirectory(ctx));
        VolleyConfig.setImageCache(new BitmapCache());
        VolleyConfig.setFileCache(new DiskFileCache(FileUtils.getCacheDirectory(ctx)));
		mRequestQueue = Volley.newRequestQueue(ctx, "muu_android");
        mImageLoader = new ImageLoader(mRequestQueue);
        mFileLoader = new FileLoader(mRequestQueue);
    }

    /**
     * 获取默认的图片下载器，只能在UI线程调用
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月16日 上午11:55:04
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * 获取文件下载器，只能在UI线程调用
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月16日 上午11:54:30
     */
    public FileLoader getFileLoader() {
        return mFileLoader;
    }

    /**
     * 添加一个网络请求到Volley框架，只能在UI线程调用
     * 
     * @param request
     *
     * @author xuegang
     * @version Created: 2014年9月16日 上午11:55:22
     */
    public void add(Request<?> request) {
        mRequestQueue.add(request);
    }

    /**
     * 取消tag的网络请求
     * 
     * @param tag
     *
     * @author xuegang
     * @version Created: 2014年9月16日 上午11:55:44
     */
    public void cancelRequests(final Object tag) {
        if (null == tag) {
            return;
        }

        mRequestQueue.cancelAll(tag);
    }

    /**
     * 获取默认的网络执行器
     * 
     * @return
     *
     * @author xuegang
     * @version Created: 2014年9月16日 上午11:56:12
     */
    public Network getDefaultNetwork() {
        return Volley.getDefaultNetwork("muu_android");
    }
}
