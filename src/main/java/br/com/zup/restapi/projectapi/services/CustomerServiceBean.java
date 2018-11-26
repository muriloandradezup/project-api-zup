package br.com.zup.restapi.projectapi.services;

import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.models.Customer;
import br.com.zup.restapi.projectapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class CustomerServiceBean implements CustomerService{

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CityServiceBean cityService;

    @Override
    public Customer findCustomer(Long id) {
        Optional<Customer> foundCustomer = customerRepository.findById(id);
        return foundCustomer.orElse(null);
    }

    @Override
    public Page<Customer> findAllCustomers(Pageable pageInfo) {
        return customerRepository.findAll(pageInfo);
    }

    @Override
    public Customer createCustomer(String name, Long cityId) throws CustomException {
        City city = cityService.findCity(cityId);
        if (city != null){
            return customerRepository.saveAndFlush(new Customer(name, city));
        } else
            throw new CustomException(HttpStatus.BAD_REQUEST,"Erro ao criar cliente: Cidade inválida");
    }

    @Override
    public Customer updateCustomer(Long id, String newName, Long cityId) throws CustomException {
        Customer foundCustomer = this.findCustomer(id);
        if (foundCustomer !=null){
                if (cityId!=null) {
                    City foundCity = cityService.findCity(cityId);
                    if (foundCity !=null){
                        foundCustomer.setCity(foundCity);
                    } else throw new CustomException(HttpStatus.BAD_REQUEST,"Erro ao editar cliente: Cidade inválida");
                }
                if (newName!=null) {
                    foundCustomer.setName(newName);
                }
                return customerRepository.saveAndFlush(foundCustomer);
        } else  throw new CustomException(HttpStatus.NOT_FOUND,"Cliente não encontrado");
    }

    @Override
    public void deleteCustomer(Long id) throws CustomException {
        try {
            customerRepository.deleteById(id);
        } catch (Exception e){
            throw new CustomException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @Override
    public Page<Customer> findCustomerByName(String keyWord, Pageable pageInfo) {
        return  customerRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo);
    }
}
