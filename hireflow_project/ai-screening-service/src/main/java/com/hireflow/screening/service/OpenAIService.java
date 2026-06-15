package com.hireflow.screening.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hireflow.screening.dto.ScreeningResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * AI Screening — powered by Groq (free tier, llama-3.3-70b-versatile).
 * Same OpenAI-compatible API format. Get free key at: https://console.groq.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.model:llama-3.3-70b-versatile}")
    private String model;

    private final ObjectMapper objectMapper;

    // Plain (non-load-balanced) client: Groq is an EXTERNAL host, so it must NOT
    // go through the @LoadBalanced WebClient.Builder (which would try to resolve
    // api.groq.com via Eureka and fail with 503 "from UNKNOWN").
    private final WebClient externalClient = WebClient.builder().build();

    private static final String SYSTEM_PROMPT =
            "You are an expert HR recruiter. Respond ONLY with valid JSON, no markdown, no explanation.";

    public ScreeningResultDto screenResume(String jobDescription, String resumeText) {
        log.info("Calling Groq API [model={}]", model);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", buildPrompt(jobDescription, resumeText))
                ),
                "temperature", 0.2,
                "max_tokens", 1000,
                "response_format", Map.of("type", "json_object")
        );

        try {
            Map response = externalClient
                    .post()
                    .uri(groqApiUrl)
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String content = extractContent(response);
            ScreeningResultDto result = objectMapper.readValue(content, ScreeningResultDto.class);
            log.info("Groq screening complete — score: {}/100", result.getScore());
            return result;

        } catch (WebClientResponseException e) {
            log.error("Groq API error [{}]: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Groq API failed: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("AI screening failed: {}", e.getMessage());
            throw new RuntimeException("AI screening failed", e);
        }
    }

    private String buildPrompt(String jd, String resume) {
        return "Job Description:\n" + jd + "\n\n---\n\nResume:\n" + resume +
               "\n\n---\n\nEvaluate how well this resume matches the job. Respond ONLY in JSON with this exact shape:\n" +
               "{\"score\": <integer 0-100 overall match, where 0 is no match and 100 is a perfect match>, " +
               "\"strengths\": [string], \"weaknesses\": [string], " +
               "\"skillsMatched\": [string], \"skillsMissing\": [string], " +
               "\"recommendation\": string}\n" +
               "The score MUST be a whole number between 0 and 100 (not a fraction, not out of 10).";
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        var choices = (java.util.List<Map<String, Object>>) response.get("choices");
        var message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
