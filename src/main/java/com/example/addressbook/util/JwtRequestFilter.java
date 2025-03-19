package com.example.addressbook.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    JwtToken jwtToken;

    @Autowired
    UserDetailsService userDetailsService;

    private static final List<String> EXCLUDED_URLS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/"
    );



    /**
     * This method is called for every request to check if the JWT token is valid.
     * If valid, it sets the authentication in the security context.
     *
     * @param request  - The HTTP request
     * @param response - The HTTP response
     * @param chain    - The filter chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Request URI: " + requestURI);

//        for (String excludedUrl : EXCLUDED_URLS) {
//            if (requestURI.startsWith(excludedUrl)) {
//                logger.info("Request URI is excluded from authentication: " + requestURI);
//                chain.doFilter(request, response);
//                return;
//            }
//        }

        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: " + authorizationHeader);

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            jwt = authorizationHeader.substring(7);

        if (jwt != null) {
            email = jwtToken.decodeToken(jwt);
        }

        try {
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (!jwtToken.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

            }
        } catch (Exception e) {
            System.err.println("JWT verification failed: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        chain.doFilter(request, response);
    }
}
