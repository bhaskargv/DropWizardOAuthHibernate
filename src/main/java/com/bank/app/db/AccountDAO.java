package com.bank.app.db;

import com.bank.app.model.Account;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

public class AccountDAO extends AbstractDAO<Account> {

    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void add(Account a) {
        String randomString = UUID.randomUUID().toString().substring(1, 7);
        StringBuilder idBuilder = new StringBuilder();
        switch (a.getAccountType()) {
            case Loan: idBuilder.append("LN");
                break;
            case Savings:idBuilder.append("SA");
                break;
            case Checking:idBuilder.append("CH");
                break;
        }
        a.setId(idBuilder.append(randomString).toString());
        persist(a);
    }

    public List<Account> findAll() {
        Query query = this.currentSession().createQuery("from Account");
        return query.getResultList();
        //return list(namedQuery("com.bank.app.model.Account.findAll"));
    }

    public Optional<Account> findById(String id) {
        return Optional.fromNullable(get(id));
    }

    public void update(Account a) {
        this.currentSession().merge(a);
    }

    public void delete(String id) {
        Session session = this.currentSession();
        session.delete(get(id));
        session.getTransaction().commit();
    }
}
