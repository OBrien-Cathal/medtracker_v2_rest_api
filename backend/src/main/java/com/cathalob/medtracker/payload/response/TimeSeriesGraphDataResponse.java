package com.cathalob.medtracker.payload.response;

import com.cathalob.medtracker.payload.data.GraphData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TimeSeriesGraphDataResponse extends Response {
    private GraphData graphData;

    public TimeSeriesGraphDataResponse(boolean successful, GraphData graphData) {
        super(successful);
        this.graphData = graphData;
    }
    public TimeSeriesGraphDataResponse(boolean successful) {
        super(successful);
        this.graphData = new GraphData();
    }
    public static TimeSeriesGraphDataResponse Success(GraphData graphData){
        return new TimeSeriesGraphDataResponse(true, graphData);
    }
    public static TimeSeriesGraphDataResponse Failure(){
        return new TimeSeriesGraphDataResponse(false);
    }
}
