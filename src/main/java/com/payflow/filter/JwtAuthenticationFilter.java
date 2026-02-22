package com.payflow.filter;

import com.payflow.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 🔥 Skip auth endpoints
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
      try {
          String authHeader = request.getHeader("Authorization");
          // Header missing ->continue
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
              filterChain.doFilter(request, response);
              return;
          }

          String token = authHeader.substring(7);
          //extract email
          String email = jwtService.extractUsername(token);
          String type = jwtService.extractTokenType(token);

          if (email != null && "ACCESS".equals(type) && SecurityContextHolder.getContext().getAuthentication() == null) {

              UserDetails userDetails = userDetailsService.loadUserByUsername(email);

              UsernamePasswordAuthenticationToken authToken =
                      new UsernamePasswordAuthenticationToken(
                              userDetails,
                              null,
                              userDetails.getAuthorities()
                      );

              SecurityContextHolder.getContext()
                      .setAuthentication(authToken);
          }
      }catch (Exception e)
      {
          //If token is invalid/expired/tampered
           SecurityContextHolder.clearContext();
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           response.getWriter().write("Invalid or Expired JWT token");

           return; //stop further processing
      }
        filterChain.doFilter(request, response);
    }
}
