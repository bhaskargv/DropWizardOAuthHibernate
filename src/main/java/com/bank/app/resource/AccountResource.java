package com.bank.app.resource;

import com.bank.app.db.AccountDAO;
import com.bank.app.db.CustomerDAO;
import com.bank.app.db.TransactionDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Customer;
import com.bank.app.model.CustomerId;
import com.bank.app.model.Transaction;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.NonEmptyStringParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {

    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private CustomerDAO customerDAO;

    public AccountResource(AccountDAO accountDAO,
                           TransactionDAO transactionDAO,
                           CustomerDAO customerDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
        this.customerDAO = customerDAO;
    }

    @POST
    @UnitOfWork
    public void addAccount(Account a) {
        accountDAO.add(a);
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Account> findById(@PathParam("id") NonEmptyStringParam id) {
        return accountDAO.findById(id.get().get());
    }

    @GET
    @UnitOfWork
    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    @GET
    @Path("/{id}/transactions")
    @UnitOfWork
    public List<Transaction> findTransacationsByAccountId(@PathParam("id") NonEmptyStringParam accountId) {
        Optional<Account> accountOptional = accountDAO.findById(accountId.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException();
        return accountOptional.get().getTransactions();
    }

    @PUT
    @Path("/{id}/link")
    @UnitOfWork
    public void linkCustomer(@PathParam("id") NonEmptyStringParam accountId,
                              CustomerId customerId) {
        Optional<Account> accountOptional = accountDAO.findById(accountId.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException();
        Optional<Customer> customerOptional = customerDAO.findById(customerId.getId());
        if (!customerOptional.isPresent())
            throw new NotFoundException();
        accountDAO.update(accountOptional.get().withCustomer(customerOptional.get()));
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteAccount(@PathParam("id") NonEmptyStringParam id,
                              Account a) {
        Optional<Account> accountOptional = accountDAO.findById(id.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException();
        accountDAO.delete(id.get().get());
    }
}
