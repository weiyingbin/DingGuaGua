package com.ebook.exception;

/**
 * 业务层异常
 */
public class EBookServiceRuntimeException extends Exception {
    private Throwable rootCause;

    public EBookServiceRuntimeException(String s) {
        super(s);
    }

    public EBookServiceRuntimeException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    public EBookServiceRuntimeException(Throwable rootCause, String s) {
        super(s);
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public void setRootCause(Throwable rootCause) {
        this.rootCause = rootCause;
    }
}
