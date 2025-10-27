package revel8.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import revel8.dto.AmountRequest;
import revel8.dto.CreateAccountRequest;
import revel8.dto.CreateAccountResponse;
import revel8.dto.TransferRequest;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testCreateAccountSuccess() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "100.00");
        
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.balance").value("100.00"));
    }
    
    @Test
    void testCreateAccountWithNegativeDeposit() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "-10.00");
        
        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testDepositToNonExistentAccount() throws Exception {
        UUID fakeId = UUID.randomUUID();
        AmountRequest request = new AmountRequest("50.00");
        
        mockMvc.perform(post("/api/accounts/" + fakeId + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDepositZeroAmount() throws Exception {
        CreateAccountRequest createReq = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "100.00");
        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andReturn();
        
        CreateAccountResponse account = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );
        
        AmountRequest depositReq = new AmountRequest("0.00");
        mockMvc.perform(post("/api/accounts/" + account.accountId() + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositReq)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testWithdrawSuccess() throws Exception {
        CreateAccountRequest createReq = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "100.00");
        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andReturn();
        
        CreateAccountResponse account = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );

        AmountRequest withdrawReq = new AmountRequest("30.00");
        mockMvc.perform(post("/api/accounts/" + account.accountId() + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("70.00"));
    }
    
    @Test
    void testWithdrawInsufficientFunds() throws Exception {
        CreateAccountRequest createReq = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "50.00");
        MvcResult createResult = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
                .andReturn();
        
        CreateAccountResponse account = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );
        
        AmountRequest withdrawReq = new AmountRequest("100.00");
        mockMvc.perform(post("/api/accounts/" + account.accountId() + "/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawReq)))
                .andExpect(status().isConflict());
    }
    
    @Test
    void testTransferSuccess() throws Exception {
        CreateAccountRequest createReq1 = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "100.00");
        MvcResult result1 = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq1)))
                .andReturn();
        CreateAccountResponse acc1 = objectMapper.readValue(
            result1.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );
        
        CreateAccountRequest createReq2 = new CreateAccountRequest("Test User 2", "test2@example.com", 25, "Test City 2", "50.00");
        MvcResult result2 = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq2)))
                .andReturn();
        CreateAccountResponse acc2 = objectMapper.readValue(
            result2.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );

        TransferRequest transferReq = new TransferRequest(
            acc1.accountId(),
            acc2.accountId(),
            "30.00"
        );
        
        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").exists())
                .andExpect(jsonPath("$.toAccountId").value(acc2.accountId().toString()))
                .andExpect(jsonPath("$.amount").value("30.00"))
                .andExpect(jsonPath("$.resultingBalance").value("70.00"));
    }
    
    @Test
    void testGetOutgoingTransfers() throws Exception {
        CreateAccountRequest createReq1 = new CreateAccountRequest("Test User", "test@example.com", 30, "Test City", "100.00");
        MvcResult result1 = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq1)))
                .andReturn();
        CreateAccountResponse acc1 = objectMapper.readValue(
            result1.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );
        
        CreateAccountRequest createReq2 = new CreateAccountRequest("Test User 2", "test2@example.com", 25, "Test City 2", "50.00");
        MvcResult result2 = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq2)))
                .andReturn();
        CreateAccountResponse acc2 = objectMapper.readValue(
            result2.getResponse().getContentAsString(),
            CreateAccountResponse.class
        );

        TransferRequest transferReq = new TransferRequest(
            acc1.accountId(),
            acc2.accountId(),
            "20.00"
        );
        mockMvc.perform(post("/api/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferReq)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/accounts/" + acc1.accountId() + "/outgoing-transfers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transfers").isArray())
                .andExpect(jsonPath("$.transfers", hasSize(1)))
                .andExpect(jsonPath("$.transfers[0].amount").value("20.00"));
    }

}

