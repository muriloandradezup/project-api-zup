package br.com.zup.restapi.ProjectApi.models;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class PagedContent {
    private Map<String,Object> _embedded;
    private Map<String,Integer> page;

    public PagedContent(Page<?> data, String name) {
        this._embedded = new HashMap<>();
        this.page = new HashMap<>();

        _embedded.put(name,data.getContent());
        page.put("size",data.getSize());
        page.put("totalElements",data.getNumberOfElements());
        page.put("totalPages",data.getTotalPages());
        page.put("number",data.getNumber());
    }

    public Map<String, Object> get_embedded() {
        return _embedded;
    }

    public Map<String, Integer> getPage() {
        return page;
    }
}