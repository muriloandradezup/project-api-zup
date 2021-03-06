package br.com.zup.restapi.projectapi.repository;

import br.com.zup.restapi.projectapi.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Page<Customer> findByNameContainingIgnoreCaseOrderByName(String keyWord, Pageable pageInfo);

}
