import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Categoria {
  id: number;
  nome: string;
  descricao: string;
}
export interface ProdutoRequest {
  nome: string;
  preco: number;
  tamanho: string;
  cor: string;
  estoqueAtual: number;
  categoriaId: number;
}

export interface Produto {
  id?: number;
  nome: string;
  preco: number;
  tamanho: string;
  cor: string;
  estoqueAtual: number;
  categoria: Categoria; // ✅ não é number, é objeto
}
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

@Injectable({ providedIn: 'root' })
export class ProdutoService {
  private apiUrl = 'http://localhost:8080/api/v1/produtos';
  private categoriaUrl = 'http://localhost:8080/api/v1/categorias';

  constructor(private http: HttpClient) {}

  listarPaginado(page: number) {
  return this.http.get<PageResponse<Produto>>(`${this.apiUrl}/paginado?page=${page}`);
}

buscarPorNomeOuCor(termo: string, page: number) {
  return this.http.get<PageResponse<Produto>>(`${this.apiUrl}/buscar?termo=${termo}&page=${page}`);
}

  buscarPorId(id: number): Observable<Produto> {
    return this.http.get<Produto>(`${this.apiUrl}/${id}`);
  }

  cadastrarProduto(produto: ProdutoRequest): Observable<Produto> {
    return this.http.post<Produto>(this.apiUrl, produto);
  }

  atualizarProduto(id: number, produto: ProdutoRequest): Observable<Produto> {
    return this.http.put<Produto>(`${this.apiUrl}/${id}`, produto);
  }

  deletarProduto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.categoriaUrl);
  }
}

