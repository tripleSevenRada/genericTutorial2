package com.etnetera.hr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
