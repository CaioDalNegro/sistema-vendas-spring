package br.com.aweb.sistema_vendas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.ItemPedido;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.repository.PedidoRepository;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteService ClienteService;
    
    // CREATE - Criar novo pedido
    @Transactional
    public Pedido criarPedido(Cliente cliente) {
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
        
        // Verificação de status do pedido
        if (pedido.getStatus() != StatusPedido.ATIVO) {
            throw new IllegalStateException("Não é possível alterar pedido cancelado");
        }
        
        // Verificação de estoque
        if (produto.getQuantidadeEmEstoque() < quantidade) {
            throw new IllegalStateException("Quantidade insuficiente para o produto: " + produto.getNome());
        }
        
        // Cria o item do pedido
        ItemPedido item = new ItemPedido(produto, quantidade);
        item.setPedido(pedido);
        
        // Adiciona à lista do pedido
        pedido.getItens().add(item);
        
        // Atualiza estoque
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - quantidade);
        
        // Recalcula valor total
        calcularValorTotal(pedido);
        
        // Salva alterações
        pedidoRepository.save(pedido);
        produtoRepository.save(produto);
    }
    
    // REMOVER ITEM do pedido
    @Transactional
    public void removerItem(Long pedidoId, Long itemId) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        
        if (!optionalPedido.isPresent()) {
            throw new IllegalArgumentException("Pedido não encontrado");
        }
        
        Pedido pedido = optionalPedido.get();
        
        // Verifica se pedido está ativo
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
        
        // Devolver estoque
        Produto produto = itemParaRemover.getProduto();
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + itemParaRemover.getQuantidade());
        
        // Remove item da lista
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
            throw new IllegalArgumentException("Pedido não encontrado");
        }
        
        Pedido pedido = optionalPedido.get();
        
        // Devolver todos os itens ao estoque
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        }
        
        // Altera status para cancelado
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
    
    // Método para calcular valor total
    private void calcularValorTotal(Pedido pedido) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (ItemPedido item : pedido.getItens()) {
            BigDecimal valorItem = item.getPrecoUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(valorItem);
        }
        
        pedido.setValorTotal(total);
    }
    
    // READ - Buscar pedido por ID
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    
    // READ - Listar todos os pedidos
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }
    
    // READ - Listar pedidos por status
    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }
    // FINALIZAR PEDIDO
@Transactional
public void finalizarPedido(Long pedidoId) {
    Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);

    if (!optionalPedido.isPresent()) {
        throw new IllegalArgumentException("Pedido não encontrado");
    }

    Pedido pedido = optionalPedido.get();

    // Não permite finalizar pedido cancelado
    if (pedido.getStatus() == StatusPedido.CANCELADO) {
        throw new IllegalStateException("Não é possível finalizar um pedido cancelado");
    }

    // Verifica se o pedido tem itens
    if (pedido.getItens().isEmpty()) {
        throw new IllegalStateException("Não é possível finalizar um pedido sem itens");
    }

    // Calcula o total pela última vez antes de finalizar
    calcularValorTotal(pedido);

    // Muda o status para FINALIZADO
    pedido.setStatus(StatusPedido.FINALIZADO);

    pedidoRepository.save(pedido);
}


}