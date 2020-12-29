package com.bank.app.db;

import com.bank.app.model.Employee;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class EmployeeDAO extends AbstractDAO<Employee> {

    public EmployeeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Employee> findAll() {
        return list(namedQuery("com.bank.app.model.Employee.findAll"));
    }

    public List<Employee> findByName(String name) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(name).append("%");
        return list(
                namedQuery("com.bank.app.model.Employee.findByName")
                .setParameter("name", builder.toString())
        );
    }

    public List<Employee> findByEmail(String email) {
        StringBuilder builder = new StringBuilder("%");
        builder.append(email).append("%");
        return list(
                namedQuery("com.bank.app.model.Employee.findByEmail")
                        .setParameter("email", builder.toString())
        );
    }

    public Optional<Employee> findById(long id) {
        return Optional.fromNullable(get(id));
    }

    public void add(Employee e) {
        persist(e);
    }

    public void delete(long id) {
        Session session = this.currentSession();
        session.delete(session.get(Employee.class, id));
        session.getTransaction().commit();
    }
}
