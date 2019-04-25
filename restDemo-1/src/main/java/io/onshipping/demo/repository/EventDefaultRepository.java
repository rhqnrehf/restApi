package io.onshipping.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import io.onshipping.demo.events.Event;

@NoRepositoryBean
public interface EventDefaultRepository extends JpaRepository<Event, Integer>{

}
