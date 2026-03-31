package com.br.retailmanager.service;

import com.br.retailmanager.dtos.FuncionarioRequestDto;
import com.br.retailmanager.dtos.FuncionarioResponseDto;
import com.br.retailmanager.dtos.mapper.FuncionarioMapper;
import com.br.retailmanager.entity.Funcionario;
import com.br.retailmanager.repository.FuncionarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {
    private final FuncionarioRepository repository;
    private final FuncionarioMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(FuncionarioRepository repository, FuncionarioMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }


    // Criar funcionário
    public FuncionarioResponseDto criar(FuncionarioRequestDto dto) {
        Funcionario funcionario = mapper.toEntity(dto);
        // 🔹 criptografa antes de salvar
        funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));
        Funcionario salvo = repository.save(funcionario);
        return mapper.toResponseDto(salvo);
    }

    // Buscar por ID
    public FuncionarioResponseDto buscarPorId(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        return mapper.toResponseDto(funcionario);
    }

    // Buscar por CPF
    public FuncionarioResponseDto buscarPorCpf(String cpf) {
        Funcionario funcionario = repository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        return mapper.toResponseDto(funcionario);
    }
    public FuncionarioResponseDto buscarPorLogin(String login){
        Funcionario funcionario =repository.findByLogin(login)
                .orElseThrow(()->new RuntimeException("Login não encontrado"));
        return mapper.toResponseDto(funcionario);
    }

    // Buscar por Email
    public FuncionarioResponseDto buscarPorEmail(String email) {
        Funcionario funcionario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        return mapper.toResponseDto(funcionario);
    }

    // Listar todos
    public List<FuncionarioResponseDto> listarTodos() {
        return mapper.toResponseDtoList(repository.findAll());
    }


    // Atualizar funcionário
    public FuncionarioResponseDto atualizar(Long id, FuncionarioRequestDto dto) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        funcionario.setNome(dto.getNome());
        funcionario.setCpf(dto.getCpf());
        funcionario.setEmail(dto.getEmail());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setDataContratacao(dto.getDataContratacao());
        funcionario.setSalario(dto.getSalario());
        funcionario.setCargo(dto.getCargo());
        funcionario.setLogin(dto.getLogin());
        funcionario.setPerfil(dto.getPerfil());

        // 🔹 criptografa a nova senha
        funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Funcionario atualizado = repository.save(funcionario);
        return mapper.toResponseDto(atualizado);
    }


    // Deletar funcionário
    public void deletar(Long id) {
        Funcionario funcionario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        repository.delete(funcionario);
    }


}
