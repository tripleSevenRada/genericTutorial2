package com.etnetera.hr.service;

import com.etnetera.hr.data.JavaScriptFramework;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

@Service
public class HibernateSearchService {

    private static final Logger LOG = LoggerFactory
            .getLogger(HibernateSearchService.class);

    @Autowired
    private final EntityManager entityManager;

    @Autowired
    HibernateSearchService(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    void initializeHibernateSearch() {
        try {
            FullTextEntityManager fullTextEntityManager =
                    Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // set interrupt flag
            LOG.error(e.getMessage());
        }
    }

    @Transactional
    public List<JavaScriptFramework> search(String searchFor,
                                            String onField) throws NoResultException {
        FullTextEntityManager fullTextEntityManager =
                Search.getFullTextEntityManager(entityManager);

        QueryBuilder qb = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(JavaScriptFramework.class)
                .get();

        Query keywordQuery = qb
                .keyword()
                .onField(onField)
                .matching(searchFor)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager
                .createFullTextQuery(keywordQuery, JavaScriptFramework.class);

        return jpaQuery.getResultList();
    }
    // TODO fuzzy search etc.
}
