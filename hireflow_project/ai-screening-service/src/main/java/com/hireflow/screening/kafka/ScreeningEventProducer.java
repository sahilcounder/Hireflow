package com.hireflow.screening.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScreeningEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishScreeningCompleted(Long applicationId, int score,
                                           String candidateEmail, String recommendation,
                                           String jobTitle) {
        try {
            String event = objectMapper.writeValueAsString(Map.of(
                    "applicationId", applicationId,
                    "score", score,
                    "candidateEmail", candidateEmail,
                    "recommendation", recommendation,
                    "jobTitle", jobTitle
            ));
            kafkaTemplate.send("screening-completed", String.valueOf(applicationId), event);
            log.info("Published screening-completed for applicationId={} score={}", applicationId, score);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish screening-completed", e);
        }
    }
}
