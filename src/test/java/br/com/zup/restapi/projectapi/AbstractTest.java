package br.com.zup.restapi.projectapi;

import br.com.zup.restapi.projectapi.controllers.CityController;
import br.com.zup.restapi.projectapi.controllers.CustomerController;
import br.com.zup.restapi.projectapi.repository.CityRepository;
import br.com.zup.restapi.projectapi.repository.CustomerRepository;
import br.com.zup.restapi.projectapi.services.CityServiceBean;
import br.com.zup.restapi.projectapi.services.CustomerServiceBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProjectApiApplication.class)
@Transactional
public abstract class AbstractTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    public WebApplicationContext context;

    @Autowired
    public CityController cityController;

    @Autowired
    public CityServiceBean cityService;

    @Autowired
    public CityRepository cityRepository;

    @Autowired
    public CustomerController customerController;

    @Autowired
    public CustomerServiceBean customerService;

    @Autowired
    public CustomerRepository customerRepository;

    protected MockMvc mockMvc;

    @Before
    public void init() {

        RestDocumentationResultHandler documentationResultHandler = MockMvcRestDocumentation.document("{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
                .alwaysDo(documentationResultHandler)
                .build();
    }
}