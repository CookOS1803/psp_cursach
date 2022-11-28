package com.cookos.dao;

import java.util.List;

import org.hibernate.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.Configuration;

import jakarta.persistence.NoResultException;

public class GenericDao<T> {
    protected static final StandardServiceRegistry serviceRegistry;
    protected static final SessionFactory sessionFactory;
    protected static final Configuration configuration;
    protected final Class<T> type;

    static {
        serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();

        var meta = new MetadataSources(serviceRegistry).getMetadataBuilder().build();  
        sessionFactory = meta.getSessionFactoryBuilder().build();

        configuration = new Configuration();
    }

    public GenericDao(Class<T> type) {
        this.type = type;
        configuration.addAnnotatedClass(type);
    }
    
    public List<T> selectAll() {
        try (var session = sessionFactory.openSession()) {
            
            var query = session.getCriteriaBuilder().createQuery(type);
            var root = query.from(type);

            query.select(root);

            var selected = session.createQuery(query).list();

            return selected;
        }
    }

    public T findById(int id) {
        return findByColumn("id", id);
    }

    public T findByColumn(String columnName, Object value) {
        try (var session = sessionFactory.openSession()) {
            var cb = session.getCriteriaBuilder();
            var query = cb.createQuery(type);
            var root = query.from(type);

            query.select(root).where(cb.equal(root.get(columnName), value));

            try {
                var result = session.createQuery(query).getSingleResult();
                return result;
            } catch (NoResultException e) {
                return null;
            }
        }
    }

    public void update(T newRecord) {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            session.merge(newRecord);
            
            transaction.commit();            
        }
    }

    public int add(T newRecord) throws Exception {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            session.persist(newRecord);
            var id = (Integer)session.getIdentifier(newRecord);
            
            transaction.commit();

            return id;
        }
    }

    public void remove(T existingRecord) throws Exception {
        try (var session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            session.remove(existingRecord);
            
            transaction.commit();         
        }
    }
}
