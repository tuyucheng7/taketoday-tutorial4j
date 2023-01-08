package cn.tuyucheng.taketoday.openliberty.person.dao;

import cn.tuyucheng.taketoday.openliberty.person.model.Person;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequestScoped
public class PersonDao {

    @PersistenceContext(name = "jpa-unit")
    private EntityManager em;

    public Person createPerson(Person person) {
        em.persist(person);
        return person;
    }

    public Person readPerson(int personId) {
        return em.find(Person.class, personId);
    }
}