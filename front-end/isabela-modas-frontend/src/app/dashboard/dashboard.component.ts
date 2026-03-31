import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { filter, takeUntil } from 'rxjs/operators';
import { forkJoin, Subject } from 'rxjs';
import { CommonModule, CurrencyPipe, NgIf, NgFor } from '@angular/common';

import { RelatoriosService } from '../relatorios/relatorios.service';
import { LembreteService } from '../lembrete/lembrete.service';
import { MovimentacaoEstoqueService } from '../movimentacao-estoque/movimentacao-estoque.service';
import { AuthService } from '../auth/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatBadgeModule } from '@angular/material/badge';



@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatCardModule,
    MatListModule,
    MatSnackBarModule,
    MatBadgeModule,
    CurrencyPipe,
    NgIf,
    NgFor,
    CommonModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {
  totalDiario: number = 0;
  totalMensal: number = 0;
  topCliente: any = null;
  formaPagamentoMaisUsada: any = null;

  estoqueAtual: any[] = [];
  estoqueBaixo: any[] = [];
  valoresAReceber: any[] = [];
  totalValoresAReceber: number = 0;

  // Aliases para o novo template
  totalVendasHoje: number = 0;
  qtdVendasHoje: number = 0;
  totalVendasMes: number = 0;
  produtosEmEstoque: number = 0;
  produtosBaixoEstoque: any[] = [];

  lembretesAtivos: number = 0;
  lembretesProximos: any[] = [];

  private destroy$ = new Subject<void>();
  private isLoading = false;
  carregandoDados = true;

  constructor(
    private router: Router,
    private relatoriosService: RelatoriosService,
    private movimentacaoService: MovimentacaoEstoqueService,
    private lembreteService: LembreteService,
    private snackBar: MatSnackBar,
    public authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    setTimeout(() => {
      this.carregarDados();
    }, 0);

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe((event: NavigationEnd) => {
      if (event.urlAfterRedirects === '/dashboard') {
        setTimeout(() => {
          this.carregarDados();
        }, 0);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private carregarDados(): void {
    if (this.isLoading) {

      return;
    }

    if (!this.authService.isTokenValid()) {

      return;
    }

    this.isLoading = true;
    this.carregandoDados = true;

    
    const hoje = new Date().toISOString().split('T')[0];
    const agora = new Date();
    const primeiroDia = new Date(agora.getFullYear(), agora.getMonth(), 1).toISOString().split('T')[0];
    const ultimoDia = new Date(agora.getFullYear(), agora.getMonth() + 1, 0).toISOString().split('T')[0];

    forkJoin({
      diario: this.relatoriosService.relatorioDiario(hoje),
      mensal: this.relatoriosService.relatorioMensal(primeiroDia, ultimoDia),
      clientes: this.relatoriosService.relatorioPorCliente(),
      formas: this.relatoriosService.relatorioPorFormaPagamento(),
      estoque: this.movimentacaoService.relatorioEstoqueAtual(),
      parcelas: this.relatoriosService.relatorioParcelasAReceber(),
      formasLembrete: this.lembreteService.listarFormasPagamento()
    }).pipe(
      takeUntil(this.destroy$)
    ).subscribe({
      next: resultados => {
        this.totalDiario = resultados.diario ?? 0;
        this.totalMensal = resultados.mensal ?? 0;
        
        // Aliases para compatibilidade com o template
        this.totalVendasHoje = this.totalDiario;
        this.totalVendasMes = this.totalMensal;
        this.qtdVendasHoje = 0; // TODO: Implementar contagem de vendas
        this.produtosEmEstoque = 0; // Será calculado abaixo

        if (resultados.clientes?.length > 0) {
          this.topCliente = resultados.clientes.sort((a, b) => b.totalComprado - a.totalComprado)[0];
        } else {
          this.topCliente = null;
        }

        if (resultados.formas?.length > 0) {
          this.formaPagamentoMaisUsada = resultados.formas.sort((a, b) => b.total - a.total)[0];
        } else {
          this.formaPagamentoMaisUsada = null;
        }

        this.estoqueAtual = resultados.estoque ?? [];
        this.estoqueBaixo = this.estoqueAtual.filter(p => p.quantidadeAtual < 5);
        
        // Aliases para template
        this.produtosBaixoEstoque = this.estoqueBaixo;
        this.produtosEmEstoque = this.estoqueAtual.reduce((sum, p) => sum + (p.quantidadeAtual || 0), 0);

        this.valoresAReceber = resultados.parcelas ?? [];
        this.totalValoresAReceber = this.valoresAReceber.reduce((sum, p) => sum + (p.valor || 0), 0);

        const lembretes = this.lembreteService.gerarLembretesDeVencimento(resultados.formasLembrete ?? []);
        this.lembretesAtivos = lembretes.filter(l => !l.concluido).length;
        this.lembretesProximos = lembretes.filter(l => !l.concluido);

        if (this.lembretesProximos.length > 0) {
          this.snackBar.open(
            `⚠️ Você tem ${this.lembretesProximos.length} lembrete(s) próximo(s) do vencimento`,
            'Ver',
            { duration: 5000 }
          );
        }

        this.isLoading = false;
        this.carregandoDados = false;
        this.cdr.detectChanges();

      },
      error: err => {
        console.error('Erro ao carregar dados do dashboard:', err);
        this.snackBar.open('Erro ao carregar dados do dashboard', 'Fechar', { duration: 4000 });
        this.isLoading = false;
        this.carregandoDados = false;
        this.cdr.detectChanges();
      }
    });
  }

  logout() {
    this.authService.logoutWithMessage();
    window.location.href = '/login';
  }
}
