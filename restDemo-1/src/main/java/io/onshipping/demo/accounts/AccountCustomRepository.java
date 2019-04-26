package io.onshipping.demo.accounts;

import java.util.Optional;

public interface AccountCustomRepository {
	Optional<Account> findByEmail(String username);
}
