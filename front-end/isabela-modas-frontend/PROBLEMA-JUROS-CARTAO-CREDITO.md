# 🔍 PROBLEMA: Acréscimo Indevido em Cartão de Crédito 2x

## 📋 Descrição do Problema

**Situação**: Ao fazer uma venda parcelada em **2x no cartão de crédito**, o sistema está adicionando um acréscimo de **R$ 11,57** ao valor total.

**Comportamento Esperado**: Parcelamento em 2x no cartão de crédito **NÃO deveria ter juros**.

**Comportamento Atual**: 
- 1x no cartão: Valor exato ✅
- 2x no cartão: Valor original + R$ 11,57 ❌

---

## 🔍 Investigação

### Frontend (Angular) ✅ CORRETO

O frontend calcula o total corretamente:

```typescript
// venda.component.ts - linha 160
calcularTotal() {
  const total = this.itens.controls.reduce(
    (acc, item) => acc + (item.value.subtotal || 0),
    0
  );
  this.vendaForm.patchValue({ valorTotal: total }, { emitEvent: false });
}
```

**Conclusão**: O frontend envia o valor correto para o backend.

---

### Backend (Spring Boot) ⚠️ PROBLEMA AQUI

O backend **DEVE estar recalculando** o valor total e aplicando taxa de juros automaticamente.

#### 📂 Locais Suspeitos no Backend Java

**1️⃣ Service de Venda**
```java
// VendaService.java ou similar
public VendaResponseDto cadastrarVenda(VendaRequestDto dto) {
    // ❌ VERIFICAR: O service está recalculando o valor total?
    // ❌ VERIFICAR: Há lógica de aplicação de juros aqui?
    
    // Exemplo do que pode estar acontecendo:
    if (dto.getNumeroParcelas() > 1) {
        // ❌ PROBLEMA: Aplicando juros automaticamente
        BigDecimal taxaJuros = formaPagamento.getTaxaJuros(); // Ex: 0.05 (5%)
        dto.setValorTotal(dto.getValorTotal().multiply(taxaJuros));
    }
}
```

**2️⃣ Entity FormaPagamento**
```java
// FormaPagamento.java
@Entity
public class FormaPagamento {
    private String tipo; // CREDITO, DEBITO, PIX, CARNE
    private Integer numeroParcelas;
    
    // ❌ VERIFICAR: Existe este campo?
    private BigDecimal taxaJuros; // ou percentualJuros
    
    // ❌ VERIFICAR: Existe este método?
    public BigDecimal calcularValorComJuros(BigDecimal valorBase) {
        if (this.numeroParcelas > 1 && this.taxaJuros != null) {
            return valorBase.multiply(BigDecimal.ONE.add(this.taxaJuros));
        }
        return valorBase;
    }
}
```

**3️⃣ Listener ou Interceptor**
```java
// ❌ VERIFICAR: Existe algum @PrePersist ou @PreUpdate?
@Entity
public class Venda {
    @PrePersist
    public void calcularTotal() {
        // ❌ PROBLEMA: Pode estar recalculando aqui
        if (this.formaPagamento.getNumeroParcelas() > 1) {
            // Aplicando juros automaticamente
        }
    }
}
```

**4️⃣ Tabela no Banco de Dados**
```sql
-- ❌ VERIFICAR: A tabela tem este campo?
SELECT * FROM forma_pagamento;
-- Colunas: id, tipo, numero_parcelas, taxa_juros, percentual_juros

-- ❌ VERIFICAR: Há valores configurados?
SELECT id, tipo, numero_parcelas, taxa_juros 
FROM forma_pagamento 
WHERE tipo = 'CREDITO' AND numero_parcelas = 2;
-- Se taxa_juros > 0, ESSE É O PROBLEMA!
```

---

## ✅ Soluções

### Solução 1: Remover Taxa de Juros da Tabela

```sql
-- Zerar taxa de juros para 2x no cartão
UPDATE forma_pagamento 
SET taxa_juros = 0.0 
WHERE tipo = 'CREDITO' AND numero_parcelas = 2;
```

### Solução 2: Condicional no Service

```java
// VendaService.java
public VendaResponseDto cadastrarVenda(VendaRequestDto dto) {
    Venda venda = mapper.toEntity(dto);
    
    // ✅ NÃO recalcular valor total
    // venda.setValorTotal(dto.getValorTotal()); // Usar valor do frontend
    
    // ✅ Aplicar juros APENAS se configurado E se parcelas > 2
    if (formaPagamento.getNumeroParcelas() > 2 && formaPagamento.getTaxaJuros() != null) {
        BigDecimal valorComJuros = dto.getValorTotal()
            .multiply(BigDecimal.ONE.add(formaPagamento.getTaxaJuros()));
        venda.setValorTotal(valorComJuros);
    }
    
    vendaRepository.save(venda);
    return mapper.toResponseDto(venda);
}
```

### Solução 3: Configuração de Juros por Parcela

```java
// FormaPagamento.java
public BigDecimal calcularValorFinal(BigDecimal valorBase) {
    // ✅ 1x e 2x SEM juros
    if (this.numeroParcelas <= 2) {
        return valorBase;
    }
    
    // ✅ 3x ou mais COM juros (se configurado)
    if (this.taxaJuros != null && this.taxaJuros.compareTo(BigDecimal.ZERO) > 0) {
        return valorBase.multiply(BigDecimal.ONE.add(this.taxaJuros));
    }
    
    return valorBase;
}
```

---

## 🧪 Como Testar Após Correção

### 1. Teste no Frontend (com console aberto F12)

Fazer uma venda de R$ 100,00 parcelada em 2x no cartão:

**Logs esperados:**
```
📤 Enviando venda para o backend:
   Tipo Pagamento: CREDITO
   Número Parcelas: 2
   Valor Total (FRONTEND): 100.00
   
📥 Resposta do backend:
   Valor Total (BACKEND): 100.00
   
✅ Valores iguais! Nenhum acréscimo aplicado.
```

### 2. Verificar no Banco de Dados

```sql
-- Buscar a venda recém-criada
SELECT id, cliente_id, valor_total, tipo_pagamento 
FROM venda 
ORDER BY id DESC LIMIT 1;

-- Verificar parcelas
SELECT numero, valor, data_vencimento 
FROM pagamento 
WHERE venda_id = <ID_DA_VENDA>;

-- Resultado esperado:
-- Parcela 1: R$ 50,00
-- Parcela 2: R$ 50,00
-- TOTAL: R$ 100,00 (SEM acréscimo)
```

---

## 📝 Checklist de Verificação

- [ ] Verificar tabela `forma_pagamento` no banco
- [ ] Verificar campo `taxa_juros` ou `percentual_juros`
- [ ] Verificar `VendaService.cadastrarVenda()`
- [ ] Verificar método `calcularTotal()` em `Venda` entity
- [ ] Verificar `@PrePersist` ou `@PreUpdate` listeners
- [ ] Testar venda 2x após correção
- [ ] Verificar logs do console (frontend)
- [ ] Verificar valor no banco de dados

---

## 🎯 Resultado Esperado

**ANTES**: R$ 100,00 → 2x = R$ 111,57 ❌
**DEPOIS**: R$ 100,00 → 2x = R$ 100,00 (2x de R$ 50,00) ✅

---

## 📞 Próximos Passos

1. Abrir backend Java no IntelliJ/Eclipse
2. Buscar pelos arquivos mencionados acima
3. Adicionar `System.out.println()` para rastrear onde o valor está sendo alterado
4. Aplicar uma das soluções sugeridas
5. Testar com venda real
6. Verificar logs do frontend (F12 → Console)

---

**Gerado em**: 2026-02-12
**Sistema**: Isabela Modas ERP
**Módulo**: Vendas - Parcelamento Cartão de Crédito
