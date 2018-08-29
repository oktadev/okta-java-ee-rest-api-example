package com.okta.developer;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Stateless
public class BeerService {

    @PersistenceContext(unitName = "beer-pu")
    private EntityManager entityManager;

    public void addBeer(Beer beer) {
        entityManager.persist(beer);
    }

    public List<Beer> getAllBeers() {
        CriteriaQuery<Beer> cq = entityManager.getCriteriaBuilder().createQuery(Beer.class);
        cq.select(cq.from(Beer.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public void clear() {
        Query removeAll = entityManager.createQuery("delete from Beer");
        removeAll.executeUpdate();
    }
}
