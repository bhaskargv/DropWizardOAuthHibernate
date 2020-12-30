package com.bank.app.db;

import com.bank.app.model.Customer;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.UUID;

@Slf4j
public class CustomerDAO extends AbstractDAO<Customer> {

    public CustomerDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Add a customer to the database.
     *
     * @param customer - details of the customer.
     */
    public void add(Customer customer) {
        String randomString = UUID.randomUUID().toString().substring(1, 7);
        StringBuilder idBuilder = new StringBuilder();
        customer.setId(idBuilder.append("CID").append(randomString).toString());
        persist(customer);
    }

    /**
     * Return all the customers.
     *
     * @return list of customers.
     */
    public List<Customer> findAll() {
        return list(namedQuery("com.bank.app.model.Customer.findAll"));
    }

    /**
     * Retrieve list of customers whose first name or last name matched with the
     * parameter.
     *
     * @param name - String used to match the first name or last name of the customer.
     * @return list of customers whose first name or last name matched with the string.
     */
    public List<Customer> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("com.bank.app.model.Account.findByName")
                        .setParameter("name", builder.toString())
        );
    }

    /**
     * Retrieve list of customers whose email contains the characters passed in the parameter.
     *
     * @param email - email string to be used to retrieve the customers.
     * @return list of customers.
     */
    public List<Customer> findByEmail(String email) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(email).append("%");
        return list(
                namedQuery("com.bank.app.model.Customer.findByEmail")
                        .setParameter("email", builder.toString())
        );
    }

    /**
     * Retrieve the customer with the identifier.
     *
     * @param customerId - unique identifier to retrieve the customer.
     * @return the customer object with the identifier passed in as a parameter.
     */
    public Optional<Customer> findById(String customerId) {
        return Optional.fromNullable(get(customerId));
    }

    /**
     * Update the customer with the details passed in.
     *
     * @param customer - details of the customer to be used to update the customer.
     */
    public void update(Customer customer) {
        this.currentSession().merge(customer);
    }

    /**
     * Deletes the customer identified by the unique identifier passed in as a parameter.
     *
     * @param customerId - unique identifier of the customer.
     */
    public void delete(String customerId) {
        Session session = this.currentSession();
        session.delete(get(customerId));
        session.getTransaction().commit();
    }
}
