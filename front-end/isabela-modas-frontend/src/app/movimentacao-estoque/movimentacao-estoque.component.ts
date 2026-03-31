import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatTooltipModule } from '@angular/material/tooltip';

import { MovimentacaoEstoqueService, MovimentacaoEstoque, ItemMovimentacao } from './movimentacao-estoque.service';

@Component({
  selector: 'app-movimentacao-estoque',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
    MatAutocompleteModule,
    MatTooltipModule
  ],
  templateUrl: './movimentacao-estoque.component.html',
  styleUrls: ['./movimentacao-estoque.component.scss']
})
export class MovimentacaoEstoqueComponent implements OnInit {
  movimentacaoForm!: FormGroup;
  
  // Autocompletes
  clienteControl = new FormControl('');
  produtoControl = new FormControl('');
  quantidadeControl = new FormControl(1, [Validators.required, Validators.min(1)]);
  
  // Dados
  clientes: any[] = [];
  produtos: any[] = [];
  clientesFiltrados: Observable<any[]>;
  produtosFiltrados: Observable<any[]>;
  
  // Cliente e Produto selecionados
  clienteSelecionado: any = null;
  produtoSelecionado: any = null;
  
  // Lista de produtos adicionados
  produtosAdicionados: ItemMovimentacao[] = [];

  constructor(
    private fb: FormBuilder,
    private movimentacaoService: MovimentacaoEstoqueService,
    private router: Router
  ) {
    this.movimentacaoForm = this.fb.group({
      clienteId: ['', Validators.required],
      tipoMovimentacao: ['SAIDA_TEMPORARIA', Validators.required],
      observacao: ['']
    });

    // Inicializar autocomplete de clientes
    this.clientesFiltrados = this.clienteControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filtrarClientes(value || ''))
    );

    // Inicializar autocomplete de produtos
    this.produtosFiltrados = this.produtoControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filtrarProdutos(value || ''))
    );
  }

  ngOnInit(): void {
    this.carregarClientes();
    this.carregarProdutos();
  }

  carregarClientes(): void {
    this.movimentacaoService.listarClientes().subscribe({
      next: (data) => {
        this.clientes = data ?? [];

      },
      error: () => console.error('Erro ao carregar clientes')
    });
  }

  carregarProdutos(): void {
    this.movimentacaoService.listarProdutos().subscribe({
      next: (data) => {
        this.produtos = data ?? [];

      },
      error: () => console.error('Erro ao carregar produtos')
    });
  }

  private _filtrarClientes(value: string | any): any[] {
    if (!value) return this.clientes;
    
    const filterValue = typeof value === 'string' 
      ? value.toLowerCase() 
      : (value.nome || '').toLowerCase();
    
    return this.clientes.filter(c => 
      c.nome?.toLowerCase().includes(filterValue) ||
      c.cpf?.includes(filterValue)
    );
  }

  private _filtrarProdutos(value: string | any): any[] {
    if (!value) return this.produtos;
    
    const filterValue = typeof value === 'string' 
      ? value.toLowerCase() 
      : (value.nome || '').toLowerCase();
    
    return this.produtos.filter(p => 
      p.nome?.toLowerCase().includes(filterValue) ||
      p.cor?.toLowerCase().includes(filterValue) ||
      p.tamanho?.toLowerCase().includes(filterValue)
    );
  }

  selecionarCliente(cliente: any): void {
    this.clienteSelecionado = cliente;
    this.movimentacaoForm.patchValue({ clienteId: cliente.id });

  }

  exibirCliente(cliente: any): string {
    if (!cliente) return '';
    return `${cliente.nome} - CPF: ${cliente.cpf}`;
  }

  adicionarProduto(produto: any): void {
    this.produtoSelecionado = produto;

  }

  exibirProduto(produto: any): string {
    if (!produto) return '';
    let texto = produto.nome || '';
    if (produto.cor) texto += ` - ${produto.cor}`;
    if (produto.tamanho) texto += ` (${produto.tamanho})`;
    return texto;
  }

  adicionarProdutoManual(): void {
    if (!this.produtoSelecionado || !this.quantidadeControl.value) {
      alert('Selecione um produto e informe a quantidade');
      return;
    }

    const quantidade = this.quantidadeControl.value;
    const estoqueDisponivel = this.produtoSelecionado.estoqueAtual || 0;

    // Verificar estoque disponível
    if (estoqueDisponivel < quantidade) {
      alert(`❌ Estoque insuficiente!\n\nProduto: ${this.produtoSelecionado.nome}\nDisponível: ${estoqueDisponivel}\nSolicitado: ${quantidade}\n\nAjuste a quantidade ou escolha outro produto.`);
      return;
    }

    // Verificar se produto já foi adicionado
    const jaAdicionado = this.produtosAdicionados.find(p => p.produtoId === this.produtoSelecionado.id);
    if (jaAdicionado) {
      alert('Produto já adicionado! Remova-o primeiro se quiser alterar a quantidade.');
      return;
    }

    // Adicionar à lista
    this.produtosAdicionados.push({
      produtoId: this.produtoSelecionado.id,
      produtoNome: this.produtoSelecionado.nome,
      quantidade: quantidade,
      statusItem: 'PENDENTE'
    });




    // Limpar campos
    this.produtoControl.setValue('');
    this.quantidadeControl.setValue(1);
    this.produtoSelecionado = null;
  }

  removerProduto(index: number): void {
    const produto = this.produtosAdicionados[index];
    this.produtosAdicionados.splice(index, 1);

  }

  getTotalUnidades(): number {
    return this.produtosAdicionados.reduce((sum, item) => sum + item.quantidade, 0);
  }

  onSubmit(): void {
    if (!this.movimentacaoForm.valid) {
      alert('Preencha todos os campos obrigatórios');
      return;
    }

    if (this.produtosAdicionados.length === 0) {
      alert('Adicione pelo menos um produto à movimentação');
      return;
    }



    // Validações antes de enviar
    const clienteId = this.movimentacaoForm.value.clienteId;
    if (!clienteId) {
      alert('❌ Cliente não selecionado!');
      return;
    }

    // Backend ainda não suporta múltiplos itens, então vamos criar 1 movimentação por produto
    const requests = this.produtosAdicionados.map((item, index) => {
      // Validação individual por produto
      if (!item.produtoId) {
        console.error(`❌ Produto ${index + 1} sem ID:`, item);
      }
      if (!item.quantidade || item.quantidade <= 0) {
        console.error(`❌ Produto ${index + 1} com quantidade inválida:`, item);
      }

      const movimentacao = {
        clienteId: clienteId,
        tipoMovimentacao: this.movimentacaoForm.value.tipoMovimentacao,
        observacao: this.movimentacaoForm.value.observacao || '',
        produtoId: item.produtoId,
        quantidade: item.quantidade
      };



      if (movimentacao.tipoMovimentacao === 'SAIDA_TEMPORARIA') {
        return this.movimentacaoService.registrarSaidaTemporaria(movimentacao);
      } else {
        return this.movimentacaoService.registrarDevolucao(movimentacao);
      }
    });

    // Executar todas as requisições em paralelo
    let concluidas = 0;
    let erros = 0;
    const errosPorProduto: string[] = [];

    requests.forEach((req, index) => {
      req.subscribe({
        next: () => {
          concluidas++;
          const nomeProduto = this.produtosAdicionados[index]?.produtoNome || 'Desconhecido';

          
          if (concluidas + erros === this.produtosAdicionados.length) {
            if (erros === 0) {
              alert(`✅ Movimentação registrada com sucesso!\n${concluidas} produto(s) adicionado(s).`);
              this.router.navigate(['/movimentacoes-estoque'], { 
                queryParams: { reload: new Date().getTime() }
              });
            } else {
              const mensagemErro = errosPorProduto.join('\n');
              alert(`⚠️ Parcialmente concluído:\n\n✅ ${concluidas} produto(s) registrado(s)\n❌ ${erros} falha(s):\n\n${mensagemErro}\n\nOs produtos salvos já estão na lista.`);
              this.router.navigate(['/movimentacoes-estoque'], { 
                queryParams: { reload: new Date().getTime() }
              });
            }
          }
        },
        error: (err) => {
          erros++;
          const nomeProduto = this.produtosAdicionados[index]?.produtoNome || 'Desconhecido';
          const motivoErro = err.error?.message || err.message || 'Erro desconhecido';
          errosPorProduto.push(`• ${nomeProduto}: ${motivoErro}`);
          
          console.error(`❌ Erro ao salvar produto ${index + 1} (${nomeProduto}):`, err);
          console.error('📦 Detalhes do erro:', {
            status: err.status,
            mensagem: err.error?.message || err.message,
            produto: this.produtosAdicionados[index]
          });
          
          if (concluidas + erros === this.produtosAdicionados.length) {
            const mensagemErro = errosPorProduto.join('\n');
            if (concluidas > 0) {
              alert(`⚠️ Parcialmente concluído:\n\n✅ ${concluidas} produto(s) registrado(s)\n❌ ${erros} falha(s):\n\n${mensagemErro}\n\nOs produtos salvos já estão na lista.`);
            } else {
              alert(`❌ Nenhum produto foi registrado:\n\n${mensagemErro}\n\nVerifique os dados e tente novamente.`);
            }
            this.router.navigate(['/movimentacoes-estoque'], { 
              queryParams: { reload: new Date().getTime() }
            });
          }
        }
      });
    });
  }
}
