package com.weswu.gcpauth.base;

/**
 * @author weswu
 * @date 2021/05/23
 */
public class GAException extends Exception {
    private static final long serialVersionUID = 1L;
    private String status;
    private String error;
    public GAException(String status, String error) {
        super(error);
        this.status = status;
        this.error = error;
    }
    public GAException(String status, String error, Throwable ex) {
        super(error, ex);
        this.status = status;
        this.error = error;
    }
    public GAException(Throwable exception) {
        super(exception);
    }
    public GAException(IMessageEnum msg) {
        super(msg.getError());
        this.error = msg.getError();
        this.status = msg.getStatus();
    }

    public GAException(IMessageEnum msg, Throwable ex) {
        super(msg.getError(), ex);
        this.error = msg.getError();
        this.status = msg.getStatus();
    }

    public GAException(IMessageEnum msg, String err) {
        super(msg.getError());
        this.error = String.format("%s, %s", msg.getError(), err);
        this.status = msg.getStatus();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}