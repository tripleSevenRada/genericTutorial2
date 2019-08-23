package com.etnetera.hr.repository;

import com.etnetera.hr.data.JavaScriptFrameworkVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JavaScriptFrameworkVersionRepository extends CrudRepository<JavaScriptFrameworkVersion, Long> {
    // Difference between CrudRepository and JpaRepository interfaces in Spring Data JPA.
    // https://www.javapedia.net/Spring-Data-Access/900
    Iterable<JavaScriptFrameworkVersion> findByFrameworkId(Long frameworkId);
}