package com.payflow.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.entity.User;
import com.payflow.repository.UserRepository;
import com.payflow.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
class UserFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldExecuteUserFlow() throws Exception {

        // ---------------------------
        // 1️⃣ REGISTER USER
        // ---------------------------
        String email = "test" + System.currentTimeMillis() + "@payflow.com";

        String registerRequest = """
{
  "name": "Divyanshu",
  "email": "%s",
  "password": "123456"
}
""".formatted(email);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andDo(print())
                .andExpect(status().isOk());

        // ---------------------------
        // 2️⃣ LOGIN USER
        // ---------------------------
        String loginRequest = """
{
  "email": "%s",
  "password": "123456"
}
""".formatted(email);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andDo(print())   // 🔥 important for debugging
                .andExpect(status().isOk())
                .andReturn();

        // ---------------------------
        // 3️⃣ EXTRACT TOKEN SAFELY
        // ---------------------------
        String response = loginResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(response);

        String token;

        if (root.get("accessToken") != null) {
            token = root.get("accessToken").asText();
        } else if (root.get("token") != null) {
            token = root.get("token").asText();
        } else {
            throw new RuntimeException("Token not found in response: " + response);
        }

        // ---------------------------
        // 4️⃣ GET USER WALLET
        // ---------------------------
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long walletId = user.getWallet().getId();

        // GET BALANCE
        mockMvc.perform(get("/wallets/" + walletId + "/balance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

// CREDIT
        mockMvc.perform(post("/wallets/" + walletId + "/credit")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotence-Key", "key1234567")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
              "amount": 100
            }
        """))
                .andExpect(status().isOk());

// VERIFY BALANCE
        mockMvc.perform(get("/wallets/" + walletId + "/balance")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        // ---------------------------
// VERIFY DB STATE (IMPORTANT)
// ---------------------------
        BigDecimal balance = walletRepository.findById(walletId)
                .orElseThrow()
                .getBalance();

        assertEquals(0,balance.compareTo(BigDecimal.valueOf(100)));
    }

}