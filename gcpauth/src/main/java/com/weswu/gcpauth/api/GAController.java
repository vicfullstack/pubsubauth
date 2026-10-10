package com.weswu.gcpauth.api;

import com.google.auth.oauth2.AccessToken;
import com.weswu.gcpauth.base.GAException;
import com.weswu.gcpauth.entity.TokenReq;
import com.weswu.gcpauth.base.ResFactory;
import com.weswu.gcpauth.base.Result;
import com.weswu.gcpauth.service.SATokenService;
import com.weswu.gcpauth.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author weswu
 * @date 2021/05/23
 */
@RestController
@RequestMapping("/")
public class GAController {
    private static final Logger log = LoggerFactory.getLogger(GAController.class);
    private static SATokenService saTokenService = new SATokenService();

    /* 获取google api的 access token */
    @SuppressWarnings("rawtypes")
    @PostMapping(value = { "/v1/token" })
    public ResponseEntity<Result> get_access_token(
            @RequestHeader(value = "Authorization", required = false) String gcpAuthToken,
            @RequestBody TokenReq tokenReq){
        Result loggedRes = null;
        ResponseEntity<Result> sentRes = null;
        try{
            AccessToken tokenRes = saTokenService.getAccessToken(gcpAuthToken, tokenReq);
            loggedRes = ResFactory.wrapper(tokenRes);
            log.info(loggedRes.toString() + ", GUID:" + (tokenReq != null ? tokenReq.getGuid() : "") + ".");
            // return succeed
            sentRes = new ResponseEntity<>(loggedRes, resolveHttpStatus(loggedRes.getStatus()));
            return sentRes;
        } catch (Exception e){
            //return error
            GAException gaException = Utils.format_exception(e);
            loggedRes = ResFactory.wrapper(gaException, ", GUID:" + (tokenReq != null ? tokenReq.getGuid() : "") + ".");
            log.error(loggedRes.toString());
            sentRes = new ResponseEntity<>(loggedRes, resolveHttpStatus(loggedRes.getStatus()));
            return sentRes;
        }
    }

    private HttpStatus resolveHttpStatus(String statusStr) {
        try {
            HttpStatus status = HttpStatus.resolve(Integer.parseInt(statusStr));
            return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception ignored) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
