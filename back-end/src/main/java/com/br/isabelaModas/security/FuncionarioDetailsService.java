package com.br.isabelaModas.security;

import com.br.isabelaModas.dtos.FuncionarioResponseDto;
import com.br.isabelaModas.entity.Funcionario;
import com.br.isabelaModas.entity.enums.Perfil;
import com.br.isabelaModas.repository.FuncionarioRepository;
import com.br.isabelaModas.service.FuncionarioService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioDetailsService implements UserDetailsService {
    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioService funcionarioService;


    public FuncionarioDetailsService(FuncionarioRepository funcionarioRepository,
                                     FuncionarioService funcionarioService) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioService = funcionarioService;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Funcionario funcionario = funcionarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new User(
                funcionario.getLogin(),
                funcionario.getSenha(),
                List.of(new SimpleGrantedAuthority("ROLE_" + funcionario.getPerfil().name()))
        );
    }

    public String getTokenAuthenticated(String login) {
        FuncionarioResponseDto funcionario = funcionarioService.buscarPorLogin(login);

        Perfil perfil = funcionario.getPerfil(); // enum Perfil
        Long userId = funcionario.getId();

        // ✅ passa o nome do enum direto
        return JwtUtils.createToken(login, perfil.name(), userId);
    }
}