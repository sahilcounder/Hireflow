package com.hireflow.application.kafka;

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
public class ApplicationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishApplicationSubmitted(Long applicationId, Long jobId,
                                             Long candidateId, String candidateEmail,
                                             String resumeFilePath) {
        try {
            String event = objectMapper.writeValueAsString(Map.of(
                    "applicationId", applicationId,
                    "jobId", jobId,
                    "candidateId", candidateId,
                    "candidateEmail", candidateEmail,
                    "resumeFilePath", resumeFilePath
            ));
            kafkaTemplate.send("application-submitted", String.valueOf(applicationId), event);
            log.info("Published application-submitted event for applicationId={}", applicationId);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish application-submitted event", e);
        }
    }

    public void publishStatusChanged(Long applicationId, String candidateEmail,
                                      String status, String jobTitle) {
        try {
            String event = objectMapper.writeValueAsString(Map.of(
                    "applicationId", applicationId,
                    "candidateEmail", candidateEmail,
                    "status", status,
                    "jobTitle", jobTitle
            ));
            kafkaTemplate.send("application-status-changed", String.valueOf(applicationId), event);
            log.info("Published application-status-changed event for applicationId={}", applicationId);
        } catch (JsonProcessingException e) {
            log.error("Failed to publish status-changed event", e);
        }
    }
}
