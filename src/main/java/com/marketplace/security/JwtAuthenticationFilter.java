package com.marketplace.security;

import com.marketplace.entity.User;
import com.marketplace.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/login") || 
               path.startsWith("/api/auth/register") || 
               path.startsWith("/api/auth/verify") ||
               path.equals("/v3/api-docs") ||
               path.equals("/swagger-ui.html") ||
               path.startsWith("/swagger-ui/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");
            log.debug("Processing request with Authorization header: {}", authorizationHeader);

            String username = null;
            String jwt = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                try {
                    username = jwtUtil.extractUsername(jwt);
                    log.debug("Extracted username from token: {}", username);
                } catch (Exception e) {
                    log.error("Error extracting username from token: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token");
                    return;
                }
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    Optional<User> userOptional = userRepository.findByEmail(username);
                    if (userOptional.isEmpty()) {
                        log.error("User not found: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("User not found");
                        return;
                    }
                    
                    User user = userOptional.get();
                    if (!user.isEnabled()) {
                        log.warn("User is disabled: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("User is disabled");
                        return;
                    }
                    
                    if (jwtUtil.validateToken(jwt, user)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        log.debug("Successfully authenticated user: {}", username);
                    } else {
                        log.warn("Invalid JWT token for user: {}", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid token");
                        return;
                    }
                } catch (Exception e) {
                    log.error("Error processing authentication: {}", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Authentication error");
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Error processing token");
            return;
        }
    }
} 