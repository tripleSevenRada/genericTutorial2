package com.etnetera.hr.repository;

import org.springframework.data.repository.CrudRepository;

import com.etnetera.hr.data.JavaScriptFramework;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * Spring data repository interface used for accessing the data in database.
 * 
 * @author Etnetera
 *
 */
@Repository
public interface JavaScriptFrameworkRepository extends CrudRepository<JavaScriptFramework, Long> {
    // Difference between CrudRepository and JpaRepository interfaces in Spring Data JPA.
    // https://www.javapedia.net/Spring-Data-Access/900
}
