# 🔧 CORREÇÃO: Acréscimo Indevido de R$ 11,57 em 2x Cartão

## 🎯 PROBLEMA IDENTIFICADO

O `VendaService.java` está **recalculando** o valor total, mas a lógica parece correta (apenas soma os itens). O acréscimo de **R$ 11,57** deve estar vindo de:

1. **Tabela `forma_pagamento`** com taxa configurada
2. **Listener `@PrePersist`** na entity `Venda`
3. **Método `gerarParcelas()`** do `ParcelaService`

---

## ✅ SOLUÇÃO 1: Verificar Tabela no Banco de Dados

### Execute este SQL para verificar:

```sql
-- Ver todas as formas de pagamento
SELECT * FROM forma_pagamento;

-- Verificar especificamente CRÉDITO 2x
SELECT * FROM forma_pagamento 
WHERE tipo = 'CREDITO' 
  AND numero_parcelas = 2;

-- Verificar se há campo de taxa
DESCRIBE forma_pagamento;
```

### Se encontrar campo `taxa_juros` ou similar:

```sql
-- Zerar taxa para 2x no cartão
UPDATE forma_pagamento 
SET taxa_juros = 0.0, 
    percentual_juros = 0.0
WHERE tipo = 'CREDITO' 
  AND numero_parcelas = 2;

-- Verificar
SELECT * FROM forma_pagamento WHERE tipo = 'CREDITO';
```

---

## ✅ SOLUÇÃO 2: Verificar Entity Venda

### Abra o arquivo `Venda.java` e procure:

```java
@Entity
public class Venda {
    // ... campos ...
    
    // ❌ VERIFICAR SE EXISTE ALGO ASSIM:
    @PrePersist
    public void calcularTotal() {
        // Se houver lógica de juros aqui, REMOVER ou AJUSTAR
    }
    
    @PreUpdate
    public void recalcularTotal() {
        // Se houver lógica de juros aqui, REMOVER ou AJUSTAR
    }
}
```

### Se encontrar, COMENTAR ou REMOVER:

```java
@Entity
public class Venda {
    // ... campos ...
    
    // ✅ COMENTADO - Não recalcular automaticamente
    // @PrePersist
    // public void calcularTotal() {
    //     ...
    // }
}
```

---

## ✅ SOLUÇÃO 3: Verificar ParcelaService

### Abra `ParcelaService.java` e procure o método `gerarParcelas()`:

```java
public void gerarParcelas(Venda venda, int numeroParcelas, LocalDate primeiraParcela, int intervaloDias) {
    BigDecimal valorTotal = venda.getValorTotal();
    
    // ❌ VERIFICAR SE HÁ ALGO ASSIM:
    if (numeroParcelas > 1) {
        BigDecimal taxaJuros = ...; // Busca taxa de juros
        valorTotal = valorTotal.multiply(BigDecimal.ONE.add(taxaJuros)); // ← PROBLEMA!
    }
    
    BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(numeroParcelas), 2, RoundingMode.HALF_UP);
    
    // ... resto do código
}
```

### Correção:

```java
public void gerarParcelas(Venda venda, int numeroParcelas, LocalDate primeiraParcela, int intervaloDias) {
    BigDecimal valorTotal = venda.getValorTotal();
    
    // ✅ NÃO aplicar juros - usar valor direto da venda
    // Juros só devem ser aplicados se explicitamente configurado E se parcelas > 2
    
    BigDecimal valorParcela = valorTotal.divide(
        BigDecimal.valueOf(numeroParcelas), 
        2, 
        RoundingMode.HALF_UP
    );
    
    for (int i = 1; i <= numeroParcelas; i++) {
        Parcela parcela = new Parcela();
        parcela.setVenda(venda);
        parcela.setNumero(i);
        parcela.setValor(valorParcela);
        parcela.setDataVencimento(primeiraParcela.plusDays((long) (i - 1) * intervaloDias));
        parcela.setStatus(StatusPagamento.PENDENTE);
        
        parcelaRepository.save(parcela);
    }
}
```

---

## ✅ SOLUÇÃO 4: Modificar VendaService (RECOMENDADO)

### Substituir o método `criar()` por esta versão:

```java
@Transactional
public VendaResponseDto criar(VendaRequestDto dto) {
    Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

    Venda venda = vendaMapper.toEntity(dto);
    venda.setCliente(cliente);
    venda.setTipoPagamento(dto.getTipoPagamento());
    venda.setNumeroParcelas(dto.getNumeroParcelas());

    List<ItemVenda> itens = dto.getItens().stream().map(itemDto -> {
        Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getEstoqueAtual() < itemDto.getQuantidade()) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemVenda item = new ItemVenda();
        item.setProduto(produto);
        item.setQuantidade(itemDto.getQuantidade());
        item.setPrecoUnitario(itemDto.getPrecoUnitario());
        item.setVenda(venda);
        return item;
    }).toList();

    venda.setItens(itens);

    // ✅ USAR VALOR DO DTO (frontend) em vez de recalcular
    BigDecimal valorTotal;
    if (dto.getValorTotal() != null && dto.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
        // Usar valor enviado pelo frontend
        valorTotal = dto.getValorTotal();
        System.out.println("✅ Usando valor do frontend: " + valorTotal);
    } else {
        // Fallback: calcular se não foi enviado
        valorTotal = itens.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("⚠️ Calculando valor no backend: " + valorTotal);
    }

    venda.setValorTotal(valorTotal);
    
    System.out.println("📊 VALOR FINAL DA VENDA: " + venda.getValorTotal());
    System.out.println("📊 Tipo Pagamento: " + venda.getTipoPagamento());
    System.out.println("📊 Número Parcelas: " + venda.getNumeroParcelas());

    // salva a venda
    Venda salva = vendaRepository.save(venda);

    // dar baixa no estoque
    for (ItemVenda item : salva.getItens()) {
        Produto produto = item.getProduto();
        int novoEstoque = produto.getEstoqueAtual() - item.getQuantidade();
        produto.setEstoqueAtual(novoEstoque);
        produtoRepository.save(produto);
    }

    // gerar parcelas apenas para CARNE
    if ("CARNE".equalsIgnoreCase(dto.getTipoPagamento()) && salva.getNumeroParcelas() > 1) {
        System.out.println("📝 Gerando parcelas para CARNÊ...");
        parcelaService.gerarParcelas(
                salva,
                salva.getNumeroParcelas(),
                dto.getPrimeiraParcela(),
                dto.getIntervaloDias()
        );
    } else if ("CREDITO".equalsIgnoreCase(dto.getTipoPagamento()) && salva.getNumeroParcelas() > 1) {
        System.out.println("💳 CRÉDITO parcelado - SEM juros para 2x");
        // Não gerar parcelas para crédito parcelado (diferente de carnê)
    }

    VendaResponseDto response = vendaMapper.toResponseDto(salva);
    System.out.println("📤 Retornando para frontend - Valor Total: " + response.getValorTotal());
    
    return response;
}
```

---

## 🧪 TESTE APÓS CORREÇÃO

### 1. Adicionar Logs no Backend

No início do método `criar()`, adicione:

```java
System.out.println("════════════════════════════════════════════");
System.out.println("📥 RECEBENDO VENDA DO FRONTEND");
System.out.println("   Cliente ID: " + dto.getClienteId());
System.out.println("   Tipo Pagamento: " + dto.getTipoPagamento());
System.out.println("   Número Parcelas: " + dto.getNumeroParcelas());
System.out.println("   Valor Total (DTO): " + dto.getValorTotal());
System.out.println("   Itens: " + dto.getItens().size());
System.out.println("════════════════════════════════════════════");
```

No final, antes do `return`:

```java
System.out.println("════════════════════════════════════════════");
System.out.println("📤 RETORNANDO VENDA PARA FRONTEND");
System.out.println("   ID: " + salva.getId());
System.out.println("   Valor Total (SALVO): " + salva.getValorTotal());
System.out.println("════════════════════════════════════════════");
```

### 2. Fazer Venda de Teste

1. Frontend: Venda de R$ 100,00 em 2x CRÉDITO
2. Verificar logs no **console do IntelliJ** (backend)
3. Verificar logs no **console do navegador** (frontend)

### 3. Logs Esperados

**Backend (IntelliJ)**:
```
════════════════════════════════════════════
📥 RECEBENDO VENDA DO FRONTEND
   Valor Total (DTO): 100.00
   Tipo Pagamento: CREDITO
   Número Parcelas: 2
════════════════════════════════════════════
✅ Usando valor do frontend: 100.00
📊 VALOR FINAL DA VENDA: 100.00
💳 CRÉDITO parcelado - SEM juros para 2x
════════════════════════════════════════════
📤 RETORNANDO VENDA PARA FRONTEND
   Valor Total (SALVO): 100.00
════════════════════════════════════════════
```

**Frontend (Navegador F12)**:
```
📤 Enviando venda para o backend:
   Valor Total (FRONTEND): 100.00
   
📥 Resposta do backend:
   Valor Total (BACKEND): 100.00
   
✅ Valores iguais! Problema resolvido!
```

---

## 📋 CHECKLIST DE VERIFICAÇÃO

- [ ] Verificar tabela `forma_pagamento` no banco (SQL acima)
- [ ] Verificar entity `Venda.java` por `@PrePersist` ou `@PreUpdate`
- [ ] Verificar `ParcelaService.gerarParcelas()` por lógica de juros
- [ ] Aplicar SOLUÇÃO 4 (modificar `VendaService.criar()`)
- [ ] Adicionar logs no início e fim do método `criar()`
- [ ] Reiniciar backend Spring Boot
- [ ] Fazer venda de teste no frontend
- [ ] Verificar logs no IntelliJ
- [ ] Verificar logs no navegador (F12)
- [ ] Confirmar que valores são iguais

---

## 🎯 RESULTADO ESPERADO

**ANTES**:
```
Frontend envia: R$ 100,00
Backend salva:  R$ 111,57 ❌
```

**DEPOIS**:
```
Frontend envia: R$ 100,00
Backend salva:  R$ 100,00 ✅
```

---

## 📞 SE O PROBLEMA PERSISTIR

Envie para mim:

1. **Logs completos do IntelliJ** (backend)
2. **Logs completos do navegador** (F12 → Console)
3. **Screenshot da venda no banco de dados**:
   ```sql
   SELECT * FROM venda ORDER BY id DESC LIMIT 1;
   ```
4. **Estrutura da tabela `forma_pagamento`**:
   ```sql
   DESCRIBE forma_pagamento;
   SELECT * FROM forma_pagamento;
   ```

Com isso, identificarei o problema exato!

---

**Criado em**: 2026-02-12
**Arquivo**: CORRECAO-BACKEND-JUROS.md
