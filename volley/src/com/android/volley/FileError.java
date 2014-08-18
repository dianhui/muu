package com.android.volley;

@SuppressWarnings("serial")
public class FileError extends VolleyError {
    public FileError(String exceptionMessage) {
        super(exceptionMessage);
    };

    public FileError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public FileError(Throwable cause) {
        super(cause);
    }
}
