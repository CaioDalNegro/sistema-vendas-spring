package br.com.aweb.sistema_vendas.model;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome completo é obrigatório")
    @Column(nullable = false, length = 150)
    private String nomeCompleto;

    @Email(message = "E-mail deve ser válido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @NotBlank(message = "O CPF é obrigatório")
    @Column(nullable = false, length = 11, unique = true)
    @CPF(message = "CPF invalido")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 digitos")
    private String cpf;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "O logradouro é obrigatório")
    @Column(nullable = false)
    private String logradouro;

    private String numero;
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Column(nullable = false)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(nullable = false)
    private String cidade;

    @NotBlank(message = "A UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String uf;

    @NotBlank(message = "O CEP é obrigatório")
    @Column(nullable = false)
    private String cep;
}