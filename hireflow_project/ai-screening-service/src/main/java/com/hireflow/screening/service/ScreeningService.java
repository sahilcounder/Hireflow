package com.hireflow.screening.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireflow.screening.dto.ScreeningResultDto;
import com.hireflow.screening.kafka.ScreeningEventProducer;
import com.hireflow.screening.model.ScreeningResult;
import com.hireflow.screening.repository.ScreeningResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningService {

    private final ResumeParserService resumeParserService;
    private final OpenAIService openAIService;
    private final ScreeningResultRepository repository;
    private final ScreeningEventProducer eventProducer;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    /**
     * Full screening pipeline triggered by Kafka event.
     */
    public void screen(Long applicationId, Long jobId, String candidateEmail, String resumeFilePath) {
        try {
            log.info("Starting screening for applicationId={}", applicationId);

            // 1. Fetch job description from Job Service
            String jobJson = webClientBuilder.build()
                    .get()
                    .uri("lb://JOB-SERVICE/api/jobs/" + jobId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jobNode = objectMapper.readTree(jobJson);
            String jobDescription = jobNode.get("description").asText()
                    + " Skills: " + jobNode.get("skills").asText();

            // 2. Parse resume
            String resumeText = resumeParserService.extractText(resumeFilePath);

            // 3. Call Groq AI
            ScreeningResultDto dto = openAIService.screenResume(jobDescription, resumeText);

            // 4. Save to MongoDB
            ScreeningResult result = ScreeningResult.builder()
                    .applicationId(applicationId)
                    .jobId(jobId)
                    .candidateEmail(candidateEmail)
                    .score(dto.getScore())
                    .strengths(dto.getStrengths())
                    .weaknesses(dto.getWeaknesses())
                    .skillsMatched(dto.getSkillsMatched())
                    .skillsMissing(dto.getSkillsMissing())
                    .recommendation(dto.getRecommendation())
                    .screenedAt(LocalDateTime.now())
                    .build();
            repository.save(result);

            // 5. Publish screening-completed -> Application Service updates score, Notification emails recruiter
            eventProducer.publishScreeningCompleted(applicationId, dto.getScore(),
                    candidateEmail, dto.getRecommendation(), jobNode.get("title").asText());

            log.info("Screening complete: applicationId={} score={}", applicationId, dto.getScore());

        } catch (Exception e) {
            log.error("Screening failed for applicationId={}: {}", applicationId, e.getMessage(), e);
        }
    }

    public ScreeningResult getResult(Long applicationId) {
        return repository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("Screening result not found for applicationId: " + applicationId));
    }
}
