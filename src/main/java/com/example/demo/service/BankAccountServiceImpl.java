package com.example.demo.service;

import com.example.demo.domain.BankAccountEntity;
import com.example.demo.enums.PercentType;
import com.example.demo.enums.ShopType;
import com.example.demo.exception.APIException;
import com.example.demo.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository repository;
    private final MessageSource messageSource;
    private static final BigDecimal amountWriteOff = BigDecimal.valueOf(20);
    private static final BigDecimal amountForMAxPercent = BigDecimal.valueOf(300);
    private static final Logger logger = Logger.getLogger(BankAccountServiceImpl.class);


    @Override
    @Transactional
    public void getPurchasePayment(String bankAccountNumber, BigDecimal amount, ShopType shopType) {
        Optional<BankAccountEntity> bankAccountEntity = repository.findBankAccountEntityByBankAccountNumber(bankAccountNumber);
        if (bankAccountEntity.isPresent()) {
            debitMoneyFromBankAccount(bankAccountEntity.get(), amount, shopType);
        } else {
            throw new APIException(HttpStatus.UNPROCESSABLE_ENTITY.value(), messageSource
                    .getMessage("bankAccount.error.notFoundByBankAccountNumber", new Object[]{bankAccountNumber}, LocaleContextHolder.getLocale()));
        }
    }

    @Override
    public BigDecimal findBankAccountMoneyAmount(String bankAccountNumber) throws APIException {
        Optional<BankAccountEntity> bankAccountEntity = repository.findBankAccountEntityByBankAccountNumber(bankAccountNumber);
        return bankAccountEntity
                .orElseThrow(() ->
                        new APIException(HttpStatus.NOT_FOUND.value(), messageSource
                                .getMessage("bankAccount.error.notFoundByBankAccountNumber", new Object[]{bankAccountNumber}, LocaleContextHolder.getLocale()))).getBankAccountMoney();
    }

    @Override
    public BigDecimal findBankAccountEMoneyAmount(String bankAccountNumber) throws APIException {
        Optional<BankAccountEntity> bankAccountEntity = repository.findBankAccountEntityByBankAccountNumber(bankAccountNumber);
        return bankAccountEntity
                .orElseThrow(() ->
                        new APIException(HttpStatus.NOT_FOUND.value(), messageSource
                                .getMessage("bankAccount.error.notFoundByBankAccountNumber", new Object[]{bankAccountNumber}, LocaleContextHolder.getLocale()))).getBankAccountEMoney();
    }

    private void debitMoneyFromBankAccount(BankAccountEntity bankAccountEntity, BigDecimal amount, ShopType shopType) throws APIException {
        BigDecimal remainBalance = bankAccountEntity.getBankAccountMoney().subtract(amount);
        if (remainBalance.signum() < 0) {
            throw new APIException(HttpStatus.NOT_FOUND.value(), messageSource
                    .getMessage("bankAccount.error.notEnoughMoneyOnBankAccount", new Object[]{bankAccountEntity.getBankAccountNumber()}, LocaleContextHolder.getLocale()));
        } else {
            bankAccountEntity.setBankAccountMoney(remainBalance);
            if (amount.compareTo(amountWriteOff) < 0) {
                changeDataOnBankAccountTakingIntoWriteOff(bankAccountEntity, amount);
            } else {
                changeDataOnBankAccountTakingIntoBonuses(bankAccountEntity, amount, percentDetermination(amount, shopType));
            }
        }
    }

    private PercentType percentDetermination(BigDecimal amount, ShopType shopType) {
        if (amount.compareTo(amountForMAxPercent) >= 0) {
            return PercentType.MAX_BY_AMOUNT;
        } else {
            if (shopType.equals(ShopType.OFFLINE)) {
                return PercentType.OFFLINE_SHOP;
            } else {
                return PercentType.ONLINE_SHOP;
            }
        }
    }

    private void changeDataOnBankAccountTakingIntoWriteOff(BankAccountEntity bankAccountEntity, BigDecimal amount) {
        logger.info(messageSource
                .getMessage("bankAccount.info.percentType.writeOff",
                        new Object[]{PercentType.convertToPercent(PercentType.WRITE_OFF_BY_AMOUNT)}, LocaleContextHolder.getLocale()));
        bankAccountEntity.setBankAccountMoney(bankAccountEntity.getBankAccountMoney()
                .subtract(amount.multiply(PercentType.WRITE_OFF_BY_AMOUNT.getValue())));
        repository.save(bankAccountEntity);
    }

    private void changeDataOnBankAccountTakingIntoBonuses(BankAccountEntity bankAccountEntity, BigDecimal amount, PercentType percentType) {
        messageSource
                .getMessage("bankAccount.info.percentType.bonus",
                        new Object[]{PercentType.convertToPercent(percentType)}, LocaleContextHolder.getLocale());
        bankAccountEntity.setBankAccountEMoney(bankAccountEntity.getBankAccountEMoney()
                .add(amount.multiply(percentType.getValue())));
        repository.save(bankAccountEntity);
    }
}