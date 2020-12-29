package com.bank.app.model;

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
@Table(name = "customers")
@NamedQueries({
        @NamedQuery(name = "com.bank.app.model.Customer.findAll",
                query = "select c from Customer c"),
        @NamedQuery(name = "com.bank.app.model.Customer.findByName",
                query = "select c from Customer c "
                        + "where c.firstName like :name "
                        + "or c.lastName like :name"),
        @NamedQuery(name = "com.bank.app.model.Customer.findByEmail",
                query = "select e from Customer e "
                        + "where e.email like :email")
})
public class Customer {
    @Id
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "ssn")
    private String ssn;

    @JsonManagedReference
    @OneToMany(mappedBy = "customer", targetEntity = Account.class)
    private List<Account> accounts;
}
