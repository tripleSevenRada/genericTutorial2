package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.rest.ValidationError;
import com.etnetera.hr.rest.ValidationErrorAlphabeticalComparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        frameworks.add(new JavaScriptFramework("frameworkXY"));
        frameworks.add(new JavaScriptFramework("frameworkXY"));
        frameworks.add(new JavaScriptFramework("frameworkXZ"));
        frameworks.add(new JavaScriptFramework("AframeworkXZ"));// for fuzzy search
        frameworks.add(new JavaScriptFramework("BframeworkXZ"));
        for (JavaScriptFramework f : frameworks) {
            mockMvc.perform(post("/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(f)));
        }
    }

    @Test
    public void A_testValidationErrorAlphabeticalComparator() {

        // Proc to cele?
        // Edge case: Name pro framework "   vice nez x whitespace   " je porusenim dvou
        // validacnich constraints. Validacni mechanismus negarantuje stejne poradi
        // odpovidajicich FieldError's v BindingResult. Proto custom sort, hlavne kvuli testum.

        Map<Integer, ValidationError> expectedOrder = new HashMap<Integer, ValidationError>() {
            {
                put(0, new ValidationError("", ""));
                put(1, new ValidationError(null, "value"));
                put(2, new ValidationError("value", null));
                put(3, new ValidationError(null, null));
                put(4, new ValidationError("A", "B"));
                put(5, new ValidationError("A", "C"));
                put(6, new ValidationError("B", "D"));
                put(7, new ValidationError("B", "E"));
            }
        };

        List<ValidationError> veList = new ArrayList<>();
        for (int i = 7; i >= 4; i--) veList.add(expectedOrder.get(i)); // reasonable reversed
        for (int i = 0; i < 4; i++) veList.add(expectedOrder.get(i)); // edge cases not reversed
        veList.sort(new ValidationErrorAlphabeticalComparator());
        //veList.forEach(it -> LOG.debug(it != null ? it.toString() : "null"));
        for (int i = 0; i < 8; i++) assert (veList.get(i) == expectedOrder.get(i));
    }

    @Test
    public void B_frameworksTest() throws Exception {
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
    public void C_addFrameworkInvalid() throws JsonProcessingException, Exception {
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
            // napadaji me obecnejsi otazky v souvislosti s mutability ve Springu
            framework.setName(blankName);
            mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(framework)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("name")))
                    .andExpect(jsonPath("$.errors[0].message", is("NotBlank"))); // ne NotEmpty
        }

        framework.setName("                                  ");// > 30
        mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                // ValidationErrorAlphabeticalComparator pro stejne poradi u errors tuples.
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("NotBlank")))
                .andExpect(jsonPath("$.errors[1].field", is("name")))
                .andExpect(jsonPath("$.errors[1].message", is("Size")))
                .andReturn();

        framework.setName("ThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirtyThirty");
        mockMvc.perform(post("/add").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("Size")));
    }

    //getFrameworkById
    @Test
    public void D_getFrameworkByIdTest() throws Exception {
        mockMvc.perform(get("/framework/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ReactJS")));
    }

    @Test
    public void E_getFrameworkByNonExistingIdTest() throws Exception {
        mockMvc.perform(get("/framework/1000").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        // custom vyjimka zachycena handlerem v EtnRestController
        // loguje do spring.log

        // 2019-08-21 12:30:21.452  WARN 1243 --- [main] c.e.hr.controller.EtnRestController
        // : JavaScriptFramework not found with id : '1000'
    }

    //updateFramework
    @Test
    public void F_updateFrameworkTest() throws Exception {
        JavaScriptFramework updated = new JavaScriptFramework("ReactJSupdated");
        mockMvc.perform(put("/update/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ReactJSupdated")));
        // direct retrieval sanity check
        mockMvc.perform(get("/framework/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("ReactJSupdated")));
    }

    @Test
    public void G_updateFrameworkInvalid() throws Exception {
        JavaScriptFramework updated = new JavaScriptFramework(" ");// blank name
        mockMvc.perform(put("/update/1").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updated)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("NotBlank")));
    }

    @Test
    public void H_updateFrameworkByNonExistingIdTest() throws Exception {
        JavaScriptFramework updated = new JavaScriptFramework("nonExistingId");
        mockMvc.perform(put("/update/999").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updated)))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        // custom vyjimka zachycena handlerem v EtnRestController
        // loguje do spring.log

        // 2019-08-21 13:35:26.662  WARN 9933 --- [main] c.e.hr.controller.EtnRestController
        // : JavaScriptFramework not found with id : '999'
    }

    @Test
    public void I_deleteFrameworkTest()throws Exception {
        // idempotency in REST:
        // http://restcookbook.com/HTTP%20Methods/idempotency/
        mockMvc.perform(delete("/delete/1"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/delete/1"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void J_hibernateSearchTest() throws Exception{
        String searchFor = "frameworkXY";
        MvcResult result = mockMvc.perform(get("/search/name/" + searchFor))
                .andExpect(status().isOk())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        List<JavaScriptFramework> frameworks = mapper
                .readValue(responseAsString, new TypeReference<List<JavaScriptFramework>>(){});
        assertEquals(3, frameworks.size());
        frameworks.forEach(searchResultFramework ->
                assertEquals(searchFor, searchResultFramework.getName()));
    }

    @Test
    public void K_hibernateSearchTestForNonExistingName() throws Exception{
        String searchFor = "frameworkNotYetInvented";
        mockMvc.perform(get("/search/name/" + searchFor))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
        // nebo vracet 204 a ocekavat prazdny list?
        // https://benramsey.com/blog/2008/05/http-status-204-no-content-and-205-reset-content/
        // https://stackoverflow.com/questions/11746894/what-is-the-proper-rest-response-code-for-a-valid-request-but-an-empty-data
    }

    @AfterClass
    public static void testLogLevels() {
        LOG.debug("Test DEBUG from JavaScriptFrameworkTests");
        LOG.info("Test INFO from JavaScriptFrameworkTests");
        LOG.warn("Test WARN from JavaScriptFrameworkTests");
        LOG.error("Test ERROR from JavaScriptFrameworkTests");
    }

}
