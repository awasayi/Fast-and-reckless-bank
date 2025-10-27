package revel8.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import revel8.dto.*;
import revel8.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccountResponse createAccount(@Valid @RequestBody CreateAccountRequest req) {
        return accountService.createAccount(req);
    }

    @PostMapping("/accounts/{id}/deposit")
    public BalanceResponse deposit(@PathVariable UUID id, @Valid @RequestBody AmountRequest req) {
        return accountService.deposit(id, req);
    }

    @PostMapping("/accounts/{id}/withdraw")
    public BalanceResponse withdraw(@PathVariable UUID id, @Valid @RequestBody AmountRequest req) {
        return accountService.withdraw(id, req);
    }

    @PostMapping("/transfers")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest req) {
        return accountService.transfer(req);
    }

    @GetMapping("/accounts/{id}/outgoing-transfers")
    public OutgoingTransfersResponse outgoing(@PathVariable UUID id) {
        return accountService.getOutgoingTransfers(id);
    }

    @GetMapping("/accounts")
    public List<CreateAccountResponse> getAllAccounts() {
        return accountService.getAllAccounts();
    }
}

