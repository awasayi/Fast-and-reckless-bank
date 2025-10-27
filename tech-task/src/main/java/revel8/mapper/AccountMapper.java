package revel8.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import revel8.dto.BalanceResponse;
import revel8.dto.CreateAccountResponse;
import revel8.dto.TransferResponse;
import revel8.model.Account;
import revel8.model.TransferRecord;

@Mapper
public interface AccountMapper {

    AccountMapper ACCOUNT_MAPPER = Mappers.getMapper(AccountMapper.class);

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "balanceInCents", target = "balance", qualifiedByName = "formatAmount")
    CreateAccountResponse toCreateAccountResponse(Account account);

    default BalanceResponse toBalanceResponse(String balance) {
        return new BalanceResponse(balance);
    }

    @Named("formatAmount")
    default String formatAmount(long cents) {
        return String.format("%d.%02d", cents / 100, Math.abs(cents % 100));
    }
}

