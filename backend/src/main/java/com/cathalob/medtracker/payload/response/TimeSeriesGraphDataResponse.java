package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.GraphData;
import com.cathalob.medtracker.payload.response.generic.Response;
import com.cathalob.medtracker.payload.response.generic.ResponseInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TimeSeriesGraphDataResponse extends Response {
    private GraphData graphData;

    public TimeSeriesGraphDataResponse(ResponseInfo responseInfo) {
        super(responseInfo);
    }

    public TimeSeriesGraphDataResponse(ResponseInfo responseInfo, GraphData graphData) {
        super(responseInfo);
        this.graphData = graphData;
    }

    public static TimeSeriesGraphDataResponse Success(GraphData graphData) {
        return new TimeSeriesGraphDataResponse(ResponseInfo.Success(), graphData);
    }

    public static TimeSeriesGraphDataResponse Failure() {
        return new TimeSeriesGraphDataResponse(ResponseInfo.Failed());
    }
    public static TimeSeriesGraphDataResponse Failure(List<String> errors) {
        return new TimeSeriesGraphDataResponse(ResponseInfo.Failed(errors));
    }
}
