# 🎯 Código para Adicionar no Backend Java

## ═══════════════════════════════════════════════════════════════
## 📁 PARTE 1: MovimentacaoEstoqueController.java
## ═══════════════════════════════════════════════════════════════

**Localização:** Adicione DEPOIS do método `relatorioEstoqueAtual()` (última linha antes do `}` final)

```java
    // 📌 Devolver saída temporária ao estoque
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{id}/devolver")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> devolverAoEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.devolverAoEstoque(id));
    }
}
```

**Ficará assim:**

```java
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @GetMapping("/relatorio/estoque")
    public ResponseEntity<List<ProdutoEstoqueDto>> relatorioEstoqueAtual() {
        return ResponseEntity.ok(movimentacaoEstoqueService.relatorioEstoqueAtual());
    }

    // 📌 Devolver saída temporária ao estoque
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
    @PostMapping("/{id}/devolver")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> devolverAoEstoque(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoEstoqueService.devolverAoEstoque(id));
    }
}
```

---

## ═══════════════════════════════════════════════════════════════
## 📁 PARTE 2: MovimentacaoEstoqueService.java
## ═══════════════════════════════════════════════════════════════

**Localização:** Adicione DEPOIS do método `relatorioEstoqueAtual()` (última linha antes do `}` final)

```java
    /**
     * Devolve uma saída temporária ao estoque.
     * Busca a movimentação original, valida o tipo, e cria uma nova movimentação de DEVOLUCAO.
     * Atualiza o estoque do produto automaticamente.
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
```

**Ficará assim:**

```java
    public List<ProdutoEstoqueDto> relatorioEstoqueAtual() {
        return produtoRepository.relatorioEstoqueAtual().stream()
                .map(obj -> new ProdutoEstoqueDto(
                        (String) obj[0],   // nome
                        (String) obj[1],   // tamanho
                        (Integer) obj[2]   // quantidade atual
                ))
                .toList();
    }

    /**
     * Devolve uma saída temporária ao estoque.
     * Busca a movimentação original, valida o tipo, e cria uma nova movimentação de DEVOLUCAO.
     * Atualiza o estoque do produto automaticamente.
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
```

---

## ═══════════════════════════════════════════════════════════════
## 🧪 TESTE RÁPIDO
## ═══════════════════════════════════════════════════════════════

### 1. **Reinicie o backend**
```bash
# No IntelliJ: Stop → Run
# Procure por: "Started IsabelaModasApplication"
```

### 2. **Teste via Postman/Insomnia (opcional)**
```
POST http://localhost:8080/api/v1/movimentacoes-estoque/1/devolver
Headers:
  Authorization: Bearer SEU_TOKEN_JWT
  Content-Type: application/json
```

**Resposta esperada (200 OK):**
```json
{
  "id": 2,
  "produtoId": 3,
  "produtoNome": "Camisa Nike",
  "tipoMovimentacao": "DEVOLUCAO",
  "quantidade": 1,
  "dataMovimentacao": "2026-02-11T16:30:00",
  "observacao": "Devolução da movimentação #1"
}
```

### 3. **Teste no frontend**
1. Aguarde backend reiniciar
2. No navegador: **Ctrl+F5** (recarregar)
3. Dashboard → Operações → Movimentações
4. Clique no **botão verde** (🔄 Devolver)
5. Confirme
6. ✅ Deve aparecer alerta: "Produto devolvido com sucesso!"

---

## ❓ TROUBLESHOOTING

### Erro: "Apenas saídas temporárias podem ser devolvidas"
- **Causa:** Tentando devolver uma movimentação tipo DEVOLUCAO ou SAIDA
- **Solução:** Só é possível devolver movimentações tipo SAIDA_TEMPORARIA

### Erro: "Movimentação não encontrada"
- **Causa:** ID inválido ou movimentação foi excluída
- **Solução:** Verifique se o ID existe no banco de dados

### Erro: Método não encontrado no Service
- **Causa:** Esqueceu de adicionar o método no MovimentacaoEstoqueService
- **Solução:** Copie o código da PARTE 2 acima

---

## ✅ PRONTO!

Após adicionar esses 2 métodos, seu sistema terá:
- ✅ Cliente leva produtos para experimentar (já funciona)
- ✅ Cliente devolve produtos (botão verde no frontend)
- ✅ Cliente compra produtos (botão azul redireciona para venda)
- ✅ Histórico completo de movimentações
- ✅ Estoque sempre atualizado automaticamente
