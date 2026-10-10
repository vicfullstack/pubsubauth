package com.weswu.gcpauth.entity;

import java.io.Serializable;

/**
 * @author weswu
 * @date 2021/5/23
 */
public class TokenRes implements Serializable {
    private static final long serialVersionUID = 1L;
    private String access_token;
    private int expires_in; // seconds
    private String token_type;

    public TokenRes() {
    }

    public TokenRes(String access_token, int expires_in, String token_type) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
