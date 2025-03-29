package org.marketplace.persistence.connection;

import lombok.Getter;
import org.marketplace.persistence.model.PersistableEntity;

import jakarta.persistence.TypedQuery;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DatabaseConnection extends Connection {
    @Getter
    private DatabaseConnectionAbstract dbConnAbs;

    public DatabaseConnection(String persistenceUnit) {
        this.dbConnAbs = new DatabaseConnectionAbstract(persistenceUnit);
    }

    @Override
    public <T extends PersistableEntity> void save(T entity) throws Exception {
        this.dbConnAbs.executeTransaction(entityManager -> entityManager.persist(entity));
    }


    @Override
    public <T extends PersistableEntity> List<T> findAll(Class<T> entityType) throws Exception {
        String abstractQuery = "SELECT e FROM %s e".formatted(entityType.getSimpleName());

        TypedQuery<T> query = this.dbConnAbs
                .executeQueryTransaction(entityManager -> entityManager
                        .createQuery(abstractQuery, entityType), TypedQuery.class);

        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> Optional<T> findById(Class<T> entityType, Long id){
        T entity = this.dbConnAbs.executeQueryTransaction(entityManager ->
                entityManager.find(entityType, id), entityType);
        return entity!=null?Optional.of(entity):Optional.empty();
    }

    @Override
    public <T extends PersistableEntity> Optional<T> findFirstByParams(Class<T> entityType, ParameterPair ... params) {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery.setMaxResults(1);
        }, TypedQuery.class);
        T result = query.getResultList().stream().findFirst().orElse(null);
        return result!=null?Optional.of(result):Optional.empty();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllByParams(Class<T> entityType, ParameterPair... params) throws Exception {
        // Construct the base query string
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        // Construct the WHERE clause based on parameter names
        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        // Combine base query with the dynamic WHERE clause
        String finalQuery = baseQuery + whereClause;

        // Execute the query transaction and get the results as a list
        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            return typedQuery;
        }, TypedQuery.class);

        // Return the full result list
        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> List<T> findAllByParamsLimited(Class<T> entityType, int offset, int limit, ParameterPair... params) throws Exception {
        String baseQuery = "SELECT e FROM %s e WHERE ".formatted(entityType.getSimpleName());

        String whereClause = IntStream.range(0, params.length)
                .mapToObj(i -> "e." + params[i].getName() + " = :param" + i)
                .collect(Collectors.joining(" AND "));

        String finalQuery = baseQuery + whereClause;

        TypedQuery<T> query = this.dbConnAbs.executeQueryTransaction(entityManager -> {
            TypedQuery<T> typedQuery = entityManager.createQuery(finalQuery, entityType);
            for (int i = 0; i < params.length; i++) {
                typedQuery.setParameter("param" + i, params[i].getValue());
            }
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
            return typedQuery;
        }, TypedQuery.class);

        return query.getResultList();
    }

    @Override
    public <T extends PersistableEntity> void update(T entity) throws Exception {
        dbConnAbs.executeTransaction(entityManager -> entityManager.merge(entity));
    }

    @Override
    public <T extends PersistableEntity> void delete(T entity) throws Exception {
        T managedEntity = dbConnAbs.getEntityManager().merge(entity);
        dbConnAbs.executeTransaction(entityManager -> entityManager.remove(managedEntity));
    }

    @Override
    public void close() throws IOException {
        this.dbConnAbs.close();
    }
}