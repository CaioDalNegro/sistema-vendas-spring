package br.com.aweb.sistema_vendas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.ItemPedido;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.repository.PedidoRepository;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    // CREATE - Criar novo pedido
    @Transactional
    public Pedido criarPedido(Cliente cliente) { // Recebe um objeto Cliente
        Pedido pedido = new Pedido(cliente);
        return pedidoRepository.save(pedido);
    }

    // ADICIONAR ITEM ao pedido
    @Transactional
    public void adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        Optional<Produto> optionalProduto = produtoRepository.findById(produtoId);

        if (!optionalPedido.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }
        if (!optionalProduto.isPresent()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }

        Pedido pedido = optionalPedido.get();
        Produto produto = optionalProduto.get();

        // Verifica se o pedido está ativo
        if (pedido.getStatus() != StatusPedido.ATIVO) {
            throw new IllegalStateException("Não é possível alterar pedido cancelado");
        }

        // Verifica estoque
        if (produto.getQuantidadeEmEstoque() < quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o produto " + produto.getNome());
        }

        // Cria e adiciona item ao pedido
        ItemPedido item = new ItemPedido(produto, quantidade);
        item.setPedido(pedido); // Garante a referência bidirecional
        pedido.getItens().add(item); 

        // Atualiza estoque
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - quantidade);

        // Recalcula valor total
        calcularValorTotal(pedido);

        // Salva alterações
        pedidoRepository.save(pedido);
        produtoRepository.save(produto);
    }

    // Método auxiliar para calcular o valor total
    private void calcularValorTotal(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : pedido.getItens()) {
            BigDecimal valorItem = item.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(valorItem);
        }

        pedido.setValorTotal(total);
    }

    // REMOVER ITEM do pedido
    @Transactional
    public void removerItem(Long pedidoId, Long itemId) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);

        if (!optionalPedido.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }

        Pedido pedido = optionalPedido.get();

        // Verifica se o pedido está ativo
        if (pedido.getStatus() != StatusPedido.ATIVO) {
            throw new IllegalStateException("Não é possível alterar pedido cancelado");
        }

        // Busca o item no pedido
        ItemPedido itemParaRemover = null;
        for (ItemPedido item : pedido.getItens()) {
            if (item.getId().equals(itemId)) {
                itemParaRemover = item;
                break;
            }
        }

        if (itemParaRemover == null) {
            throw new IllegalArgumentException("Item não encontrado no pedido");
        }

        // Devolve ao estoquE
        Produto produto = itemParaRemover.getProduto();
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + itemParaRemover.getQuantidade());
        
        // Remove Item da Lista (orphanRemoval do @OneToMany cuidará da exclusão)
        pedido.getItens().remove(itemParaRemover);

        // Recalcula valor total
        calcularValorTotal(pedido);

        // Salva alterações
        pedidoRepository.save(pedido);
        produtoRepository.save(produto);
    }

    // CANCELAR PEDIDO
    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);

        if (!optionalPedido.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado.");
        }

        Pedido pedido = optionalPedido.get();

        // Devolve todos os itens ao estoque
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        // Altera status para CANCELADO
        pedido.setStatus(StatusPedido.CANCELADO);

        // Salva alterações
        pedidoRepository.save(pedido);
    }

    // BUSCAR PEDIDO POR ID
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    // LISTAR TODOS OS PEDIDOS
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // LISTAR PEDIDOS POR STATUS
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }
}
