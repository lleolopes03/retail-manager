import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ItemVenda {
  produtoId: number;
  quantidade: number;
  precoUnitario: number;
}

export interface Venda {
  id?: number;   // 🔹 obrigatório
  dataVenda?: string;
  clienteId: number;
  tipoPagamento: string;
  numeroParcelas: number;
  itens: ItemVenda[];
  valorTotal: number;
  linkPagamento?: string;

  primeiraParcela?: string;   // formato ISO (yyyy-MM-dd)
  intervaloDias?: number;
  formaPagamento?: {   // 🔹 agora opcional
    tipo: string;
    numeroParcelas: number;
  };



}

// 🔹 Estrutura que o back retorna quando usa Page<VendaResponseDto>
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // página atual
}

@Injectable({ providedIn: 'root' })
export class VendaService {
  private apiUrl = 'http://localhost:8080/api/v1/vendas';
  private clienteUrl = 'http://localhost:8080/api/v1/clientes';
  private formaPagamentoUrl = 'http://localhost:8080/api/v1/formas-pagamento';
  private produtoUrl = 'http://localhost:8080/api/v1/produtos';

  constructor(private http: HttpClient) {}

  // 🔹 CRUD de Vendas
  listarTodas(): Observable<Venda[]> {
    return this.http.get<Venda[]>(`${this.apiUrl}/todas`);
  }

  listarPaginado(page: number): Observable<PageResponse<Venda>> {
    return this.http.get<PageResponse<Venda>>(`${this.apiUrl}?page=${page}`);
  }

  buscarPorId(id: number): Observable<Venda> {
    return this.http.get<Venda>(`${this.apiUrl}/${id}`);
  }

  cadastrarVenda(venda: Venda): Observable<Venda> {
    return this.http.post<Venda>(this.apiUrl, venda);
  }

  atualizarVenda(id: number, venda: Venda): Observable<Venda> {
    return this.http.put<Venda>(`${this.apiUrl}/${id}`, venda);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Buscas com paginação
  buscarPorCpf(cpf: string, page: number): Observable<PageResponse<Venda>> {
    return this.http.get<PageResponse<Venda>>(`${this.apiUrl}/buscar/cpf?cpf=${cpf}&page=${page}`);
  }

  buscarPorNome(nome: string, page: number): Observable<PageResponse<Venda>> {
    return this.http.get<PageResponse<Venda>>(`${this.apiUrl}/buscar/nome?nome=${nome}&page=${page}`);
  }

  buscarPorPeriodo(inicio: string, fim: string, page: number): Observable<PageResponse<Venda>> {
    return this.http.get<PageResponse<Venda>>(
      `${this.apiUrl}/buscar/periodo?inicio=${inicio}&fim=${fim}&page=${page}`
    );
  }

  buscarComFiltros(tipoPagamento?: string, status?: string, page: number = 0): Observable<PageResponse<Venda>> {
    let url = `${this.apiUrl}/filtro?page=${page}`;
    if (tipoPagamento) {
      url += `&tipoPagamento=${tipoPagamento}`;
    }
    if (status) {
      url += `&status=${status}`;
    }
    return this.http.get<PageResponse<Venda>>(url);
  }

  // 🔹 Auxiliares
  listarClientes(): Observable<any[]> {
    return this.http.get<any[]>(this.clienteUrl);
  }

  listarProdutos(): Observable<any[]> {
    return this.http.get<any[]>(this.produtoUrl);
  }

  criarFormaPagamento(
  vendaId: number,
  dto: { tipo: string; numeroParcelas: number; valorTotal: number }
): Observable<any> {
  return this.http.post<any>(`${this.formaPagamentoUrl}/venda/${vendaId}`, dto);
}
}
