package me.mikholsky.jwtauthstudy.repository;

import me.mikholsky.jwtauthstudy.controller.body.TokenResponse;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<TokenResponse, String> {
}
