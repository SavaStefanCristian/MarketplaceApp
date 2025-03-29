package org.marketplace.persistence.dao;


import lombok.Getter;
import org.marketplace.persistence.connection.Connection;
import org.marketplace.persistence.connection.ParameterPair;
import org.marketplace.persistence.model.PersistableEntity;

import java.util.List;
import java.util.Optional;

public class EntityDao<T extends PersistableEntity> {
    @Getter
    private final Connection connection;
    public EntityDao(Connection connection) {
        this.connection = connection;
    }

    public void save(T object) throws Exception {
        connection.save(object);
    }

    public Optional<T> findById(Class<T> entityType, Long id) throws Exception{
        return connection.findById(entityType, id);
    }


    public List<T> findAll(Class<T> entityType) throws Exception{
        return connection.findAll(entityType);
    }

    public Optional<T> findFirstByParams(Class<T> entityType, ParameterPair... params) throws Exception{
        return connection.findFirstByParams(entityType, params);
    }

    public List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception {
        return connection.findAllByParams(entityType, params);
    }

    public List<T> findAllByParamsLimited(Class<T> entityType, int offset, int limit, ParameterPair... params) throws Exception {
        return connection.findAllByParamsLimited(entityType, offset, limit, params);
    }

    public void update(T object) throws Exception {
        connection.update(object);
    }
    public void delete(T object) throws Exception {
        connection.delete(object);
    }
}
