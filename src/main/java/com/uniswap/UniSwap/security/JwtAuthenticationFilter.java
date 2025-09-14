// src/main/java/com/uniswap/UniSwap/security/JwtAuthenticationFilter.java
package com.uniswap.UniSwap.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.uniswap.UniSwap.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired 
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired 
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(method, requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String username = jwtTokenUtil.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // JWT processing failed, continue without authentication
            }
        }
        
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String method, String requestURI) {
        // Allow all OPTIONS requests
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // Public GET endpoints
        if ("GET".equals(method)) {
            return requestURI.startsWith("/api/items") ||
                   requestURI.startsWith("/api/categories") ||
                   requestURI.startsWith("/api/locations") ||
                   requestURI.startsWith("/api/posts");
        }
        
        // Auth endpoints - fixed to match without trailing slash
        return requestURI.startsWith("/api/auth") ||
               requestURI.startsWith("/api/public") ||
               requestURI.equals("/api/health") ||
               requestURI.startsWith("/api/admin/reset");
    }
}
