package com.hireflow.screening.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScreeningResultDto {
    private int score;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> skillsMatched;
    private List<String> skillsMissing;
    private String recommendation;
}
