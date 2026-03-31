import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProdutoService, Produto } from './produto.service';

@Component({
  selector: 'app-produtos-lista',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatChipsModule,
    RouterModule
  ],
  templateUrl: './produtos-lista.component.html',
  styleUrls: ['./produtos-lista.component.scss']
})
export class ProdutosListaComponent implements OnInit {
  displayedColumns: string[] = ['icone', 'nome', 'variacoes', 'preco', 'estoqueAtual', 'acoes'];
  dataSource = new MatTableDataSource<Produto>();
  page = 0;
  totalPages = 0;
  filtroAtivo: 'TODOS' | 'BUSCA' = 'TODOS';
  termo: string = '';
  filtroEstoque: 'todos' | 'baixo' | 'zerado' = 'todos';

  private debounceTimer: any;

  constructor(private produtoService: ProdutoService) {}

  ngOnInit(): void {
    this.carregarProdutos();
  }

  carregarProdutos() {
    this.produtoService.listarPaginado(this.page).subscribe({
      next: (data) => {
        this.dataSource.data = data.content ?? [];
        this.totalPages = data.totalPages;
      },
      error: () => alert('Erro ao carregar produtos')
    });
  }

  buscar() {
    clearTimeout(this.debounceTimer);
    this.debounceTimer = setTimeout(() => {
      this.page = 0;
      this.filtroAtivo = 'BUSCA';
      this.produtoService.buscarPorNomeOuCor(this.termo, this.page).subscribe({
        next: (data) => {
          this.dataSource.data = data.content ?? [];
          this.totalPages = data.totalPages;
        },
        error: () => alert('Erro ao buscar produtos')
      });
    }, 300); // ✅ espera 300ms após digitar
  }

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
    if (this.filtroAtivo === 'BUSCA') {
      this.produtoService.buscarPorNomeOuCor(this.termo, this.page).subscribe({
        next: (data) => {
          this.dataSource.data = data.content ?? [];
          this.totalPages = data.totalPages;
        }
      });
    } else {
      this.carregarProdutos();
    }
  }

  deletar(id: number) {
    if (confirm('Deseja realmente excluir este produto?')) {
      this.produtoService.deletarProduto(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(p => p.id !== id);
          alert('✅ Produto excluído com sucesso!');
        },
        error: (err) => {
          console.error('Erro ao excluir produto:', err);
          const mensagemErro = err.error?.message || err.message || 'Erro desconhecido';
          alert(`❌ Erro ao excluir produto:\n\n${mensagemErro}\n\nPossíveis causas:\n- Produto está vinculado a vendas\n- Produto está vinculado a movimentações de estoque\n- Backend não está rodando`);
        }
      });
    }
  }

  limparBusca() {
    this.termo = '';
    this.filtroAtivo = 'TODOS';
    this.page = 0;
    this.carregarProdutos();
  }

  filtrarPorEstoque(filtro: 'todos' | 'baixo' | 'zerado') {
    this.filtroEstoque = filtro;
    this.page = 0;
    
    if (filtro === 'todos') {
      this.carregarProdutos();
    } else {
      this.produtoService.listarPaginado(this.page).subscribe({
        next: (data) => {
          let filtered = data.content ?? [];
          
          if (filtro === 'baixo') {
            filtered = filtered.filter(p => p.estoqueAtual > 0 && p.estoqueAtual <= 5);
          } else if (filtro === 'zerado') {
            filtered = filtered.filter(p => p.estoqueAtual === 0);
          }
          
          this.dataSource.data = filtered;
          this.totalPages = Math.ceil(filtered.length / 8);
        },
        error: () => alert('Erro ao filtrar produtos')
      });
    }
  }
}
