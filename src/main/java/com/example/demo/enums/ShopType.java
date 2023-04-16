package com.example.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopType {
    OFFLINE(1),
    ONLINE(2);
    private final Integer value;
}