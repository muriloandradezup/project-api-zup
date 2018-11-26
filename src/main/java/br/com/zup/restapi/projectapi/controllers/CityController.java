package br.com.zup.restapi.projectapi.controllers;

import br.com.zup.restapi.projectapi.models.PagedContent;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.services.CityServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CityController {

    private String pageType = "cities";

    @Autowired
    CityServiceBean cityService;

    @GetMapping("/cities")
    public PagedContent getAllCities(Pageable pageInfo) {
        return new PagedContent(cityService.findAllCities(pageInfo),pageType);
    }

    @GetMapping("/cities/{id}")
    public City getOneCity(@PathVariable Long id) {
        return cityService.findCity(id);
    }

    @PostMapping("/cities")
    @ResponseStatus(HttpStatus.CREATED)
    public City createCity(@RequestBody City city) {
        return cityService.createCity(city.getName());
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<City> updateCity(@PathVariable("id") Long id, @RequestBody City city) {
        try {
            City updatedCity = cityService.updateCity(id, city.getName());
            return new ResponseEntity<>(updatedCity, HttpStatus.OK);
        } catch (RuntimeException error) {
            throw new RuntimeException(error.getMessage());
        }
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity deleteCity(@PathVariable("id") Long id) {
        try {
            cityService.deleteCity(id);
        } catch (Exception error) {
        throw new RuntimeException(error.getMessage());
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/cities/search/findByNameIgnoreCaseContaining")
    public PagedContent searchCityByName(@RequestParam(value = "name") String keyword, Pageable pageInfo) {
        return new PagedContent(cityService.findCityByName(keyword, pageInfo),pageType);
    }
}