import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MovimentacaoEstoqueService, MovimentacaoEstoque } from './movimentacao-estoque.service';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-movimentacoes-lista',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
    MatChipsModule,
    MatAutocompleteModule
  ],
  templateUrl: './movimentacoes-lista.component.html',
  styleUrls: ['./movimentacoes-lista.component.scss']
})
export class MovimentacoesListaComponent implements OnInit {
  displayedColumns: string[] = ['dataMovimentacao', 'tipo', 'status', 'produto', 'cliente', 'quantidade', 'acoes'];
  dataSource = new MatTableDataSource<MovimentacaoEstoque>();
  dadosOriginais: MovimentacaoEstoque[] = []; // Cache dos dados originais

  // Autocomplete
  produtoControl = new FormControl('');
  produtos: any[] = [];
  produtosFiltrados: Observable<any[]>;

  // Filtros
  filtros = {
    produtoNome: '',
    clienteCpf: '',
    tipo: '',
    status: ''
  };

  tiposMovimentacao = [
    { value: 'SAIDA_TEMPORARIA', label: 'Saída Temporária' },
    { value: 'DEVOLUCAO', label: 'Devolução' },
    { value: 'VENDA', label: 'Venda Definitiva' }
  ];

  statusMovimentacao = [
    { value: 'ATIVA', label: 'Ativa' },
    { value: 'DEVOLVIDA', label: 'Devolvida' },
    { value: 'VENDIDA', label: 'Vendida' }
  ];

  // Flag para controlar renderização da tabela (evita erro NG0100)
  tabelaVisivel = false;

  constructor(
    private movimentacaoService: MovimentacaoEstoqueService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    // Inicializar autocomplete
    this.produtosFiltrados = this.produtoControl.valueChanges.pipe(
      startWith(''),
      map(value => this._filtrarProdutos(value || ''))
    );
  }

  ngOnInit(): void {
    // Recarregar sempre que query params mudarem
    this.route.queryParams.subscribe(() => {

      this.carregarMovimentacoes();
      this.carregarProdutos();
    });
  }

  carregarProdutos(): void {
    this.movimentacaoService.listarProdutos().subscribe({
      next: (data) => {
        this.produtos = data ?? [];

      },
      error: (err) => {
        console.error('❌ Erro ao carregar produtos:', err);
        if (err.status !== 401) {
          console.warn('⚠️ Continuando sem autocomplete de produtos');
        }
      }
    });
  }

  private _filtrarProdutos(value: string | any): any[] {
    if (!value) return this.produtos;
    
    // Se value for um objeto (produto selecionado), usar o nome
    const filterValue = typeof value === 'string' 
      ? value.toLowerCase() 
      : (value.nome || '').toLowerCase();
    
    return this.produtos.filter(p => 
      p.nome?.toLowerCase().includes(filterValue) ||
      p.cor?.toLowerCase().includes(filterValue) ||
      p.tamanho?.toLowerCase().includes(filterValue)
    );
  }

  selecionarProduto(produto: any): void {
    this.filtros.produtoNome = produto.nome;

  }

  exibirProduto(produto: any): string {
    if (!produto) return '';
    let texto = produto.nome || '';
    if (produto.cor) texto += ` - ${produto.cor}`;
    if (produto.tamanho) texto += ` (${produto.tamanho})`;
    return texto;
  }

  carregarMovimentacoes(): void {

    this.movimentacaoService.listar().subscribe({
      next: (data) => {

        
        // Converter formato antigo (1 produto) para novo formato (array de itens)
        const movimentacoesNormalizadas = (data ?? []).map(m => this.normalizarMovimentacao(m));
        
        this.dadosOriginais = movimentacoesNormalizadas;
        
        // Forçar atualização do dataSource (solução NG0100)
        this.tabelaVisivel = false;
        this.dataSource.data = [];
        this.cdr.detectChanges(); // Forçar detecção imediata
        
        setTimeout(() => {
          this.dataSource.data = [...this.dadosOriginais];
          this.dataSource._updateChangeSubscription();
          this.tabelaVisivel = true;
          this.cdr.detectChanges(); // Forçar detecção após mudança
          

          
          // Debug: verificar se botões vão aparecer
          const comBotoes = this.dataSource.data.filter(m => this.podeDevolver(m));

          if (comBotoes.length > 0) {

          }
        }, 0);
      },
      error: (err) => {
        console.error('❌ Erro ao carregar movimentações:', err);
        if (err.status === 401) {
          alert('Sessão expirada. Por favor, faça login novamente.');
        } else {
          alert('Erro ao carregar movimentações');
        }
      }
    });
  }

  // Normaliza movimentação: converte formato antigo (1 produto) para novo (array de itens)
  private normalizarMovimentacao(m: MovimentacaoEstoque): MovimentacaoEstoque {
    // Se já tem array de itens, retorna como está
    if (m.itens && Array.isArray(m.itens) && m.itens.length > 0) {
      return m;
    }

    // Se é formato antigo (produtoId, quantidade), converte para array de itens
    if (m.produtoId && m.quantidade) {
      return {
        ...m,
        itens: [{
          id: m.id,
          produtoId: m.produtoId,
          produtoNome: m.produtoNome,
          quantidade: m.quantidade,
          statusItem: this.mapearStatusLegado(m.status)
        }]
      };
    }

    // Se não tem nem itens nem produto, retorna vazio
    return { ...m, itens: [] };
  }

  private mapearStatusLegado(status?: string): 'PENDENTE' | 'DEVOLVIDO' | 'VENDIDO' {
    switch (status) {
      case 'DEVOLVIDA': return 'DEVOLVIDO';
      case 'VENDIDA': return 'VENDIDO';
      default: return 'PENDENTE';
    }
  }

  aplicarFiltros(): void {
    const { tipo, status, clienteCpf, produtoNome } = this.filtros;
    



    // Se não houver filtros, mostra tudo
    if (!tipo && !status && !clienteCpf && !produtoNome) {
      this.dataSource.data = this.dadosOriginais;

      return;
    }

    // Sempre usar filtro local (mais confiável)
    this.filtrarLocalmente();
  }

  private filtrarLocalmente(): void {
    let dados = [...this.dadosOriginais]; // Sempre partir dos dados originais
    



    if (this.filtros.produtoNome) {
      const termo = this.filtros.produtoNome.toLowerCase();


      dados = dados.filter(m => {
        // Backend pode retornar produtoNome direto OU produto.nome
        const nomeProduto = m.produtoNome || m.produto?.nome || '';
        return nomeProduto.toLowerCase().includes(termo);
      });

    }

    if (this.filtros.clienteCpf) {
      const cpf = this.filtros.clienteCpf.replace(/\D/g, ''); // Remove caracteres não numéricos


      dados = dados.filter(m => {
        // Backend pode retornar cliente.cpf OU apenas clienteId
        const cpfCliente = m.cliente?.cpf?.replace(/\D/g, '') || '';
        return cpfCliente.includes(cpf) || m.clienteId?.toString() === cpf;
      });

    }

    if (this.filtros.tipo) {

      dados = dados.filter(m => m.tipoMovimentacao === this.filtros.tipo);

    }

    if (this.filtros.status) {

      dados = dados.filter(m => m.status === this.filtros.status);

    }

    this.dataSource.data = dados;

  }

  limparFiltros(): void {
    this.filtros = {
      produtoNome: '',
      clienteCpf: '',
      tipo: '',
      status: ''
    };
    this.produtoControl.setValue(''); // Limpar autocomplete também
    this.dataSource.data = this.dadosOriginais; // Restaurar dados originais

  }

  devolverAoEstoque(movimentacao: MovimentacaoEstoque): void {
    if (!movimentacao.id) return;

    const nomeProduto = movimentacao.produto?.nome || movimentacao.produtoNome || 'este produto';
    const confirma = confirm(
      `🔄 Confirma a devolução?\n\nProduto: ${nomeProduto}\nQuantidade: ${movimentacao.quantidade} unidade(s)\n\nO estoque será atualizado automaticamente.`
    );

    if (confirma) {
      this.movimentacaoService.devolverAoEstoque(movimentacao.id).subscribe({
        next: () => {
          alert(`✅ Produto devolvido com sucesso!\n\nO estoque de "${nomeProduto}" foi atualizado.`);
          this.carregarMovimentacoes();
        },
        error: (err) => {
          console.error('Erro ao devolver produto:', err);
          alert(`❌ Erro ao devolver produto ao estoque.\n\nVerifique se o backend está rodando e tente novamente.`);
        }
      });
    }
  }

  converterParaVenda(movimentacao: MovimentacaoEstoque): void {

    
    // Buscar TODAS as movimentações ATIVAS do mesmo cliente
    const movimentacoesDoCliente = this.dadosOriginais.filter(m => 
      m.clienteId === movimentacao.clienteId &&
      m.tipoMovimentacao === 'SAIDA_TEMPORARIA' &&
      (!m.status || m.status === 'ATIVA')
    );
    

    
    if (movimentacoesDoCliente.length === 1) {
      // CASO SIMPLES: Apenas 1 produto
      const nomeProduto = movimentacao.produto?.nome || movimentacao.produtoNome || 'produto';
      const confirma = confirm(
        `🛒 Converter em venda?\n\nProduto: ${nomeProduto}\nQuantidade: ${movimentacao.quantidade}\n\nVocê será redirecionado para registrar a forma de pagamento.`
      );

      if (confirma) {
        const params = {
          clienteId: movimentacao.clienteId,
          produtos: JSON.stringify([{
            produtoId: movimentacao.produtoId,
            quantidade: movimentacao.quantidade,
            movimentacaoId: movimentacao.id
          }])
        };
        

        
        this.router.navigate(['/vendas/novo'], { queryParams: params });
      }
    } else {
      // CASO AVANÇADO: Múltiplos produtos - permite selecionar quais vender
      this.exibirDialogoMultiplosItens(movimentacoesDoCliente);
    }
  }
  
  /**
   * Exibe diálogo para selecionar múltiplos produtos do mesmo cliente
   */
  private exibirDialogoMultiplosItens(movimentacoes: MovimentacaoEstoque[]): void {
    const nomeCliente = movimentacoes[0].cliente?.nome || 'Cliente';
    
    // Montar lista de produtos
    const listaProdutos = movimentacoes.map((m, idx) => {
      const nome = m.produto?.nome || m.produtoNome || 'produto';
      return `[${idx + 1}] ${nome} (Qtd: ${m.quantidade})`;
    }).join('\n');
    
    const mensagem = `🛒 ${nomeCliente} tem ${movimentacoes.length} produtos em experimentação:\n\n${listaProdutos}\n\n📝 Digite os NÚMEROS dos produtos para vender (ex: 1,3,5)\n💡 Deixe vazio para vender TODOS`;
    
    const resposta = prompt(mensagem);
    
    if (resposta === null) {

      return;
    }
    
    let movimentacoesSelecionadas: MovimentacaoEstoque[];
    
    if (resposta.trim() === '') {
      // Vender TODOS
      movimentacoesSelecionadas = movimentacoes;
    } else {
      // Vender apenas os selecionados
      const indices = resposta.split(',').map(s => parseInt(s.trim()) - 1);
      movimentacoesSelecionadas = indices
        .filter(idx => idx >= 0 && idx < movimentacoes.length)
        .map(idx => movimentacoes[idx]);
    }
    
    if (movimentacoesSelecionadas.length === 0) {
      alert('❌ Nenhum produto válido selecionado!');
      return;
    }
    
    // Montar lista de produtos para confirmação
    const resumo = movimentacoesSelecionadas.map(m => {
      const nome = m.produto?.nome || m.produtoNome;
      return `  • ${nome} (Qtd: ${m.quantidade})`;
    }).join('\n');
    
    const confirma = confirm(`🛒 Vender ${movimentacoesSelecionadas.length} produto(s)?\n\n${resumo}\n\nVocê será redirecionado para registrar a forma de pagamento.`);
    
    if (confirma) {
      const produtos = movimentacoesSelecionadas.map(m => ({
        produtoId: m.produtoId,
        quantidade: m.quantidade,
        movimentacaoId: m.id
      }));
      
      const params = {
        clienteId: movimentacoesSelecionadas[0].clienteId,
        produtos: JSON.stringify(produtos)
      };
      

      
      this.router.navigate(['/vendas/novo'], { queryParams: params });
    }
  }

  getStatusIcon(movimentacao: MovimentacaoEstoque): string {
    if (movimentacao.tipoMovimentacao === 'DEVOLUCAO') return 'check_circle';
    if (movimentacao.tipoMovimentacao === 'VENDA' || movimentacao.tipoMovimentacao === 'SAIDA') return 'shopping_bag';
    return 'done';
  }

  getStatusTooltip(movimentacao: MovimentacaoEstoque): string {
    if (movimentacao.tipoMovimentacao === 'DEVOLUCAO') return 'Devolvido ao estoque';
    if (movimentacao.tipoMovimentacao === 'VENDA' || movimentacao.tipoMovimentacao === 'SAIDA') return 'Vendido';
    return 'Finalizado';
  }

  getTipoLabel(tipo: string): string {
    const encontrado = this.tiposMovimentacao.find(t => t.value === tipo);
    return encontrado ? encontrado.label : tipo;
  }

  getStatusColor(status?: string): string {
    switch (status) {
      case 'ATIVA': return 'accent';
      case 'DEVOLVIDA': return 'primary';
      case 'VENDIDA': return 'warn';
      default: return '';
    }
  }

  podeDevolver(movimentacao: MovimentacaoEstoque): boolean {
    // Se status não existe (undefined), considera como ATIVA por padrão
    const statusAtivo = !movimentacao.status || movimentacao.status === 'ATIVA';
    return movimentacao.tipoMovimentacao === 'SAIDA_TEMPORARIA' && statusAtivo;
  }

  podeConverterParaVenda(movimentacao: MovimentacaoEstoque): boolean {
    // Se status não existe (undefined), considera como ATIVA por padrão
    const statusAtivo = !movimentacao.status || movimentacao.status === 'ATIVA';
    return movimentacao.tipoMovimentacao === 'SAIDA_TEMPORARIA' && statusAtivo;
  }
}
