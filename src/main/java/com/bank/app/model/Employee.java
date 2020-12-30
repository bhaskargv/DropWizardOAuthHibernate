package com.bank.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "employees")
@NamedQueries({
    @NamedQuery(name = "com.bank.app.model.Employee.findAll",
            query = "select e from Employee e"),
    @NamedQuery(name = "com.bank.app.model.Employee.findByName",
            query = "select e from Employee e "
            + "where e.firstName like :name "
            + "or e.lastName like :name"),
    @NamedQuery(name = "com.bank.app.model.Employee.findByEmail",
                query = "select e from Employee e "
                        + "where e.email like :email")
})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "designation")
    private String designation;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "date_of_joining")
    private Date dateOfJoining;
}
