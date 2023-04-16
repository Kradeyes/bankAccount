package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "bank_account")
@Generated
public class BankAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bankAccount_id_seq")
    @SequenceGenerator(name = "bankAccount_id_seq", sequenceName = "bankAccount_id_seq", allocationSize = 1)
    @Column(name = "bankaccountid", columnDefinition = "numeric(15,0)")
    @EqualsAndHashCode.Exclude
    @JsonProperty("bankAccountId")
    private Long bankAccountId;

    @Column(name = "bankaccountnumber", columnDefinition = "varchar(255)")
    @JsonProperty("bankAccountNumber")
    private String bankAccountNumber;

    @Column(name = "bankaccountmoney", columnDefinition = "numeric(20, 5)")
    @JsonProperty("bankAccountMoney")
    private BigDecimal bankAccountMoney;

    @Column(name = "bankaccountemoney", columnDefinition = "numeric(20, 5)")
    @JsonProperty("bankAccountEMoney")
    private BigDecimal bankAccountEMoney;
}