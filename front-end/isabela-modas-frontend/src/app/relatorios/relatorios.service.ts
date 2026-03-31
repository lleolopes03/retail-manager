import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RelatoriosService {
  private apiUrl = 'http://localhost:8080/api/v1/vendas';

  constructor(private http: HttpClient) {}

  // 🔹 Função auxiliar para gerar datas padrão
  private getDefaultPeriodo(): { inicio: string; fim: string } {
    const hoje = new Date();
    const primeiroDiaMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);

    const inicio = primeiroDiaMes.toISOString().split('T')[0]; // yyyy-MM-dd
    const fim = hoje.toISOString().split('T')[0];

    return { inicio, fim };
  }

  relatorioDiario(data?: string): Observable<number> {
    if (!data) {
      data = new Date().toISOString().split('T')[0]; // hoje
    }
    return this.http.get<number>(`${this.apiUrl}/relatorio/diario?data=${data}`);
  }

  relatorioMensal(inicio?: string, fim?: string): Observable<number> {
    if (!inicio || !fim) {
      const periodo = this.getDefaultPeriodo();
      inicio = periodo.inicio;
      fim = periodo.fim;
    }
    return this.http.get<number>(`${this.apiUrl}/relatorio/mensal?inicio=${inicio}&fim=${fim}`);
  }

  relatorioPorFormaPagamento(inicio?: string, fim?: string): Observable<any[]> {
    if (!inicio || !fim) {
      const periodo = this.getDefaultPeriodo();
      inicio = periodo.inicio;
      fim = periodo.fim;
    }
    const url = `${this.apiUrl}/forma-pagamento?inicio=${inicio}&fim=${fim}`;
    return this.http.get<any[]>(url);
  }

  relatorioPorCliente(inicio?: string, fim?: string): Observable<any[]> {
    if (!inicio || !fim) {
      const periodo = this.getDefaultPeriodo();
      inicio = periodo.inicio;
      fim = periodo.fim;
    }
    const url = `${this.apiUrl}/clientes?inicio=${inicio}&fim=${fim}`;
    return this.http.get<any[]>(url);
  }

  relatorioValoresAReceber(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorio/valores-a-receber`);
  }

  relatorioParcelasAReceber(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/v1/parcelas/relatorio/pendentes');
  }

  relatorioInadimplencia(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorio/inadimplencia`);
  }

  // 🔹 Relatório de parcelas atrasadas (carnê)
  relatorioParcelasAtrasadas(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/v1/parcelas/relatorio/atrasadas');
  }

  // 🔹 Relatório unificado de inadimplência (vendas + parcelas)
  relatorioInadimplenciaGeral(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/v1/parcelas/relatorio/inadimplencia-geral');
  }
}
