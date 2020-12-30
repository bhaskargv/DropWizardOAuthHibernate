## Banking Application
A simple banking application using dropwizard, hibernate and oauth addressing the following requirements

User with Admin role can do the following:

    1.Sign in/out as admin.

    2.Add bank employees.

    3.Delete employees.

Bank Employee user can do the following :

    1.Sign in/out as an employee.

    2.Create a customer.

    3.Create accounts like savings, salary, loan, current account etc.

    4.Link customers with accounts.

    5.Update KYC for a customer.

    6.Get details of a customer.

    7.Delete customer.

    8.Get account balance for an account.

    9.Transfer money from one account to another.



## Setup database

Run the following queries to create tables

    create table employees(
        -- auto-generated primary key
        id bigint primary key not null auto_increment,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        designation  varchar(255) not null,
        phone  varchar(255) not null,
        email varchar(255) not null,
        date_of_joining DATE not null
    );

    create table customers(
        id varchar(255) primary key not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        address  varchar(255) not null,
        phone  varchar(255) not null,
        email varchar(255) not null,
        date_of_birth DATE not null,
        ssn varchar(255) not null
    );

    create table accounts(
        id varchar(255) primary key not null,
        account_type varchar(255) not null,
        balance DECIMAL(10,2),
        created_on DATE not null,
        customer_id varchar(255),
        foreign key (customer_id)  references customers(id)
    );

    create table transactions(
        id varchar(255) primary key not null,
        type varchar(255) not null,
        ammount DECIMAL(10,2),
        posted_on DATE not null,
        balance_after DECIMAL(10,2),
        balance_before DECIMAL(10,2),
        account_id varchar(255),
        foreign key (account_id)
        references accounts(id)
    );

---

## Setup OAuth application


1. Choose Okta or Google to create an application.
2. Add scopes if necessary
3. Generate an API token (to manage users and groups)
4. Create a group (admin) and add a user(admin user) to the group.
5. Update claims for the app to get isAdmin = true when a user from admin group gets an access token.


## Update the config file
config.yml should contain database connection details and oauth application details. You can use the template.

## Run 
mvn clean install

java -jar  -jar target/BankingApplication-1.0-SNAPSHOT.jar server config.yml

## Use postman to test the APIs
