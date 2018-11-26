package br.com.zup.restapi.projectapi.controllers;

import br.com.zup.restapi.projectapi.AbstractTest;
import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.models.Customer;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CustomerControllerIntegrationTest extends AbstractTest {

    private City city;
    private Customer customer1;
    private Customer customer2;
    private long invalidId = -1;
    private Map<String,String> cityJson = new HashMap<>();
    private Map<String,Object> customerJson = new HashMap<>();
    private Customer newCustomer;
    private String mainPath = "/customers";
    private String searchPath = "/customers/search/findByNameIgnoreCaseContaining";
    private String namePath = "$.name";
    private String totalElementsPath = "$.page.totalElements";
    private String utf8 = StandardCharsets.UTF_8.name();

    @Before
    public void setUp() throws Exception {
        city = cityService.createCity("CIDADE");
        customer1 = customerService.createCustomer("CLIENTE1",city.getId());
        customer2 = customerService.createCustomer("CLIENTE2",city.getId());
        newCustomer = new Customer("NOVO CLIENTE",city);
        cityJson.put("id", city.getId().toString());
        cityJson.put("name",city.getName());
        customerJson.put("id",newCustomer.getId().toString());
        customerJson.put("name",newCustomer.getName());
        customerJson.put("city",new JSONObject(cityJson));

    }

    @After
    public void tearDown(){
        cityJson.clear();
        customerJson.clear();
    }

    @Test
    public void getOneCustomerTest() throws Exception{
        mockMvc.perform(get(mainPath+"/"+customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(namePath, Matchers.is(customer1.getName())));
    }

    @Test
    public void getNonExistentCustomerTest() throws Exception{
        this.mockMvc.perform(get(mainPath+"/"+invalidId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void getAllCustomersTest() throws Exception{
        this.mockMvc.perform(get(mainPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath, Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    public void createCustomerTest() throws Exception{
        this.mockMvc.perform(post(mainPath).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath(namePath, Matchers.is(newCustomer.getName())));
    }


    @Test
    public void updateCustomerTest() throws Exception{
        this.mockMvc.perform(put(mainPath+"/"+customer1.getId()).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(namePath, Matchers.is(newCustomer.getName())))
                .andExpect(jsonPath("$.cityId",Matchers.is((int) city.getId().longValue())));
    }

    @Test
    public void updateInvalidCustomerTest() throws Exception{
        this.mockMvc.perform(put(mainPath+"/"+invalidId).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCustomerTest() throws Exception{
        this.mockMvc.perform(delete(mainPath+"/"+customer2.getId()).characterEncoding(utf8))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidCustomerTest() throws Exception{
        this.mockMvc.perform(delete("/cities/"+invalidId).characterEncoding(utf8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchCustomerTest() throws Exception{
        this.mockMvc.perform(get(searchPath)
                .param("name",customer1.getName()).characterEncoding(utf8))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath,Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$._embedded.customers[0].name", Matchers.is(customer1.getName())));
    }

    @Test
    public void emptyCustomerSearchTest() throws Exception{
        this.mockMvc.perform(get(searchPath)
                .param("name","jfjnalknfkalnfaoajfkameglçrsmgms,r,msr.,bms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath,Matchers.is(0)));
    }

    @Test
    public void searchCustomerWithPageableTest() throws Exception{
        this.mockMvc.perform(get(searchPath)
                .param("name",customer1.getName()).param("page","0")
                .param("size", "20").param("sort","name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath,Matchers.greaterThanOrEqualTo(1)));
    }
}