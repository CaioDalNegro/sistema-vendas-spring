package br.com.aweb.sistema_vendas.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.aweb.sistema_vendas.entity.Produto;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("/produto")
public class ProdutoController {
    
    @Autowired
    private ProdutoRepository produtoRepository;

    // Criar ----------------------------------->
    @GetMapping("/novo")
    public ModelAndView criarProduto() {
        return new ModelAndView("produto/form", Map.of("produto", new Produto()));
    }

    @PostMapping("/novo")
    public String criarProduto(@Valid Produto produto, BindingResult result) {
        if(result.hasErrors())
            return "produto/form";
        produtoRepository.save(produto);
            return "redirect:/produto";
    }
    
    
}
