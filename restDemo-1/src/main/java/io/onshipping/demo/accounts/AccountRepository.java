 package io.onshipping.demo.accounts;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends AccountCustomRepository,JpaRepository<Account, Integer>{

}
