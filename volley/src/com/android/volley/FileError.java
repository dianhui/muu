package com.android.volley;

public class FileError extends VolleyError {
    /**
     * generated serial id
     */
    private static final long serialVersionUID = 1073196521319545356L;

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
