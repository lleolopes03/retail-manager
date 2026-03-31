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
import { ClienteService, Cliente } from './cliente.service';

@Component({
  selector: 'app-clientes-lista',
  standalone: true,
  imports: [CommonModule, FormsModule, MatTableModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule, MatTooltipModule, RouterModule],
  templateUrl: './clientes-lista.component.html',
  styleUrls: ['./clientes-lista.component.scss']
})
export class ClientesListaComponent implements OnInit {
  displayedColumns: string[] = ['icone','nome','cpf','telefone','email','acoes'];
  dataSource = new MatTableDataSource<Cliente>();
  termo: string = '';
  page = 0;
  totalPages = 0;

  constructor(private clienteService: ClienteService) {}

  ngOnInit(): void {
    this.carregarClientes();
  }

  carregarClientes() {
    this.clienteService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = data ?? [];
        this.totalPages = Math.ceil((data?.length ?? 0) / 8);
      },
      error: () => alert('Erro ao carregar clientes')
    });
  }

  buscar() {
    if (!this.termo.trim()) {
      this.carregarClientes();
      return;
    }

    const termoLower = this.termo.toLowerCase();
    this.clienteService.listarTodos().subscribe({
      next: (data) => {
        this.dataSource.data = (data ?? []).filter(c => 
          c.nome?.toLowerCase().includes(termoLower) ||
          c.cpf?.toLowerCase().includes(termoLower) ||
          c.telefone?.toLowerCase().includes(termoLower)
        );
        this.totalPages = Math.ceil(this.dataSource.data.length / 8);
      },
      error: () => alert('Erro ao buscar clientes')
    });
  }

  limparBusca() {
    this.termo = '';
    this.carregarClientes();
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
    if (confirm('Deseja realmente excluir este cliente?')) {
      this.clienteService.deletar(id).subscribe({
        next: () => {
          this.dataSource.data = this.dataSource.data.filter(c => c.id !== id);
        },
        error: () => alert('Erro ao excluir cliente')
      });
    }
  }

  verCompras(clienteId: number, nomeCliente: string) {
    alert(`🚧 Funcionalidade em desenvolvimento!\n\nEm breve você poderá visualizar o histórico de compras de ${nomeCliente}.\n\nPor enquanto, acesse:\nOperações → Vendas → Filtrar por cliente`);
    // TODO: Implementar tela de histórico de compras do cliente
    // this.router.navigate(['/vendas'], { queryParams: { clienteId: clienteId } });
  }
}
