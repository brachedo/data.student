package com.grandp.data.security.config;

import com.grandp.data.security.captcha.ReCaptchaFilter;
import com.grandp.data.security.handler.AuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .cors(Customizer.withDefaults())
          .csrf().disable()
          .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .authorizeHttpRequests()
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/profile", "/grades", "/program").hasAnyAuthority("STUDENT", "ROLE_ADMIN")
                    .requestMatchers("/login", "/logouted", "/error").permitAll()
                    .anyRequest().permitAll()
                .and()
                    .exceptionHandling()
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            request.setAttribute("errorMessage", "You do not have permission to access this resource.");
                            request.setAttribute("exception", accessDeniedException);
                            throw accessDeniedException;
                        })
                .and()
                    .addFilterBefore(new ReCaptchaFilter(), UsernamePasswordAuthenticationFilter.class)
                    .formLogin()
                    .successHandler(new AuthenticationSuccessHandler())
                    .loginPage("/login")
                    .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                    .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                    .defaultSuccessUrl("/profile")
                    .failureForwardUrl("/login-error")
                    .failureHandler((request, response, exception) -> {
                        String email = request.getParameter("username");
                        String error = exception.getMessage();
                        LOG.error("A failed login attempt with email: " + email + ". Reason: " + error);
                        LOG.error(error, exception);

                        String redirectUrl = request.getContextPath() + "/login?error";
                        response.sendRedirect(redirectUrl);
                    })
                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/logouted")
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true);

        http.exceptionHandling().accessDeniedPage("/error");

        return http.build();
    }
}
