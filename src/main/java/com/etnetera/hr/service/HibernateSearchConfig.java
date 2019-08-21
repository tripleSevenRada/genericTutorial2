package com.etnetera.hr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateSearchConfig {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    HibernateSearchService hibernateSearchService() {
        HibernateSearchService service = new HibernateSearchService(entityManagerFactory);
        service.initializeHibernateSearch();
        return service;
    }

}
