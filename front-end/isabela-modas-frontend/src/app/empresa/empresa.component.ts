import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { EmpresaService } from './empresa.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-empresa',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './empresa.component.html',
  styleUrls: ['./empresa.component.scss']
})
export class EmpresaComponent implements OnInit {
  empresaForm;
  empresaId: number | null = null; // ✅ guarda o ID da empresa

  constructor(
    private fb: FormBuilder,
    private empresaService: EmpresaService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.empresaForm = this.fb.group({
      nome: ['', Validators.required],
      cnpj: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      endereco: this.fb.group({
        cep: ['', Validators.required],
        logradouro: [''],
        bairro: [''],
        localidade: [''],
        uf: [''],
        numero: ['', Validators.required]
      })
    });
  }

  ngOnInit(): void {
    // ✅ busca empresa já cadastrada
    this.empresaService.buscarEmpresa().subscribe({
      next: (dados) => {
        this.empresaId = dados.id;
        this.empresaForm.patchValue(dados);
      },
      error: () => {
        // se não existir empresa, mantém formulário vazio para cadastro

      }
    });
  }

  buscarCep() {
    const cep = this.empresaForm.get('endereco.cep')?.value;
    if (cep) {
      this.empresaService.buscarCep(cep).subscribe({
        next: (dados: any) => {
          this.empresaForm.patchValue({
            endereco: {
              logradouro: dados.logradouro,
              bairro: dados.bairro,
              localidade: dados.localidade,
              uf: dados.uf
            }
          });
        },
        error: () => this.snackBar.open('CEP não encontrado', 'Fechar', { duration: 3000 })
      });
    }
  }
  voltar() {
  this.router.navigate(['/dashboard']); // ou ['/dashboard'] se preferir
  }

  onSubmit() {
    if (this.empresaForm.valid) {
      const empresa = this.empresaForm.value;

      if (this.empresaId) {
        // ✅ Atualizar empresa existente
        this.empresaService.atualizarEmpresa(this.empresaId, empresa).subscribe({
          next: () => {
            this.snackBar.open('Empresa atualizada com sucesso!', 'Fechar', { duration: 3000 });
            this.router.navigate(['/dashboard']);
          },
          error: () => this.snackBar.open('Erro ao atualizar empresa', 'Fechar', { duration: 3000 })
        });
      } else {
        // ✅ Criar nova empresa
        this.empresaService.cadastrarEmpresa(empresa).subscribe({
          next: () => {
            this.snackBar.open('Empresa cadastrada com sucesso!', 'Fechar', { duration: 3000 });
            this.router.navigate(['/dashboard']);
          },
          error: () => this.snackBar.open('Erro ao cadastrar empresa', 'Fechar', { duration: 3000 })
        });
      }
    }
  }
}
