package com.bank.app.resource;

import com.bank.app.db.AccountDAO;
import com.bank.app.db.TransactionDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Transaction;
import com.bank.app.model.TransferDetails;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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

    @POST
    @UnitOfWork
    public void transfer(TransferDetails transferDetails) {
        Optional<Account> fromAccount = accountDAO.findById(transferDetails.getFromAccountId());
        Optional<Account> toAccount = accountDAO.findById(transferDetails.getToAccountId());
        if (transferDetails.getAmmount() <= 0.0
                || !fromAccount.isPresent()
                || !toAccount.isPresent()
                || fromAccount.get().getAccountType().equals(Account.AccountType.Loan))
            throw new BadRequestException();

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
        accountDAO.update(fromAccount.get().withBalance(fromAccount.get().getBalance() - transferDetails.getAmmount()));
        accountDAO.update(toAccount.get().withBalance(toAccount.get().getBalance() + transferDetails.getAmmount()));
    }
}

