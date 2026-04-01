import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FormaPagamentoResponseDto {
  id: number;
  tipo: string;
  numeroParcelas: number;   // 🔹 obrigatório
  primeiraParcela?: string;
  intervaloDias?: number;
  linkPagamento?: string;
  valorTotal: number;       // 🔹 obrigatório
}

export interface ParcelaDto {
  id: number;
  numero: number;
  valor: number;
  dataVencimento: string;
  status: string;
}

export interface RelatorioParcelasAtrasadasDto {
  nomeCliente: string;
  cpfCliente: string;
  numeroParcela: number;
  valor: number;
  dataVencimento: string;
  diasAtraso: number;
}

@Injectable({ providedIn: 'root' })
export class FormaPagamentoService {
  private apiUrl = '/api/v1/formas-pagamento';

  constructor(private http: HttpClient) {}

  criarFormaPagamento(vendaId: number, dto: any): Observable<FormaPagamentoResponseDto> {
    return this.http.post<FormaPagamentoResponseDto>(`${this.apiUrl}/venda/${vendaId}`, dto);
  }

  buscarPorId(id: number): Observable<FormaPagamentoResponseDto> {
    return this.http.get<FormaPagamentoResponseDto>(`${this.apiUrl}/${id}`);
  }

  buscarParcelasPorVenda(vendaId: number): Observable<ParcelaDto[]> {
    return this.http.get<ParcelaDto[]>(`/api/v1/parcelas/venda/${vendaId}`);
  }

  gerarLinkParcela(parcelaId: number): Observable<any> {
    return this.http.post<any>(`/api/v1/parcelas/${parcelaId}/link-pagamento`, {});
  }

  buscarParcelasAtrasadas(): Observable<RelatorioParcelasAtrasadasDto[]> {
    return this.http.get<RelatorioParcelasAtrasadasDto[]>(`/api/v1/parcelas/relatorio/atrasadas`);
  }

  enviarCobrancaWhatsApp(parcelaId: number, numeroCliente: string): Observable<void> {
    return this.http.post<void>(`/api/v1/parcelas/${parcelaId}/cobranca-whatsapp?numeroCliente=${numeroCliente}`, {});
  }

  darBaixaParcela(parcelaId: number): Observable<void> {
    return this.http.put<void>(`/api/v1/parcelas/${parcelaId}/dar-baixa`, {});
  }
}
