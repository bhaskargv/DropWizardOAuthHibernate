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

import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

    @POST
    @UnitOfWork
    public void createEmployee(@Auth AccessTokenPrincipal tokenPrincipal,
                            Employee e) {
        if (!tokenPrincipal.isAdmin())
            throw new NotAuthorizedException("Not authorized");
        employeeDAO.add(e);
        Client client = Clients.builder()
                .setOrgUrl(config.getAuthConfig().getBaseUrl())
                .setClientCredentials(new TokenClientCredentials(config.getAuthConfig().getApiToken()))
                .build();
        User user = UserBuilder.instance()
                .setEmail(e.getEmail())
                .setFirstName(e.getFirstName())
                .setLastName(e.getLastName())
                .setActive(true)
                .setPassword("Abcd@1234".toCharArray())
                .setMobilePhone(e.getPhone())
                .buildAndCreate(client);
    }

    @GET
    @UnitOfWork
    public List<Employee> findByName(@Auth AccessTokenPrincipal tokenPrincipal,
                                     @QueryParam("name") Optional<String> name,
                                     @QueryParam("email") Optional<String> email) {
        if (email.isPresent()) {
            return employeeDAO.findByEmail(email.get());
        } else if (name.isPresent()) {
            return employeeDAO.findByName(name.get());
        } else {
            return employeeDAO.findAll();
        }
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Optional<Employee> findById(@Auth AccessTokenPrincipal tokenPrincipal,
                                       @PathParam("id") LongParam id) {
        return employeeDAO.findById(id.get());
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public void deleteEmployee(@Auth AccessTokenPrincipal tokenPrincipal,
                               @PathParam("id") LongParam id) {
        if (!tokenPrincipal.isAdmin())
            throw new NotAuthorizedException("Not authorized");
        Optional<Employee> e = employeeDAO.findById(id.get());
        if (!e.isPresent())
            throw new NotFoundException();

        Client client = Clients.builder()
                .setOrgUrl(config.getAuthConfig().getBaseUrl())
                .setClientCredentials(new TokenClientCredentials(config.getAuthConfig().getApiToken()))
                .build();
        UserList userList = client.listUsers();
        User user = userList.stream().filter(u -> u.getProfile().getEmail().equalsIgnoreCase(e.get().getEmail())).findFirst().get();
        user.delete();
        user.delete(true);
        employeeDAO.delete(id.get());
    }
}
