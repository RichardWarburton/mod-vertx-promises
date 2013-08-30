package com.insightfullogic.vertx.promises;

public class PromiseException extends RuntimeException {

    private static final long serialVersionUID = 1650841002432396986L;

    public PromiseException(Throwable cause) {
        super(cause);
    }

}
