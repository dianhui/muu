package com.muu.net;

public class NewCookie extends Cookie {

    public NewCookie(String name, String value) {
        super(name, value);
    }

    public NewCookie(String name, String value, String path, String domain) {
        super(name, value, path, domain);
    }

	public NewCookie(String name, String value, String path, String domain,
			int version) {
		super(name, value, path, domain, version);
	}
	
	public NewCookie(String name, String value, String path, String domain,
			String expires, int version) {
		super(name, value, path, domain, expires, version);
	}

    /**
     * Creates a new instance of NewCookie by parsing the supplied string.
     *
     * Example of Set-Cookie header:
     *
     * Set-Cookie: Customer="WILE_E_COYOTE"; Version="1"; Path="/acme"
     * Set-Cookie: authKey=23dfhkewlekwDek
     *
     * @param value
     *            the cookie string
     * @return the newly created NewCookie
     * @throws IllegalArgumentException
     *             if the supplied string cannot be parsed or is null
     */
    public static NewCookie valueOf(String header) throws IllegalArgumentException {
    	
        if (header == null) {
            throw new IllegalArgumentException();
        }

        int pos = header.indexOf('=');
        if (pos == -1) {
            throw new IllegalArgumentException();
        }
        
		String name = header.substring(0, pos);
		String value = null;
		String path = null;
		String domain = null;
		String expires = null;
		int version = DEFAULT_VERSION;

		int pos_semi = header.indexOf(';');
		if (pos_semi == -1) {
			value = header.substring(pos + 1);
		} 
		else {
			value = header.substring(pos + 1, pos_semi);
		}
	        
		//split header by ';'
		String[] attrs = header.split(";");
		
		if (attrs.length >= 2) {
			for (int i = 1; i < attrs.length; i++) {
				pos = attrs[i].indexOf('=');
				
				if (pos != -1) {
					String attr = attrs[i].substring(0, pos).trim();

					if (attr.equalsIgnoreCase("path")) {
						path = attrs[i].substring(pos + 1);
					} else if (attr.equalsIgnoreCase("domain")) {
						domain = attrs[i].substring(pos + 1);
					} else if (attr.equalsIgnoreCase("version")) {
						String str = attrs[i].substring(pos + 1);
						version = Integer.parseInt(str);
					} else if (attr.equalsIgnoreCase("expires")) {
						expires = attrs[i].substring(pos + 1);
					}
				}
			}
		}

		return new NewCookie(name, value, path, domain, expires, version);
	}

    /**
     * Convert the cookie to a string suitable for use as the value of the
     * corresponding HTTP header.
     *
     * @return a stringified cookie
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(getName()).append("=").append(getValue());
        if (getPath() != null) {
			sb.append("; path=").append(getPath());
		}
		if (getDomain() != null) {
			sb.append("; domain=").append(getDomain());
		}
		if (getVersion() != DEFAULT_VERSION) {
			sb.append("; version=").append(getVersion());
		}
		
        return sb.toString();
    }
}
