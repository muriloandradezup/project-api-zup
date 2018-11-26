package br.com.zup.restapi.projectapi.controllers;
import br.com.zup.restapi.projectapi.models.City;
import br.com.zup.restapi.projectapi.services.CityServiceBean;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

    @RunWith(SpringRunner.class)
    @WebMvcTest(CityController.class)
public class CityControllerTest {

    @MockBean
    CityServiceBean cityService;

    @Autowired
    MockMvc mockMvc;

    private City city;
    private Long id;
    private String name = "Uberlândia";
    private List<City> citiesList = new ArrayList<>();
    private City city1= new City("Patos de Minas");
    private City city2= new City("Lagoa Formosa");
    private Pageable pageInfo = PageRequest.of(0,10, Sort.Direction.ASC,"name");
    private String mainPath = "/cities";
    private String searchPath = "/cities/search/findByNameIgnoreCaseContaining";
    private String namePath = "$.name";
    private String name0 = "$._embedded.cities[0].name";
    private String name1 = "$._embedded.cities[1].name";
    private String citiesPath = "$._embedded.cities";
    private String utf8 = StandardCharsets.UTF_8.name();


    @Before
    public void setUp(){
        city = new City(name);
        id = city.getId();
        citiesList.add(city1);
        citiesList.add(city2);
    }

    @After
    public void tearDown(){
        citiesList.clear();
    }

    @Test
    public void getOneCityTest() throws Exception{
        when(cityService.findCity(id)).thenReturn(city);
        mockMvc.perform(get("/cities/"+id))
                .andExpect(status().isOk())
                .andExpect(jsonPath(namePath, Matchers.is(city.getName())));
    }

    @Test
    public void getNonExistentCityTest() throws Exception{
        when(cityService.findCity(id)).thenReturn(null);
        mockMvc.perform(get("/cities/"+id))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCitiesTest() throws Exception{
        when(cityService.findAllCities(notNull())).thenReturn(new PageImpl<>(citiesList));
        mockMvc.perform(get(mainPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath(citiesPath, Matchers.hasSize(2)))
                .andExpect(jsonPath(name0, Matchers.is(city1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(city2.getName())));
    }

    @Test
    public void getEmptyCityListTest() throws Exception{
        when(cityService.findAllCities(notNull())).thenReturn(Page.empty(pageInfo));
        mockMvc.perform(get(mainPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath(citiesPath, Matchers.hasSize(0)));
    }

    @Test
    public void createCityTest() throws Exception{
        when(cityService.createCity(notNull())).thenReturn(city);
        mockMvc.perform(post(mainPath).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\""+name+"\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andExpect(jsonPath(namePath, Matchers.is(name)));
    }

    @Test
    public void updateCityTest() throws Exception{
        String newName ="Uberlandia2";
        city.setName(newName);
        when(cityService.updateCity(id,newName)).thenReturn(city);
        mockMvc.perform(put("/cities/"+id).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\""+newName+"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is((city.getId().intValue()))))
                .andExpect(jsonPath(namePath, Matchers.is(newName)));
    }

    @Test
    public void updateInvalidCityTest() throws Exception{
        when(cityService.updateCity(anyLong(),anyString())).thenThrow(new RuntimeException("Cidade inválida!"));
        mockMvc.perform(put("/cities/"+id).characterEncoding(utf8)
                .contentType(MediaType.APPLICATION_JSON)
                .content(name))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCityTest() throws Exception{
        doNothing().when(cityService).deleteCity(anyLong());
        mockMvc.perform(delete(mainPath+"/"+id).characterEncoding(utf8))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidCityTest() throws Exception{
        doThrow(new RuntimeException("Cidade não pôde ser deletada!")).when(cityService).deleteCity(anyLong());
        mockMvc.perform(delete(mainPath+"/"+id).characterEncoding(utf8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchCityTest() throws Exception{
        when(cityService.findCityByName(notNull(),notNull())).thenReturn(new PageImpl<>(citiesList));
        mockMvc.perform(get(searchPath)
                .param("name","aaaaaa").characterEncoding(utf8))
                .andExpect(status().isOk())
                .andExpect(jsonPath(citiesPath,Matchers.hasSize(2)))
                .andExpect(jsonPath(name0, Matchers.is(city1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(city2.getName())));
    }

    @Test
    public void emptyCitySearchTest() throws Exception{
        when(cityService.findCityByName(notNull(),notNull())).thenReturn(Page.empty(pageInfo));
        mockMvc.perform(get(searchPath)
                .param("name","bbbbb"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(citiesPath,Matchers.hasSize(0)));
    }

    @Test
    public void searchCityWithPageableTest() throws Exception{
        when(cityService.findCityByName(notNull(),notNull())).thenReturn(new PageImpl<>(citiesList));
        mockMvc.perform(get(searchPath)
                .param("name","cccccc").param("page","0")
                .param("size", "20").param("sort","name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(citiesPath,Matchers.hasSize(2)))
                .andExpect(jsonPath(name0, Matchers.is(city1.getName())))
                .andExpect(jsonPath(name1, Matchers.is(city2.getName())));
    }

}
