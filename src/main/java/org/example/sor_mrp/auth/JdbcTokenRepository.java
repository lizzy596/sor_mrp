package org.example.sor_mrp.auth;

import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTokenRepository implements TokenRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public JdbcTokenRepository(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<Token> tokenRowMapper =
      (rs, rowNum) -> {
        Token token = new Token();
        token.setId(rs.getLong("id"));
        token.setUserId(rs.getLong("user_id"));
        token.setToken(rs.getString("token"));
        token.setTokenType(Token.TokenType.valueOf(rs.getString("token_type")));
        if (rs.getTimestamp("expires_at") != null) {
          token.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        }
        token.setBlacklisted(rs.getBoolean("blacklisted"));
        if (rs.getTimestamp("created_at") != null) {
          token.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
          token.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return token;
      };

  @Override
  public Token save(Token tokenRecord) {
    String sql =
        """
            INSERT INTO tokens (user_id, token, token_type, expires_at, blacklisted)
            VALUES (:userId, :token, :tokenType, :expiresAt, :blacklisted)
            RETURNING id, created_at, updated_at
        """;

    MapSqlParameterSource params =
        new MapSqlParameterSource()
            .addValue("userId", tokenRecord.getUserId())
            .addValue("token", tokenRecord.getToken())
            .addValue("tokenType", tokenRecord.getTokenType().name())
            .addValue("expiresAt", tokenRecord.getExpiresAt())
            .addValue(
                "blacklisted",
                tokenRecord.getBlacklisted() != null ? tokenRecord.getBlacklisted() : false);

    jdbcTemplate.query(
        sql,
        params,
        rs -> {
          if (rs.next()) {
            tokenRecord.setId(rs.getLong("id"));
            if (rs.getTimestamp("created_at") != null) {
              tokenRecord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            if (rs.getTimestamp("updated_at") != null) {
              tokenRecord.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            }
          }
        });

    return tokenRecord;
  }

  @Override
  public Optional<Token> findByTokenAndType(String token, Token.TokenType type) {
    String sql = "SELECT * FROM tokens WHERE token = :token AND token_type = :tokenType";
    var params =
        new MapSqlParameterSource().addValue("token", token).addValue("tokenType", type.name());
    var results = jdbcTemplate.query(sql, params, tokenRowMapper);
    return results.stream().findFirst();
  }

  @Override
  public Optional<Token> findByTokenAndTypeAndUserIdAndBlacklistedFalse(
      String token, Token.TokenType type, Long userId) {
    // Direct equivalent to Node's: Token.findOne({ token, type, user: payload.sub, blacklisted:
    // false })
    String sql =
        """
            SELECT * FROM tokens
            WHERE token = :token
              AND token_type = :tokenType
              AND user_id = :userId
              AND blacklisted = false
        """;
    var params =
        new MapSqlParameterSource()
            .addValue("token", token)
            .addValue("tokenType", type.name())
            .addValue("userId", userId);

    var results = jdbcTemplate.query(sql, params, tokenRowMapper);
    return results.stream().findFirst();
  }

  @Override
  public void deleteByUserId(Long userId) {
    // Equivalent to Node's: Token.deleteMany({ user: userId })
    String sql = "DELETE FROM tokens WHERE user_id = :userId";
    var params = new MapSqlParameterSource("userId", userId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void deleteById(Long id) {
    // Equivalent to Node's: Token.findByIdAndDelete(id)
    String sql = "DELETE FROM tokens WHERE id = :id";
    var params = new MapSqlParameterSource("id", id);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void updateBlacklisted(Long id, boolean blacklisted) {
    String sql =
        """
            UPDATE tokens
            SET blacklisted = :blacklisted,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = :id
        """;
    var params =
        new MapSqlParameterSource().addValue("id", id).addValue("blacklisted", blacklisted);
    jdbcTemplate.update(sql, params);
  }
}
