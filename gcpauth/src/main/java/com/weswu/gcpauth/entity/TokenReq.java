package com.weswu.gcpauth.entity;

/**
 * @author weswu
 * @date 2021/5/23
 */
public class TokenReq {
    private String guid;
    private String scope;

    public TokenReq() {
    }

    public TokenReq(String guid, String scope) {
        this.guid = guid;
        this.scope = scope;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
