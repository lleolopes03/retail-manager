// ═══════════════════════════════════════════════════════════════
// 📁 ARQUIVO: MovimentacaoEstoqueController.java
// 🔹 ADICIONE ESTE MÉTODO no Controller (depois do método buscarPorProduto)
// ═══════════════════════════════════════════════════════════════

    // 📌 Devolver saída temporária ao estoque
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{id}/devolver")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> devolverAoEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.devolverAoEstoque(id));
    }

// ═══════════════════════════════════════════════════════════════
// 📁 ARQUIVO: MovimentacaoEstoqueService.java
// 🔹 ADICIONE ESTE MÉTODO no Service (depois do método relatorioEstoqueAtual)
// ═══════════════════════════════════════════════════════════════

    /**
     * Devolve uma saída temporária ao estoque.
     * Cria uma nova movimentação de DEVOLUCAO e atualiza o estoque do produto.
     */
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

        // 4. Devolver estoque
        produto.adicionarEstoque(movimentacaoOriginal.getQuantidade());

        // 5. Criar movimentação de devolução
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

// ═══════════════════════════════════════════════════════════════
// 🎯 OPCIONAL - Para remover o botão de lixeira (Opção A)
// ═══════════════════════════════════════════════════════════════
// Você pode comentar ou remover o método DELETE se quiser forçar
// o fluxo de devolver/vender ao invés de apenas excluir.
// Mas isso não é obrigatório - pode manter para casos de erro.

