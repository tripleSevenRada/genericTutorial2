package com.etnetera.hr.repository;

import com.etnetera.hr.data.JavaScriptFrameworkVersion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JavaScriptFrameworkVersionRepository extends CrudRepository<JavaScriptFrameworkVersion, Long> {
    // Difference between CrudRepository and JpaRepository interfaces in Spring Data JPA.
    // https://www.javapedia.net/Spring-Data-Access/900
    Iterable<JavaScriptFrameworkVersion> findAllByFrameworkId(Long frameworkId);
    Optional<JavaScriptFrameworkVersion> findByIdAndFrameworkId(Long id, Long frameworkId);
}