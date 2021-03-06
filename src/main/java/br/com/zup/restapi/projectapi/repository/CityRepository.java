package br.com.zup.restapi.projectapi.repository;

import br.com.zup.restapi.projectapi.models.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City,Long> {
    Page<City> findByNameContainingIgnoreCaseOrderByName(String keyWord, Pageable pageInfo);
}
