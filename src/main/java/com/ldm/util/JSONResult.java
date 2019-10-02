package com.ldm.util;

import lombok.Data;

/**
 * @author ldm
 * @create 2019/9/17 23:39
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
    }

    public JSONResult(int code, Object result, boolean success) {
        this.code = code;
        this.result = result;
        this.success = success;
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

    public JSONResult(Object result, boolean success, Object error) {
        this.result = result;
        this.success = success;
        this.error = error;
    }

    public JSONResult(Object error, int code) {
        this.code = code;
        this.success = Boolean.FALSE;
        this.error = error;
    }

}
