package com.bengono.userService.configuration;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
            logger.info("Received JWT: {}", jwt);

            try {
                if (jwt.chars().filter(ch -> ch == '.').count() != 2) {
                    logger.error("Malformed JWT: {}", jwt);
                    throw new BadCredentialsException("Token invalide....");
                }

                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));

                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("Successfully validated JWT for user: {}", email);

            } catch (SecurityException | MalformedJwtException e) {
                logger.error("Invalid JWT: {}", e.getMessage());
                throw new BadCredentialsException("Token invalide....", e);
            } catch (Exception e) {
                logger.error("JWT validation error: {}", e.getMessage());
                throw new BadCredentialsException("Token invalide....", e);
            }
        } else {
            logger.warn("Authorization header missing or does not start with 'Bearer '");
        }

        filterChain.doFilter(request, response);
    }
}
