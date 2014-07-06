package com.muu.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class WebResource {
    private URL mUrl;
    private ClientRequest mClientRequest;

    public WebResource(WebResource other, String url) {
        mClientRequest = new ClientRequest(other.mClientRequest.getConfig());
        buildUrl(url);
    }

    public WebResource(String url, ClientConfig config) {
        mClientRequest = new ClientRequest(config);
        buildUrl(url);
    }

    private void buildUrl(String url) {
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            Log.e("idscapi", "WebResource: invalid url " + url);
        }
    }

    /**
     * Return a new WebResource with path appended.
     *
     * @param path path to append
     * @return new WebResource
     */
    public WebResource path(String path) {
        return new WebResource(this, mUrl.toExternalForm() + "/" + path);
    }

    public WebResource accept(String type) {
        mClientRequest.accept(type);
        return this;
    }

    public WebResource header(String name, String value) {
        mClientRequest.addHeader(name, value);
        return this;
    }

    public WebResource type(String contentType) {
        mClientRequest.type(contentType);
        return this;
    }

    public WebResource cookie(Cookie cookie) {
        mClientRequest.cookie(cookie);
        return this;
    }

    public ClientResponse post(byte[] byteArray) throws IOException {
        return mClientRequest.post(mUrl, byteArray);
    }
    
    public ClientResponse put(byte[] byteArray) throws IOException {
        return mClientRequest.put(mUrl, byteArray);
    }

    public ClientResponse get() throws IOException {
        return mClientRequest.get(mUrl);
    }
    
    public ClientResponse head() throws IOException {
    	return mClientRequest.head(mUrl);
    }
    
    public ClientResponse delete() throws IOException {
    	return mClientRequest.delete(mUrl);
    }
}
