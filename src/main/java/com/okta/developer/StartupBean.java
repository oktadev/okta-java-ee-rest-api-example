package com.okta.developer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.stream.Stream;

@Singleton
@Startup
public class StartupBean {
    private final BeerService beerService;

    @Inject
    public StartupBean(BeerService beerService) {
        this.beerService = beerService;
    }

    @PostConstruct
    private void startup() {
        // Top beers from https://www.beeradvocate.com/lists/top/
        Stream.of("Kentucky Brunch Brand Stout", "Marshmallow Handjee", "Barrel-Aged Abraxas", "Heady Topper",
                "Budweiser", "Coors Light", "PBR").forEach(name ->
                beerService.addBeer(new Beer(name))
        );
        beerService.getAllBeers().forEach(System.out::println);
    }

    @PreDestroy
    private void shutdown() {
        beerService.clear();
    }
}