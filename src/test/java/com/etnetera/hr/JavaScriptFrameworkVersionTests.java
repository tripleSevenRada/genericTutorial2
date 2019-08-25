package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFrameworkVersion;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        for (int i = 3; i >= 0; i--) {
            // constructor 1
            JavaScriptFrameworkVersion version =
                    new JavaScriptFrameworkVersion(i, i * 20, LocalDate.of(2030, 1, 1));
            MvcResult result = mockMvc.perform(post("/frameworks/3/versions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(version)))
                    .andExpect(status().isOk())
                    .andReturn();
        }
        // constructor 2
        mockMvc.perform(post("/frameworks/3/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(
                        new JavaScriptFrameworkVersion(4,1,2025,3))))
                .andExpect(status().isOk());
    }

    @Test
    public void B_createVersionInvalidTest() throws Exception{
        List<Integer> versionsMajor = new ArrayList(){
            {
                add(-1); add(1); add(1);
            }
        };
        List<Integer> hypeLevels = new ArrayList(){
            {
                add(1); add(-1); add(1);
            }
        };
        List<LocalDate> deprecationDates = new ArrayList(){
            {
                add(LocalDate.of(2030,1,1));
                add(LocalDate.of(2030,1,1));
                add(LocalDate.of(2018,1,1));
            }
        };
        //c2
        List<Integer> deprecationDatesYear = new ArrayList(){
            {
                add(1999);
                add(2025);
                add(LocalDate.now().getYear());
            }
        };
        List<Integer> deprecationDatesMonths = new ArrayList(){
            {
                add(LocalDate.now().plusMonths(1).getMonth().getValue());
                add(0);
                add(13);
            }
        };
        List<String> errorFieldsExpectedC1 = new ArrayList(){
            {
                add("versionMajor");
                add("hypeLevel");
                add("deprecationDate");
            }
        };
        List<String> errorMessagesExpectedC1 = new ArrayList(){
            {
                add("Positive or zero");
                add("Positive or zero");
                add("We are only interested in cutting edge stuff");
            }
        };

        for (int i = 0; i < 3; i++){
            // constructor 1
            JavaScriptFrameworkVersion versionC1 =
                    new JavaScriptFrameworkVersion(
                            versionsMajor.get(i),
                            hypeLevels.get(i),
                            deprecationDates.get(i));
            mockMvc.perform(post("/frameworks/3/versions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(versionC1)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is(errorFieldsExpectedC1.get(i))))
                    .andExpect(jsonPath("$.errors[0].message", is(errorMessagesExpectedC1.get(i))));

            // constructor 2
            JavaScriptFrameworkVersion versionC2 = null;
            try {
                versionC2 =
                        new JavaScriptFrameworkVersion(1, 1,
                                deprecationDatesYear.get(i), deprecationDatesMonths.get(i));
            } catch (DateTimeException e){
                assert (i == 1 || i == 2);
                continue;
            }
            mockMvc.perform(post("/frameworks/3/versions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(versionC2)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].field", is("deprecationDate")))
                    .andExpect(jsonPath("$.errors[0].message", is("We are only interested in cutting edge stuff")));
        }
    }

    @Test
    public void C_getAllVersionsByFrameworkIdTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/frameworks/3/versions"))
                .andExpect(status().isOk())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        List<JavaScriptFrameworkVersion> versions = mapper
                .readValue(responseAsString, new TypeReference<List<JavaScriptFrameworkVersion>>(){});
        Collections.sort(versions); //TODO @OrderBy?
        assertEquals(5, versions.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(i, versions.get(i).getVersionMajor());
        }
    }
}
