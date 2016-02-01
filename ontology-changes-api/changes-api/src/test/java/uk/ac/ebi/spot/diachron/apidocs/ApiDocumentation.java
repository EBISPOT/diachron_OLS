package uk.ac.ebi.spot.diachron.apidocs;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.spot.diachron.ChangesApp;
import uk.ac.ebi.spot.diachron.ChangesWebApplication;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.request.RequestDocumentation.*;

/**
 * @author Simon Jupp
 * @date 29/01/2016
 * Samples, Phenotypes and Ontologies Team, EMBL-EBI
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ChangesWebApplication.class)
@WebAppConfiguration
@Ignore
public class ApiDocumentation {

    @Rule
    public final RestDocumentation restDocumentation = new RestDocumentation("src/main/asciidoc/generated-snippets");

    private RestDocumentationResultHandler document;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.document = document("{method-name}"
                ,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())
        );

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation)
                )
                .alwaysDo(this.document)
                .build();
    }

    @Test
    public void apiExample () throws Exception {

        this.document.snippets(
                responseFields(
                        fieldWithPath("_links").description("Links to other resources")
                ),
                links(halLinks(),
                        linkWithRel("changesummaries").description("Collection of change summaries for all ontologies"),
                        linkWithRel("changes").description("Collection of specific changes for all ontologies"),
                        linkWithRel("profile").description("Link to all the properties in OLS")
                        )
        );
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void changesExample () throws Exception {

        this.document.snippets(
                   responseFields(
                           fieldWithPath("_embedded").description("Date changed"),
                           fieldWithPath("page").description("pages"),
                           fieldWithPath("_links").description("Links to other resources")
                   ),
                   links(halLinks(),
                           linkWithRel("self").description("Collection of change summaries for all ontologies"),
                           linkWithRel("profile").description("Collection of specific changes for all ontologies")
                           )
           );
           this.mockMvc.perform(get("/changes"))
                   .andExpect(status().isOk());

    }


}
