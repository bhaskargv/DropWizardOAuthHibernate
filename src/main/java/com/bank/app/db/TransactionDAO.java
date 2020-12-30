package com.bank.app.db;

import com.bank.app.model.Transaction;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

@Slf4j
public class TransactionDAO extends AbstractDAO<Transaction> {

    public TransactionDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Add the transaction details to the database.
     *
     * @param transaction - details of the transaction.
     */
    public void add(Transaction transaction) {
        persist(transaction);
    }

    /**
     *
     * @return
     */
    public List<Transaction> findAll() {
        return list(namedQuery("com.bank.app.model.Transaction.findAll"));
    }
}
