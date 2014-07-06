package com.muu.net;

import java.util.HashMap;
import java.util.Map;

public class DefaultClientConfig extends ClientConfig {
    private Map<String, Object> mProperties = new HashMap<String, Object>();

    public DefaultClientConfig() {
        mProperties.put(PROPERTY_CONNECT_TIMEOUT, 30000);   // 30 seconds
        mProperties.put(PROPERTY_READ_TIMEOUT, 30000);      // 30 seconds
        mProperties.put(PROPERTY_FOLLOW_REDIRECTS, false);
    }

    @Override
    public Map<String, Object> getProperties() {
        return mProperties;
    }

    @Override
    public Object getProperty(String name) {
        return mProperties.get(name);
    }

    public void setProperty(String key, Object value) {
        mProperties.put(key, value);
    }

    /**
     * Set connection timeout.
     * @param ms timeout value, in milliseconds.
     */
    public void setConnectionTimeout(int ms) {
        setProperty(PROPERTY_CONNECT_TIMEOUT, ms);
    }

    /**
     * Set socket read timeout.
     * @param ms timeout value, in milliseconds.
     */
    public void setSocketReadTimeout(int ms) {
        setProperty(PROPERTY_READ_TIMEOUT, ms);
    }

    /**
     * Set whether follow redirections.
     *
     * @param follow true for follow redirection, false for not follow.
     */
    public void setFollowRedirections(boolean follow) {
        setProperty(PROPERTY_FOLLOW_REDIRECTS, follow);
    }
}
