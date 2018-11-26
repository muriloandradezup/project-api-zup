package br.com.zup.restapi.projectapi.services;

import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.models.Customer;
import br.com.zup.restapi.projectapi.repository.CustomerRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {
    private Long id;
    private Customer customer;
    private String name = "José";
    private String newName = "João";
    private City city = new City("Uberlândia");
    private Long cityId = city.getId();
    private City newCity = new City("Uberlândia");
    private Long newCityId = newCity.getId();
    private List<Customer> customersList = new ArrayList<>();
    private Customer customer1 = new Customer("Fulano",city);
    private Customer customer2 = new Customer("Ciclano",city);
    private Pageable pageInfo = PageRequest.of(0,10, Sort.Direction.ASC,"name");
    private String keyWord = "PESQUISA";

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CityServiceBean cityService;

    @InjectMocks
    CustomerServiceBean customerService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        customer = new Customer(name,city);
        id = customer.getId();
        customersList.add(customer1);
        customersList.add(customer2);
    }

    @After
    public void tearDown(){
        customersList.clear();
    }

    @Test
    public void findOneCustomerTest(){
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        Customer foundCustomer = customerService.findCustomer(id);
        assertNotNull(foundCustomer);
        assertEquals(id, foundCustomer.getId());
    }

    @Test
    public void findNonExistentCustomerTest(){
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        Customer foundCustomer = customerService.findCustomer(id);
        assertNull(foundCustomer);
    }

    @Test
    public void findAllCustomersTest(){
        when(customerRepository.findAll(pageInfo)).thenReturn(new PageImpl<>(customersList));
        Page<Customer> list = customerService.findAllCustomers(pageInfo);
        assertNotNull(list.getContent());
        assertEquals(2,list.getTotalElements());
        assertTrue(list.getContent().contains(customer1));
        assertTrue(list.getContent().contains(customer2));
    }

    @Test
    public void findEmptyCustomerListTest(){
        when(customerRepository.findAll(pageInfo)).thenReturn(Page.empty(pageInfo));
        Page<Customer> list = customerService.findAllCustomers(pageInfo);
        assertEquals(0,list.getTotalElements());
    }

    @Test
    public void createCustomerTest() throws CustomException {
        when(cityService.findCity(cityId)).thenReturn(city);
        when(customerRepository.saveAndFlush(any(Customer.class))).then(returnsFirstArg());
        Customer newCustomer = customerService.createCustomer(name, cityId);
        assertNotNull(newCustomer);
        assertEquals(name, newCustomer.getName());
    }

    @Test
    public void createBlankCustomerTest() throws CustomException {
        when(cityService.findCity(cityId)).thenReturn(city);
        when(customerRepository.saveAndFlush(any(Customer.class))).then(returnsFirstArg());
        Customer newCustomer = customerService.createCustomer("", cityId);
        assertNotNull(newCustomer);
        assertEquals("", newCustomer.getName());
    }

    @Test(expected = CustomException.class)
    public void createCustomerWithoutCityTest() throws CustomException {
        when(cityService.findCity(anyLong())).thenReturn(null);
        Customer newCustomer = customerService.createCustomer(name, cityId);
        assertNull(newCustomer);
        verify(customerRepository, times(0)).saveAndFlush(any(Customer.class));
    }

    @Test
    public void updateCustomerNameTest() throws CustomException {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.saveAndFlush(any(Customer.class))).then(returnsFirstArg());
        when(cityService.findCity(cityId)).thenReturn(city);
        Customer updatedCustomer = customerService.updateCustomer(id,newName,cityId);
        assertNotNull(updatedCustomer);
        assertEquals(id,updatedCustomer.getId());
        assertEquals(newName,updatedCustomer.getName());
        assertEquals(cityId,updatedCustomer.getCityId());
    }

    @Test
    public void updateCustomerCityTest() throws CustomException {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.saveAndFlush(any(Customer.class))).then(returnsFirstArg());
        when(cityService.findCity(newCityId)).thenReturn(newCity);
        Customer updatedCustomer = customerService.updateCustomer(id,name,newCityId);
        assertNotNull(updatedCustomer);
        assertEquals(id,updatedCustomer.getId());
        assertEquals(name,updatedCustomer.getName());
        assertEquals(newCityId,updatedCustomer.getCityId());
    }

    @Test(expected = CustomException.class)
    public void updateNonExistentCustomerTest() throws CustomException {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());
        Customer updatedCustomer = customerService.updateCustomer(id,newName,cityId);
        assertNull(updatedCustomer);
        verify(customerRepository, times(0)).saveAndFlush(any(Customer.class));
    }

    @Test(expected = CustomException.class)
    public void updateCustomerInvalidCityTest() throws CustomException {
        Customer updatedCustomer = customerService.updateCustomer(id,newName,cityId);
        assertNull(updatedCustomer);
        verify(customerRepository, times(0)).saveAndFlush(any(Customer.class));
    }

    @Test
    public void deleteCustomerTest() throws CustomException {
        customerService.deleteCustomer(id);
        verify(customerRepository,times(1)).deleteById(id);
    }

    @Test(expected = CustomException.class)
    public void deleteNonExistentCustomer() throws CustomException {
        doThrow(new RuntimeException("Cliente não pôde ser deletado!")).when(customerRepository).deleteById(anyLong());
        customerService.deleteCustomer(id);
        verify(customerRepository, times(0)).saveAndFlush(any(Customer.class));
    }

    @Test
    public void searchCustomerByNameReturnsPageTest(){
        when(customerRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo))
                .thenReturn(new PageImpl<>(customersList));
        Page<Customer> list = customerService.findCustomerByName(keyWord,pageInfo);
        assertNotNull(list.getContent());
        assertEquals(2,list.getTotalElements());
        assertTrue(list.getContent().contains(customer1));
        assertTrue(list.getContent().contains(customer2));
    }

    @Test
    public void cityNotFoundTest(){
        when(customerRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo))
                .thenReturn(Page.empty(pageInfo));
        Page<Customer> list = customerService.findCustomerByName(keyWord,pageInfo);
        assertEquals(0,list.getTotalElements());
    }
}
