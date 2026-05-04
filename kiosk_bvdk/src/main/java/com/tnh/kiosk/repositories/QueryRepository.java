package com.tnh.kiosk.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QueryRepository {

    private final EntityManager entityManager;

    public QueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Tuple> getData(String sql, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        if (params != null) {
            params.forEach(query::setParameter);
        }
        return query.getResultList();
    }
}