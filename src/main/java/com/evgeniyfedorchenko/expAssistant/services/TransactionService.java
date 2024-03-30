package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;

import java.util.List;

public interface TransactionService {

    boolean commitTransaction(TransactionInputDto transactionInputDto);

    List<TransactionOverLimitDto> findOverLimitTransactions();

}
