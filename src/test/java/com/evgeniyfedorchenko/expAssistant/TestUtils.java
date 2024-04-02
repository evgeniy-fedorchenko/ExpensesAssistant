package com.evgeniyfedorchenko.expAssistant;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class TestUtils {


    private static String localZonedId;

    public Transaction fromDto(TransactionInputDto inputDto) {
        Transaction transaction = new Transaction();

        transaction.setAccountTo(Long.parseLong(inputDto.getAccountTo()));
        transaction.setAccountFrom(Long.parseLong(inputDto.getAccountFrom()));
        transaction.setSum(BigDecimal.valueOf(inputDto.getSum()).stripTrailingZeros());
        transaction.setCurrency(inputDto.getCurrency());
        transaction.setCategory(inputDto.getExpenseCategory());
        transaction.setDateTime(inputDto.getDateTime());

        return transaction;
    }

    @Value("${local-zoned-id}")
    private void initZonedId(String value) {
        localZonedId = value;
    }

    public ZonedDateTime getStartOfMonth() {
        Instant now = Instant.now();
        return now.atZone(ZoneId.of(localZonedId))
                .withDayOfMonth(1)
                .with(LocalTime.MIN);
    }

    public Transaction fromOverLimitDto(TransactionOverLimitDto overLimitDto) {
        Transaction transaction = new Transaction();

        transaction.setAccountTo(overLimitDto.getAccountTo());
        transaction.setAccountFrom(overLimitDto.getAccountFrom());
        transaction.setSum(overLimitDto.getTransactionSum().stripTrailingZeros());
        transaction.setCurrency(overLimitDto.getTrscnCurrency());
        transaction.setCategory(overLimitDto.getExpenseCategory());
        transaction.setDateTime(overLimitDto.getTransactionDateTime());

        return transaction;
    }
}
