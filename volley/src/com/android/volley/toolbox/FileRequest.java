package com.android.volley.toolbox;

import com.android.volley.FileError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

/**
 * 文件下载
 * 
 * 不建议直接使用本请求。应该使用FileLoader
 * 同样，本请求只适合下载较小的文件，如图片。
 * @author xuegang
 *
 */
public class FileRequest extends Request<String> {
    private String mRequestUrl;
    private Listener<String> mListener;
    private FileLoader mFileLoader;
    public FileRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mRequestUrl = url;
        mListener = listener;
    }
    
    public FileRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String fileName = mFileLoader.saveFile(mRequestUrl, response.data);
        if (null != fileName) {
            return Response.success(fileName, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            return Response.error(new FileError("Cache file error: " + mRequestUrl));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    /*package*/void setFileLoader(FileLoader fileLoader) {
        mFileLoader = fileLoader;
    }
}
