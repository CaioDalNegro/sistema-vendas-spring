package br.com.aweb.sistema_vendas.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.service.ClienteService;
import br.com.aweb.sistema_vendas.service.PedidoService;
import br.com.aweb.sistema_vendas.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ClienteService clienteService; // Assumindo a existência
    @Autowired
    private ProdutoService produtoService; // Assumindo a existência

    // # LISTAR TODOS OS PEDIDOS [cite: 334]
    @GetMapping
    public ModelAndView listarPedidos() {
        List<Pedido> pedidos = pedidoService.listarTodos();
        return new ModelAndView("pedido/list", Map.of("pedidos", pedidos)); // [cite: 335]
    }

    // # FORMULÁRIO DE NOVO PEDIDO - GET [cite: 336]
    @GetMapping("/novo")
    public ModelAndView novoPedidoForm() {
        List<Cliente> clientes = clienteService.listarTodos(); // Assumindo o serviço ClienteService
        List<Produto> produtos = produtoService.listarTodos(); // Assumindo o serviço ProdutoService

        return new ModelAndView("pedido/form", Map.of(
                "pedido", new Pedido(),
                "clientes", clientes,
                "produtos", produtos
        ));
    }

    // # CRIAR NOVO PEDIDO - POST
    @PostMapping("/novo")
    public String criarPedido(@RequestParam Long clienteId) {
        Optional<Cliente> optionalCliente = clienteService.buscarPorId(clienteId);

        if (!optionalCliente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado");
        }

        Pedido pedido = pedidoService.criarPedido(optionalCliente.get());
        return "redirect:/pedidos/edit/" + pedido.getId(); // Redireciona para edição
    }
    
    // # FORMULÁRIO DE EDIÇÃO DE PEDIDO - GET
    @GetMapping("/edit/{id}")
    public ModelAndView editarPedidoForm(@PathVariable Long id) {
        Optional<Pedido> optionalPedido = pedidoService.buscarPorId(id);

        if (!optionalPedido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Pedido não existe
        }

        Pedido pedido = optionalPedido.get();

        // Não permite editar pedidos cancelados
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido cancelado não pode ser editado");
        }

        List<Produto> produtos = produtoService.listarTodos(); // Todos os produtos
        
        return new ModelAndView("pedido/edit", Map.of(
                "pedido", pedido, 
                "produtos", produtos
        )); // [cite: 347]
    }

    // # ADICIONAR ITEM AO PEDIDO - POST
    @PostMapping("/{pedidoId}/adicionar-item")
    public String adicionarItem(@PathVariable Long pedidoId,
                                @RequestParam Long produtoId,
                                @RequestParam Integer quantidade) {
        try {
            pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
            return "redirect:/pedidos/edit/" + pedidoId; // Redireciona para edição
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // # REMOVER ITEM DO PEDIDO - POST
    @PostMapping("/{pedidoId}/remover-item/{itemId}")
    public String removerItem(@PathVariable Long pedidoId,
                              @PathVariable Long itemId) { // Usando @PathVariable para o itemId
        try {
            pedidoService.removerItem(pedidoId, itemId);
            return "redirect:/pedidos/edit/" + pedidoId; // Redireciona para edição
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // # FINALIZAR PEDIDO - POST [cite: 350]
    @PostMapping("/{id}/finalizar")
    public String finalizarPedido(@PathVariable Long id) {
        // A lógica de finalização (como mudar o status para 'FECHADO') pode ir aqui ou ser
        // apenas o redirecionamento conforme o slide.
        return "redirect:/pedidos"; // Redireciona para listagem
    }

    // # CANCELAR PEDIDO - GET (Formulário de confirmação)
    @GetMapping("/cancelar/{id}")
    public ModelAndView cancelarPedidoForm(@PathVariable Long id) {
        Optional<Pedido> optionalPedido = pedidoService.buscarPorId(id);

        if (!optionalPedido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Renderiza a view de cancelamento
        return new ModelAndView("pedido/cancelar", Map.of("pedido", optionalPedido.get()));
    }

    // # CANCELAR PEDIDO - POST (Ação de cancelamento)
    @PostMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable Long id) {
        try {
            pedidoService.cancelarPedido(id); // Tenta cancelar o pedido
            return "redirect:/pedidos"; // Redireciona para listagem
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    
    // # DETALHES DO PEDIDO - GET
    @GetMapping("/detalhes/{id}")
    public ModelAndView detalhesPedido(@PathVariable Long id) {
        Optional<Pedido> optionalPedido = pedidoService.buscarPorId(id);
        
        if (!optionalPedido.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Renderiza a view de detalhes
        return new ModelAndView("pedido/detalhes", Map.of("pedido", optionalPedido.get()));
    }
}