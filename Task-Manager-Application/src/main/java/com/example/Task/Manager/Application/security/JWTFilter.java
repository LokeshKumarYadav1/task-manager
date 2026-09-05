package com.example.Task.Manager.Application.security;

import com.example.Task.Manager.Application.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;

        }

        String jwt = authHeader.substring(7);

        try{

            String username = jwtService.extractUsername(jwt);

            System.out.println("USERNAME FROM TOKEN = " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

                System.out.println("USER LOADED = " + userDetails.getUsername());
                System.out.println("AUTHORITIES = " + userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    System.out.println("Token Valid");

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Authentication set");

                } else {

                    System.out.println("Token Invalid");

                }

            }

        } catch (Exception e) {

            System.out.println("JWT ERROR: " + e.getClass().getName());
            System.out.println("JWT ERROR: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

            filterChain.doFilter(request, response);

    }

}