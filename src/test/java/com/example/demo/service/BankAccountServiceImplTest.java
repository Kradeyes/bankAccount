package com.example.demo.service;

import com.example.demo.domain.BankAccountEntity;
import com.example.demo.enums.PercentType;
import com.example.demo.enums.ShopType;
import com.example.demo.exception.APIException;
import com.example.demo.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Сервис для работы с банковским счётом должен:")
class BankAccountServiceImplTest {
    private BankAccountService service;
    @Autowired
    private BankAccountRepository repository;
    @Autowired
    private MessageSource messageSource;

    private final String correctBankAccountNumber = "123";
    private final String wrongBankAccountNumber = "1234";
    private BankAccountEntity beforeOperationEntity;

    @BeforeEach
    void setUp() {
        beforeOperationEntity = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        service = new BankAccountServiceImpl(repository, messageSource);
    }

    @Test
    @DisplayName("найти кол-во денег на банковском счёте")
    void SuccessfullyFindBankAccountMoneyAmount() {
        BigDecimal foundMoney = service.findBankAccountMoneyAmount(correctBankAccountNumber);

        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney();
        assertEquals(expectedMoney, foundMoney);
    }

    @Test
    @DisplayName("выбросить исключение т.к. не найден банковский счёт")
    void throwExceptionWhenFindBankAccountMoneyAmount() {
        assertThrows(APIException.class, () -> service.findBankAccountMoneyAmount(wrongBankAccountNumber));
    }

    @Test
    @DisplayName("найти кол-во бонусов на банковском счёте")
    void SuccessfullyFindBankAccountEMoneyAmount() {
        BigDecimal foundEMoney = service.findBankAccountEMoneyAmount(correctBankAccountNumber);

        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney();
        assertEquals(expectedEMoney, foundEMoney);
    }

    @Test
    @DisplayName("выбросить исключение т.к. не найден банковский счёт")
    void throwExceptionWhenBankAccountEMoneyAmount() {
        assertThrows(APIException.class, () -> service.findBankAccountEMoneyAmount(wrongBankAccountNumber));
    }

    @Test
    @DisplayName("выбросить исключение т.к. не найден банковский счёт")
    void throwExceptionWhenGetPurchasePayment() {
        assertThrows(APIException.class, () -> service.getPurchasePayment(wrongBankAccountNumber, BigDecimal.ONE, ShopType.ONLINE));
    }

    @Test
    @DisplayName("выбросить исключение т.к. не хватает денег на банковском счёте")
    void throwExceptionWhenGetPurchasePaymentBecauseNotEnoughMaoney() {
        assertThrows(APIException.class, () -> service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(100000), ShopType.ONLINE));
    }

    @Test
    @DisplayName("произвести списание средств в размере 120 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForShopWithAmount120() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(120), ShopType.OFFLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(120));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(120).multiply(PercentType.OFFLINE_SHOP.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 1000 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForShopWithAmount1000() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(1000), ShopType.OFFLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(1000));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(1000).multiply(PercentType.MAX_BY_AMOUNT.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 1000 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForShopWithAmount570() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(570), ShopType.OFFLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(570));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(570).multiply(PercentType.MAX_BY_AMOUNT.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 100 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForOnlineWithAmount100() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(100), ShopType.ONLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(100));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(100).multiply(PercentType.ONLINE_SHOP.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 301 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForOnlineWithAmount301() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(301), ShopType.ONLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(301));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(301).multiply(PercentType.MAX_BY_AMOUNT.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 17 и дополнительное списание")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForOnlineWithAmount17() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(17), ShopType.ONLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney()
                .subtract(BigDecimal.valueOf(17)).subtract(BigDecimal.valueOf(17).multiply(PercentType.WRITE_OFF_BY_AMOUNT.getValue()));
        BigDecimal expectedEMoney = BigDecimal.ZERO.setScale(5, RoundingMode.CEILING);
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }

    @Test
    @DisplayName("произвести списание средств в размере 21 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForOnlineWithAmount21() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(21), ShopType.ONLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(21));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(21).multiply(PercentType.ONLINE_SHOP.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }


    @Test
    @DisplayName("произвести списание средств в размере 700 и начислить бонусы")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void SuccessfullyGetPurchasePaymentForOnlineWithAmount700() {
        service.getPurchasePayment(correctBankAccountNumber, BigDecimal.valueOf(700), ShopType.ONLINE);

        BankAccountEntity entityAfterOperation = repository.findBankAccountEntityByBankAccountNumber(correctBankAccountNumber).get();
        BigDecimal expectedMoney = beforeOperationEntity.getBankAccountMoney().subtract(BigDecimal.valueOf(700));
        BigDecimal expectedEMoney = beforeOperationEntity.getBankAccountEMoney().add(BigDecimal.valueOf(700).multiply(PercentType.MAX_BY_AMOUNT.getValue()));
        assertEquals(expectedMoney, entityAfterOperation.getBankAccountMoney());
        assertEquals(expectedEMoney, entityAfterOperation.getBankAccountEMoney());
    }
}