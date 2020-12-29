package com.bank.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferDetails {
    private String fromAccountId;

    private String toAccountId;

    private double ammount = 0.0;
}
