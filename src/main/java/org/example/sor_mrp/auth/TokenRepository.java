package org.example.sor_mrp.auth;

import java.util.Optional;

public interface TokenRepository {
  Token save(Token tokenRecord);

  Optional<Token> findByTokenAndType(String token, Token.TokenType type);

  Optional<Token> findByTokenAndTypeAndUserIdAndBlacklistedFalse(
      String token, Token.TokenType type, Long userId);

  void deleteByUserId(Long userId);

  void deleteById(Long id);

  void updateBlacklisted(Long id, boolean blacklisted);
}
