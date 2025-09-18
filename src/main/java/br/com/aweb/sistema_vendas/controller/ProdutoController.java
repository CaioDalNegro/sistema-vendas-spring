package br.com.aweb.sistema_vendas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.service.ProdutoService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private final ProdutoService produtoService;

    ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Listar ----------------------------------->
    @GetMapping
    public ModelAndView list() {
        return new ModelAndView("produto/list", Map.of("produtos",  produtoService.listarProduto()));
    }

    // Criar ----------------------------------->
    @GetMapping("/novo")
    public ModelAndView criarProduto() {
        return new ModelAndView("produto/form", Map.of("produto", new Produto()));
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid Produto produto, BindingResult result) {
        if(result.hasErrors())
            return "produto/form";
        produtoService.criarProduto(produto);
            return "redirect:/produtos";
    }

    // Atualizar -------------------------------->
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id) {

        var optionalProduto = produtoService.buscarPorId(id);
        if(optionalProduto.isPresent()){
            return new ModelAndView("produto/form", Map.of("produto", new Produto()));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/edit/{id}")
    public String edit(@Valid Produto produto, BindingResult result) {
        if(result.hasErrors())
            return "produto/form";
        produtoService.atualizar(produto.getId(), produto);
            return "redirect:/produtos";
    }

    // Exluir -------------------------------->
    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id) {

        var optionalProduto = produtoService.buscarPorId(id);
        if(optionalProduto.isPresent()){
            return new ModelAndView("produto/delete", Map.of("produto", new Produto()));
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/delete/{id}")
    public String delete(Produto produto) {

        produtoService.excluir(produto.getId());
        return "redirect:/produtos";
    }

}
