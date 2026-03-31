import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { CompraService, Compra } from './compra.service';

@Component({
  selector: 'app-compras-lista',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, RouterModule],
  templateUrl: './compra-lista.component.html',
  styleUrls: ['./compra-lista.component.scss']
})
export class ComprasListaComponent implements OnInit {
  displayedColumns: string[] = ['dataCompra','fornecedor','valorTotal','acoes'];
  dataSource = new MatTableDataSource<Compra>();

  constructor(private compraService: CompraService) {}

  ngOnInit(): void {
    this.compraService.listarTodas().subscribe({
      next: (data) => {
        this.dataSource.data = data ?? [];
      },
      error: () => alert('Erro ao carregar compras')
    });
  }

  deletar(id: number) {
    if (confirm('Deseja realmente excluir esta compra?')) {
      this.compraService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(c => c.id !== id);
        },
        error: () => alert('Erro ao excluir compra')
      });
    }
  }
}
