package com.hireflow.notification.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireflow.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "application-submitted", groupId = "notification-group")
    public void onApplicationSubmitted(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String candidateEmail = node.get("candidateEmail").asText();
            Long jobId = node.get("jobId").asLong();
            log.info("Notifying candidate {} of application receipt for jobId={}", candidateEmail, jobId);
            emailService.sendApplicationReceived(candidateEmail, "Job #" + jobId);
        } catch (Exception e) {
            log.error("Error processing application-submitted notification: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "screening-completed", groupId = "notification-group")
    public void onScreeningCompleted(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            int score = node.get("score").asInt();
            String candidateEmail = node.get("candidateEmail").asText();
            String jobTitle = node.get("jobTitle").asText();
            // In production, fetch recruiter email from User Service
            String recruiterEmail = "recruiter@hireflow.com";
            log.info("Notifying recruiter of screening completion: score={}", score);
            emailService.sendScreeningComplete(recruiterEmail, jobTitle, candidateEmail, score);
        } catch (Exception e) {
            log.error("Error processing screening-completed notification: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "application-status-changed", groupId = "notification-group")
    public void onStatusChanged(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String candidateEmail = node.get("candidateEmail").asText();
            String status = node.get("status").asText();
            String jobTitle = node.get("jobTitle").asText();
            log.info("Notifying candidate {} of status change: {}", candidateEmail, status);
            emailService.sendStatusUpdate(candidateEmail, jobTitle, status);
        } catch (Exception e) {
            log.error("Error processing status-changed notification: {}", e.getMessage());
        }
    }
}
