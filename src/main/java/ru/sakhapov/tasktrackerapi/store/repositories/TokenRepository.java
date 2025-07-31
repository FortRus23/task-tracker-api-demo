package ru.sakhapov.tasktrackerapi.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.sakhapov.tasktrackerapi.store.jwt.entites.Token;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
      select t from Token t join t.user u
      where u.id = :id and t.expired = false and t.revoked = false
    """)

    List<Token> findAllValidTokenByUser(Long id);

    Token findByToken(String token);

}
