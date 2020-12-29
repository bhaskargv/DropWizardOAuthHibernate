package com.bank.app.db;

import com.bank.app.model.Customer;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

public class CustomerDAO extends AbstractDAO<Customer> {

    public CustomerDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void add(Customer c) {
        String randomString = UUID.randomUUID().toString().substring(1, 7);
        StringBuilder idBuilder = new StringBuilder();
        c.setId(idBuilder.append("CID").append(randomString).toString());
        persist(c);
    }

    public List<Customer> findAll() {
        return list(namedQuery("com.bank.app.model.Customer.findAll"));
    }

    public List<Customer> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("com.bank.app.model.Account.findByName")
                        .setParameter("name", builder.toString())
        );
    }

    public List<Customer> findByEmail(String email) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(email).append("%");
        return list(
                namedQuery("com.bank.app.model.Customer.findByEmail")
                        .setParameter("email", builder.toString())
        );
    }

    public Optional<Customer> findById(String id) {
        return Optional.fromNullable(get(id));
    }

    public void update(Customer customer) {
        this.currentSession().merge(customer);
    }

    public void delete(String id) {
        Session session = this.currentSession();
        session.delete(get(id));
        session.getTransaction().commit();
    }
}
