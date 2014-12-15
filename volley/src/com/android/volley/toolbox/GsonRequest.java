package com.android.volley.toolbox;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GsonRequest<T> extends Request<T> {
    private Listener<T> mListener;
    private Class<T> mClazz;
    private Gson mGson;
    private Map<String, String> mParams;

    public GsonRequest(int method, String url, Class<T> clazz,
            Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mClazz = clazz;
        mGson = new Gson();
    }

    /**
     * get方式请求数据
     * 
     * @param url
     *            带有请求参数的拼装好的url
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public GsonRequest(String url, Class<T> clazz, Listener<T> listener,
            ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    /**
     * post方式请求数据
     * 
     * @param url
     *            请求的url
     * @param params
     *            post需要的相关参数
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public GsonRequest(String url, Map<String, String> params, Class<T> clazz,
            Listener<T> listener, ErrorListener errorListener) {
        this(Method.POST, url, clazz, listener, errorListener);
        mParams = params;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String data;
        try {
            data = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            data = new String(response.data);
        }

        try {
            return Response.success(mGson.fromJson(data, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }
}
