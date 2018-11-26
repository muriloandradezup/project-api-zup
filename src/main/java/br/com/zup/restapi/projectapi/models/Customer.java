package br.com.zup.restapi.projectapi.models;

import javax.persistence.*;

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @ManyToOne
    private City city;

    public Customer(String name, City city) {
        this.name = name;
        this.city = city;
    }

    public Customer() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public Long getCityId() {
        return city.getId();
    }

    public void setCity(City city) {
        this.city = city;
    }
}
