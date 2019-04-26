package io.onshipping.demo.accounts;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AccountCustomRepositoryImpl implements AccountCustomRepository{
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Optional<Account> findByEmail(String username) {
		
		try {
			Account account =entityManager.createQuery("FROM Account WHERE email = :email",Account.class).setParameter("email", username).getSingleResult();
			Optional<Account> o=Optional.of(account);
			return o;
		}catch(Exception e) {
			return Optional.ofNullable(null);
		}
	}

}
