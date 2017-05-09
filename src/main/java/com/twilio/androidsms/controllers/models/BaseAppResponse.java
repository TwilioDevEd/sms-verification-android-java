package com.twilio.androidsms.controllers.models;

public class BaseAppResponse {
    private boolean success;
    private String phone;
    private String msg;
    private Integer time;

    public BaseAppResponse(boolean success, String phone, String message) {
        this.success = success;
        this.phone = phone;
        this.msg = message;
    }

    public BaseAppResponse(boolean success, Integer time) {

        this.success = success;
        this.time = time;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
