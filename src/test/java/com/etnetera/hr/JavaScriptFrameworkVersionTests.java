package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFrameworkVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JavaScriptFrameworkVersionTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptFrameworkVersionTests.class);
    private ObjectMapper mapper = new ObjectMapper();

    //@Autowired
    //private JavaScriptFrameworkRepository frameworkRepository;

    //@Autowired
    //private JavaScriptFrameworkVersionRepository versionRepository;

    @Test
    public void A_createVersionTest() throws Exception {
        JavaScriptFrameworkVersion version =
                new JavaScriptFrameworkVersion(1, 100, LocalDate.of(2030, 1, 1));
        MvcResult result = mockMvc.perform(post("/frameworks/3/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(version)))
                .andExpect(status().isOk())
                .andReturn();
    }

    //TODO validation tests

    @Test
    public void B_getAllVersionsByFrameworkIdTest() throws Exception{
        MvcResult result = mockMvc.perform(get("/frameworks/3/versions"))
                .andExpect(status().isOk())
                .andReturn();
    }
}
