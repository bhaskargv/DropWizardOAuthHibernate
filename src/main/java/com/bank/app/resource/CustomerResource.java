package com.bank.app.resource;

import com.bank.app.db.AccountDAO;
import com.bank.app.db.CustomerDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Customer;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.NonEmptyStringParam;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
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

    /**
     * Add a customer.
     *
     * @param customer - details of the customer.
     */
    @POST
    @UnitOfWork
    public void createCustomer(Customer customer) {
        //Validate the customer details
        log.info("Validate the customer details");
        customerDAO.add(customer);
        log.info("Successfully added the customer");
    }

    /**
     * Retrieve all the customers with the below criteria.
     * If email is specified in the query parameter, retrieve the customers whose email match with the
     * email specified in the parameter.
     * If name is specified in the query parameter, retrieve the customers whose first name or last name
     * matches the name specified in the parameter.
     * If no query parameter is specified, retrieve all the customers.
     *
     * @param name - String to be used to match the first and last names of the customer.
     * @param email - String to be used to match the email of the customer.
     * @return - the list of customers matching the criteria.
     */
    @GET
    @UnitOfWork
    public List<Customer> findByName(@QueryParam("name") Optional<String> name,
                                     @QueryParam("email") Optional<String> email) {
        log.info("Retrieve the list of customers matching the criteria");
        if (email.isPresent()) {
            return customerDAO.findByEmail(email.get());
        } else if (name.isPresent()) {
            return customerDAO.findByName(name.get());
        } else {
            return customerDAO.findAll();
        }
    }

    /**
     * Retrieve the customer with the identifier passed in as a path parameter.
     *
     * @param customerId - unique identifier of the customer.
     * @return - the customer.
     */
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Customer> findById(@PathParam("id") NonEmptyStringParam customerId) {
        log.info("Retrieve the customer with the id {}", customerId);
        return customerDAO.findById(customerId.get().get());
    }

    /**
     * Update the details of the customer identified by the identifier passed in as a path parameter
     * with the details passed in the payload.
     *
     * @param customerId - unique identifier of the customer.
     * @param customer - details of the customer to be used to update the customer.
     */
    @PUT
    @Path("/{id}")
    @UnitOfWork
    public void updateCustomer(@PathParam("id") NonEmptyStringParam customerId,
                               Customer customer) {
        log.info("Update the customer with the id {}", customerId);
        Optional<Customer> customerOptional = customerDAO.findById(customerId.get().get());
        if (!customerOptional.isPresent())
            throw new NotFoundException("No customer found with the id " + customerId);
        if (nonNull(customer.getDateOfBirth()))
            throw new BadRequestException("Customer's date-of-birth cannot be updated");

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
        log.info("Successfully updated the customer");

    }

    /**
     * Delete the customer with the identifier passed in as path parameter.
     *
     * @param customerId - unique identifier of the customer.
     */
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteCustomer(@PathParam("id") NonEmptyStringParam customerId) {
        log.info("Delete the customer with the id {}", customerId);
        Optional<Customer> customerOptional = customerDAO.findById(customerId.get().get());
        if (!customerOptional.isPresent())
            throw new NotFoundException("No customer found with the id " + customerId);
        List<Account> accounts = customerOptional.get().getAccounts();
        if (accounts.size() > 0)
            throw new NotAuthorizedException("Customer cannot be deleted, as the associated accounts are not deleted.");
        customerDAO.delete(customerId.get().get());
        log.info("Successfully deleted the customer");
    }
}
