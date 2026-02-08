package com.payflow.controller;


import com.payflow.dto.WalletCreditRequest;
import com.payflow.dto.WalletDebitRequest;
import com.payflow.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<?>credit(@PathVariable Long id, @RequestHeader("Idempotence-Key") String key,@Valid @RequestBody WalletCreditRequest req)
    {
        walletService.credit(id,req.getAmount(),key);
        return ResponseEntity.ok(Map.of("status","credited"));
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<?>debit(@PathVariable Long id,@RequestHeader("Idempotence-Key") String key ,@Valid @RequestBody WalletDebitRequest req)
    {
        walletService.debit(id,req.getAmount(),key);
        return ResponseEntity.ok(Map.of("status","debited"));
    }

}
