import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatIconModule } from '@angular/material/icon';
import { Router, ActivatedRoute } from '@angular/router';
import { VendaService, Venda } from './venda.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { Observable, BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-venda',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule,
    MatAutocompleteModule,
    MatIconModule
  ],
  templateUrl: './venda.component.html',
  styleUrls: ['./venda.component.scss']
})
export class VendaComponent implements OnInit {
  vendaForm!: FormGroup;
  clientes$!: Observable<any[]>;
  produtos$!: Observable<any[]>;
  produtosLista: any[] = [];
  produtosFiltrados$ = new BehaviorSubject<any[]>([]);
  displayedColumns: string[] = ['produto','tamanho','cor','quantidade','preco','subtotal','acoes'];
  dataSource = new MatTableDataSource<FormGroup>();

  modoEdicao = false;
  vendaId!: number;

  constructor(
    private fb: FormBuilder,
    private vendaService: VendaService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
  ) {
    this.vendaForm = this.fb.group({
      dataVenda: ['', Validators.required],
      clienteId: ['', Validators.required],
      tipoPagamento: ['', Validators.required],
      numeroParcelas: [1, Validators.required],
      valorTotal: [0, Validators.required],
      primeiraParcela: [''],
      intervaloDias: [30],
      itens: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.clientes$ = this.vendaService.listarClientes();
    this.produtos$ = this.vendaService.listarProdutos();

    // IMPORTANTE: Aguardar produtos carregarem antes de processar queryParams
    this.produtos$.subscribe(produtos => {

      this.produtosLista = produtos;
      this.produtosFiltrados$.next(produtos);
      
      // AGORA que produtos estão prontos, verificar se há pré-preenchimento
      this.route.queryParams.subscribe(params => {
        if (params['clienteId'] && (params['produtoId'] || params['produtos'])) {


          this.preencherDaMovimentacao(params);
        }
      });
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.modoEdicao = true;
      this.vendaId = Number(idParam);
      this.vendaService.buscarPorId(this.vendaId).subscribe({
        next: (venda) => this.carregarVenda(venda),
        error: () => this.snackBar.open('Erro ao carregar venda', 'Fechar', { duration: 3000 })
      });
    }
  }

  get itens(): FormArray {
    return this.vendaForm.get('itens') as FormArray;
  }

  filtrarProdutos(valor: string) {
    if (!valor || valor.trim() === '') {
      this.produtosFiltrados$.next(this.produtosLista);
      return;
    }
    const filtro = valor.toLowerCase();
    const filtrados = this.produtosLista.filter(p =>
      p.nome.toLowerCase().includes(filtro) ||
      p.categoria?.nome.toLowerCase().includes(filtro)
    );

    this.produtosFiltrados$.next(filtrados);
  }

  onProdutoSelecionado(produtoId: number) {


    
    const produto = this.produtosLista.find(p => p.id === produtoId);

    
    if (produto) {
      const item = this.fb.group({
        produtoId: [produto.id, Validators.required],
        nome: [produto.nome],
        tamanho: [produto.tamanho],
        cor: [produto.cor],
        quantidade: [1, Validators.required],
        precoUnitario: [produto.preco, Validators.required],
        subtotal: [produto.preco]
      });
      this.itens.push(item);
      this.atualizarTabela();


      this.calcularTotal();
    } else {
      console.error('Produto não encontrado com ID:', produtoId);
    }
  }

  calcularSubtotalERP(item: FormGroup) {
    const qtd = item.value.quantidade || 0;
    const preco = item.value.precoUnitario || 0;
    item.patchValue({ subtotal: qtd * preco });
    this.calcularTotal();
  }

  removerItem(index: number) {
    this.itens.removeAt(index);
    this.atualizarTabela();
    this.calcularTotal();
  }

  atualizarTabela() {
    this.dataSource.data = this.itens.controls as FormGroup[];
  }

  calcularTotal() {
    const total = this.itens.controls.reduce(
      (acc, item) => acc + (item.value.subtotal || 0),
      0
    );
    this.vendaForm.patchValue({ valorTotal: total }, { emitEvent: false });
  }

  carregarVenda(venda: any) {
    this.vendaForm.patchValue({
      dataVenda: venda.dataVenda,
      clienteId: venda.cliente?.id,
      tipoPagamento: venda.formaPagamento?.tipo,
      numeroParcelas: venda.formaPagamento?.numeroParcelas ?? 1,
      valorTotal: venda.valorTotal ?? 0,
      primeiraParcela: venda.primeiraParcela,
      intervaloDias: venda.intervaloDias
    });

    venda.itens.forEach((item: any) => {
      this.itens.push(this.fb.group({
        produtoId: [item.produtoId],
        nome: [item.nome],
        tamanho: [item.tamanho],
        cor: [item.cor],
        quantidade: [item.quantidade],
        precoUnitario: [item.precoUnitario],
        subtotal: [item.subtotal]
      }));
    });
    
    this.atualizarTabela();
  }

  voltar() {
    this.router.navigate(['/vendas']);
  }

  onSubmit() {
    if (this.vendaForm.valid) {
      const venda: Venda = {
        dataVenda: this.vendaForm.get('dataVenda')?.value ?? '',
        clienteId: Number(this.vendaForm.get('clienteId')?.value ?? 0),
        tipoPagamento: this.vendaForm.get('tipoPagamento')?.value ?? '',
        numeroParcelas: Number(this.vendaForm.get('numeroParcelas')?.value ?? 1),
        valorTotal: Number(this.vendaForm.get('valorTotal')?.value ?? 0),
        primeiraParcela: this.vendaForm.get('primeiraParcela')?.value,
        intervaloDias: this.vendaForm.get('intervaloDias')?.value,
        itens: this.itens.value,
        ...(this.modoEdicao ? { id: this.vendaId } : {})
      };








      if (this.modoEdicao) {
        this.vendaService.atualizarVenda(this.vendaId, venda).subscribe({
          next: () => {
            this.snackBar.open('Venda atualizada com sucesso!', 'Fechar', { duration: 3000 });
            this.router.navigate(['/vendas']);
          },
          error: () => this.snackBar.open('Erro ao atualizar venda', 'Fechar', { duration: 3000 })
        });
      } else {
        this.vendaService.cadastrarVenda(venda).subscribe({
          next: (resposta) => {




            
            if (resposta.valorTotal !== venda.valorTotal) {
              console.warn('⚠️ ATENÇÃO: Valor total foi alterado pelo backend!');
              console.warn('   Frontend enviou:', venda.valorTotal);
              console.warn('   Backend retornou:', resposta.valorTotal);
              console.warn('   Diferença:', resposta.valorTotal - venda.valorTotal);
            }
            
            this.snackBar.open('Venda cadastrada com sucesso!', 'Fechar', { duration: 3000 });
            this.router.navigate(['/comprovante', resposta.id!]);
          },
          error: (err) => {
            console.error('❌ Erro ao cadastrar venda:', err);
            this.snackBar.open('Erro ao cadastrar venda', 'Fechar', { duration: 3000 });
          }
        });
      }
    }
  }

  /**
   * Preenche o formulário com dados vindos da movimentação de estoque
   */
  private preencherDaMovimentacao(params: any): void {

    
    // Preencher cliente
    if (params['clienteId']) {
      this.vendaForm.patchValue({
        clienteId: Number(params['clienteId'])
      });

    }

    // Verificar se é múltiplos produtos (novo formato) ou produto único (legado)
    if (params['produtos']) {
      // NOVO FORMATO: múltiplos produtos via JSON
      this.preencherMultiplosProdutos(params);
    } else if (params['produtoId'] && params['quantidade']) {
      // FORMATO LEGADO: produto único
      this.preencherProdutoUnico(params);
    }
  }
  
  /**
   * Preenche múltiplos produtos da movimentação (novo formato)
   */
  private preencherMultiplosProdutos(params: any): void {
    try {


      
      const produtos = JSON.parse(params['produtos']);

      
      let adicionados = 0;
      
      produtos.forEach((item: any, index: number) => {

        
        const produto = this.produtosLista.find(p => p.id === item.produtoId);
        
        if (produto) {
          const formItem = this.fb.group({
            produtoId: [produto.id, Validators.required],
            nome: [produto.nome],
            tamanho: [produto.tamanho],
            cor: [produto.cor],
            quantidade: [item.quantidade, Validators.required],
            precoUnitario: [produto.preco, Validators.required],
            subtotal: [produto.preco * item.quantidade]
          });
          
          this.itens.push(formItem);
          adicionados++;
          

        } else {
          console.error(`❌ Produto ID ${item.produtoId} não encontrado na lista`);

        }
      });
      
      this.atualizarTabela();
      this.calcularTotal();
      



      
      if (adicionados > 0) {
        this.snackBar.open(`✅ ${adicionados} produto(s) pré-preenchido(s)!`, 'OK', { duration: 3000 });
      } else {
        console.error('❌ Nenhum produto foi adicionado!');
        this.snackBar.open('❌ Erro ao carregar produtos!', 'OK', { duration: 3000 });
      }
    } catch (error) {
      console.error('❌ Erro ao parsear produtos:', error);
      console.error('❌ Valor que causou erro:', params['produtos']);
      this.snackBar.open('❌ Erro ao processar produtos!', 'OK', { duration: 3000 });
    }
  }
  
  /**
   * Preenche produto único da movimentação (formato legado)
   */
  private preencherProdutoUnico(params: any): void {
    const produtoId = Number(params['produtoId']);
    const quantidade = Number(params['quantidade']) || 1;
    

    
    const produto = this.produtosLista.find(p => p.id === produtoId);
    if (produto) {

      
      // Criar item do FormArray COM TODOS OS CAMPOS
      const item = this.fb.group({
        produtoId: [produto.id, Validators.required],
        nome: [produto.nome],
        tamanho: [produto.tamanho],
        cor: [produto.cor],
        quantidade: [quantidade, Validators.required],
        precoUnitario: [produto.preco, Validators.required],
        subtotal: [produto.preco * quantidade]
      });
      
      this.itens.push(item);
      this.atualizarTabela();
      this.calcularTotal();
      



      this.snackBar.open('✅ Venda pré-preenchida da movimentação!', 'OK', { duration: 3000 });
    } else {
      console.error('❌ Produto não encontrado na lista! ID:', produtoId);

      this.snackBar.open('❌ Produto não encontrado!', 'OK', { duration: 3000 });
    }
  }
}
