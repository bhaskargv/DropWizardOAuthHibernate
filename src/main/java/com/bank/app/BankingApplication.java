package com.bank.app;

import com.bank.app.config.AuthConfig;
import com.bank.app.config.BankingAppConfig;
import com.bank.app.auth.AccessTokenPrincipal;
import com.bank.app.auth.OktaOAuthAuthenticator;
import com.bank.app.db.AccountDAO;
import com.bank.app.db.CustomerDAO;
import com.bank.app.db.EmployeeDAO;
import com.bank.app.db.TransactionDAO;
import com.bank.app.model.Account;
import com.bank.app.model.Customer;
import com.bank.app.model.Employee;
import com.bank.app.model.Transaction;
import com.bank.app.resource.AccountResource;
import com.bank.app.resource.CustomerResource;
import com.bank.app.resource.EmployeeResource;
import com.bank.app.resource.TransactionResource;
import com.okta.jwt.JwtHelper;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.apache.commons.lang3.StringUtils;

public class BankingApplication extends Application<BankingAppConfig> {

    private final HibernateBundle<BankingAppConfig> hibernateBundle
            = new HibernateBundle<BankingAppConfig>(Employee.class, Account.class, Customer.class, Transaction.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(BankingAppConfig configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new BankingApplication().run(args);
    }

    @Override
    public String getName() {
        return "BankingApplication";
    }

    @Override
    public void initialize(final Bootstrap<BankingAppConfig> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
    }


    @Override
    public void run(final BankingAppConfig configuration,
                    final Environment environment) {
        final EmployeeDAO employeeDAO
                = new EmployeeDAO(hibernateBundle.getSessionFactory());
        final CustomerDAO customerDAO
                = new CustomerDAO(hibernateBundle.getSessionFactory());
        final AccountDAO accountDAO
                = new AccountDAO(hibernateBundle.getSessionFactory());
        final TransactionDAO transactionDAO
                = new TransactionDAO(hibernateBundle.getSessionFactory());

        // base url for our resources
        environment.jersey().setUrlPattern("/api/*");

        // configure OAuth
        if (configuration.getAuthConfig().isAuthEnabled())
            configureOAuth(configuration, environment);

        // add resources
        environment.jersey().register(new EmployeeResource(employeeDAO, configuration));
        environment.jersey().register(new CustomerResource(customerDAO, accountDAO));
        environment.jersey().register(new AccountResource(accountDAO, transactionDAO, customerDAO));
        environment.jersey().register(new TransactionResource(transactionDAO, accountDAO));
    }

    private void configureOAuth(final BankingAppConfig configuration, final Environment environment) {
        try {
            AuthConfig authConfig = configuration.getAuthConfig();

            JwtHelper helper = new JwtHelper()
                    .setIssuerUrl(authConfig.getIssuer())
                    .setClientId(authConfig.getClientId());

            String audience = authConfig.getAudience();
            if (StringUtils.isNotEmpty(audience)) {
                helper.setAudience(audience);
            }

            environment.jersey().register(new AuthDynamicFeature(
                new OAuthCredentialAuthFilter.Builder<AccessTokenPrincipal>()
                    .setAuthenticator(new OktaOAuthAuthenticator(helper.build()))
                    .setPrefix("Bearer")
                    .buildAuthFilter()));

            environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AccessTokenPrincipal.class));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to configure JwtVerifier", e);
        }
    }
}