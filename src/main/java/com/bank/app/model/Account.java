package com.bank.app.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@NamedQueries({
        @NamedQuery(name = "com.bank.app.model.Account.findAll",
                query = "select a from Account a")
})
public class Account {
    @Id
    private String id;

    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "balance")
    private Double balance = 0.0;

    @JsonBackReference
    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name="customer_id")
    private Customer customer;

    @JsonManagedReference
    @OneToMany(mappedBy = "account", targetEntity = Transaction.class)
    private List<Transaction> transactions;

    public enum AccountType {
        Loan, Checking, Savings
    }
}


