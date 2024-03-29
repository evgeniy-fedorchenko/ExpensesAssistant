package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;


import java.util.List;

public interface TransactionService {

    boolean commitTransaction(Transaction transaction);

    boolean setLimit(Limit newLimit);

    List<Transaction> findOverLimitTransactions();

}
