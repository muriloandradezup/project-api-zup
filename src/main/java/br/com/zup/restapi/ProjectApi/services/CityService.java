package br.com.zup.restapi.ProjectApi.services;

import br.com.zup.restapi.ProjectApi.models.PagedContent;
import br.com.zup.restapi.ProjectApi.models.City;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {
    City findCity(Long id);

    Page<City> findAllCities(Pageable pageInfo);

    City createCity(String name);

    City updateCity(Long id, String newName);

    void deleteCity(Long id);

    Page<City> findCityByName(String keyWord, Pageable pageInfo);
}
