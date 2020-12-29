package com.bank.app.resource;

import com.bank.app.db.AccountDAO;
import com.bank.app.db.CustomerDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Customer;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.NonEmptyStringParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.Objects.nonNull;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    public CustomerResource(CustomerDAO customerDAO,
                            AccountDAO accountDAO) {
        this.customerDAO = customerDAO;
        this.accountDAO = accountDAO;
    }

    @POST
    @UnitOfWork
    public void createCustomer(Customer e) {
        customerDAO.add(e);
    }

    @GET
    @UnitOfWork
    public List<Customer> findByName(@QueryParam("name") Optional<String> name,
                                     @QueryParam("email") Optional<String> email) {
        if (email.isPresent()) {
            return customerDAO.findByEmail(email.get());
        } else if (name.isPresent()) {
            return customerDAO.findByName(name.get());
        } else {
            return customerDAO.findAll();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Customer> findById(@PathParam("id") NonEmptyStringParam id) {
        return customerDAO.findById(id.get().get());
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public void updateCustomer(@PathParam("id") NonEmptyStringParam id,
                               Customer customer) {
        Optional<Customer> customerOptional = customerDAO.findById(id.get().get());
        if (!customerOptional.isPresent())
            throw new NotFoundException();
        if (nonNull(customer.getDateOfBirth()))
            throw new BadRequestException();

        Customer cust = customerOptional.get();

        if (nonNull(customer.getFirstName()))
            cust.setFirstName(customer.getFirstName());
        if (nonNull(customer.getLastName()))
            cust.setLastName(customer.getLastName());
        if (nonNull(customer.getSsn()))
            cust.setSsn(customer.getSsn());
        if (nonNull(customer.getAddress()))
            cust.setAddress(customer.getAddress());
        if (nonNull(customer.getEmail()))
            cust.setEmail(customer.getEmail());
        if (nonNull(customer.getPhone()))
            cust.setPhone(customer.getPhone());
        customerDAO.update(cust);
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteCustomer(@PathParam("id") NonEmptyStringParam id,
                               Customer customer) {
        Optional<Customer> customerOptional = customerDAO.findById(id.get().get());
        if (!customerOptional.isPresent())
            throw new NotFoundException();
        List<Account> accounts = customerOptional.get().getAccounts();
        if (accounts.size() > 0)
            throw new NotAuthorizedException("Customer cannot be deleted");
        customerDAO.delete(id.get().get());
    }
}
