package br.com.croa.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private final Environment environment;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (Arrays.stream(environment.getActiveProfiles())
                .parallel().anyMatch(s -> s.equalsIgnoreCase("local") ||
                        s.equalsIgnoreCase("heroku") ||
                        s.equalsIgnoreCase("test"))) {
            http.csrf().disable().cors().disable().authorizeRequests().anyRequest().permitAll();
        } else {
            http.authorizeRequests()
                    .antMatchers("/actuator/**").permitAll()
                    .antMatchers("/unprotected").permitAll()
                    .anyRequest().authenticated().and()
                    .oauth2Login(withDefaults());
        }
    }

}