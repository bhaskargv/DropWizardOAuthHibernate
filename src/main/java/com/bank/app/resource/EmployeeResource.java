package com.bank.app.resource;

import com.bank.app.auth.AccessTokenPrincipal;
import com.bank.app.config.BankingAppConfig;
import com.bank.app.model.Employee;
import com.google.common.base.Optional;
import com.bank.app.db.EmployeeDAO;
import com.okta.sdk.authc.credentials.TokenClientCredentials;
import com.okta.sdk.client.Client;
import com.okta.sdk.client.Clients;
import com.okta.sdk.resource.user.User;
import com.okta.sdk.resource.user.UserBuilder;
import com.okta.sdk.resource.user.UserList;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    private EmployeeDAO employeeDAO;
    private BankingAppConfig config;

    public EmployeeResource(EmployeeDAO employeeDAO,
                            BankingAppConfig config) {
        this.employeeDAO = employeeDAO;
        this.config = config;
    }

    /**
     * Add an employee with the details passed in the payload.
     *
     * @param tokenPrincipal - principal performing the operation.
     * @param employee - details of the employee to be added.
     */
    @POST
    @UnitOfWork
    public void createEmployee(@Auth AccessTokenPrincipal tokenPrincipal,
                            Employee employee) {
        if (!tokenPrincipal.isAdmin())
            throw new NotAuthorizedException("Principal not authorized to perform this operation");
        log.info("Add an employee.");
        //Validate the employee details
        employeeDAO.add(employee);
        //Add the employee to the Okta account.
        Client client = Clients.builder()
                .setOrgUrl(config.getAuthConfig().getBaseUrl())
                .setClientCredentials(new TokenClientCredentials(config.getAuthConfig().getApiToken()))
                .build();
        User user = UserBuilder.instance()
                .setEmail(employee.getEmail())
                .setFirstName(employee.getFirstName())
                .setLastName(employee.getLastName())
                .setActive(true)
                .setPassword("Abcd@1234".toCharArray())
                .setMobilePhone(employee.getPhone())
                .buildAndCreate(client);
    }

    /**
     * Retrieve all the employees with the below criteria.
     * If email is specified in the query parameter, retrieve the employees whose email match with the
     * email specified in the parameter.
     * If name is specified in the query parameter, retrieve the employees whose first name or last name
     * matches the name specified in the parameter.
     * If no query parameter is specified, retrieve all the employees.
     *
     * @param tokenPrincipal - principal performing the operation.
     * @param name
     * @param email
     * @return
     */
    @GET
    @UnitOfWork
    public List<Employee> findByName(@Auth AccessTokenPrincipal tokenPrincipal,
                                     @QueryParam("name") Optional<String> name,
                                     @QueryParam("email") Optional<String> email) {
        log.info("Retrive the employees with the criteria mentioned in query parameters");
        if (email.isPresent()) {
            return employeeDAO.findByEmail(email.get());
        } else if (name.isPresent()) {
            return employeeDAO.findByName(name.get());
        } else {
            return employeeDAO.findAll();
        }
    }

    /**
     * Retrieve the details of an employee with the identifier passed in as path parameter.
     *
     * @param tokenPrincipal - principal performing the operation.
     * @param employeeId - unique identifier of the employee.
     * @return
     */
    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Employee> findById(@Auth AccessTokenPrincipal tokenPrincipal,
                                       @PathParam("id") LongParam employeeId) {
        log.info("Retrieve the employee with the id " + employeeId);
        return employeeDAO.findById(employeeId.get());
    }

    /**
     * Remove the employee with the identifier passed in as path parameter.
     *
     * @param tokenPrincipal - principal performing the operation.
     * @param employeeId - unique identifier of the employee.
     */
    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteEmployee(@Auth AccessTokenPrincipal tokenPrincipal,
                               @PathParam("id") LongParam employeeId) {
        if (!tokenPrincipal.isAdmin())
            throw new NotAuthorizedException("Principal not authorized to perform this operation");
        Optional<Employee> e = employeeDAO.findById(employeeId.get());
        if (!e.isPresent())
            throw new NotFoundException("No employee found with the id " + employeeId);
        log.info("Remove the employee with id {}", employeeId);

        //Delete the user from okta.
        log.info("Remove the employee from okta");
        Client client = Clients.builder()
                .setOrgUrl(config.getAuthConfig().getBaseUrl())
                .setClientCredentials(new TokenClientCredentials(config.getAuthConfig().getApiToken()))
                .build();
        UserList userList = client.listUsers();
        User user = userList.stream().filter(u -> u.getProfile().getEmail().equalsIgnoreCase(e.get().getEmail())).findFirst().get();
        user.delete();
        user.delete(true);

        //Delete the employee from the database.
        log.info("Remove the employee from database.");
        employeeDAO.delete(employeeId.get());
    }
}
