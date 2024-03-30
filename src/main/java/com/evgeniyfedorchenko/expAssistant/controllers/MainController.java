package com.evgeniyfedorchenko.expAssistant.controllers;

import com.evgeniyfedorchenko.expAssistant.dto.TransactionInputDto;
import com.evgeniyfedorchenko.expAssistant.dto.TransactionOverLimitDto;
import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.services.LimitService;
import com.evgeniyfedorchenko.expAssistant.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "User's available actions")
@Validated
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
    public boolean commitTransaction(@RequestBody @Valid TransactionInputDto transactionInputDto) {
        return transactionService.commitTransaction(transactionInputDto);
    }

    @PutMapping(path = "/limit")
    @Operation(summary = "Set new limit for future transactions")
    public void setNewLimit(@RequestParam Category forCategory,
                            @RequestParam @Positive(message = "Limit value must be positive") BigDecimal value) {
        limitService.createNewCustomLimit(forCategory, value);
    }

    @GetMapping
    @Operation(summary = "Get all transactions that have exceeded specific limit")
    public List<TransactionOverLimitDto> getOverLimitTransactions() {
        return transactionService.findOverLimitTransactions();
    }
}
