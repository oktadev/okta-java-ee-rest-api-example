package com.okta.developer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class BeerBean {

    @Inject
    private BeerService beerService;
    private List<Beer> beersAvailable;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Beer> getBeersAvailable() {
        return beersAvailable;
    }

    public void setBeersAvailable(List<Beer> beersAvailable) {
        this.beersAvailable = beersAvailable;
    }

    public String fetchBeers() {
        beersAvailable = beerService.getAllBeers();
        return "success";
    }

    public String add() {
        Beer beer = new Beer();
        beer.setName(name);
        beerService.addBeer(beer);
        return "success";
    }
}
