package com.evgeniyfedorchenko.expAssistant.controllers;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.services.LimitService;
import com.evgeniyfedorchenko.expAssistant.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "User's available actions")
@RestController
@RequestMapping(path = "/user-actions")
public class MainController {

    private final TransactionService transactionService;
    private final LimitService limitService;

    public MainController(TransactionService transactionService,
                          LimitService limitService) {
        this.transactionService = transactionService;
        this.limitService = limitService;
    }

    @PostMapping
    @Operation(summary = "Register the new transaction")
    public boolean commitTransaction(TransactionInputDto transactionInputDto) {
        return transactionService.commitTransaction(transactionInputDto);
    }

    @PutMapping(path = "/limit")
    @Operation(summary = "Set new limit for future transactions")
    public void setNewLimit(Category forCategory, BigDecimal value) {
        limitService.createNewCustomLimit(forCategory, value);
    }

    @GetMapping
    @Operation(summary = "Get all transactions that have exceeded specific limit")
    public List<Transaction> getOverLimitTransactions() {
        return transactionService.findOverLimitTransactions();
    }
}
