package br.com.aweb.sistema_vendas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracoesDeSeguranca {

    @Bean
    public SecurityFilterChain filtrosSeguranca(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/login", "/css/**", "/js/**").permitAll();
                    req.requestMatchers("/produto/**").hasRole("ADMIN");
                    req.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .rememberMe(r -> r.key("lembrarDeMim"))
                .build();
    }

    @Bean
    public PasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder();
    }
}