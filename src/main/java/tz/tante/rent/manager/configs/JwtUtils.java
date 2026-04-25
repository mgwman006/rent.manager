package tz.tante.rent.manager.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtils
{
  private final String jwtSecret = "lets-explore-tanzania-secret-key";
  private final long jwtExpirationMs = 86400000; // 1 day

  public String generateToken(String email, Set<String> roles)
  {
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
      .setSubject(email)
      .claim("roles", roles)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
      .signWith(key)
      .compact();
  }

  public boolean validateToken(String token)
  {
    try
    {
      System.out.println("JWT validation : ");
      Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

      Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token);

      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public String getEmailFromToken(String token)
  {
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    return Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public Set<String> getRolesFromToken(String token)
  {
    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(key)
      .build()
      .parseClaimsJws(token)
      .getBody();

    return ((java.util.List<?>) claims.get("roles")).stream()
      .map(Object::toString)
      .collect(Collectors.toSet());
  }


}
