package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;

import java.util.List;

public interface TransactionService {

    boolean commitTransaction(TransactionInputDto transactionInputDto);

    List<Transaction> findOverLimitTransactions();

}
