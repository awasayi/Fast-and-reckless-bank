package revel8.service;

import revel8.dto.AmountRequest;
import revel8.dto.BalanceResponse;
import revel8.dto.CreateAccountRequest;
import revel8.dto.CreateAccountResponse;
import revel8.dto.OutgoingTransfersResponse;
import revel8.dto.TransferRequest;
import revel8.dto.TransferResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    CreateAccountResponse createAccount(CreateAccountRequest request);
    BalanceResponse deposit(UUID accountId, AmountRequest request);
    BalanceResponse withdraw(UUID accountId, AmountRequest request);
    TransferResponse transfer(TransferRequest request);
    OutgoingTransfersResponse getOutgoingTransfers(UUID accountId);
    List<CreateAccountResponse> getAllAccounts();
}
