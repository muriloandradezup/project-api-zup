package br.com.zup.restapi.projectapi.controllers;

import br.com.zup.restapi.projectapi.AbstractTest;
import br.com.zup.restapi.projectapi.models.City;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CityControllerIntegrationTest extends AbstractTest {

    private City city1;
    private City city2;
    private long invalidId = -1;
    private Map<String,String> cityJson = new HashMap<>();
    private City newCity =  new City("NOVA CIDADE");

    @Before
    public void setUp(){
        city1 = cityService.createCity("CIDADE1");
        city2 = cityService.createCity("CIDADE2");
        cityJson.put("id", newCity.getId().toString());
        cityJson.put("name",newCity.getName());
    }

    @After
    public void tearDown(){
        cityJson.clear();
    }

    @Test
    public void getOneCityTest() throws Exception{
        this.mockMvc.perform(get("/cities/"+city1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(city1.getName())));
    }

    @Test
    public void getNonExistentCityTest() throws Exception{
        this.mockMvc.perform(get("/cities/"+invalidId))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllCitiesTest() throws Exception{
        this.mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    public void createCityTest() throws Exception{
        this.mockMvc.perform(post("/cities").characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(cityJson).toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", Matchers.is(newCity.getName())));
    }

    @Test
    public void updateCityTest() throws Exception{
        this.mockMvc.perform(put("/cities/"+city1.getId()).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(cityJson).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is((city1.getId().intValue()))))
                .andExpect(jsonPath("$.name", Matchers.is(newCity.getName())));
    }

    @Test
    public void updateInvalidCityTest() throws Exception{
        this.mockMvc.perform(put("/cities/"+invalidId).characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JSONObject(cityJson).toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCityTest() throws Exception{
        this.mockMvc.perform(delete("/cities/"+city1.getId()).characterEncoding("utf-8"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteInvalidCityTest() throws Exception{
        this.mockMvc.perform(delete("/cities/"+invalidId).characterEncoding("utf-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchCityTest() throws Exception{
        this.mockMvc.perform(get("/cities/search/findByNameIgnoreCaseContaining")
                .param("name",city2.getName()).characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements",Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    public void emptyCitySearchTest() throws Exception{
        this.mockMvc.perform(get("/cities/search/findByNameIgnoreCaseContaining")
                .param("name","knfklanfkanflknakfnalknge,e;.;.f,fea"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cities",Matchers.hasSize(0)));
    }

    @Test
    public void searchCityWithPageableTest() throws Exception{
        this.mockMvc.perform(get("/cities/search/findByNameIgnoreCaseContaining")
                .param("name",city2.getName()).param("page","0")
                .param("size", "20").param("sort","name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements",Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$._embedded.cities[0].name", Matchers.is(city2.getName())));
    }

}
