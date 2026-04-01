import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { ProdutoService, Produto, ProdutoRequest, Categoria } from './produto.service';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-produto',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './produto.component.html',
  styleUrls: ['./produto.component.scss']
})
export class ProdutoComponent implements OnInit {
  produtoForm;
  categorias: Categoria[] = [];
  produtoId?: number;
  modoEdicao = false;

  constructor(
    private fb: FormBuilder,
    private produtoService: ProdutoService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // 🔹 Usando nonNullable.group para evitar null nos controles
    this.produtoForm = this.fb.nonNullable.group({
      nome: ['', Validators.required],
      preco: [0, Validators.required],
      tamanho: ['', Validators.required],
      cor: ['', Validators.required],
      estoqueAtual: [0, Validators.required],
      categoriaId: [0, Validators.required] // ✅ inicializa com 0 (sempre number)
    });
  }

  ngOnInit(): void {
    // Carregar categorias
    this.produtoService.listarCategorias().subscribe({
      next: (dados) => this.categorias = dados,
      error: () => alert('Erro ao carregar categorias')
    });

    // Verificar se é edição
    this.produtoId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.produtoId) {
      this.modoEdicao = true;
      this.produtoService.buscarPorId(this.produtoId).subscribe({
        next: (p: Produto) => this.produtoForm.patchValue({
          nome: p.nome ?? '',
          preco: p.preco ?? 0,
          tamanho: p.tamanho ?? '',
          cor: p.cor ?? '',
          estoqueAtual: p.estoqueAtual ?? 0,
          categoriaId: p.categoria?.id ?? 0 // ✅ corrigido
        }),
        error: () => alert('Erro ao carregar produto')
      });
    }
  }
  voltar() {
  this.router.navigate(['/produtos']); // ou ['/dashboard'] se preferir
  }

  onSubmit() {
    if (this.produtoForm.valid) {
      const formValue = this.produtoForm.getRawValue();

      const produtoRequest: ProdutoRequest = {
        nome: formValue.nome,
        preco: formValue.preco,
        tamanho: formValue.tamanho,
        cor: formValue.cor,
        estoqueAtual: formValue.estoqueAtual,
        categoriaId: formValue.categoriaId
      };

      if (this.modoEdicao && this.produtoId) {
        this.produtoService.atualizarProduto(this.produtoId, produtoRequest).subscribe({
          next: () => {
            alert('Produto atualizado com sucesso!');
            this.router.navigate(['/produtos']);
          },
          error: () => alert('Erro ao atualizar produto')
        });
      } else {
        this.produtoService.cadastrarProduto(produtoRequest).subscribe({
          next: () => {
            alert('Produto cadastrado com sucesso!');
            this.router.navigate(['/produtos']);
          },
          error: () => alert('Erro ao cadastrar produto')
        });
      }
    }
  }
}
