package com.cathalob.medtracker.payload.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphData {
    private List<String> columnNames;
    private List<List<Object>> dataRows;

}
