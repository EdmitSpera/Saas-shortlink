package com.nageoffer.shortlink.project.common.enums;


import com.nageoffer.shortlink.project.common.convention.errorcode.IErrorCode;

public enum LinkErrorCodeEnum implements IErrorCode {
    LINK_CREATE_ALREADY("B000206","源链接已创建短链服务");

    private final String code;

    private final String message;

    LinkErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
