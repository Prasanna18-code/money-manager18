package in.pras.moneymanage.service;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${FROM_EMAIL}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> requestBody = Map.of(
                    "sender", Map.of("email", fromEmail),
                    "to", new Object[]{Map.of("email", to)},
                    "subject", subject,
                    "htmlContent", htmlContent
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            System.out.println("Email sent successfully: " + response.getBody());

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }
}
