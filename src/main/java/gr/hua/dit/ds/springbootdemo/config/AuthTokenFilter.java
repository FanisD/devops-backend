package gr.hua.dit.ds.springbootdemo.config;

import gr.hua.dit.ds.springbootdemo.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            logger.info("Processing request: {} {} (servletPath: {})", 
                request.getMethod(), 
                request.getRequestURI(),
                request.getServletPath());
            
            String jwt = parseJwt(request);
            logger.debug("Extracted JWT: {}", jwt != null ? "present" : "null");
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                logger.debug("Username from JWT: {}", username);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                logger.info("User authorities: {}", userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Successfully set authentication for user: {} with authorities: {}", 
                    username, 
                    userDetails.getAuthorities());
                
                // Log authentication state before proceeding with filter chain
                logger.info("Authentication state before filter chain: {}", 
                    SecurityContextHolder.getContext().getAuthentication());
            } else {
                logger.warn("No valid JWT token found in request");
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
            logger.error("Stack trace:", e);
        }

        // Log authentication state after try-catch block
        logger.info("Authentication state before doFilter: {}", 
            SecurityContextHolder.getContext().getAuthentication());
        
        filterChain.doFilter(request, response);
        
        // Log authentication state after filter chain
        logger.info("Authentication state after filter chain: {}", 
            SecurityContextHolder.getContext().getAuthentication());
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", headerAuth);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String token = headerAuth.substring(7);
            logger.debug("Extracted token: {}", token);
            return token;
        }

        return null;
    }
}
