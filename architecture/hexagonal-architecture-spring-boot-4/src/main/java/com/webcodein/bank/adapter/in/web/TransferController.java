package com.webcodein.bank.adapter.in.web;

import com.webcodein.bank.domain.model.AccountId;
import com.webcodein.bank.domain.model.InsufficientFundsException;
import com.webcodein.bank.domain.port.in.TransferMoneyUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TransferController {

    private final TransferMoneyUseCase transferMoneyUseCase;

    public TransferController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @PostMapping("/api/transfers")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        transferMoneyUseCase.transfer(
            new AccountId(request.source()),
            new AccountId(request.target()),
            request.amount()
        );
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Transfer completed successfully"));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", ex.getMessage()));
    }
}
