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
import { FuncionarioService, Funcionario } from './funcionario.service';

@Component({
  selector: 'app-funcionarios-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTooltipModule, RouterModule],
  templateUrl: './funcionarios-lista.component.html',
  styleUrls: ['./funcionarios-lista.component.scss']
})
export class FuncionariosListaComponent implements OnInit {
  displayedColumns: string[] = ['icone','nome','cpf','telefone','email','acoes'];
  dataSource = new MatTableDataSource<Funcionario>();
  termo: string = '';
  page = 0;
  totalPages = 0;

  constructor(private funcionarioService: FuncionarioService) {}

  ngOnInit(): void {
    this.carregarFuncionarios();
  }

  carregarFuncionarios() {
    this.funcionarioService.listarTodos().subscribe({
      next: (data: Funcionario[]) => {
        this.dataSource.data = data ?? [];
        this.totalPages = Math.ceil((data?.length ?? 0) / 8);
      },
      error: () => alert('Erro ao carregar funcionários')
    });
  }

  buscar() {
    if (!this.termo.trim()) {
      this.carregarFuncionarios();
      return;
    }

    const termoLower = this.termo.toLowerCase();
    this.funcionarioService.listarTodos().subscribe({
      next: (data: Funcionario[]) => {
        this.dataSource.data = (data ?? []).filter((f: Funcionario) => 
          f.nome?.toLowerCase().includes(termoLower) ||
          f.cpf?.toLowerCase().includes(termoLower) ||
          f.telefone?.toLowerCase().includes(termoLower)
        );
        this.totalPages = Math.ceil(this.dataSource.data.length / 8);
      },
      error: () => alert('Erro ao buscar funcionários')
    });
  }

  limparBusca() {
    this.termo = '';
    this.carregarFuncionarios();
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
    if (confirm('Deseja realmente excluir este funcionário?')) {
      this.funcionarioService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(f => f.id !== id);
        },
        error: () => alert('Erro ao excluir funcionário')
      });
    }
  }
}
