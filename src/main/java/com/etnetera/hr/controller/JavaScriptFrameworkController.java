package com.etnetera.hr.controller;

import com.etnetera.hr.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;

import javax.validation.Valid;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

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
    public JavaScriptFramework addFramework(@Valid @RequestBody JavaScriptFramework framework){
	    return repository.save(framework);
    }

    @GetMapping("/framework/{id}")
	public JavaScriptFramework getFrameworkById(@PathVariable(value = "id") Long frameworkId){
		return repository.findById(frameworkId)
				.orElseThrow(() -> new ResourceNotFoundException("framework","id", frameworkId));
	}
}
