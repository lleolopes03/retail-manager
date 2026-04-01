import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { CommonModule } from '@angular/common';
import { MovimentacaoEstoqueService } from '../movimentacao-estoque/movimentacao-estoque.service';

@Component({
  selector: 'app-relatorio-estoque',
  standalone: true,
  imports: [MatCardModule, MatTableModule, CommonModule],
  templateUrl: './relatorio-estoque.component.html',
  styleUrls: ['./relatorio-estoque.component.scss']
})
export class RelatorioEstoqueComponent {
  estoqueAtual = new MatTableDataSource<any>([]);
  displayedColumns: string[] = ['nome', 'tamanho', 'quantidadeAtual'];

  constructor(private movimentacaoService: MovimentacaoEstoqueService) {
    this.movimentacaoService.relatorioEstoqueAtual().subscribe({
      next: dados => this.estoqueAtual.data = dados ?? [],
      error: () => alert('Erro ao carregar estoque atual')
    });
  }
}
