package com.cookos.dao;

import java.util.List;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;

public class GenericDao<T> implements AutoCloseable {
    protected static final EntityManagerFactory entityManagerFactory;
    protected final Class<T> type;

    protected EntityManager entityManager = null;

    static {
        entityManagerFactory = Persistence.createEntityManagerFactory("persik");
    }

    public GenericDao(Class<T> type) {
        this.type = type;
        entityManager = entityManagerFactory.createEntityManager();
    }
    
    public List<T> selectAll() {
        var query = entityManager.getCriteriaBuilder().createQuery(type);
        var root = query.from(type);

        query.select(root);

        var result = entityManager.createQuery(query);
        var selected = result.getResultList();

        return selected;
    }

    public T findByUniqueColumn(String columnName, Object value) {
        var list = findByColumn(columnName, value);

        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public List<T> findByColumn(String columnName, Object value) {
        var cb = entityManager.getCriteriaBuilder();
        var query = cb.createQuery(type);
        var root = query.from(type);

        query.select(root).where(cb.equal(root.get(columnName), value));

        try {
            var result = entityManager.createQuery(query).getResultList();
            return result;
        } catch (NoResultException e) {
            return null;
        }
    }

    public void update(T newRecord) {
        entityManager.getTransaction().begin();
        entityManager.merge(newRecord);
        entityManager.getTransaction().commit();
    }

    public void add(T newRecord) throws Exception {
        entityManager.getTransaction().begin();
        entityManager.persist(newRecord);
        entityManager.getTransaction().commit();        
    }

    public void remove(T existingRecord) throws Exception {
        entityManager.getTransaction().begin();
        entityManager.remove(existingRecord);        
        entityManager.getTransaction().commit();
    }

    @Override
    public void close() throws Exception {
        entityManager.close();
    }

}
