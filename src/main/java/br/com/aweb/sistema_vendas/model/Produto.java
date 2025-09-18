package br.com.aweb.sistema_vendas.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@AllArgsConstructor
@Data
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "A descrição é obrigatório")
    @Column(nullable = false, length = 255)
    private String descricao;

    @Positive(message = "O preço deve ser positivo")
    @NotNull(message = "O preço deve ser obrigatório")
    @Column(nullable = false)
    private BigDecimal preco;

    @PositiveOrZero(message = "A quantidade deve ser maior ou igual a zero")
    @NotNull(message = "A quantidade é obrigatório")
    @Column(nullable = false)
    private Integer quantidadeEmEstoque;

    public Produto() {
    }
}
