package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {


    @Override
    public boolean commitTransaction(Transaction transaction) {
        return false;
    }

    @Override
    public boolean setLimit(Limit newLimit) {
        return false;
    }

    @Override
    public List<Transaction> findOverLimitTransactions() {
        return null;
    }
}
