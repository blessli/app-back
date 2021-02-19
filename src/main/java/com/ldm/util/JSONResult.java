package com.ldm.util;

import lombok.Data;

/**
 * @title 统一返回格式
 * @description
 * @author lidongming
 * @updateTime 2020/4/14 23:06
 */
@Data
public class JSONResult {
    private int code;
    private boolean success;
    private Object result;
    private Object error;

    public JSONResult(Object result, boolean success) {
        this.result = result;
        this.success = success;
        if (!success){
            this.error="操作失败";
        }else {
            this.error="操作成功";
        }
    }

    public JSONResult(int code, Object result, boolean success) {
        this.code = code;
        this.result = result;
        this.success = success;
        if (!success){
            this.error="操作失败";
        }else {
            this.error="操作成功";
        }
    }

    public static JSONResult result(Object result, boolean success) {
        return new JSONResult(result,success);
    }

    public static JSONResult success(Object result) {
        return new JSONResult(200,result, true);
    }

    public static JSONResult success() {
        return success(null);
    }

    public static JSONResult fail(Object error) {
        JSONResult result = result(null, false);
        result.error = error;
        return result;
    }

    public static JSONResult deleted() {
        return new JSONResult(1314,null, true);
    }

}
