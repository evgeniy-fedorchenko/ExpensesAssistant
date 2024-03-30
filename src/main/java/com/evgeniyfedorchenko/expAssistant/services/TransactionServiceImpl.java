package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Обрабатываем команды от пользователя
 * */

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final LimitService limitService;
    private final ExchangeRateService exchangeRateService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  LimitService limitService,
                                  ExchangeRateService exchangeRateService) {
        this.transactionRepository = transactionRepository;
        this.limitService = limitService;
        this.exchangeRateService = exchangeRateService;
    }

    /**
     * Внести в таблицу новую транзакцию
     * */
    @Override
    public boolean commitTransaction(TransactionInputDto inputDto) {

        Transaction newTransaction = new Transaction();

        newTransaction.setAccountTo(Long.parseLong(inputDto.getAccountTo()));
        newTransaction.setCurrency(inputDto.getCurrency());
        newTransaction.setSum(BigDecimal.valueOf(inputDto.getSum()));
        newTransaction.setCategory(inputDto.getExpenseCategory());
        newTransaction.setDateTime(inputDto.getDateTime());

        long accountFrom =  inputDto.getAccountFrom() == null
                ? DEFAULT_ACCOUNT_FROM_VALUE
                : Long.parseLong(inputDto.getAccountFrom());
        newTransaction.setAccountFrom(accountFrom);

        Limit actualLimit = getActualLimit(inputDto.getExpenseCategory());
        newTransaction.setLimit(actualLimit);

        newTransaction.setLimitExceeded(isLimitExceeded(inputDto, actualLimit));

        Transaction savedTransaction = transactionRepository.save(newTransaction);
        limitService.addTransaction(savedTransaction, actualLimit);
        return true;
    }

    /**
     * Получить транзакции сверх лимита
     */
    @Override
    public List<TransactionOverLimitDto> findOverLimitTransactions() {
        return transactionRepository.findOverLimitTransactions().stream()
                .peek(t -> t.setAccountFrom(DEFAULT_ACCOUNT_FROM_VALUE))
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
            long toStartOfMonth = ChronoZonedDateTime.from(limitService.getStartOfMonth()).toEpochSecond();
            long toLastLimitStarts = lastLimit.get().getDatetimeStarts().toInstant().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

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
