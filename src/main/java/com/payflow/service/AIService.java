package com.payflow.service;

import com.payflow.enums.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AIService {

    private final WebClient webClient;

    public AIService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com").build();
    }

    public Category categorize(String description) {

        try {
            String prompt = "Classify this transaction into one category: FOOD, TRANSPORT, SHOPPING, INCOME, OTHER. Only return one word. Text: "
                    + description;

            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"))
                    .bodyValue(Map.of(
                            "model", "gpt-4o-mini",
                            "messages", List.of(Map.of("role", "user", "content", prompt))
                    ))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractCategory(response);

        } catch (WebClientResponseException.TooManyRequests e) {
            log.warn("Rate limit / quota exceeded → fallback used");
            return fallback(description);
        }
        catch (Exception e) {
            log.error("AI ERROR: ", e);
            return fallback(description);
        }
    }

    private Category extractCategory(String response) {
        if (response.contains("FOOD")) return Category.FOOD;
        if (response.contains("TRANSPORT")) return Category.TRAVEL;
        if (response.contains("SHOPPING")) return Category.SHOPPING;
        if (response.contains("INCOME")) return Category.OTHER;

        return Category.OTHER;
    }

    private Category fallback(String description) {
        description = description.toLowerCase();

        if (description.contains("zomato") || description.contains("swiggy")) {
            return Category.FOOD;
        }

        if (description.contains("uber") || description.contains("ola")) {
            return Category.TRAVEL;
        }

        if (description.contains("amazon") || description.contains("flipkart")) {
            return Category.SHOPPING;
        }

        if (description.contains("electricity") || description.contains("bill")) {
            return Category.BILLS;
        }

        return Category.OTHER;
    }
}