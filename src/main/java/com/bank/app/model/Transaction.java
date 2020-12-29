package com.bank.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

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

    @Column(name = "posted_on")
    private String postedOn;

    @JsonBackReference
    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name="account_id", nullable=false)
    private Account account;

    public enum TransactionType {
        CREDIT, DEBIT
    }
}
