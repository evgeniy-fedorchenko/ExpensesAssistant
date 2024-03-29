package com.evgeniyfedorchenko.expAssistant.controllers;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User's available actions")
@RestController
@RequestMapping(path = "/user-actions")
public class MainController {

    private final TransactionService transactionService;

    public MainController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(summary = "Register the new transaction")
    public boolean commitTransaction(Transaction transaction) {
        return transactionService.commitTransaction(transaction);
    }

    @PutMapping(path = "/limit")
    @Operation(summary = "Set new limit for future transactions")
    public boolean setNewLimit(Limit newLimit) {
        return transactionService.setLimit(newLimit);
    }

    @GetMapping
    @Operation(summary = "Get all transactions that have exceeded specific limit")
    public List<Transaction> getOverLimitTransactions() {
        return transactionService.findOverLimitTransactions();
    }
}
