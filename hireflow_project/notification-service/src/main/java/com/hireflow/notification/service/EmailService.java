package com.hireflow.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendApplicationReceived(String candidateEmail, String jobTitle) {
        send(candidateEmail,
             "Application Received — " + jobTitle,
             "Hi,\n\nYour application for \"" + jobTitle + "\" has been received.\n" +
             "We will review your profile and get back to you soon.\n\nBest,\nHireFlow Team");
    }

    public void sendScreeningComplete(String recruiterEmail, String jobTitle, String candidateEmail, int score) {
        send(recruiterEmail,
             "New Candidate Screened — " + jobTitle,
             "A candidate (" + candidateEmail + ") has been screened for \"" + jobTitle + "\"\n" +
             "AI Score: " + score + "/100\n\nLogin to HireFlow to view the full analysis.");
    }

    public void sendStatusUpdate(String candidateEmail, String jobTitle, String status) {
        String subject = "Application Update — " + jobTitle;
        String body = switch (status) {
            case "SHORTLISTED" -> "Congratulations! You have been shortlisted for \"" + jobTitle + "\".";
            case "INTERVIEW"   -> "Great news! You have been invited for an interview for \"" + jobTitle + "\".";
            case "HIRED"       -> "Congratulations! You have been hired for \"" + jobTitle + "\"!";
            case "REJECTED"    -> "Thank you for applying. After careful review, we will not be moving forward with your application for \"" + jobTitle + "\".";
            default            -> "Your application status for \"" + jobTitle + "\" has been updated to: " + status;
        };
        send(candidateEmail, subject, body + "\n\nBest,\nHireFlow Team");
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@hireflow.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} | Subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
