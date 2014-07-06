package com.muu.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.util.Log;

class ClientRequest {
    private final static String TAG = "ClientRequest";
    private ClientConfig mClientConfig;
    private HashMap<String, List<String>> mHeaders = new HashMap<String, List<String>>();
    public final static String METHOD_POST = "POST";
    public final static String METHOD_GET = "GET";
    public final static String METHOD_HEAD = "HEAD";
    public final static String METHOD_DELETE = "DELETE";
    public final static String METHOD_PUT = "PUT";

    public ClientRequest(ClientConfig config) {
        mClientConfig = config;
    }

    /**
     * Add header name and value. name can be duplicated.
     *
     * @param name  Header name
     * @param value Header value
     */
    public void addHeader(String name, String value) {
        if (mHeaders.containsKey(name)) {
            mHeaders.get(name).add(value);
        } else {
            addSingleHeader(name, value);
        }
    }

    /**
     * Add single header name and value, if the header already exist, replace its value.
     *
     * @param name  Header name
     * @param value Header value
     */
    public void addSingleHeader(String name, String value) {
        LinkedList<String> values = new LinkedList<String>();
        values.add(value);
        mHeaders.put(name, values);
    }

    /**
     * Add Accept header
     *
     * @param type  Accept content type value. e.g. text/html
     */
    public void accept(String type) {
        addHeader("Accept", type);
    }

    /**
     * Add Content-Type header
     *
     * @param contentType  Content type value. e.g. text/html
     */
    public void type(String contentType) {
        addSingleHeader("Content-Type", contentType);
    }

    /**
     * Add Cookie header
     *
     * @param cookie Cookie value.
     */
    public void cookie(Cookie cookie) {
        addHeader("Cookie", cookie.toString());
    }

    private HttpURLConnection createHttpConnection(URL url, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        Integer readTimeout = (Integer)mClientConfig.getProperty(ClientConfig.PROPERTY_READ_TIMEOUT);
        conn.setRequestMethod(method);

        Log.v(TAG, String.format("createHttpConnection(%s, %s)", url.toExternalForm(), method));

        Integer connectTimeout = (Integer)mClientConfig.getProperty(ClientConfig.PROPERTY_CONNECT_TIMEOUT);
        if (connectTimeout != null) {
            conn.setConnectTimeout(connectTimeout);
        }

        if (readTimeout != null) {
            conn.setReadTimeout(readTimeout);
        }

        Boolean followRedirections = (Boolean)mClientConfig.getProperty(ClientConfig.PROPERTY_FOLLOW_REDIRECTS);
        if (followRedirections != null) {
            HttpURLConnection.setFollowRedirects(followRedirections);
        }

        if (method.equals(ClientRequest.METHOD_POST)
        		|| method.equals(ClientRequest.METHOD_PUT)) {
            conn.setDoOutput(true);
            conn.setUseCaches(false);
        } else {
            conn.setDoOutput(false);
        }

        // allow read from connection
        conn.setDoInput(true);

        // Add request headers.
        StringBuilder headers = new StringBuilder();
        Iterator<Map.Entry<String, List<String>>> iter = mHeaders.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, List<String>> entry = iter.next();
            String header = entry.getKey();
            List<String> values = entry.getValue();
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String value : values) {
                if (!first) {
                    sb.append(";");
                }
                sb.append(value);
                first = false;
            }
            conn.setRequestProperty(header, sb.toString());
            headers.append(header).append(": ").append(sb.toString()).append("\r\n");
        }

        Log.v(TAG, headers.toString());
        return conn;
    }

    /**
     * Send POST request to <i>url</i>.
     *
     * @param url
     * @param byteArray body
     * @return  ClientResponse
     * @throws IOException
     */
    public ClientResponse post(URL url, byte[] byteArray) throws IOException {
        HttpURLConnection conn = createHttpConnection(url, ClientRequest.METHOD_POST);
        conn.setRequestProperty("Content-Length", Integer.toString(byteArray.length));

        // write data to remote server
        DataOutputStream output = new DataOutputStream(conn.getOutputStream());
        output.write(byteArray);
        output.flush();
        output.close();
        return new ClientResponse(conn);
    }
    
    /**
     * Send PUT request to <i>url</i>.
     *
     * @param url
     * @param byteArray body
     * @return  ClientResponse
     * @throws IOException
     */
    public ClientResponse put(URL url, byte[] byteArray) throws IOException {
        HttpURLConnection conn = createHttpConnection(url, ClientRequest.METHOD_PUT);
        conn.setRequestProperty("Content-Length", Integer.toString(byteArray.length));

        // write data to remote server
        DataOutputStream output = new DataOutputStream(conn.getOutputStream());
        output.write(byteArray);
        output.flush();
        output.close();

        return new ClientResponse(conn);
    }

    /**
     * Send GET request to <i>url</i>.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public ClientResponse get(URL url) throws IOException {
        HttpURLConnection conn = createHttpConnection(url, ClientRequest.METHOD_GET);
        return new ClientResponse(conn);
    }
    
    public ClientResponse head(URL url) throws IOException{
   	 	HttpURLConnection conn = createHttpConnection(url, ClientRequest.METHOD_HEAD);
        return new ClientResponse(conn);
   }
    
    /**
     * Send DELETE request to <i>url</i>.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public ClientResponse delete(URL url) throws IOException {
        HttpURLConnection conn = createHttpConnection(url, ClientRequest.METHOD_DELETE);
        return new ClientResponse(conn);
    }

    public ClientConfig getConfig() {
        return mClientConfig;
    }
}
