package com.cathalob.medtracker.payload.request.graph;

import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GraphDataForDateRangeRequest {
    private LocalDate start;
    private LocalDate end;

}
