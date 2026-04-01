import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 🔹 Interface de resposta do backend
export interface PagamentoResponseDto {
  linkPagamento?: string;
  mensagem?: string;
  statusPagamento?: string;
  tipoPagamento?: string;
}

@Injectable({ providedIn: 'root' })
export class PagamentoService {
  constructor(private http: HttpClient) {}

  criarPagamento(dto: any): Observable<PagamentoResponseDto> {
    return this.http.post<PagamentoResponseDto>('http://localhost:8080/api/pagamentos/criar', dto);
  }

  consultarStatus(id: number): Observable<{ statusPagamento: string }> {
    return this.http.get<{ statusPagamento: string }>(
      `http://localhost:8080/api/pagamentos/status/${id}`
    );
  }

  buscarVenda(id: number): Observable<any> {
    return this.http.get<any>(`http://localhost:8080/api/v1/vendas/${id}`);
  }
}
