package com.blazebit.persistence.impl.hibernate;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.spi.*;
import org.hibernate.event.spi.EventSource;
import org.hibernate.loader.hql.QueryLoader;
import org.hibernate.query.internal.AbstractProducedQuery;
import org.hibernate.query.internal.QueryParameterBindingsImpl;
import org.hibernate.query.spi.QueryImplementor;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;

import com.blazebit.apt.service.ServiceProvider;
import org.hibernate.type.Type;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

@ServiceProvider(HibernateAccess.class)
public class Hibernate52Access implements HibernateAccess {

    private static final Logger LOG = Logger.getLogger(HibernateExtendedQuerySupport.class.getName());
    
    @Override
    public SessionImplementor wrapSession(SessionImplementor session, boolean generatedKeys, String[][] columns, HibernateReturningResult<?> returningResult) {
        JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
        
        Object jdbcCoordinatorProxy = Proxy.newProxyInstance(jdbcCoordinator.getClass().getClassLoader(), new Class[]{ JdbcCoordinator.class }, new JdbcCoordinatorInvocationHandler(jdbcCoordinator, session.getFactory(), generatedKeys, columns, returningResult));
        Object sessionProxy = Proxy.newProxyInstance(session.getClass().getClassLoader(), new Class[]{ SessionImplementor.class, EventSource.class }, new Hibernate52SessionInvocationHandler(session, jdbcCoordinatorProxy));
        return (SessionImplementor) sessionProxy;
    }

    @Override
    public void checkTransactionSynchStatus(SessionImplementor session) {
        TransactionCoordinator coordinator = session.getTransactionCoordinator();
        coordinator.pulse();
        if (coordinator instanceof JtaTransactionCoordinatorImpl) {
            ((JtaTransactionCoordinatorImpl) coordinator).getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
        }
    }

    @Override
    public void afterTransaction(SessionImplementor session, boolean success) {
        TransactionCoordinator coordinator = session.getTransactionCoordinator();
        if (!session.isTransactionInProgress() ) {
            session.getJdbcCoordinator().afterTransaction();
        }
        if (coordinator instanceof JtaTransactionCoordinatorImpl) {
            ((JtaTransactionCoordinatorImpl) coordinator).getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
        }
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public List<Object[]> list(QueryLoader queryLoader, SessionImplementor sessionImplementor, QueryParameters queryParameters) {
        return queryLoader.list(sessionImplementor, queryParameters);
    }

    @Override
    public List<Object> performList(HQLQueryPlan queryPlan, SessionImplementor sessionImplementor, QueryParameters queryParameters) {
        return queryPlan.performList(queryParameters, sessionImplementor);
    }

    @Override
    public int performExecuteUpdate(HQLQueryPlan queryPlan, SessionImplementor sessionImplementor, QueryParameters queryParameters) {
        return queryPlan.performExecuteUpdate(queryParameters, sessionImplementor);
    }

    @Override
    public QueryParameters getQueryParameters(Query hibernateQuery, Map<String, TypedValue> namedParams) {
        return ((AbstractProducedQuery) hibernateQuery).getQueryParameters();
    }

    @Override
    public Map<String, TypedValue> getNamedParams(Query hibernateQuery) {
        QueryParameterBindingsImpl queryParameterBindings = hibernateQuery.unwrap(QueryParameterBindingsImpl.class);
        return queryParameterBindings.collectNamedParameterBindings();
    }

    @Override
    public String expandParameterLists(SessionImplementor session, org.hibernate.Query hibernateQuery, Map<String, TypedValue> namedParamsCopy) {
        QueryParameterBindingsImpl queryParameterBindings = hibernateQuery.unwrap(QueryParameterBindingsImpl.class);
        SharedSessionContractImplementor producer = (SharedSessionContractImplementor) ((QueryImplementor<?>) hibernateQuery).getProducer();
        String query = hibernateQuery.getQueryString();

        query = queryParameterBindings.expandListValuedParameters(query, producer);
        return query;
    }

    private ExceptionConverter getExceptionConverter(EntityManager em) {
        return em.unwrap(SharedSessionContractImplementor.class).getExceptionConverter();
    }

    @Override
    public RuntimeException convert(EntityManager em, HibernateException e) {
        return getExceptionConverter(em).convert(e);
    }

    @Override
    public void handlePersistenceException(EntityManager em, PersistenceException e) {
        getExceptionConverter(em).convert(e);
    }

    @Override
    public void throwPersistenceException(EntityManager em, HibernateException e) {
        getExceptionConverter(em).convert(e);
    }

    @Override
    public QueryParameters createQueryParameters(
            final Type[] positionalParameterTypes,
            final Object[] positionalParameterValues,
            final Map<String,TypedValue> namedParameters,
            final LockOptions lockOptions,
            final RowSelection rowSelection,
            final boolean isReadOnlyInitialized,
            final boolean readOnly,
            final boolean cacheable,
            final String cacheRegion,
            //final boolean forceCacheRefresh,
            final String comment,
            final List<String> queryHints,
            final Serializable[] collectionKeys) {
        return new QueryParameters(
                positionalParameterTypes,
                positionalParameterValues,
                namedParameters,
                lockOptions,
                rowSelection,
                isReadOnlyInitialized,
                readOnly,
                cacheable,
                cacheRegion,
                comment,
                queryHints,
                collectionKeys,
                null
        );
    }

}