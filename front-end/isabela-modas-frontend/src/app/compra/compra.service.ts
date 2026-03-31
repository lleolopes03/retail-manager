import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ItemCompra {
  produtoId: number;
  quantidade: number;
  precoUnitario: number;
}

export interface Compra {
  id?: number;
  dataCompra?: string;
  fornecedorId: number;
  itens: ItemCompra[];
  valorTotal?: number;
}

@Injectable({ providedIn: 'root' })
export class CompraService {
  private apiUrl = 'http://localhost:8080/api/v1/compras';
  private fornecedorUrl = 'http://localhost:8080/api/v1/fornecedores';
  private produtoUrl = 'http://localhost:8080/api/v1/produtos';

  constructor(private http: HttpClient) {}

  // 🔹 CRUD de Compras
  listarTodas(): Observable<Compra[]> {
    return this.http.get<Compra[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.apiUrl}/${id}`);
  }

  cadastrarCompra(compra: Compra): Observable<Compra> {
    return this.http.post<Compra>(this.apiUrl, compra);
  }

  atualizarCompra(id: number, compra: Compra): Observable<Compra> {
    return this.http.put<Compra>(`${this.apiUrl}/${id}`, compra);
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // 🔹 Auxiliares: fornecedores e produtos
  listarFornecedores(): Observable<any[]> {
    return this.http.get<any[]>(this.fornecedorUrl);
  }

  listarProdutos(): Observable<any[]> {
    return this.http.get<any[]>(this.produtoUrl);
  }
}
