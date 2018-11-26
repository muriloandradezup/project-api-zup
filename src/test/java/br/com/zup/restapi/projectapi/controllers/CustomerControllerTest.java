package br.com.zup.restapi.projectapi.controllers;

import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.models.Customer;
import br.com.zup.restapi.projectapi.services.CustomerServiceBean;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @MockBean
    CustomerServiceBean customerService;

    @Autowired
    MockMvc mockMvc;

    private Long id;
    private Customer customer;
    private City city = new City("Uberlândia");
    private City newCity = new City("Uberlândia");
    private List<Customer> customersList = new ArrayList<>();
    private Customer customer1 = new Customer("Fulano",city);
    private Customer customer2 = new Customer("Ciclano",city);
    private Pageable pageInfo = PageRequest.of(0,10, Sort.Direction.ASC,"name");
    private Map<String,String> cityJson = new HashMap<>();
    private Map<String,String> customerJson = new HashMap<>();
    private String mainPath = "/customers";
    private String searchPath = "/customers/search/findByNameIgnoreCaseContaining";
    private String namePath = "$.name";
    private String totalElementsPath = "$.page.totalElements";
    private String utf8 = StandardCharsets.UTF_8.name();
    private String name0 = "$._embedded.customers[0].name";
    private String name1 = "$._embedded.customers[1].name";


    @Before
    public void setUp(){
        String name = "José";
        customer = new Customer(name,city);
        id = customer.getId();
        customersList.add(customer1);
        customersList.add(customer2);
        cityJson.put("id", city.getId().toString());
        cityJson.put("name",city.getName());
        customerJson.put("id",customer.getId().toString());
        customerJson.put("name",customer.getName());
        customerJson.put("city",cityJson.toString());
    }

    @After
    public void tearDown(){
        customersList.clear();
        cityJson.clear();
        customerJson.clear();
    }

    @Test
    public void getOneCustomerTest() throws Exception{
        when(customerService.findCustomer(anyLong())).thenReturn(customer);
        mockMvc.perform(get(mainPath+"/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(namePath, Matchers.is(customer.getName())));
    }

    @Test
    public void getNonExistentCustomerTest() throws Exception{
        when(customerService.findCustomer(id)).thenReturn(null);
        mockMvc.perform(get(mainPath+"/"+id))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCustomersTest() throws Exception{
        when(customerService.findAllCustomers(notNull())).thenReturn(new PageImpl<>(customersList));
        mockMvc.perform(get(mainPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath, Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath(name0, Matchers.is(customer1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(customer2.getName())));
    }

    @Test
    public void getEmptyCustomerList() throws Exception{
        when(customerService.findAllCustomers(notNull())).thenReturn(Page.empty(pageInfo));
        mockMvc.perform(get(mainPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers", Matchers.hasSize(0)));
    }

    @Test
    public void createCustomerTest() throws Exception{
        when(customerService.createCustomer(anyString(),anyLong())).thenReturn(customer);
        mockMvc.perform(post(mainPath).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(customer.getId().intValue())))
                .andExpect(jsonPath(namePath, Matchers.is(customer.getName())));
    }


    @Test
    public void updateCustomerTest() throws Exception{
        String newName = "João";
        customer.setName(newName);
        customer.setCity(newCity);
        when(customerService.updateCustomer(anyLong(),anyString(),anyLong())).thenReturn(customer);
        mockMvc.perform(put(mainPath+"/"+id).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andExpect(jsonPath(namePath, Matchers.is(newName)))
                .andExpect(jsonPath("$.cityId",Matchers.is((int) newCity.getId().longValue())));
    }

    @Test
    public void updateInvalidCustomerTest() throws Exception{
        when(customerService.updateCustomer(anyLong(),anyString(),anyLong()))
                .thenThrow(new RuntimeException("Update inválido!"));
        mockMvc.perform(put(mainPath+"/"+id).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(customerJson).toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$",Matchers.is("Update inválido!")));
    }

    @Test
    public void deleteCustomerTest() throws Exception{
        mockMvc.perform(delete(mainPath+"/"+id).characterEncoding(utf8))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidCustomerTest() throws Exception{
        mockMvc.perform(delete("/cities/"+id).characterEncoding(utf8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchCustomerTest() throws Exception{
        when(customerService.findCustomerByName(notNull(),notNull())).thenReturn(new PageImpl<>(customersList));
        mockMvc.perform(get(searchPath)
                .param("name","aaaaaa").characterEncoding(utf8))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath,Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath(name0, Matchers.is(customer1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(customer2.getName())));
    }

    @Test
    public void emptyCustomerSearchTest() throws Exception{
        when(customerService.findCustomerByName(notNull(),notNull())).thenReturn(Page.empty(pageInfo));
        mockMvc.perform(get(searchPath)
                .param("name","bbbbb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers",Matchers.hasSize(0)));
    }

    @Test
    public void searchCustomerWithPageableTest() throws Exception{
        when(customerService.findCustomerByName(notNull(),notNull())).thenReturn(new PageImpl<>(customersList));
        mockMvc.perform(get(searchPath)
                .param("name","cccccc").param("page","0")
                .param("size", "20").param("sort","name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(totalElementsPath,Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath(name0, Matchers.is(customer1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(customer2.getName())));
    }
}
