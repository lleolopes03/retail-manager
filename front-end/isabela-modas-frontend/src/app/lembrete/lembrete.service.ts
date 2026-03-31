import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Lembrete {
  id: number;
  titulo: string;
  descricao: string;
  dataHora: Date;
  tipo: 'manual' | 'vencimento';
  concluido: boolean;
}

@Injectable({ providedIn: 'root' })
export class LembreteService {
  private apiUrl = '/api/v1/formas-pagamento';

  constructor(private http: HttpClient) {}

  listarFormasPagamento(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  gerarLembretesDeVencimento(formas: any[]): Lembrete[] {
    const hoje = new Date();
    const lembretes: Lembrete[] = [];

    formas.forEach(fp => {
      if (fp.numeroParcelas && fp.primeiraParcela && fp.intervaloDias) {
        for (let i = 0; i < fp.numeroParcelas; i++) {
          const vencimento = new Date(fp.primeiraParcela);
          vencimento.setDate(vencimento.getDate() + i * fp.intervaloDias);

          const diffDias = Math.ceil((vencimento.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24));

          if (diffDias === 2) {
            lembretes.push({
              id: Date.now() + i,
              titulo: 'Parcela a vencer',
              descricao: `Parcela da venda #${fp.id} vence em ${vencimento.toLocaleDateString('pt-BR')}`,
              dataHora: vencimento,
              tipo: 'vencimento',
              concluido: false
            });
          }
        }
      }
    });

    return lembretes;
  }
}
