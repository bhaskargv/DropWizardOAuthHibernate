package com.bank.app.resource;

import com.bank.app.db.AccountDAO;
import com.bank.app.db.TransactionDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Transaction;
import com.bank.app.model.TransferDetails;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static java.util.Objects.isNull;

@Slf4j
@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;

    public TransactionResource(TransactionDAO transactionDAO,
                               AccountDAO accountDAO) {
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
    }

    /**
     * Post a transaction to transfer money from one account to the other.
     *
     * @param transferDetails - details of the accounts for money transfer
     */
    @POST
    @UnitOfWork
    public void transfer(TransferDetails transferDetails) {
        //Validate the transfer details
        if (isNull(transferDetails.getFromAccountId())
                || isNull(transferDetails.getToAccountId())
                || transferDetails.getAmmount() <= 0.0)
            throw new BadRequestException("Insufficient transfer details");

        log.info("Retrieve the account with the id {}", transferDetails.getFromAccountId());
        Optional<Account> fromAccount = accountDAO.findById(transferDetails.getFromAccountId());
        log.info("Retrieve the account with the id {}", transferDetails.getToAccountId());
        Optional<Account> toAccount = accountDAO.findById(transferDetails.getToAccountId());
        if (!fromAccount.isPresent()
                || !toAccount.isPresent())
            throw new BadRequestException("One of the accounts for money transfer is not found");
        if (fromAccount.get().getAccountType().equals(Account.AccountType.Loan))
            throw new BadRequestException("Cannot transfer money from a loan account");

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String randomString = UUID.randomUUID().toString().substring(1, 7);
        Transaction tr1 = new Transaction();
        tr1.setId("DB" + randomString);
        tr1.setAccount(fromAccount.get());
        tr1.setPostedOn(formatter.format(date));
        tr1.setAmmount(transferDetails.getAmmount());
        tr1.setTransactionType(Transaction.TransactionType.DEBIT);
        transactionDAO.add(tr1);
        Transaction tr2 = new Transaction();
        tr2.setId("CR" + randomString);
        tr2.setAccount(toAccount.get());
        tr2.setPostedOn(formatter.format(date));
        tr2.setAmmount(transferDetails.getAmmount());
        tr2.setTransactionType(Transaction.TransactionType.CREDIT);
        transactionDAO.add(tr2);
        log.info("Post the debit transaction in account with id {} ", transferDetails.getFromAccountId());
        accountDAO.update(fromAccount.get().withBalance(fromAccount.get().getBalance() - transferDetails.getAmmount()));
        log.info("Post the credit transaction in account with id {} ", transferDetails.getToAccountId());
        accountDAO.update(toAccount.get().withBalance(toAccount.get().getBalance() + transferDetails.getAmmount()));
    }
}

