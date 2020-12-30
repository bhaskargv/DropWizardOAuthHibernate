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
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.isNull;

@Slf4j
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

    /**
     * Creates an account with the details passed in as a parameter.
     *
     * @param account - details of the account.
     */
    @POST
    @UnitOfWork
    public void addAccount(Account account) {
        log.info("Validate the account details");
        if (isNull(account.getAccountType()))
            throw new BadRequestException("Account type is not specified");
        if (account.getBalance() <= 0.0)
            throw new BadRequestException("Account balance cannot be zero while opening the account");
        account.setCreatedOn(new Date());
        accountDAO.add(account);
        log.info("Successfully added the account");
    }

    /**
     * Retrieve the account with the identifier passed in as a path parameter.
     *
     * @param accountId - unique identifier of the account.
     * @return the account with the identifier.
     */
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Account> findById(@PathParam("id") NonEmptyStringParam accountId) {
        log.info("Retrieve the account identified by {}", accountId);
        return accountDAO.findById(accountId.get().get());
    }

    /**
     * Retrieve all the accounts.
     *
     * @return the list of accounts.
     */
    @GET
    @UnitOfWork
    public List<Account> findAll() {
        log.info("Retrieve all the accounts");
        return accountDAO.findAll();
    }

    /**
     * Retrieve all the transactions done on the account.
     *
     * @param accountId - unique identifier of the account.
     * @return the list of transactions made on the account.
     */
    @GET
    @Path("/{id}/transactions")
    @UnitOfWork
    public List<Transaction> findTransacationsByAccountId(@PathParam("id") NonEmptyStringParam accountId) {
        log.info("Retrieve all the transactions recorded for the account with id {}", accountId);
        Optional<Account> accountOptional = accountDAO.findById(accountId.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException("No account found with the id " + accountId);
        return accountOptional.get().getTransactions();
    }

    /**
     * Link the account identified by the identifier passed in as a path parameter with the customer identifier
     * passed in the payload.
     *
     * @param accountId - unique identifier of the account.
     * @param customerId - unique identifier of the customer.
     */
    @PUT
    @Path("/{id}/link")
    @UnitOfWork
    public void linkCustomer(@PathParam("id") NonEmptyStringParam accountId,
                              CustomerId customerId) {
        log.info("Link the customer with id {} to the account with id {}", customerId, accountId);
        log.info("Validate whether an account exists with the id {}", accountId);
        Optional<Account> accountOptional = accountDAO.findById(accountId.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException("No account found with the id " + accountId);
        log.info("Validate whether a customer exists with the id {}", customerId);
        Optional<Customer> customerOptional = customerDAO.findById(customerId.getId());
        if (!customerOptional.isPresent())
            throw new NotFoundException("No customer found with the id " + customerId);
        accountDAO.update(accountOptional.get().withCustomer(customerOptional.get()));
        log.info("Linked the account to the customer successfully");
    }

    /**
     * Delete the account identified with the identifier passed in as a path parameter.
     *
     * @param accountId - unique identifier of the account.
     */
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteAccount(@PathParam("id") NonEmptyStringParam accountId) {
        log.info("Delete the account with the id {}", accountId);
        Optional<Account> accountOptional = accountDAO.findById(accountId.get().get());
        if (!accountOptional.isPresent())
            throw new NotFoundException("No account found with the id " + accountId);
        accountDAO.delete(accountId.get().get());
        log.info("Deleted the account successfully");
    }
}
