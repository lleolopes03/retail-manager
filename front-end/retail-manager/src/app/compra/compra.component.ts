import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, Validators, FormArray, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CompraService, Compra } from './compra.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-compra',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
    MatTooltipModule
  ],
  templateUrl: './compra.component.html',
  styleUrls: ['./compra.component.scss']
})
export class CompraComponent implements OnInit {
  compraForm!: FormGroup;
  fornecedores: any[] = [];
  produtos: any[] = [];

  constructor(
    private fb: FormBuilder,
    private compraService: CompraService,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {
    // inicializa o formulário com nomes compatíveis com o back-end
    this.compraForm = this.fb.group({
      dataCompra: ['', Validators.required],
      fornecedorId: ['', Validators.required],
      valorTotal: [0],
      itens: this.fb.array([])
    });
  }

  ngOnInit(): void {
    // carrega fornecedores
    this.compraService.listarFornecedores().subscribe({
      next: (dados) => {
        this.fornecedores = dados;
        this.cd.detectChanges();
      },
      error: () => alert('Erro ao carregar fornecedores')
    });

    // carrega produtos
    this.compraService.listarProdutos().subscribe({
      next: (dados) => {
        this.produtos = dados;
        this.cd.detectChanges();
      },
      error: () => alert('Erro ao carregar produtos')
    });

    // adiciona um item inicial
    setTimeout(() => this.adicionarItem());
  }

  // getter seguro para itens
  get itens(): FormArray {
    return this.compraForm.get('itens') as FormArray;
  }

  // adiciona novo item
  adicionarItem() {
    const item = this.fb.group({
      produtoId: ['', Validators.required],
      quantidade: [1, Validators.required],
      precoUnitario: [0, Validators.required],
      subtotal: [0]
    });
    this.itens.push(item);
    this.calcularTotal();
  }

  // remove um item do FormArray
  removerItem(index: number) {
    if (this.itens.length > 1) {
      this.itens.removeAt(index);
      this.calcularTotal();
    } else {
      alert('⚠️ É necessário ter pelo menos 1 item na compra!');
    }
  }

  // calcula subtotal de um item
  calcularSubtotal(index: number) {
    const item = this.itens.at(index);
    if (!item) return;

    const qtd = item.get('quantidade')!.value || 0;
    const preco = item.get('precoUnitario')!.value || 0;
    const subtotal = qtd * preco;

    item.patchValue({ subtotal });
    this.calcularTotal();
  }

  // calcula valor total da compra
  calcularTotal() {
    const total = this.itens.controls.reduce(
      (acc, item) => acc + (item.get('subtotal')!.value || 0),
      0
    );
    this.compraForm.patchValue({ valorTotal: total });
  }
  voltar() {
  this.router.navigate(['/compras']); // ou ['/dashboard'] se preferir
  }

  // envia para o backend
  onSubmit() {
    if (this.compraForm.valid) {
      const compra: Compra = this.compraForm.value;

      this.compraService.cadastrarCompra(compra).subscribe({
        next: () => {
          alert('Compra cadastrada com sucesso!');
          this.router.navigate(['/compras']);
        },
        error: () => alert('Erro ao cadastrar compra')
      });
    }
  }
}
