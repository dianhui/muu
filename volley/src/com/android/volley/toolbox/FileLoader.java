package com.android.volley.toolbox;

import java.util.HashMap;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;

/**
 * 模仿自ImageLoader，用于管理图片文件下载
 * 
 * @author xuegang
 *
 */
public class FileLoader {
    /** 请求队列 */
    private final RequestQueue mRequestQueue;

    /** 派发响应的等待时间 */
    private int mBatchResponseDelayMs = 100;

    /** 缓存器 */
    private final FileCache mCache;

    /** 进行中的请求映射表 */
    private final HashMap<String, BatchedFileRequest> mInFlightRequests = new HashMap<String, BatchedFileRequest>();

    /** 等待派发的响应映射表 */
    private final HashMap<String, BatchedFileRequest> mBatchedResponses = new HashMap<String, BatchedFileRequest>();

    /** 派发响应Handler */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /** 派发响应的Runnable */
    private Runnable mRunnable;

    public interface FileCache {
        /** 缓存初始化 */
        public void init();

        /** 清空缓存 */
        public void clear();

        /** 查找缓存，返回文件名，或null */
        public String getFile(String key);

        /** 写缓存， 成功返回文件名，失败返回null */
        public String putFile(String key, byte[] data);

        /** 删除缓存 */
        public void remove(String key);
    }

    private class BatchedFileRequest {
        /** 真正的网络请求 */
        private final Request<?> mRequest;
        /** 请求成功的文件名 */
        private String mResponseFileName;
        /** 请求失败 */
        private VolleyError mError;
        /** 同一请求的等待列表 */
        private final LinkedList<FileContainer> mContainers = new LinkedList<FileContainer>();

        public BatchedFileRequest(Request<?> request, FileContainer container) {
            mRequest = request;
            mContainers.add(container);
        }

        public void setError(VolleyError error) {
            mError = error;
        }

        public VolleyError getError() {
            return mError;
        }

        public void addContainer(FileContainer container) {
            mContainers.add(container);
        }

        public boolean removeContainerAndCancelIfNecessary(FileContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    public class FileContainer {
        /** 文件名 */
        private String mFileName;
        /** 监听 */
        private final FileListener mListener;
        /** 缓存索引键 */
        private final String mCacheKey;
        /** 请求URL */
        private final String mRequestUrl;

        public FileContainer(String fileName, String requestUrl, String cacheKey,
                FileListener listener) {
            mFileName = fileName;
            mRequestUrl = requestUrl;
            mCacheKey = cacheKey;
            mListener = listener;
        }

        public void cancelRequest() {
            if (mListener == null) {
                return;
            }

            BatchedFileRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        public String getFileName() {
            return mFileName;
        }

        public String getRequestUrl() {
            return mRequestUrl;
        }
    }

    public interface FileListener extends ErrorListener {
        public void onResponse(FileContainer response, boolean isImmediate);
    }

    public FileLoader(RequestQueue queue) {
        mRequestQueue = queue;
        mCache = VolleyConfig.getFileCache();

        mCache.init();
    }

    public boolean isCached(String requestUrl) {
        throwIfNotOnMainThread();

        String cacheKey = VolleyUtil.uri2CacheKey(requestUrl);
        return mCache.getFile(cacheKey) != null;
    }

    /**
     * Get local file name
     * 
     * @param requestUrl
     * @return
     *
     * @author xuegang
     * @version Created: 2014年10月1日 上午9:44:29
     */
    public String getFileFromCache(String requestUrl) {
        String cacheKey = VolleyUtil.uri2CacheKey(requestUrl);
        return mCache.getFile(cacheKey);
    }

    /**
     * Get local file name, prefix with file scheme("file://")
     * 
     * @param requestUrl
     * @return
     *
     * @author xuegang
     * @version Created: 2014年10月1日 上午9:44:06
     */
    public String getFileFromCachePrefixFileScheme(String requestUrl) {
        String cacheKey = VolleyUtil.uri2CacheKey(requestUrl);
        String localFileName = mCache.getFile(cacheKey);
        if (null == localFileName) {
            return null;
        }

        return "file://" + localFileName;
    }

    public FileContainer get(String requestUrl, Object requestTag, FileListener fileListener) {
        return get(requestUrl, requestTag, fileListener, false);
    }

    /**
     * Issues a file request with the given URL if that file is not available
     * in the cache, and returns a file container that contains all of the data
     * relating to the request.
     * 
     * @param requestUrl The url of the remote file
     * @param requestTag The tag that can be used to cancel this request
     * @param fileListener The listener to call when the remote file is loaded
     * @param onlyCache True if only get file from cache
     * @return A container object that contains all of the properties of the request, as well as
     *         the currently available file.
     *
     * @author xuegang
     * @version Created: 2014年10月22日 上午10:53:39
     */
    public FileContainer get(String requestUrl, Object requestTag, FileListener fileListener,
            boolean onlyCache) {
        throwIfNotOnMainThread();

        final String cacheKey = VolleyUtil.uri2CacheKey(requestUrl);

        String cachedFile = mCache.getFile(cacheKey);
        if (null != cachedFile) {
            FileContainer container = new FileContainer(cachedFile, requestUrl, null, null);
            fileListener.onResponse(container, true);
            return container;
        }

        FileContainer fileContainer = new FileContainer(cachedFile, requestUrl, cacheKey,
                fileListener);

        BatchedFileRequest request = mInFlightRequests.get(cacheKey);
        if (null != request) {
            request.addContainer(fileContainer);
            return fileContainer;
        }

        FileRequest newRequest = new FileRequest(requestUrl, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                onGetFileSuccess(cacheKey, response);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                onGetFileError(cacheKey, error);
            }
        });

        newRequest.setFileLoader(this);
        newRequest.setOnlyCache(onlyCache);
        newRequest.setTag(requestTag);
        mRequestQueue.add(newRequest);
        mInFlightRequests.put(cacheKey, new BatchedFileRequest(newRequest, fileContainer));
        return fileContainer;
    }

    public void setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        mBatchResponseDelayMs = newBatchedResponseDelayMs;
    }

    private void onGetFileSuccess(String cacheKey, String response) {
        BatchedFileRequest request = mInFlightRequests.remove(cacheKey);
        if (null != request) {
            request.mResponseFileName = response;
            batchResponse(cacheKey, request);
        }
    }

    private void onGetFileError(String cacheKey, VolleyError error) {
        BatchedFileRequest request = mInFlightRequests.remove(cacheKey);
        if (null != request) {
            request.setError(error);
            batchResponse(cacheKey, request);
        }
    }

    private void batchResponse(String cacheKey, BatchedFileRequest request) {
        mBatchedResponses.put(cacheKey, request);

        if (null == mRunnable) {
            mRunnable = new Runnable() {

                @Override
                public void run() {
                    for (BatchedFileRequest bfr : mBatchedResponses.values()) {
                        for (FileContainer container : bfr.mContainers) {
                            if (null == container.mListener) {
                                continue;
                            }

                            if (null == bfr.getError()) {
                                container.mFileName = bfr.mResponseFileName;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onErrorResponse(bfr.getError());
                            }
                        }
                    }

                    mBatchedResponses.clear();
                    mRunnable = null;
                }
            };

            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    /* package */String saveFile(String requestUrl, byte[] data) {
        final String cachedKey = VolleyUtil.uri2CacheKey(requestUrl);
        return mCache.putFile(cachedKey, data);
    }

    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("FileLoader must be invoked from the main thread.");
        }
    }
}
