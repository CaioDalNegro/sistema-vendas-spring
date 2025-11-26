package br.com.aweb.sistema_vendas.model;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "CPF é obrigatório.")
    @CPF(message = "CPF inválido")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório.")
    @Column(nullable = false)
    private String telefone;

    @NotBlank(message = "Logradouro é obrigatório.")
    @Column(nullable = false)
    private String logradouro;

    private String numero;
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório.")
    @Column(nullable = false)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatório.")
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "UF é obrigatório")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres.")
    @Column(nullable = false, length = 2)
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Column(nullable = false)
    private String cep;

    // CAMPOS PARA LOGIN
    @NotBlank(message = "Senha é obrigatória.")
    private String senha;

    private String role = "ROLE_USER";

    // Implementação UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
