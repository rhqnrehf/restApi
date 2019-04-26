package io.onshipping.demo.events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EventDefaultRepository extends JpaRepository<Event, Integer>{

}
