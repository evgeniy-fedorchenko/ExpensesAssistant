package com.evgeniyfedorchenko.expAssistant.mappers;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.services.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Маппер для работы с типом данных TransactionOverLimitDto
 */
@Component
public class TransactionOverLimitMapper {

    @Value("${local-zoned-id}")
    private String localZonedId;

    /**
     * Метод для преобразования массива значений транзакций и установленных для них лимитов
     * @param result массив значений полей транзакции и лимита, который будет преобразован
     * @return объект TransactionOverLimitDto, содержащий всю информацию о транзакциях и установленных для них лимитов,
     *         значения полей TransactionDateTime и LimitDatetimeStarts будут иметь тип данных ZonedDateTime с учетом региона,
     *         указанного в конфигурационном файле
     */
    public TransactionOverLimitDto toDto(Object[] result) {
        TransactionOverLimitDto dto = new TransactionOverLimitDto();

        dto.setAccountFrom(TransactionServiceImpl.DEFAULT_ACCOUNT_FROM_VALUE);
        dto.setAccountTo((Long) result[0]);

        dto.setTrscnCurrency(CurrencyShortName.values()[(short) result[1]]);
        dto.setTransactionSum(new BigDecimal(result[2].toString()));

        dto.setExpenseCategory(Category.values()[(short) result[3]]);
        dto.setTransactionDateTime(convertFromObject(result[4]));

        dto.setLimitValue(new BigDecimal(result[5].toString()));
        dto.setLimitDatetimeStarts(convertFromObject(result[6]));
        dto.setLimitCurrency(CurrencyShortName.USD);

        return dto;
    }

    private ZonedDateTime convertFromObject(Object object) {
        Instant dateTime = (Instant) object;
        ZoneId zonedId = ZoneId.of(localZonedId);

        return dateTime.atZone(zonedId);
    }
}
