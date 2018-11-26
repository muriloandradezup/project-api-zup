package br.com.zup.restapi.projectapi.services;

import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

Customer findCustomer(Long id);

Page<Customer> findAllCustomers(Pageable pageInfo);

Customer createCustomer(String name, Long cityId) throws CustomException;

Customer updateCustomer(Long id, String name, Long cityId) throws CustomException;

void deleteCustomer(Long id) throws CustomException;

Page<Customer> findCustomerByName(String keyWord, Pageable pageInfo);

}
