package br.com.aweb.sistema_vendas.service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.repository.ClienteRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Criar e Salvar Cliente --------------->
    @Transactional
    public Cliente salvar(Cliente cliente){
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }
        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF ja cadastrado");
        }
        Cliente clienteSalvo = clienteRepository.save(cliente);
        return clienteSalvo;
    }

    // Listar Cliente ---------------------->
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional <Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Deletar Cliente ----------------------->
    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
}