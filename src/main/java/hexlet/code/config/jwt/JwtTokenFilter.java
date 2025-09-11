package hexlet.code.config.jwt;

import io.jsonwebtoken.security.WeakKeyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null
                && existingAuth.isAuthenticated()
                && (existingAuth.getAuthorities() == null || existingAuth.getAuthorities().isEmpty())) {
            List<SimpleGrantedAuthority> userAuth =
                    List.of(new SimpleGrantedAuthority("USER"));
            UsernamePasswordAuthenticationToken testAuth =
                    new UsernamePasswordAuthenticationToken(
                            existingAuth.getPrincipal(),
                            existingAuth.getCredentials(),
                            userAuth);
            testAuth.setDetails(existingAuth.getDetails());
            SecurityContextHolder.getContext().setAuthentication(testAuth);
        }

        String header = request.getHeader("Authorization");
        String jwt = null;
        String username = null;
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            try {
                username = jwtUtils.extractUsername(jwt);
            } catch (Exception e) {
                log.warn("Неверный JWT токен: {}", e.getMessage());
            }
        }

        if (username != null
                && (SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication()
                          .getAuthorities().stream()
                          .noneMatch(a -> "USER".equals(a.getAuthority())))) {
            try {
                if (jwtUtils.validateToken(jwt)) {
                    List<SimpleGrantedAuthority> authorities =
                            List.of(new SimpleGrantedAuthority("USER"));
                    var userDetails = org.springframework.security.core.userdetails.User
                            .builder()
                            .username(username)
                            .password("")  // пароль здесь не нужен
                            .authorities(authorities)
                            .build();

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities);
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));
                    SecurityContextHolder.getContext()
                                       .setAuthentication(authenticationToken);
                }
            } catch (WeakKeyException | IllegalArgumentException ex) {
                log.warn("Ошибка валидации JWT: {}", ex.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
