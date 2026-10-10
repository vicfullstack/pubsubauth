package com.weswu.gcpauth.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.util.ObjectUtils;
import java.text.MessageFormat;
import java.io.Serializable;

/**
 * @author weswu
 * @date 2021/05/23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<M> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private String error;
    private M data;

    public Result() {
    }

    public Result(M data, String status, String error) {
        if (ObjectUtils.isEmpty(status)) {
            status = ResStatus.UNKOWN_ERROR.getStatus();
        }
        this.data = data;
        this.status = status;
        this.error = error;
    }

    public Result(GAException ex) {
        String exStatus = ex.getStatus();
        if (ObjectUtils.isEmpty(exStatus)) {
            exStatus = ResStatus.UNKOWN_ERROR.getStatus();
        }
        this.status = exStatus;
        this.error = ex.getError();
    }
    public Result(IllegalAccessException ex) {
        this.status = ResStatus.FORBIDDEN.getStatus();
        this.error = ex.getLocalizedMessage();
    }

    public Result(M data, IMessageEnum msgDefined) {
        if (data != null) {
            this.data = data;
        }
        this.status = msgDefined.getStatus();
        this.error = msgDefined.getError();
    }

    public Result(IMessageEnum msgDefined) {
        this(msgDefined, null);
    }

    public Result(IMessageEnum msgDefined, String info) {
        this.status = msgDefined.getStatus();
        this.error = MessageFormat.format(msgDefined.getError(), info);
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

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }

    @Override
    public String toString() {
        try {
            return Json.encode(this);
        } catch (Exception e) {
            return "{\"status\":\"" + status + "\",\"error\":\"" + error + "\"}";
        }
    }
}
