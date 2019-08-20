package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Class used for Spring Boot/MVC based tests.
 *
 * @author Etnetera
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class JavaScriptFrameworkTests {

    @Autowired
    private MockMvc mockMvc;

    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptFrameworkTests.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JavaScriptFrameworkRepository repository;

    private List<JavaScriptFramework> frameworks;

    private void prepareData() throws Exception {
        frameworks = new ArrayList<JavaScriptFramework>();
        frameworks.add(new JavaScriptFramework("ReactJS"));
        frameworks.add(new JavaScriptFramework("Vue.js"));
        frameworks.add(new JavaScriptFramework("frameworkXY"));
        for (JavaScriptFramework f : frameworks) {
            mockMvc.perform(post("/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(f)));
        }
    }

    @Test
    public void A_frameworksTest() throws Exception {
        prepareData();
        mockMvc.perform(get("/frameworks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(frameworks.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("ReactJS")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Vue.js")));
    }

    @Test
    public void B_addFrameworkInvalid() throws JsonProcessingException, Exception {
        JavaScriptFramework framework = new JavaScriptFramework();
        // V JavascriptFramework.java je @NotBlank name,
        // "      " neni myslim validni jmeno pro framework jak by naznacovalo zadani.
        List<String> allBlankNames = new ArrayList<String>() {
            {
                add(null);
                add("");
                add(" ");
                add("            "); // < 30
            }
        };

        for (String blankName : allBlankNames) {
            // Je mutability nutna? To by byla moje otazka tady.
            framework.setName(blankName);
            mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("name")))
                    .andExpect(jsonPath("$.errors[0].message", is("NotBlank"))); // ne NotEmpty
        }

        framework.setName("                                  ");// > 30
        mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("NotBlank")))
                .andExpect(jsonPath("$.errors[1].field", is("name")))
                .andExpect(jsonPath("$.errors[1].message", is("Size")))
                .andReturn();

        framework.setName("ThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirty");
        mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("Size")));
    }

    //getFrameworkById
    @Test
    public void C_getFrameworkByIdTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/framework/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ReactJS")))
                .andReturn();
        LOG.debug("MvcResult -- C_getFrameworkByIdTest(): " + result.getResponse().getContentAsString());
    }

    @Test
    public void D_getFrameworkByNonExistingIdTest() throws Exception {
        mockMvc.perform(get("/framework/1000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @AfterClass
    public static void testLogLevels() {
        LOG.debug("Test DEBUG from JavaScriptFrameworkTests");
        LOG.info("Test INFO from JavaScriptFrameworkTests");
        LOG.warn("Test WARN from JavaScriptFrameworkTests");
        LOG.error("Test ERROR from JavaScriptFrameworkTests");
    }

}
