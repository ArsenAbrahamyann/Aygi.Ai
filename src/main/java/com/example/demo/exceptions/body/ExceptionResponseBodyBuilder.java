package com.example.demo.exceptions.body;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionResponseBodyBuilder {
    public static Map<String, Object> createExceptionResponseBody(Throwable e) {
        var response = new LinkedHashMap<String, Object>();
        var message = e.getMessage();
        if (message == null || message.trim().equals("")) {
            message = e.getClass().getSimpleName();
        }
        response.put("message", message);
        return response;
    }

    public static Map<String, String> createExceptionResponseBody(String message) {
        var response = new LinkedHashMap<String, String>();
        response.put("message", message);
        return response;
    }
}
