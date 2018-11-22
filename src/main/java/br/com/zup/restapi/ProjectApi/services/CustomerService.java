package br.com.zup.restapi.ProjectApi.services;

import br.com.zup.restapi.ProjectApi.models.Customer;

import br.com.zup.restapi.ProjectApi.models.PagedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

Customer findCustomer(Long id);

Page<Customer> findAllCustomers(Pageable pageInfo);

Customer createCustomer(String name, Long cityId);

Customer updateCustomer(Long id, String name, Long cityId);

void deleteCustomer(Long id);

Page<Customer> findCustomerByName(String keyWord, Pageable pageInfo);

}
