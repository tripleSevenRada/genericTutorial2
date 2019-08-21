package com.etnetera.hr.service;

import com.etnetera.hr.data.JavaScriptFramework;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

@Service
public class HibernateSearchService {
    @Autowired
    private final EntityManager entityManager;

    @Autowired
    HibernateSearchService(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    void initializeHibernateSearch() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public List<JavaScriptFramework> fuzzySearch(String searchTerm,
                                                 String onField,
                                                 int editDistance,
                                                 int prefixLength) {

        FullTextEntityManager fullTextEntityManager =
                Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(JavaScriptFramework.class)
                .get();
        Query luceneQuery = qb.keyword()
                .fuzzy().withEditDistanceUpTo(editDistance)
                .withPrefixLength(prefixLength).onFields(onField)
                .matching(searchTerm)
                .createQuery();

        javax.persistence.Query jpaQuery = fullTextEntityManager
                .createFullTextQuery(luceneQuery, JavaScriptFramework.class);

        // execute search

        List<JavaScriptFramework> frameworks = null;
        try {
            frameworks = jpaQuery.getResultList();
        } catch (NoResultException nre) {
            // do nothing
        }
        return frameworks;
    }
}
