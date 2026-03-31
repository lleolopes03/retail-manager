package com.br.retailmanager.service;

import com.br.retailmanager.dtos.MovimentacaoEstoqueRequestDto;
import com.br.retailmanager.dtos.MovimentacaoEstoqueResponseDto;
import com.br.retailmanager.dtos.ProdutoEstoqueDto;
import com.br.retailmanager.dtos.mapper.MovimentacaoEstoqueMapper;
import com.br.retailmanager.entity.Cliente;
import com.br.retailmanager.entity.MovimentacaoEstoque;
import com.br.retailmanager.entity.Produto;
import com.br.retailmanager.entity.Venda;
import com.br.retailmanager.entity.enums.TipoMovimentacao;
import com.br.retailmanager.repository.ClienteRepository;
import com.br.retailmanager.repository.MovimentacaoEstoqueRepository;
import com.br.retailmanager.repository.ProdutoRepository;
import com.br.retailmanager.repository.VendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoEstoqueService {
    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final MovimentacaoEstoqueMapper mapper;

    public MovimentacaoEstoqueService(MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
                                      ProdutoRepository produtoRepository,
                                      VendaRepository vendaRepository,
                                      ClienteRepository clienteRepository,
                                      MovimentacaoEstoqueMapper mapper) {
        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.clienteRepository = clienteRepository;
        this.mapper = mapper;
    }

    // 📌 Cliente leva peças para experimentar
    public MovimentacaoEstoqueResponseDto registrarSaidaTemporaria(MovimentacaoEstoqueRequestDto dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // baixa temporária usando método utilitário da entidade
        produto.baixarEstoque(dto.getQuantidade());

        MovimentacaoEstoque mov = mapper.toEntity(dto);
        mov.setProduto(produto);
        mov.setCliente(cliente);
        mov.setTipoMovimentacao(TipoMovimentacao.SAIDA_TEMPORARIA);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setObservacao("Cliente levou para experimentar");

        movimentacaoEstoqueRepository.save(mov);
        produtoRepository.save(produto);

        return mapper.toResponseDto(mov);

    }

        // 📌 Venda definitiva (cliente ficou com algumas peças)
    public MovimentacaoEstoqueResponseDto registrarVenda(MovimentacaoEstoqueRequestDto dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Venda venda = vendaRepository.findById(dto.getVendaId())
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        MovimentacaoEstoque mov = mapper.toEntity(dto);
        mov.setProduto(produto);
        mov.setVenda(venda);
        mov.setTipoMovimentacao(TipoMovimentacao.SAIDA);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setObservacao("Venda confirmada");

        movimentacaoEstoqueRepository.save(mov);

        return mapper.toResponseDto(mov);
    }

    // 📌 Devolução (peças que voltam ao estoque)
    public MovimentacaoEstoqueResponseDto registrarDevolucao(MovimentacaoEstoqueRequestDto dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // devolve para estoque usando método utilitário da entidade
        produto.adicionarEstoque(dto.getQuantidade());

        MovimentacaoEstoque mov = mapper.toEntity(dto);
        mov.setProduto(produto);
        mov.setCliente(cliente);
        mov.setTipoMovimentacao(TipoMovimentacao.DEVOLUCAO);
        mov.setDataMovimentacao(LocalDateTime.now());
        mov.setObservacao("Devolução após experimentação");

        movimentacaoEstoqueRepository.save(mov);
        produtoRepository.save(produto);

        return mapper.toResponseDto(mov);
    }

    // 📌 Listar todas movimentações
    public List<MovimentacaoEstoqueResponseDto> listar() {
        return mapper.toResponseDtoList(movimentacaoEstoqueRepository.findAll());
    }

    // 📌 Buscar movimentação por ID
    public MovimentacaoEstoqueResponseDto buscarPorId(Long id) {
        MovimentacaoEstoque mov = movimentacaoEstoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));
        return mapper.toResponseDto(mov);
    }

    // 📌 Buscar movimentações por cliente (CPF)
    public List<MovimentacaoEstoqueResponseDto> buscarPorClienteCpf(String cpf) {
        return mapper.toResponseDtoList(movimentacaoEstoqueRepository.findByClienteCpf(cpf));
    }

    // 📌 Buscar movimentações por produto
    public List<MovimentacaoEstoqueResponseDto> buscarPorProduto(Long produtoId) {
        return mapper.toResponseDtoList(movimentacaoEstoqueRepository.findByProdutoId(produtoId));
    }
    public List<ProdutoEstoqueDto> relatorioEstoqueAtual() {
        return produtoRepository.relatorioEstoqueAtual().stream()
                .map(obj -> new ProdutoEstoqueDto(
                        (String) obj[0],   // nome
                        (String) obj[1],   // tamanho
                        (Integer) obj[2]   // quantidade atual
                ))
                .toList();
    }
    public MovimentacaoEstoqueResponseDto devolverAoEstoque(Long movimentacaoId) {
        // 1. Buscar a movimentação original
        MovimentacaoEstoque movimentacaoOriginal = movimentacaoEstoqueRepository.findById(movimentacaoId)
                .orElseThrow(() -> new RuntimeException("Movimentação não encontrada"));

        // 2. Validar que é uma saída temporária
        if (movimentacaoOriginal.getTipoMovimentacao() != TipoMovimentacao.SAIDA_TEMPORARIA) {
            throw new RuntimeException("Apenas saídas temporárias podem ser devolvidas");
        }

        // 3. Buscar produto e cliente
        Produto produto = movimentacaoOriginal.getProduto();
        Cliente cliente = movimentacaoOriginal.getCliente();

        // 4. Devolver estoque usando método utilitário da entidade
        produto.adicionarEstoque(movimentacaoOriginal.getQuantidade());

        // 5. Criar nova movimentação de devolução
        MovimentacaoEstoque devolucao = new MovimentacaoEstoque();
        devolucao.setProduto(produto);
        devolucao.setCliente(cliente);
        devolucao.setTipoMovimentacao(TipoMovimentacao.DEVOLUCAO);
        devolucao.setQuantidade(movimentacaoOriginal.getQuantidade());
        devolucao.setDataMovimentacao(LocalDateTime.now());
        devolucao.setObservacao("Devolução da movimentação #" + movimentacaoId);

        // 6. Salvar tudo
        produtoRepository.save(produto);
        movimentacaoEstoqueRepository.save(devolucao);

        return mapper.toResponseDto(devolucao);
    }



}
