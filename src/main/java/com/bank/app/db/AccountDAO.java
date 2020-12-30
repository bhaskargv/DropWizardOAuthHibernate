package com.bank.app.db;

import com.bank.app.model.Account;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.Query;
import java.util.List;
import java.util.UUID;

@Slf4j
public class AccountDAO extends AbstractDAO<Account> {

    public AccountDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Add an account to the database.
     *
     * @param acoount
     */
    public void add(Account acoount) {
        String randomString = UUID.randomUUID().toString().substring(1, 7);
        StringBuilder idBuilder = new StringBuilder();
        switch (acoount.getAccountType()) {
            case Loan: idBuilder.append("LN");
                break;
            case Savings:idBuilder.append("SA");
                break;
            case Checking:idBuilder.append("CH");
                break;
        }
        acoount.setId(idBuilder.append(randomString).toString());
        persist(acoount);
    }

    /**
     * Return list of accounts added to the database.
     *
     * @return list of accounts.
     */
    public List<Account> findAll() {
        Query query = this.currentSession().createQuery("from Account");
        return query.getResultList();
        //return list(namedQuery("com.bank.app.model.Account.findAll"));
    }

    /**
     * Retrieve the account by it's id
     *
     * @param accountId  - unique identifier of the account
     * @return optional account object.
     */
    public Optional<Account> findById(String accountId) {
        return Optional.fromNullable(get(accountId));
    }

    /**
     * Updates the account in the database with the details.
     *
     * @param account - details of the account to be used to update the account.
     */
    public void update(Account account) {
        this.currentSession().merge(account);
    }

    /**
     * Delete the account identified by accountId
     *
     * @param accountId - unique identifier of the account.
     */
    public void delete(String accountId) {
        Session session = this.currentSession();
        session.delete(get(accountId));
        session.getTransaction().commit();
    }
}
