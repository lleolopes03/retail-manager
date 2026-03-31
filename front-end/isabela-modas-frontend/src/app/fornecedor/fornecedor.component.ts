import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FornecedorService, Fornecedor } from './fornecedor.service';

@Component({
  selector: 'app-fornecedor',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './fornecedor.component.html',
  styleUrls: ['./fornecedor.component.scss']
})
export class FornecedorComponent implements OnInit {
  fornecedorForm;
  fornecedorId?: number;
  modoEdicao = false;

  constructor(
    private fb: FormBuilder,
    private fornecedorService: FornecedorService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.fornecedorForm = this.fb.group({
      nome: ['', Validators.required],
      cnpj: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      endereco: this.fb.group({
       cep: ['', Validators.required],
       logradouro: [''],
       complemento: [''],
       bairro: [''],
       localidade: [''],
       uf: ['']
      })
    });
  }

  ngOnInit(): void {
    this.fornecedorId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.fornecedorId) {
      this.modoEdicao = true;
      this.fornecedorService.buscarPorId(this.fornecedorId).subscribe({
        next: (f: Fornecedor) => this.fornecedorForm.patchValue(f),
        error: () => alert('Erro ao carregar fornecedor')
      });
    }
  }
  voltar() {
  this.router.navigate(['/fornecedores']); // ou ['/dashboard'] se preferir
  }

  buscarCep() {
  const cep = this.fornecedorForm.get('endereco.cep')?.value;
  if (cep) {
    this.fornecedorService['http'].get(`http://localhost:8080/cep?cep=${cep}`).subscribe({
      next: (dados: any) => {
        this.fornecedorForm.patchValue({
          endereco: {
            logradouro: dados.logradouro,
            complemento: dados.complemento,
            bairro: dados.bairro,
            localidade: dados.localidade,
            uf: dados.uf
          }
        });
      },
      error: () => alert('CEP não encontrado')
    });
  }
}

  onSubmit() {
    if (this.fornecedorForm.valid) {
      const fornecedor = this.fornecedorForm.getRawValue() as Fornecedor;

      if (this.modoEdicao && this.fornecedorId) {
        this.fornecedorService.atualizar(this.fornecedorId, fornecedor).subscribe({
          next: () => {
            alert('Fornecedor atualizado com sucesso!');
            this.router.navigate(['/fornecedores']);
          },
          error: () => alert('Erro ao atualizar fornecedor')
        });
      } else {
        this.fornecedorService.criar(fornecedor).subscribe({
          next: () => {
            alert('Fornecedor cadastrado com sucesso!');
            this.router.navigate(['/fornecedores']);
          },
          error: () => alert('Erro ao cadastrar fornecedor')
        });
      }
    }
  }
}
