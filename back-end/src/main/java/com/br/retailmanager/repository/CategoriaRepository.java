package com.br.retailmanager.repository;

import com.br.retailmanager.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
    // Busca exata
    Optional<Categoria> findByNome(String nome);

    // Busca parcial (contendo parte do nome)
    List<Categoria> findByNomeContainingIgnoreCase(String nome);


}
