package org.example.sor_mrp.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

  // Spring Data JPA automatically derives the query from the method name!
  Optional<Token> findByTokenAndTokenType(String token, Token.TokenType tokenType);

  Optional<Token> findByTokenAndTokenTypeAndUserIdAndBlacklistedFalse(
      String token, Token.TokenType tokenType, Long userId);

  // Derived delete queries work out of the box
  void deleteByUserId(Long userId);

  // For your custom updateBlacklisted operation, you can use a simple JPQL @Modifying query:
  @Transactional
  @Modifying
  @Query("UPDATE Token t SET t.blacklisted = :blacklisted WHERE t.id = :id")
  void updateBlacklisted(@Param("id") Long id, @Param("blacklisted") boolean blacklisted);

  // Note: save(), findById(), and deleteById() are inherited automatically from JpaRepository!
}
