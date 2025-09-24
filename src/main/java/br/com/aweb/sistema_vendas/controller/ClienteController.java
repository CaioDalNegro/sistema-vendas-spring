package br.com.aweb.sistema_vendas.controller;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.service.ClienteService;
import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Listagem
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos());
        return "cliente/list";
    }

    // Formulário de cadastro
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/form";
    }

    // Salvar
    @PostMapping
    public String salvar(@Valid @ModelAttribute Cliente cliente, BindingResult result) {
        if (result.hasErrors()) {
            return "cliente/form";
        }
        clienteService.salvar(cliente);
        return "redirect:/clientes";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clienteService.buscarPorId(id));
        return "cliente/form";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView excluir(@PathVariable Long id) {

        var optionalCliente = clienteService.buscarPorId(id);
        if(optionalCliente.isPresent()){
            return new ModelAndView("cliente/delete", Map.of("cliente", new Cliente()));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete/{id}")
    public String delete(Produto produto) {

        clienteService.excluir(produto.getId());
        return "redirect:/clientes";
    }
}
