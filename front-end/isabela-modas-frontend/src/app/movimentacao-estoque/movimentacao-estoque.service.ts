import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Item individual dentro de uma movimentação
export interface ItemMovimentacao {
  id?: number;
  produtoId: number;
  produtoNome?: string;
  quantidade: number;
  statusItem: 'PENDENTE' | 'DEVOLVIDO' | 'VENDIDO';
  dataAcao?: string;
}

// Movimentação completa (agrupa múltiplos produtos)
export interface MovimentacaoEstoque {
  id?: number;
  clienteId?: number;
  cliente?: any;
  dataMovimentacao?: string;
  tipoMovimentacao: 'SAIDA_TEMPORARIA' | 'DEVOLUCAO' | 'VENDA' | 'ENTRADA' | 'SAIDA';
  status?: 'ATIVA' | 'FINALIZADA';
  observacao?: string;
  itens?: ItemMovimentacao[]; // Array de produtos (opcional para compatibilidade)
  
  // Campos legados (para compatibilidade com backend antigo que espera 1 produto por requisição)
  produtoId?: number;
  produtoNome?: string;
  produto?: any;
  quantidade?: number;
  vendaId?: number;
}

@Injectable({ providedIn: 'root' })
export class MovimentacaoEstoqueService {
  private apiUrl = 'http://localhost:8080/api/v1/movimentacoes-estoque';
  private clienteUrl = 'http://localhost:8080/api/v1/clientes';
  private produtoUrl = 'http://localhost:8080/api/v1/produtos';

  constructor(private http: HttpClient) {}

  // 🔹 Registrar movimentações
  registrarSaidaTemporaria(dto: MovimentacaoEstoque): Observable<MovimentacaoEstoque> {
    return this.http.post<MovimentacaoEstoque>(`${this.apiUrl}/saida-temporaria`, dto);
  }

  registrarVenda(dto: MovimentacaoEstoque): Observable<MovimentacaoEstoque> {
    return this.http.post<MovimentacaoEstoque>(`${this.apiUrl}/venda`, dto);
  }

  registrarDevolucao(dto: MovimentacaoEstoque): Observable<MovimentacaoEstoque> {
    return this.http.post<MovimentacaoEstoque>(`${this.apiUrl}/devolucao`, dto);
  }

  // 🔹 CRUD de movimentações
  listar(): Observable<MovimentacaoEstoque[]> {
    return this.http.get<MovimentacaoEstoque[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<MovimentacaoEstoque> {
    return this.http.get<MovimentacaoEstoque>(`${this.apiUrl}/${id}`);
  }

  buscarPorClienteCpf(cpf: string): Observable<MovimentacaoEstoque[]> {
    return this.http.get<MovimentacaoEstoque[]>(`${this.apiUrl}/cliente/${cpf}`);
  }

  buscarPorProduto(produtoId: number): Observable<MovimentacaoEstoque[]> {
    return this.http.get<MovimentacaoEstoque[]>(`${this.apiUrl}/produto/${produtoId}`);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Auxiliares
  listarClientes(): Observable<any[]> {
    return this.http.get<any[]>(this.clienteUrl);
  }

  listarProdutos(): Observable<any[]> {
    return this.http.get<any[]>(this.produtoUrl);
  }

  // 🔹 Relatório de estoque atual
  relatorioEstoqueAtual(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorio/estoque`);
  }

  // 🔹 Devolver saída temporária ao estoque
  devolverAoEstoque(id: number): Observable<MovimentacaoEstoque> {
    return this.http.post<MovimentacaoEstoque>(`${this.apiUrl}/${id}/devolver`, {});
  }

  // 🔹 Devolver item específico (novo)
  devolverItem(movimentacaoId: number, itemId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${movimentacaoId}/itens/${itemId}/devolver`, {});
  }

  // 🔹 Devolver múltiplos itens (novo)
  devolverItens(movimentacaoId: number, itemIds: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${movimentacaoId}/itens/devolver`, { itemIds });
  }

  // 🔹 Converter item para venda (novo)
  converterItemParaVenda(movimentacaoId: number, itemId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${movimentacaoId}/itens/${itemId}/vender`, {});
  }

  // 🔹 Converter múltiplos itens para venda (novo)
  converterItensParaVenda(movimentacaoId: number, itemIds: number[]): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${movimentacaoId}/itens/vender`, { itemIds });
  }

  // 🔹 Buscar movimentações com filtros
  buscarComFiltros(tipo?: string, status?: string, clienteCpf?: string, produtoNome?: string): Observable<MovimentacaoEstoque[]> {
    let url = `${this.apiUrl}?`;
    if (tipo) url += `tipo=${tipo}&`;
    if (status) url += `status=${status}&`;
    if (clienteCpf) url += `clienteCpf=${clienteCpf}&`;
    if (produtoNome) url += `produtoNome=${produtoNome}&`;
    return this.http.get<MovimentacaoEstoque[]>(url);
  }
}
