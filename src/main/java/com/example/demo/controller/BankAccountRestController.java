package com.example.demo.controller;

import com.example.demo.enums.ShopType;
import com.example.demo.exception.APIException;
import com.example.demo.service.BankAccountService;
import com.example.demo.service.BankAccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BankAccountRestController {
    private final BankAccountService bankAccountService;
    private static final Logger logger = Logger.getLogger(BankAccountServiceImpl.class);

    @GetMapping("/payment/Shop/{bankAccountNumber}/{amount}")
    public void getPurchasePaymentFromShop(@PathVariable String bankAccountNumber,
                                           @PathVariable BigDecimal amount) {
        bankAccountService.getPurchasePayment(bankAccountNumber, amount, ShopType.OFFLINE);
    }

    @GetMapping("/payment/Online/{bankAccountNumber}/{amount}")
    public void getPurchasePaymentFromOnline(@PathVariable String bankAccountNumber,
                                             @PathVariable BigDecimal amount) {
        bankAccountService.getPurchasePayment(bankAccountNumber, amount, ShopType.ONLINE);
    }

    @GetMapping("/bankAccountOfEMoney/{bankAccountNumber}")
    public BigDecimal getBankAccountEMoneyAmount(@PathVariable String bankAccountNumber) {
        return bankAccountService.findBankAccountEMoneyAmount(bankAccountNumber);
    }

    @GetMapping("/money/{bankAccountNumber}")
    public BigDecimal getBankAccountMoneyAmount(@PathVariable String bankAccountNumber) {
        return bankAccountService.findBankAccountMoneyAmount(bankAccountNumber);
    }

    @ExceptionHandler
    private ResponseEntity<String> handleAPIException(APIException apiException) {
        logger.error(apiException.getMessage());
        return ResponseEntity.status(apiException.getStatus()).body(apiException.getMessage());
    }
}