package br.com.zup.restapi.ProjectApi.controllers;

import br.com.zup.restapi.ProjectApi.models.Customer;
import br.com.zup.restapi.ProjectApi.models.PagedContent;
import br.com.zup.restapi.ProjectApi.services.CustomerServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

    private String pageType = "customers";


    @Autowired
    CustomerServiceBean customerService;

    @GetMapping("/customers/{id}")
    public Customer getOneCustomer(@PathVariable Long id) {
        return customerService.findCustomer(id);
    }

    @GetMapping("/customers")
    public PagedContent getAllCustomers(Pageable pageInfo) {
        return new PagedContent(customerService.findAllCustomers(pageInfo),pageType);
    }

    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody Customer customer) {
        try {
            return customerService.createCustomer(customer.getName(), customer.getCityId());
        } catch (RuntimeException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) {
        Long cityId;
        if (customer.getCity() == null){
            cityId = null;
        } else cityId = customer.getCityId();

        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer.getName(), cityId);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
        } catch (RuntimeException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity deleteCustomer(@PathVariable("id") Long id) {
        try {
            customerService.deleteCustomer(id);
        } catch (RuntimeException error) {
            throw new RuntimeException(error.getMessage());
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/customers/search/findByNameIgnoreCaseContaining")
    public PagedContent searchCustomerByName(@RequestParam(value = "name") String keyword, Pageable pageInfo) {
        return new PagedContent(customerService.findCustomerByName(keyword, pageInfo),pageType);
    }
}