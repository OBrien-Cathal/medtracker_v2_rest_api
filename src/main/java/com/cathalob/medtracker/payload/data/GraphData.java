package com.cathalob.medtracker.payload.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
public class GraphData {
    private List<String> columnNames;
    private List<List<Object>> dataRows;

}
