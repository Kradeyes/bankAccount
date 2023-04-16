package com.example.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum PercentType {
    WRITE_OFF_BY_AMOUNT(BigDecimal.valueOf(0.1)),
    OFFLINE_SHOP(BigDecimal.valueOf(0.1)),
    ONLINE_SHOP(BigDecimal.valueOf(0.17)),
    MAX_BY_AMOUNT(BigDecimal.valueOf(0.3));
    private final BigDecimal value;

    public static String convertToPercent(PercentType percentType) {
        return percentType.value.multiply(BigDecimal.valueOf(100)) + "%";
    }
}