package com.br.retailmanager.service;

import com.br.retailmanager.dtos.ClienteRequestDto;
import com.br.retailmanager.dtos.ClienteResponseDto;
import com.br.retailmanager.dtos.mapper.ClienteMapper;
import com.br.retailmanager.entity.Cliente;
import com.br.retailmanager.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    // Criar cliente
    public ClienteResponseDto criar(ClienteRequestDto dto) {
        if (clienteRepository.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("Já existe cliente com este CPF");
        }
        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente salvo = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(salvo);
    }

    // Buscar por ID
    public ClienteResponseDto buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toResponseDto(cliente);
    }

    // Buscar por CPF
    public ClienteResponseDto buscarPorCpf(String cpf) {
        Cliente cliente = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toResponseDto(cliente);
    }

    // Buscar por Email
    public ClienteResponseDto buscarPorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toResponseDto(cliente);
    }

    // Buscar por Nome exato
    public ClienteResponseDto buscarPorNome(String nome) {
        Cliente cliente = clienteRepository.findByNome(nome)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        return clienteMapper.toResponseDto(cliente);
    }

    // Buscar por Nome parcial (case-insensitive)
    public List<ClienteResponseDto> buscarPorNomeParcial(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clienteMapper.toResponseDtoList(clientes);
    }

    // Listar todos
    public List<ClienteResponseDto> listar() {
        return clienteMapper.toResponseDtoList(clienteRepository.findAll());
    }

    // Atualizar cliente
    public ClienteResponseDto atualizar(Long id, ClienteRequestDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(clienteMapper.toEndereco(dto.getEndereco()));

        Cliente atualizado = clienteRepository.save(cliente);
        return clienteMapper.toResponseDto(atualizado);
    }

    // Deletar cliente
    public void deletar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        clienteRepository.delete(cliente);
    }


}
