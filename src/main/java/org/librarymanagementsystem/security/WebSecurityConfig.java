package org.librarymanagementsystem.security;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.security.jwt.AuthEntryPointJwt;
import org.librarymanagementsystem.security.jwt.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    //private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
       /* http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")
        );*/
         http.cors(withDefaults());
        //! Disable the Csrf Token
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/api/auth/**").permitAll()
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v1/api/auth/user-login").hasAuthority("ROLE_STUDENT")
                .requestMatchers("/v1/api/book/all-book").permitAll()
                .requestMatchers("/v1/api/Newsletter/contact-us").permitAll()
                // Admin-only endpoints
                .requestMatchers("/v1/api/auth/admin-login").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/v1/api/analytics/**").hasAuthority("ROLE_ADMIN")
//                .requestMatchers("/v1/api/transitional/get-all-borrowings-of-a-member", "/v1/api/transitional/get-all-borrowings")
//                .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/v1/api/book/create", "/v1/api/book/delete", "/v1/api/book/edit", "/v1/api/book/update")
                .hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated());

        /*http.oauth2Login(oauth2 ->
                oauth2.successHandler(oAuth2LoginSuccessHandler));*/

        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler));

        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        log.info("Security Configuration Loaded Successfully");

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Increased security strength
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("webSecurityCustomizer-----");
        return (web) -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/webjars/**",
                "/swagger-ui.html"
        );
    }
}
