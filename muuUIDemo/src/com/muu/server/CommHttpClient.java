package com.muu.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.message.BasicHeader;

import com.muu.net.ClientResponse;
import com.muu.net.DefaultClientConfig;
import com.muu.net.HttpMethod;
import com.muu.net.WebResource;
import com.muu.util.PropertyMgr;

import android.util.Log;
import android.util.Pair;

public class CommHttpClient {
	private static final String TAG = "CommHttpClient";
	private static final String HTTP_URL_PREFIX = "http://";
	
	public static final BasicHeader HEADER_CONTENT_TYPE = new BasicHeader(
			"Content-Type", "application/json");
	public static final BasicHeader HEADER_ACCEPT = new BasicHeader(
			"Accept", "application/json;image/jpg;image/png;image/gif");
	private static final BasicHeader HEADER_CONNECTION = new BasicHeader(
			"Connection", "Keep-Alive");
	
	private String mBaseUrl;
	private int mHttpTimeout;
	private int mSocketTimeout;
	private BasicHeader mDefaultContentType = HEADER_CONTENT_TYPE;
	private BasicHeader mDefaultAcceptType = HEADER_ACCEPT;
	
	public CommHttpClient() {
		mBaseUrl = PropertyMgr.getInstance().getMuuBaseUrl();
		mHttpTimeout = PropertyMgr.getInstance().getHttpTimeout();
		mSocketTimeout = PropertyMgr.getInstance().getSocketTimeout();
	}
	
	public ClientResponse handle(HttpMethod method, String path) throws IOException {
		return handle(method, path, null, null);
	}
	
	public ClientResponse handle(HttpMethod method, String path, byte[] entity)
			throws IOException {
		return handle(method, path, entity, null);
	}
	
	public ClientResponse handle(HttpMethod method, String path, byte[] entity,
			List<Pair<String, String>> extraHeaders) throws IOException {
		if (entity != null) {
			Log.d(TAG, String.format("%s%s (entity length: %d)", mBaseUrl,
					path, entity.length));
		} else {
			Log.d(TAG, String.format("%s%s", mBaseUrl, path));
		}
		
		String url = path.contains(HTTP_URL_PREFIX) ? path : mBaseUrl
				+ path;
        DefaultClientConfig config = new DefaultClientConfig();
        config.setConnectionTimeout(mHttpTimeout);
        config.setSocketReadTimeout(mSocketTimeout);
        WebResource webRes = setHeaders(new WebResource(url, config));
        if (extraHeaders != null) {
            for (Pair<String, String> pair : extraHeaders) {
                Log.d(TAG, String.format("Add header - %s: %s", pair.first, pair.second));
                webRes.header(pair.first, pair.second);
            }   
        }   
    
        ClientResponse resp = null;
        try {
            switch (method) {
            case GET:
                resp = webRes.get();
                break;
            case POST:
                resp = webRes.post(entity);
                break;
            case DELETE:
                resp = webRes.delete();
                break;
            case PUT:
                resp = webRes.put(entity);
                break;
            default:
                throw new IOException("Method not supported.");
            }
            
        } catch (UnknownHostException e) {
        	throw e;
        }
        
        int status = -1;
        status = resp.getStatus();
        if (status == -1) {
            throw new IOException("No valid response code for " + url);
        }
        
        Log.i(TAG, String.format("%s %s%s %d", method.method, mBaseUrl, path, status));
		return resp;
	}
        
	private WebResource setHeaders(WebResource webRes) {
		webRes.header(mDefaultAcceptType.getName(),
				mDefaultAcceptType.getValue())
				.header(mDefaultContentType.getName(),
						mDefaultContentType.getValue())
				.header(HEADER_CONNECTION.getName(),
						HEADER_CONNECTION.getValue());

		String userAgent = PropertyMgr.getInstance().getUserAgent();
		if (userAgent != null && userAgent.length() > 0) {
			webRes.header("User-Agent", userAgent);
		}

		return webRes;
	}
}
