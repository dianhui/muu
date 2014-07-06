package com.muu.net;

import java.util.Map;


public abstract class ClientConfig {
    /**
     * Redirection property. A value of "true" declares that the client will
     * automatically redirect to the URI declared in 3xx responses.
     *
     * The value MUST be an instance of {@link java.lang.Boolean}.
     * If the property is absent then the default value is "true".
     */
    public static final String PROPERTY_FOLLOW_REDIRECTS =
            "com.muu.net.property.followRedirects";

    /**
     * Read timeout interval property, in milliseconds.
     *
     * The value MUST be an instance of {@link java.lang.Integer}.
     *
     * If the property is absent then the default value is an interval of
     * infinity. A value of zero 0 is equivalent to an interval of
     * infinity
     */
    public static final String PROPERTY_READ_TIMEOUT =
            "com.muu.net.property.readTimeout";

    /**
     * Connect timeout interval property, in milliseconds.
     *
     * The value MUST be an instance of {@link java.lang.Integer}.
     *
     * If the property is absent then the default value is an interval of
     * infinity. A value of  0 is equivalent to an interval of
     * infinity
     */
    public static final String PROPERTY_CONNECT_TIMEOUT =
            "com.muu.net.property.connectTimeout";

    public abstract Map<String, Object> getProperties() ;

    public abstract Object getProperty(String name);
    
    public boolean getProperty(String name, boolean defaultValue) {
    	Object obj = getProperties().get(name);
    	if (obj == null || !(obj instanceof Boolean)) {
    		return defaultValue;
    	}

    	return (Boolean)obj;
    }
    
    public int getProperty(String name, int defaultValue) {
    	Object obj = getProperties().get(name);
    	if (obj == null || !(obj instanceof Integer)) {
    		return defaultValue;
    	}

    	return (Integer)obj;
    }
}
