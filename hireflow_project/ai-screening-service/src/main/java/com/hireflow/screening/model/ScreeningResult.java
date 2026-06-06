package com.hireflow.screening.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "screening_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningResult {

    @Id
    private String id;

    private Long applicationId;
    private Long jobId;
    private String candidateEmail;

    private int score;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> skillsMatched;
    private List<String> skillsMissing;
    private String recommendation;

    private LocalDateTime screenedAt;
}
