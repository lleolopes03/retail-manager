import { Component, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { VendaService } from './venda.service';

@Component({
  selector: 'app-vendas-lista',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
    RouterModule
  ],
  templateUrl: './vendas-lista.component.html',
  styleUrls: ['./vendas-lista.component.scss']
})
export class VendasListaComponent implements AfterViewInit {
  displayedColumns: string[] = [
    'id',
    'cliente',
    'dataVenda',
    'valorTotal',
    'tipoPagamento',
    'statusPagamento',
    'acoes'
  ];
  dataSource = new MatTableDataSource<any>();
  page = 0;
  totalPages = 0;
  filtros = {
    nomeCliente: '',
    status: '',
    tipoPagamento: ''
  };

  // filtros de busca específicos
  cpf: string = '';
  nome: string = '';
  inicio: string = '';
  fim: string = '';

  // 🔹 controla qual filtro está ativo
  filtroAtivo: 'TODOS' | 'CPF' | 'NOME' | 'PERIODO' | 'FILTROS' = 'TODOS';

  constructor(private vendaService: VendaService, private cdRef: ChangeDetectorRef) {}

  ngAfterViewInit(): void {
    this.carregarVendas();
  }

  carregarVendas() {
    this.vendaService.listarPaginado(this.page).subscribe({
      next: (data) => {
        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
        this.cdRef.detectChanges();
      },
      error: () => alert('Erro ao carregar vendas')
    });
  }

  buscarPorCpf() {
    this.page = 0;
    this.filtroAtivo = 'CPF';
    this.vendaService.buscarPorCpf(this.cpf, this.page).subscribe({
      next: (data) => {
        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
        this.cdRef.detectChanges();
      },
      error: () => alert('Erro ao buscar por CPF')
    });
  }

  buscarPorNome() {
    this.page = 0;
    this.filtroAtivo = 'NOME';
    this.vendaService.buscarPorNome(this.nome, this.page).subscribe({
      next: (data) => {
        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
        this.cdRef.detectChanges();
      },
      error: () => alert('Erro ao buscar por Nome')
    });
  }

  buscarPorPeriodo() {
    this.page = 0;
    this.filtroAtivo = 'PERIODO';
    const inicioFormatado = this.inicio || '';
    const fimFormatado = this.fim || '';

    this.vendaService.buscarPorPeriodo(inicioFormatado, fimFormatado, this.page).subscribe({
      next: (data) => {
        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
        this.cdRef.detectChanges();
      },
      error: () => alert('Erro ao buscar por período')
    });
  }

  buscarComFiltros() {
    this.page = 0;
    this.filtroAtivo = 'FILTROS';
    const tipo = this.filtros.tipoPagamento || undefined;
    const status = this.filtros.status || undefined;



    this.vendaService.buscarComFiltros(tipo, status, this.page).subscribe({
      next: (data) => {

        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
        this.cdRef.detectChanges();
      },
      error: (err) => {
        console.error('❌ Erro na busca:', err);
        alert('Erro ao buscar com filtros');
      }
    });
  }

  limparFiltros() {
    this.cpf = '';
    this.nome = '';
    this.inicio = '';
    this.fim = '';
    this.filtros.nomeCliente = '';
    this.filtros.status = '';
    this.filtros.tipoPagamento = '';
    this.page = 0;
    this.filtroAtivo = 'TODOS';
    this.carregarVendas();
  }

  aplicarFiltros() {
    this.buscarComFiltros();
  }

  excluir(id: number) {
    this.deletar(id);
  }

  // 🔹 paginação respeitando filtro ativo
  proximaPagina() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.carregarPaginaAtual();
    }
  }

  paginaAnterior() {
    if (this.page > 0) {
      this.page--;
      this.carregarPaginaAtual();
    }
  }

  carregarPaginaAtual() {
    switch (this.filtroAtivo) {
      case 'CPF':
        this.vendaService.buscarPorCpf(this.cpf, this.page).subscribe({
          next: (data) => {
            this.dataSource.data = data.content ?? [];
            this.totalPages = data.totalPages;
            this.cdRef.detectChanges();
          }
        });
        break;
      case 'NOME':
        this.vendaService.buscarPorNome(this.nome, this.page).subscribe({
          next: (data) => {
            this.dataSource.data = data.content ?? [];
            this.totalPages = data.totalPages;
            this.cdRef.detectChanges();
          }
        });
        break;
      case 'PERIODO':
        const inicioFormatado = this.inicio || '';
        const fimFormatado = this.fim || '';
        this.vendaService.buscarPorPeriodo(inicioFormatado, fimFormatado, this.page).subscribe({
          next: (data) => {
            this.dataSource.data = data.content ?? [];
            this.totalPages = data.totalPages;
            this.cdRef.detectChanges();
          }
        });
        break;
      case 'FILTROS':
        const tipo = this.filtros.tipoPagamento || undefined;
        const status = this.filtros.status || undefined;
        this.vendaService.buscarComFiltros(tipo, status, this.page).subscribe({
          next: (data) => {
            this.dataSource.data = data.content ?? [];
            this.totalPages = data.totalPages;
            this.cdRef.detectChanges();
          }
        });
        break;
      default:
        this.carregarVendas();
    }
  }

  deletar(id: number) {
    if (confirm('Deseja realmente excluir esta venda?')) {
      this.vendaService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(v => v.id !== id);
          this.cdRef.detectChanges();
        },
        error: () => alert('Erro ao excluir venda')
      });
    }
  }
}
