package com.muu.net;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * Represents the value of a HTTP cookie, transferred in a request.
 * RFC 2109 specifies the legal characters for name,
 * value, path and domain. The default version of 1 corresponds to RFC 2109.
 * @see <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>
 */
public class Cookie {
	private final String TAG = "Cookie";

    /**
     * Cookies using the default version correspond to RFC 2109.
     */
    public static final int DEFAULT_VERSION = 0;

    private String name;
    private String value;
    private int version;
    private String path;
    private String domain;
    private String expires;
	private Date expiryDate;

	private final static String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	private final static String O2_DATE_FORMAT = "EEE, dd-MMM-yyyy HH:mm:ss zzz";

	
    /**
     * Create a new instance.
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the URI path for which the cookie is valid
     * @param domain the host domain for which the cookie is valid
     * @param version the version of the specification to which the cookie complies
     * @throws IllegalArgumentException if name is null
     */
    public Cookie(String name, String value, String path, String domain, String expires, int version) {
        if (name == null)
            throw new IllegalArgumentException("name==null");
        this.name = name;
        this.value = value;
        this.version = version;
        this.domain = domain;
        this.path = path;
        this.expires = expires;
        parseExpires(expires);
    }
    
	private void parseExpires(String value) {
		if (value == null || value.length() == 0)
			return;

		SimpleDateFormat format = new SimpleDateFormat(O2_DATE_FORMAT, Locale.US);
		try {
			expiryDate = format.parse(value);
			return;
		} catch (ParseException e) {
			Log.w(TAG, "Failed to parse cookie expires as O2 date format. Trying HTTP standard.");
		}
		
		format = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		try {
			expiryDate = format.parse(value);
		} catch (ParseException e) {
			Log.w(TAG, String.format("Failed to parse cookie expires as HTTP date format. %s.", value));
		}
	}
    
    /**
     * Create a new instance.
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the URI path for which the cookie is valid
     * @param domain the host domain for which the cookie is valid
     * @param version the version of the specification to which the cookie complies
     * @throws IllegalArgumentException if name is null
     */
    public Cookie(String name, String value, String path, String domain, int version) {
        if (name == null)
            throw new IllegalArgumentException("name==null");
        this.name = name;
        this.value = value;
        this.version = version;
        this.domain = domain;
        this.path = path;
    }

    /**
     * Create a new instance.
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the URI path for which the cookie is valid
     * @param domain the host domain for which the cookie is valid
     * @throws IllegalArgumentException if name is null
     */
    public Cookie(String name, String value, String path, String domain) {
        this(name, value, path, domain, DEFAULT_VERSION);
    }

    /**
     * Create a new instance.
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @throws IllegalArgumentException if name is null
     */
    public Cookie(String name, String value) {
        this(name, value, null, null);
    }

    /**
     * Get the name of the cookie
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of the cookie
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the version of the cookie
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get the domain of the cookie
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }
    
	/**
	 * Check whether the cooke has expired.
	 * If there is no expire field in cookie, we assume it never expires.
	 * @return
	 */
    public boolean isExpired() {
    	return expiryDate != null && expiryDate.getTime() <= System.currentTimeMillis();
    }
    
    /**
     * Get the original expires value in the cookie.
     * @return
     */
    public String getExpires() {
    	return expires;
    }

    /**
     * Get the path of the cookie
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Convert the cookie to a string suitable for use as the value of the
     * corresponding HTTP header.
     * @return a stringified cookie
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);
		if (path != null) {
			sb.append("; path=").append(path);
		}
		if (domain != null) {
			sb.append("; domain=").append(domain);
		}
		if (version != DEFAULT_VERSION) {
			sb.append("; version=").append(version);
		}

        return sb.toString();
    }

    /**
     * Generate a hashcode by hashing all of the cookies properties
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + this.version;
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        hash = 97 * hash + (this.domain != null ? this.domain.hashCode() : 0);
        return hash;
    }

    /**
     * Compare for equality
     * @param obj the object to compare to
     * @return true if the object is a {@code Cookie} with the same value for
     * all properties, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cookie other = (Cookie) obj;
        if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.version != other.version) {
            return false;
        }
        if (this.path != other.path && (this.path == null || !this.path.equals(other.path))) {
            return false;
        }
        if (this.domain != other.domain && (this.domain == null || !this.domain.equals(other.domain))) {
            return false;
        }
        return true;
    }
}
