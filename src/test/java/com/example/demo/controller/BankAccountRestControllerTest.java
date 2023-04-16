package com.example.demo.controller;

import com.example.demo.enums.ShopType;
import com.example.demo.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Сервис для работы с контроллерами должен: ")
@ExtendWith(MockitoExtension.class)
class BankAccountRestControllerTest {
    @Mock
    private BankAccountService bankAccountService;

    private BankAccountRestController service;
    private final String correctBankAccountNumber = "123";
    private final BigDecimal amount = BigDecimal.ONE;

    @BeforeEach
    void setUp() {
        service = new BankAccountRestController(bankAccountService);
    }

    @Test
    @DisplayName("вызвать соответствующий метод для списания средств через магазин")
    void SuccessfullyGetPurchasePaymentFromShop() {
        service.getPurchasePaymentFromShop(correctBankAccountNumber, amount);

        verify(bankAccountService, times(1)).getPurchasePayment(correctBankAccountNumber, amount, ShopType.OFFLINE);
    }

    @Test
    @DisplayName("вызвать соответствующий метод для списания средств через онлайн магазин")
    void SuccessfullyGetPurchasePaymentFromOnline() {
        service.getPurchasePaymentFromOnline(correctBankAccountNumber, amount);

        verify(bankAccountService, times(1)).getPurchasePayment(correctBankAccountNumber, amount, ShopType.ONLINE);
    }

    @Test
    @DisplayName("найти кол-во бонусов на банковском счёте")
    void SuccessfullyGetBankAccountEMoneyAmount() {
        when(bankAccountService.findBankAccountEMoneyAmount(any())).thenReturn(amount);

        BigDecimal actualAmount = service.getBankAccountEMoneyAmount(correctBankAccountNumber);

        verify(bankAccountService, times(1)).findBankAccountEMoneyAmount(correctBankAccountNumber);
        assertEquals(actualAmount, amount);
    }

    @Test
    @DisplayName("найти кол-во денег на банковском счёте")
    void SuccessfullyGetBankAccountMoneyAmount() {
        when(bankAccountService.findBankAccountMoneyAmount(any())).thenReturn(amount);

        BigDecimal actualAmount = service.getBankAccountMoneyAmount(correctBankAccountNumber);

        verify(bankAccountService, times(1)).findBankAccountMoneyAmount(correctBankAccountNumber);
        assertEquals(actualAmount, amount);
    }
}