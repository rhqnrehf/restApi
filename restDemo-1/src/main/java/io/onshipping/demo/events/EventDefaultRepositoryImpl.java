package io.onshipping.demo.events;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;


public class EventDefaultRepositoryImpl extends SimpleJpaRepository<Event, Integer> implements EventDefaultRepository{
	EntityManager entityManager;

	public EventDefaultRepositoryImpl(Class<Event> domainClass, EntityManager em) {
		super(domainClass, em);
		this.entityManager = em;
	}

	

}
