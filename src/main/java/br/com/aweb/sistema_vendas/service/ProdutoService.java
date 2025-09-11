package br.com.aweb.sistema_vendas.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.entity.Produto;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {
    
    @Autowired
    ProdutoRepository produtoRepository;

    // Listar Produto ------------->
    public List<Produto> listarProduto(){
        return produtoRepository.findAll();
    }

    // Criar Produto --------------->
    @Transactional
    public Produto criarProduto(Produto produto){
        return produtoRepository.save(produto);
    }


    // Excluir produto------------------------------->
    public void deletarProduto(Long id){
        if(!produtoRepository.existsById(id))
            throw new RuntimeException("produto não encontrado!");
        produtoRepository.deleteById(id);
    }
}
