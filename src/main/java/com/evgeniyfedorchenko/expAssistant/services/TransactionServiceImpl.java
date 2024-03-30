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

        Optional<Limit> lastLimit = limitService.findLastLimit();
        Limit actualLimit = lastLimit.orElseGet(() -> limitService.createNewDefaultLimit(inputDto.getExpenseCategory()));
        newTransaction.setLimit(actualLimit);

        transactionRepository.save(newTransaction);
        return true;
    }


    /**
     * Получить транзакции сверх лимита
     * */
    @Override
    public List<Transaction> findOverLimitTransactions() {
        return null;
    }
}
