package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.exception.HibernateSearchException;
import com.etnetera.hr.exception.ResourceNotFoundException;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.service.HibernateSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import javax.validation.Valid;
import java.util.List;

/**
 * Simple REST controller for accessing application logic.
 *
 * @author Etnetera
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptFrameworkController.class);
    private final JavaScriptFrameworkRepository repository;

    @Autowired
    private HibernateSearchService searchService;

    @Autowired
    public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/frameworks")
    public Iterable<JavaScriptFramework> frameworks() {
        return repository.findAll();
    }

    @PostMapping("/add")
    public JavaScriptFramework addFramework(@Valid @RequestBody JavaScriptFramework framework) {
        return repository.save(framework);
    }

    @GetMapping("/framework/{id}")
    public JavaScriptFramework getFrameworkById(@PathVariable(value = "id") Long frameworkId) {
        return repository.findById(frameworkId)
                .orElseThrow(() -> new ResourceNotFoundException("JavaScriptFramework", "id", frameworkId));
    }

    @PutMapping("/update/{id}")
    public JavaScriptFramework updateFramework(@PathVariable(value = "id") Long frameworkId,
                                               @Valid @RequestBody JavaScriptFramework framework) {
        JavaScriptFramework frameworkRetrieved = repository.findById(frameworkId)
                .orElseThrow(() -> new ResourceNotFoundException("JavaScriptFramework", "id", frameworkId));
        frameworkRetrieved.setName(framework.getName());
        return repository.save(frameworkRetrieved);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity deleteFramework(@PathVariable(value = "id") Long frameworkId) {
        JavaScriptFramework toBeDeleted = repository.findById(frameworkId)
                .orElseThrow(() -> new ResourceNotFoundException("JavaScriptFramework", "id", frameworkId));
        repository.delete(toBeDeleted);
        return ResponseEntity.ok().build();
    }

    @GetMapping("search/{onField}/{searchFor}")
    public List<JavaScriptFramework> searchByName(
            @PathVariable(value = "onField") String onField,
            @PathVariable(value = "searchFor") String searchFor) {

        List<JavaScriptFramework> searchResults = null;
        try {
            searchResults = searchService.search(searchFor, onField);
        } catch (NoResultException nre){
            throw new ResourceNotFoundException("JavaScriptFramework", onField, searchFor);
        } catch (Exception ex) {
            throw new HibernateSearchException(ex.getMessage());
        }
        if(searchResults == null || searchResults.isEmpty())
            throw new ResourceNotFoundException("JavaScriptFramework", onField, searchFor);
        return searchResults;
    }

    //TODO fuzzy search e.t.c

    @GetMapping("/testLogLevel")
    public String testLogLevel() {
        LOG.trace("This is a TRACE log");
        LOG.debug("This is a DEBUG log");
        LOG.info("This is an INFO log");
        LOG.warn("This is an WARN log");
        LOG.error("This is an ERROR log");
        return "Added some log output to console...";
    }

}
