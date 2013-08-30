package com.insightfullogic.vertx.promises.codegen;

public class CodegenException extends RuntimeException {

    private static final long serialVersionUID = 1419400530755599225L;

    public CodegenException() {
        super();
    }

    public CodegenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CodegenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodegenException(String message) {
        super(message);
    }

    public CodegenException(Throwable cause) {
        super(cause);
    }

}
