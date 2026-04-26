package tz.tante.rent.manager.configs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tz.tante.rent.manager.utilities.JwtUtils;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
  private final JwtUtils jwtUtils;

  public JwtAuthenticationFilter(JwtUtils jwtUtils)
  {
    this.jwtUtils = jwtUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
  {

    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer "))
    {
      String token = authHeader.substring(7);
      if (jwtUtils.validateToken(token))
      {
        String email = jwtUtils.getEmailFromToken(token);
        var roles = jwtUtils.getRolesFromToken(token).stream()
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());

        var authToken = new UsernamePasswordAuthenticationToken(email, null, roles);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    else
    {
      System.out.println(" NO Bear JWT validation : ");
    }
    filterChain.doFilter(request, response);
  }
}
