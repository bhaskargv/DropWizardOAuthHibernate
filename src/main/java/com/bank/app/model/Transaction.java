package com.bank.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@NamedQueries({
        @NamedQuery(name = "com.bank.app.model.Transaction.findAll",
                query = "select e from Transaction e")
})
public class Transaction {
    @Id
    private String id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Transaction.TransactionType transactionType;

    @Column(name = "ammount")
    private Double ammount = 0.0;

    @Column(name = "balance_before")
    private Double balanceBefore = 0.0;

    @Column(name = "balance_after")
    private Double balanceAfter = 0.0;

    @Column(name = "posted_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date postedOn;

    @JsonBackReference
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

    public enum TransactionType {
        CREDIT, DEBIT
    }
}
