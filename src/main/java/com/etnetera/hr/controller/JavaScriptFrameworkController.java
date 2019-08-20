package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.exception.ResourceNotFoundException;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
