package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;
import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.exceptions.InvalidControllerParameterException;
import com.evgeniyfedorchenko.expAssistant.mappers.TransactionOverLimitMapper;
import com.evgeniyfedorchenko.expAssistant.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName.*;

/**
 * Класс для работы с транзакциями: регистрации новых, а также получения тех, что превысили лимит
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    public static final Long DEFAULT_ACCOUNT_FROM_VALUE = 9_265_749_302L;

    @Value("${local-zoned-id}")
    private String localZonedId;

    private final TransactionRepository transactionRepository;
    private final LimitService limitService;
    private final ExchangeRateService exchangeRateService;
    private final TransactionOverLimitMapper trscnOLMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  LimitService limitService,
                                  ExchangeRateService exchangeRateService,
                                  TransactionOverLimitMapper trscnOLMapper) {
        this.transactionRepository = transactionRepository;
        this.limitService = limitService;
        this.exchangeRateService = exchangeRateService;
        this.trscnOLMapper = trscnOLMapper;
    }

    /**
     * Метод для регистрации новой транзакции в базе данных. Присваивается последний актуальный лимит (или создается новый, если отсутствует).
     * Лимит будет уведомлен о новой транзакции и обновлен
     * @param inputDto объект на базе которого будет построен новый объект Transaction
     * @throws InvalidControllerParameterException выбрасывается, если значение поля accountFrom невозможно привести к типу long
     */
    @Override
    public void commitTransaction(TransactionInputDto inputDto) {

        Transaction newTransaction = new Transaction();

        newTransaction.setAccountTo(Long.parseLong(inputDto.getAccountTo()));
        newTransaction.setCategory(inputDto.getExpenseCategory());
        newTransaction.setDateTime(inputDto.getDateTime());
        newTransaction.setSum(BigDecimal.valueOf(inputDto.getSum()));
        newTransaction.setCurrency(inputDto.getCurrency());

        long accountFrom = 0;
        try {
            accountFrom = inputDto.getAccountFrom() == null
                    ? DEFAULT_ACCOUNT_FROM_VALUE
                    : Long.parseLong(inputDto.getAccountFrom());
        } catch (NumberFormatException e) {
            throw new InvalidControllerParameterException("Invalid param \"accountFrom\": " + accountFrom, e);
        }
        newTransaction.setAccountFrom(accountFrom);

        Limit actualLimit = getActualLimit(inputDto.getExpenseCategory());
        newTransaction.setLimit(actualLimit);

        newTransaction.setLimitExceeded(isLimitExceeded(inputDto, actualLimit));

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        limitService.addTransaction(savedTransaction, actualLimit);
    }

    /**
     * Метод для получения транзакций превысивших установленные лимиты
     * @return List объектов TransactionOverLimitDto, содержащих данные о транзакции, а также время размер и валюту
     *         установленного для них лимита, который был превышен
     */
    @Override
    public List<TransactionOverLimitDto> findOverLimitTransactions() {

        List<Object[]> overLimitTransactions = transactionRepository.findOverLimitTransactions();
        return overLimitTransactions.stream()
                .map(trscnOLMapper::toDto)
                .toList();

    }

    private boolean isLimitExceeded(TransactionInputDto inputDto, Limit actualLimit) {
        BigDecimal totalSum = countTotalSum(inputDto, actualLimit);
        BigDecimal limitValue = actualLimit.getUsdValue();

        return totalSum.compareTo(limitValue) > 0;
    }


    private BigDecimal countTotalSum(TransactionInputDto inputDto, Limit actualLimit) {
        return actualLimit.getTransactions().stream()
                .map(Transaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(convertSum(inputDto.getSum(), inputDto.getCurrency()));
    }

    private Limit getActualLimit(Category category) {

        Optional<Limit> lastLimit = limitService.findLastLimit();

        if (lastLimit.isEmpty()) {
            return limitService.createNewDefaultLimit(category);
        } else {

            /* Создаем новый лимит только тогда, когда до последнего лимита прошло меньше времени, чем до начала месяца,
               если прошло столько же, значит это дефолтный лимит, созданный в начале этого месяца,
               если прошло больше - значит это пользовательский лимит */
            long toStartOfMonth = ChronoZonedDateTime.from(limitService.getStartOfMonth())
                    .toEpochSecond();

            long toLastLimitStarts = lastLimit.get()
                    .getDatetimeStarts()
                    .toInstant()
                    .atZone(ZoneId.of(localZonedId))
                    .toEpochSecond();

            if (toStartOfMonth - toLastLimitStarts < 0) {
                return limitService.createNewDefaultLimit(category);
            } else {
                return lastLimit.get();
            }
        }
    }

    private BigDecimal convertSum(Double sum, CurrencyShortName currency) {
        return switch (currency) {
            case USD -> new BigDecimal(sum);
            case RUB -> exchangeRateService.getExchangeRate(RUB, USD)
                    .multiply(BigDecimal.valueOf(sum));
            case KZT -> exchangeRateService.getExchangeRate(KZT, USD)
                    .multiply(BigDecimal.valueOf(sum));
        };
    }
}
