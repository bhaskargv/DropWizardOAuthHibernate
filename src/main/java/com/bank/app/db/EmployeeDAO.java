package com.bank.app.db;

import com.bank.app.model.Employee;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@Slf4j
public class EmployeeDAO extends AbstractDAO<Employee> {

    public EmployeeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Add an employee to the database.
     *
     * @param employee - details of the employee.
     */
    public void add(Employee employee) {
        persist(employee);
    }

    /**
     * Retrieve all the employees.
     *
     * @return list of employees.
     */
    public List<Employee> findAll() {
        return list(namedQuery("com.bank.app.model.Employee.findAll"));
    }

    /**
     * Retrieve list of employees whose first name or last name matched with the
     * parameter.
     *
     * @param name - String used to match the first name or last name of the employee.
     * @return list of employees whose first name or last name matched with the string.
     */
    public List<Employee> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("com.bank.app.model.Employee.findByName")
                .setParameter("name", builder.toString())
        );
    }

    /**
     * Retrieve list of employees whose email contains the string passed in as a parameter.
     *
     * @param email - email string to be used to retrieve the employees.
     * @return list of employees.
     */
    public List<Employee> findByEmail(String email) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(email).append("%");
        return list(
                namedQuery("com.bank.app.model.Employee.findByEmail")
                        .setParameter("email", builder.toString())
        );
    }

    /**
     * Retrieve the employee identified by the id passed in as a parameter.
     *
     * @param employeeId - unique identifier of the employee.
     * @return the employee object with the identifier passed in as a parameter.
     */
    public Optional<Employee> findById(long employeeId) {
        return Optional.fromNullable(get(employeeId));
    }

    /**
     * Delete the employee identified by the id passed in as a parameter.
     *
     * @param employeeId - unique identifier of the employee.
     */
    public void delete(long employeeId) {
        Session session = this.currentSession();
        session.delete(session.get(Employee.class, employeeId));
        session.getTransaction().commit();
    }
}
