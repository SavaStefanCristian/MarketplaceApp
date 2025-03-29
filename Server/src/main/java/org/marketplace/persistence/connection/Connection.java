package org.marketplace.persistence.connection;

import org.marketplace.persistence.model.PersistableEntity;

import java.io.Closeable;
import java.util.List;
import java.util.Optional;

public abstract class Connection implements Closeable {
    public abstract <T extends PersistableEntity> void save(T entity) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception;
    public abstract <T extends PersistableEntity> Optional<T> findFirstByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> Optional<T> findById(Class<T> entityType, Long id) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair ... params) throws Exception;
    public abstract <T extends PersistableEntity> List<T> findAllByParamsLimited(Class<T> entityType, int offset, int limit, ParameterPair... params) throws Exception;
    public abstract <T extends PersistableEntity> void update (T entity) throws Exception;
    public abstract <T extends PersistableEntity> void delete (T entity) throws Exception;
}
