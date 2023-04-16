package com.example.demo.service;

import com.example.demo.enums.ShopType;

import java.math.BigDecimal;

/**
 * @author ryanin
 * Сервис для работы с банковским счётом
 */
public interface BankAccountService {

    /**
     * Метод для поиска денежных средств на бакновском счету
     *
     * @param bankAccountNumber - номер банковского счёта
     */
    BigDecimal findBankAccountMoneyAmount(String bankAccountNumber);

    /**
     * Метод для поиска бонусных средств на бакновском счету
     *
     * @param bankAccountNumber - номер банковского счёта
     */
    BigDecimal findBankAccountEMoneyAmount(String bankAccountNumber);

    /**
     * Метод для списания средств с банковского счёта
     *
     * @param bankAccountNumber - номер банковского счёта
     * @param amount            - кол-во средств
     * @param shopType          - тип магазина
     */
    void getPurchasePayment(String bankAccountNumber, BigDecimal amount, ShopType shopType);
}