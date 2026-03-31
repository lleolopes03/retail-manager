# 🎯 Instruções para Implementar Endpoint de Devolução no Backend

## ✅ Frontend Já Implementado
- Botão verde "Devolver ao Estoque" ✓
- Botão azul "Registrar Venda" ✓
- Removido botão de lixeira ✓

## ⚠️ Backend Precisa Adicionar

### 📁 Arquivo: `MovimentacaoEstoqueController.java`

**Adicione este método** (após `buscarPorProduto()`):

```java
// 📌 Devolver saída temporária ao estoque
@PreAuthorize("hasAnyRole('ADMIN','VENDEDOR','GERENTE_SISTEMA')")
@PostMapping("/{id}/devolver")
public ResponseEntity<MovimentacaoEstoqueResponseDto> devolverAoEstoque(@PathVariable Long id) {
    return ResponseEntity.ok(movimentacaoEstoqueService.devolverAoEstoque(id));
}
```

---

### 📁 Arquivo: `MovimentacaoEstoqueService.java`

**Adicione este método** (após `relatorioEstoqueAtual()`):

```java
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
```

---

## 🧪 Teste Completo

### 1. **Reinicie o backend Spring Boot**
```bash
# No IntelliJ: Clique em "Stop" e depois "Run"
# Ou no terminal do backend:
./mvnw spring-boot:run
```

### 2. **No frontend: Ctrl+F5 (recarregar forçado)**

### 3. **Fluxo de teste:**

#### A. Criar Movimentação
1. Dashboard → Operações → Movimentações → Nova Movimentação
2. Selecione cliente: **Leandro**
3. Adicione 2 produtos com estoque disponível
4. Clique "Registrar Movimentação"
5. ✅ Devem aparecer 2 linhas na lista

#### B. Testar Devolução
1. Na lista, localize uma movimentação tipo **SAIDA_TEMPORARIA**
2. Clique no **botão verde** (🔄 Devolver)
3. Confirme a operação
4. ✅ **Resultado esperado:**
   - Alerta: "Produto devolvido com sucesso!"
   - Estoque do produto aumenta
   - Nova linha aparece na lista: tipo **DEVOLUCAO**
   - Botões verdes/azuis somem da linha original

#### C. Testar Venda
1. Na lista, clique no **botão azul** (🛒 Vender) de outra movimentação
2. Confirme
3. ✅ **Resultado esperado:**
   - Redireciona para tela de venda
   - Cliente e produto já vêm preenchidos
   - Complete forma de pagamento e finalize

---

## 📊 Resultado Visual Esperado

```
┌─────────────────────────────────────────────────────────────┐
│ Data       │ Tipo             │ Produto        │ Ações      │
├─────────────────────────────────────────────────────────────┤
│ 11/02 14:30│ SAIDA_TEMPORARIA │ Camisa Nike    │ 🟢 🔵     │
│ 11/02 14:30│ SAIDA_TEMPORARIA │ Camisa Adidas  │ 🟢 🔵     │
└─────────────────────────────────────────────────────────────┘

[Usuário clica no botão verde da Camisa Nike]

┌─────────────────────────────────────────────────────────────┐
│ Data       │ Tipo             │ Produto        │ Ações      │
├─────────────────────────────────────────────────────────────┤
│ 11/02 15:00│ DEVOLUCAO        │ Camisa Nike    │   ✓       │ ← NOVO!
│ 11/02 14:30│ SAIDA_TEMPORARIA │ Camisa Nike    │   ✓       │ ← Finalizado
│ 11/02 14:30│ SAIDA_TEMPORARIA │ Camisa Adidas  │ 🟢 🔵     │
└─────────────────────────────────────────────────────────────┘
```

---

## ❓ Problemas Comuns

### Erro 405 (Method Not Allowed)
- **Causa:** Backend não tem o endpoint `POST /{id}/devolver`
- **Solução:** Adicione o código acima no Controller

### Erro 404 (Not Found)
- **Causa:** Caminho do endpoint está errado
- **Solução:** Verifique se o `@PostMapping("/{id}/devolver")` está correto

### Erro 500 (Server Error)
- **Causa:** Possível erro de lógica no Service
- **Solução:** Verifique os logs do backend e confirme que:
  - `produto.adicionarEstoque()` existe
  - `TipoMovimentacao.DEVOLUCAO` está definido no enum

---

## 🎉 Sucesso!
Se tudo funcionou, você agora tem um fluxo completo:
- ✅ Cliente leva produtos para experimentar
- ✅ Sistema registra saída temporária
- ✅ Cliente devolve (botão verde) → estoque atualiza
- ✅ Cliente compra (botão azul) → abre tela de venda
- ✅ Histórico completo de movimentações
