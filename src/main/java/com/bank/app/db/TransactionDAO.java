package com.bank.app.db;

import com.bank.app.model.Transaction;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;

import org.hibernate.SessionFactory;

public class TransactionDAO extends AbstractDAO<Transaction> {

    public TransactionDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void add(Transaction transaction) {
        persist(transaction);
    }

    public List<Transaction> findAll() {
        return list(namedQuery("com.bank.app.model.Transaction.findAll"));
    }
}
