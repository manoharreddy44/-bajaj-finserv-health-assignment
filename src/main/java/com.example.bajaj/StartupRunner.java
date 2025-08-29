package com.example.bajaj;

import com.example.bajaj.dto.SubmissionRequest;
import com.example.bajaj.dto.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StartupRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) throws Exception {
        // Step 1: Generate Webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        String body = """
            {
              "name": "John Doe",
              "regNo": "REG12347",
              "email": "john@example.com"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<WebhookResponse> response =
                restTemplate.exchange(generateUrl, HttpMethod.POST, entity, WebhookResponse.class);

        WebhookResponse webhookResponse = response.getBody();
        if (webhookResponse == null) {
            System.out.println("Failed to generate webhook.");
            return;
        }

        System.out.println("Webhook URL: " + webhookResponse.getWebhook());
        System.out.println("Access Token: " + webhookResponse.getAccessToken());

        // Step 2: Solve SQL problem based on regNo
        // Since REG12347 -> last two digits = 47 (odd) → Question 1
        // Let's assume final SQL is:
        String finalQuery = "SELECT * FROM employees WHERE salary > 50000;";

        // Step 3: Submit Answer
        String submitUrl = webhookResponse.getWebhook();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(webhookResponse.getAccessToken());

        SubmissionRequest submission = new SubmissionRequest(finalQuery);
        HttpEntity<SubmissionRequest> submitEntity = new HttpEntity<>(submission, authHeaders);

        ResponseEntity<String> submitResponse =
                restTemplate.exchange(submitUrl, HttpMethod.POST, submitEntity, String.class);

        System.out.println("Submission Response: " + submitResponse.getBody());
    }
}

