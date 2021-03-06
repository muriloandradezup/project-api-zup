package br.com.zup.restapi.projectapi.services;

import br.com.zup.restapi.projectapi.exceptions.CustomException;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.repository.CityRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CityServiceTest {

    private Long id;
    private City city;
    private List<City> citiesList = new ArrayList<>();
    private City city1= new City("Patos de Minas");
    private City city2= new City("Lagoa Formosa");
    private Pageable pageInfo = PageRequest.of(0,10, Sort.Direction.ASC,"name");
    private String keyWord = "PESQUISA";


    @Mock
    CityRepository cityRepository;

    @InjectMocks
    CityServiceBean cityService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        city = new City("Uberlândia");
        id = city.getId();
        citiesList.add(city1);
        citiesList.add(city2);
    }

    @After
    public void tearDown(){
        citiesList.clear();
    }

    @Test
    public void findOneCityTest(){
        when(cityRepository.findById(id)).thenReturn(Optional.of(city));
        City foundCity = cityService.findCity(id);
        assertNotNull(foundCity);
        assertEquals(city, foundCity);
    }

    @Test
    public void findNonExistentCityTest(){
        when(cityRepository.findById(id)).thenReturn(Optional.empty());
        City foundCity = cityService.findCity(id);
        assertNull(foundCity);
    }

    @Test
    public void findAllCitiesTest(){
        when(cityRepository.findAll(pageInfo)).thenReturn(new PageImpl<>(citiesList));
        Page<City> list = cityService.findAllCities(pageInfo);
        assertNotNull(list.getContent());
        assertEquals(2,list.getTotalElements());
        assertTrue(list.getContent().contains(city1));
        assertTrue(list.getContent().contains(city2));
    }

    @Test
    public void findEmptyCityListTest(){
        when(cityRepository.findAll(any(Pageable.class))).thenReturn(Page.empty(pageInfo));
        Page<City> list = cityService.findAllCities(pageInfo);
        assertEquals(0,list.getTotalElements());
    }

    @Test
    public void createCityTest(){
        when(cityRepository.saveAndFlush(any(City.class))).then(returnsFirstArg());
        String name = "UBATUBA";
        City newCity = cityService.createCity(name);
        assertNotNull(newCity);
        assertEquals(name,newCity.getName());
    }

    @Test
    public void createBlankCityTest(){
        when(cityRepository.saveAndFlush(any(City.class))).then(returnsFirstArg());
        City newCity = cityService.createCity("");
        assertNotNull(newCity);
        assertEquals("",newCity.getName());
    }

    @Test
    public void updateCityTest() throws CustomException {
        String newName ="Uberlandia2";
        when(cityRepository.findById(id)).thenReturn(Optional.of(city));
        when(cityRepository.saveAndFlush(city)).thenReturn(city);
        City updatedCity = cityService.updateCity(id,newName);
        assertNotNull(updatedCity);
        assertEquals(id,city.getId());
        assertEquals(newName,updatedCity.getName());
    }

    @Test(expected = CustomException.class)
    public void updateNonExistentCity() throws CustomException {
        when(cityRepository.findById(id)).thenReturn(Optional.empty());
        City updatedCity = cityService.updateCity(id,"Belo Horizonte");
        assertNull(updatedCity);
        verify(cityRepository,times(0)).saveAndFlush(any(City.class));
    }

    @Test
    public void deleteCityTest() throws CustomException {
        cityService.deleteCity(id);
        verify(cityRepository,times(1)).deleteById(id);
    }

    @Test
    public void deleteNonExistentCity() throws CustomException {
        cityService.deleteCity(id);
        verify(cityRepository, times(0)).saveAndFlush(any(City.class));
    }

    @Test
    public void searchByNameReturnsPageTest(){
        when(cityRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo))
                .thenReturn(new PageImpl<>(citiesList));
        Page<City> list = cityService.findCityByName(keyWord,pageInfo);
        assertNotNull(list.getContent());
        assertEquals(2,list.getTotalElements());
        assertTrue(list.getContent().contains(city1));
        assertTrue(list.getContent().contains(city2));
    }

    @Test
    public void cityNotFoundTest(){
        when(cityRepository.findByNameContainingIgnoreCaseOrderByName(keyWord,pageInfo))
                .thenReturn(Page.empty(pageInfo));
        Page<City> list = cityService.findCityByName(keyWord,pageInfo);
        assertEquals(0,list.getTotalElements());
    }

}
