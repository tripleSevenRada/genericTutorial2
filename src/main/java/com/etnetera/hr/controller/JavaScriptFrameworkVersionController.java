package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFrameworkVersion;
import com.etnetera.hr.exception.ResourceNotFoundException;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.repository.JavaScriptFrameworkVersionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class JavaScriptFrameworkVersionController extends EtnRestController {

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptFrameworkController.class);

    private JavaScriptFrameworkRepository frameworkRepository;
    private JavaScriptFrameworkVersionRepository versionRepository;

    @Autowired
    public JavaScriptFrameworkVersionController(
            JavaScriptFrameworkRepository frameworkRepository,
            JavaScriptFrameworkVersionRepository versionRepository
    ) {
        this.frameworkRepository = frameworkRepository;
        this.versionRepository = versionRepository;
    }

    @PostMapping("/frameworks/{frameworkId}/versions")
    public JavaScriptFrameworkVersion createVersion(@PathVariable(value = "frameworkId") Long frameworkId,
                                                    @Valid @RequestBody JavaScriptFrameworkVersion version) {
        return frameworkRepository.findById(frameworkId).map(
                framework -> {
                    version.setFramework(framework);
                    return versionRepository.save(version);
                }).orElseThrow(() ->
                new ResourceNotFoundException("JavaScriptFramework", "id", frameworkId));
    }

    @GetMapping("/frameworks/{frameworkId}/versions")
    public Iterable<JavaScriptFrameworkVersion> getAllVersionsByFrameworkId
            (@PathVariable(value = "frameworkId") Long frameworkId) {
        return versionRepository.findAllByFrameworkId(frameworkId);
    }

    @DeleteMapping("/frameworks/{frameworkId}/versions/{versionId}")
    public ResponseEntity<?> deleteVersion(@PathVariable (value = "frameworkId") Long frameworkId,
                                           @PathVariable (value = "versionId") Long versionId) {
        return versionRepository.findByIdAndFrameworkId(frameworkId, versionId).map(version -> {
            versionRepository.delete(version);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("JavaScriptFrameworkVersion" , "id", versionId));
    }
}
