package com.cathalob.medtracker.payload.response.generic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response2 {

    protected ResponseInfo responseInfo;

    public Response2(ResponseInfo responseInfo) {
        this.responseInfo = responseInfo;
    }

    public Response2() {
        responseInfo = new ResponseInfo();
    }
}
