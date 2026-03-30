package com.payflow.service;

import com.payflow.enums.Category;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionCategorizationService {

    public Category categorize(String description, BigDecimal amount) {

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