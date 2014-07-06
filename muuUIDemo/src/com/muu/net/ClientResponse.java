package com.muu.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClientResponse {

    HttpURLConnection mHttpConnection;

    public ClientResponse(HttpURLConnection conn) {
        mHttpConnection = conn;
    }

    public int getStatus() throws IOException {
        return mHttpConnection.getResponseCode();
    }

    public String getStatusMessage() throws IOException {
        return mHttpConnection.getResponseMessage();
    }

    public List<NewCookie> getCookies() {
        // Log.v(LOG_TAG, "getCookies()");

        List<NewCookie> ret = new LinkedList<NewCookie>();

        Map<String, List<String>> headers = mHttpConnection.getHeaderFields();

        // Debug purpose
        // for (Entry<String, List<String>> header : headers.entrySet()) {
        //    Log.v(LOG_TAG, "  [" + header.getKey() + "] values:");
        //    for (String hvalue : header.getValue()) {
        //        Log.v(LOG_TAG , "    [" + hvalue + "]");
        //    }
        // }

        List<String> cookies = headers.get("Set-Cookie");
        if (cookies == null) {
            // In SumSung GalaxyS, it use lower case
            cookies = headers.get("set-cookie");
        }

        if (cookies != null) {
            for (String c : cookies) {
                ret.add(NewCookie.valueOf(c));
            }
        }

        return ret;
    }

    /**
     * Get the input stream of the response.
     *
     * @return the input stream of the response.
     * @throws IOException
     */
    public InputStream getEntityInputStream() throws IOException {
        return mHttpConnection.getInputStream();
    }
    
	public byte[] getResponseEntity() throws IOException {
		InputStream in = mHttpConnection.getInputStream();
		if (in != null) {
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			// Read response into a buffered stream
			int readBytes;
			byte[] buffer = new byte[512];
			while ((readBytes = in.read(buffer)) != -1) {
				content.write(buffer, 0, readBytes);
			}
			in.close();
			return content.toByteArray();
		}

		return null;
	}

    public void close() {
    	if (mHttpConnection != null) {
    		mHttpConnection.disconnect();
    		mHttpConnection = null;
    	}
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }
}
