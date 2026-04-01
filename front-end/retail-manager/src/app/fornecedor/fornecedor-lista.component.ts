import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { FornecedorService, Fornecedor } from './fornecedor.service';

@Component({
  selector: 'app-fornecedores-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTooltipModule, RouterModule],
  templateUrl: './fornecedor-lista.component.html',
  styleUrls: ['./fornecedor-lista.component.scss']
})
export class FornecedoresListaComponent implements OnInit {
  displayedColumns: string[] = ['icone','nome','cpf','telefone','email','acoes'];
  dataSource = new MatTableDataSource<Fornecedor>();
  termo: string = '';
  page = 0;
  totalPages = 0;

  constructor(private fornecedorService: FornecedorService) {}

  ngOnInit(): void {
    this.carregarFornecedores();
  }

  carregarFornecedores() {
    this.fornecedorService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = data ?? [];
        this.totalPages = Math.ceil((data?.length ?? 0) / 8);
      },
      error: () => alert('Erro ao carregar fornecedores')
    });
  }

  buscar() {
    if (!this.termo.trim()) {
      this.carregarFornecedores();
      return;
    }

    const termoLower = this.termo.toLowerCase();
    this.fornecedorService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = (data ?? []).filter(f => 
          f.nome?.toLowerCase().includes(termoLower) ||
          f.cnpj?.toLowerCase().includes(termoLower) ||
          f.telefone?.toLowerCase().includes(termoLower)
        );
        this.totalPages = Math.ceil(this.dataSource.data.length / 8);
      },
      error: () => alert('Erro ao buscar fornecedores')
    });
  }

  limparBusca() {
    this.termo = '';
    this.carregarFornecedores();
  }

  proximaPagina() {
    if (this.page < this.totalPages - 1) {
      this.page++;
    }
  }

  paginaAnterior() {
    if (this.page > 0) {
      this.page--;
    }
  }

  deletar(id: number) {
    if (confirm('Deseja realmente excluir este fornecedor?')) {
      this.fornecedorService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(f => f.id !== id);
        },
        error: () => alert('Erro ao excluir fornecedor')
      });
    }
  }
}
