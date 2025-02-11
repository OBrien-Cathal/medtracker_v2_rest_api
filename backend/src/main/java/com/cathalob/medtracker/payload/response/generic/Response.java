package com.cathalob.medtracker.payload.response.generic;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Response {

    protected ResponseInfo responseInfo;

    public Response(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public Response() {
        responseInfo = new ResponseInfo();
    }

    public static Response Success() {
        return new Response(ResponseInfo.Success());
    }

    public static Response Failed() {
        return new Response(ResponseInfo.Failed());
    }
    public static Response Failed(List<String> errors) {
        return new Response(ResponseInfo.Failed(errors));
    }
}
