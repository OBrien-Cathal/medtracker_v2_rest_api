package com.cathalob.medtracker.payload.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class AbstractResponse {

    @Builder.Default
    private boolean successful = false;
    private String message;
    @Builder.Default
    private List<String> errors = new ArrayList<>();

}
