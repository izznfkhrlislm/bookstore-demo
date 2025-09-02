package id.izzanfi.bookstoreserviceapi.config;

import id.izzanfi.bookstoreserviceapi.auth.CustomUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

  @Value("${bookstore.orm.jwtSecret}")
  private String jwtSecret;

  @Value("${bookstore.orm.jwtExpirationMs}")
  private int jwtExpirationTime;

  public String generateJwtToken(Authentication authentication) {
    CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

    return Jwts.builder()
        .subject(userPrincipal.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(new Date().getTime() + jwtExpirationTime))
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private Key getKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String jwtToken) {
    return Jwts.parser().setSigningKey(getKey()).build()
        .parseClaimsJws(jwtToken).getBody().getSubject();
  }

  public boolean validateJwtToken(String jwtToken) {
    try {
      Jwts.parser()
          .setSigningKey(getKey())
          .build()
          .parse(jwtToken);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT Token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT Token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT Token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}
