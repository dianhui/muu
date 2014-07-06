package com.muu.net;

import org.apache.http.HttpStatus;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    HEAD("HEAD"),
    DELETE("DELETE");

    public String method;
    HttpMethod(String method) {
        this.method = method;
    }   

    public boolean isSuccessCode(int statusCode) {
        switch (this) {
        case POST:
            if (statusCode == HttpStatus.SC_CREATED)
                return true;
            // Fall through
        case GET:
            if (statusCode == HttpStatus.SC_OK
                    || statusCode == HttpStatus.SC_NOT_MODIFIED)
                return true;
            break;

        case DELETE:
            if (statusCode == HttpStatus.SC_NO_CONTENT)
                return true;
            // Fall through
        case PUT:
            if (statusCode == HttpStatus.SC_OK)
                return true;
            break;

        case HEAD:
            // Not used currently.
            break;
        }   

        return false;
    }
};
