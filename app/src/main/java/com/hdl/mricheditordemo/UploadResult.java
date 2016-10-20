package com.hdl.mricheditordemo;

import java.io.Serializable;

/**
 * Created by HDL on 2016/10/15.
 */

public class UploadResult implements Serializable {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UploadResult{" +
                "message='" + message + '\'' +
                '}';
    }
}
