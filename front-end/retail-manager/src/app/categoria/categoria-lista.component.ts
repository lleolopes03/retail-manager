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
import { CategoriaService, Categoria } from './categoria.service';

@Component({
  selector: 'app-categorias-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTooltipModule, RouterModule],
  templateUrl: './categoria-lista.component.html',
  styleUrls: ['./categoria-lista.component.scss']
})
export class CategoriasListaComponent implements OnInit {
  displayedColumns: string[] = ['icone', 'nome', 'acoes'];
  dataSource = new MatTableDataSource<Categoria>();
  termo: string = '';
  page = 0;
  totalPages = 0;

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.carregarcategorias();
  }

  carregarcategorias() {
    this.categoriaService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = data ?? [];
        this.totalPages = Math.ceil((data?.length ?? 0) / 8);
      },
      error: () => alert('Erro ao carregar categorias')
    });
  }

  buscar() {
    if (!this.termo.trim()) {
      this.carregarcategorias();
      return;
    }

    const termoLower = this.termo.toLowerCase();
    this.categoriaService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = (data ?? []).filter(c => 
          c.nome?.toLowerCase().includes(termoLower)
        );
        this.totalPages = Math.ceil(this.dataSource.data.length / 8);
      },
      error: () => alert('Erro ao buscar categorias')
    });
  }

  limparBusca() {
    this.termo = '';
    this.carregarcategorias();
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
    if (confirm('Deseja realmente excluir esta categoria?')) {
      this.categoriaService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(c => c.id !== id);
        },
        error: () => alert('Erro ao excluir categoria')
      });
    }
  }
}
