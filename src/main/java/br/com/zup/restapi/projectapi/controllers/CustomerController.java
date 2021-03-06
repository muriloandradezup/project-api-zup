package br.com.zup.restapi.projectapi.controllers;

import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.Customer;
import br.com.zup.restapi.projectapi.models.PagedContent;
import br.com.zup.restapi.projectapi.services.CustomerServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Customer createCustomer(@RequestBody Customer customer) throws CustomException {
        return customerService.createCustomer(customer.getName(), customer.getCityId());
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) throws CustomException {
        Long cityId;
        if (customer.getCity() == null){
            cityId = null;
        } else cityId = customer.getCityId();
            Customer updatedCustomer = customerService.updateCustomer(id, customer.getName(), cityId);
            return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity deleteCustomer(@PathVariable("id") Long id) throws CustomException {
        customerService.deleteCustomer(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/customers/search/findByNameIgnoreCaseContaining")
    public PagedContent searchCustomerByName(@RequestParam(value = "name") String keyword, Pageable pageInfo) {
        return new PagedContent(customerService.findCustomerByName(keyword, pageInfo),pageType);
    }
}