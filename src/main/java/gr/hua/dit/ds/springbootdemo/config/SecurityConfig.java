package gr.hua.dit.ds.springbootdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
  
    // Bean for JWT authentication filter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Bean for authentication manager
    @Bean
    public AuthenticationManager authenticationManagerBean (AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Main security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure CORS
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        http
                // Disable CSRF protection
                .csrf(csrf -> csrf.disable())
                // Configure CORS
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signin", "/auth/signin").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        // Swagger UI endpoints
                        .requestMatchers("/v3/api-docs/**",
                                "/v2/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Role-specific endpoints - handle both with and without /api prefix
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/pledge", "/projects/*/pledge").hasRole("SUPPORTER")
                        .requestMatchers(HttpMethod.POST, "/api/projects/**", "/projects/**").hasRole("CREATOR")
                        .requestMatchers(HttpMethod.GET, "/api/projects/**", "/projects/**").permitAll()
                        .requestMatchers("/api/projects/pending/**", "/projects/pending/**").hasRole("ADMIN")
                        .requestMatchers("/api/pledges/**", "/pledges/**").hasRole("SUPPORTER")
                        .requestMatchers("/api/creator/**", "/creator/**").hasAnyRole("CREATOR", "ADMIN")
                        .requestMatchers("/api/supporter/**", "/supporter/**").hasAnyRole("SUPPORTER", "ADMIN")
                        .requestMatchers("/api/admin/**", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/error").permitAll()  // Allow access to error endpoint
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                // Add JWT filter only for non-public endpoints
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
