package br.com.zup.restapi.ProjectApi.services;

import br.com.zup.restapi.ProjectApi.models.PagedContent;
import br.com.zup.restapi.ProjectApi.models.City;
import br.com.zup.restapi.ProjectApi.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class CityServiceBean implements CityService {

    @Autowired
    CityRepository cityRepository;

    @Override
    public City findCity(Long id){
        Optional<City> foundCity = cityRepository.findById(id);
        return foundCity.orElse(null);
    }

    @Override
    public Page<City> findAllCities(Pageable pageInfo){
        return cityRepository.findAll(pageInfo);
    }

    @Override
    public City createCity(String name){
        return cityRepository.saveAndFlush(new City(name));
    }

    @Override
    public City updateCity(Long id, String newName){
        City foundCity = this.findCity(id);
        if(foundCity != null) {
            foundCity.setName(newName);
            return cityRepository.saveAndFlush(foundCity);
        } else throw new RuntimeException("Cidade inexistente!");
    }

    @Override
    public void deleteCity(Long id) {
        cityRepository.deleteById(id);
    }

    @Override
    public Page<City> findCityByName(String keyWord, Pageable pageInfo){
        return cityRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo);
    }
}
