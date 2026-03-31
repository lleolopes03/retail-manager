import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FuncionarioService, Funcionario } from './funcionario.service';

@Component({
  selector: 'app-funcionario',
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
  templateUrl: './funcionario.component.html',
  styleUrls: ['./funcionario.component.scss']
})
export class FuncionarioComponent implements OnInit {
  funcionarioForm;
  cargos = ['GERENTE', 'VENDEDOR', 'CAIXA','ADMINISTRATIVO'];
  perfis = ['ADMIN', 'VENDEDOR', 'GERENTE_SISTEMA'];
  funcionarioId?: number;
  modoEdicao = false;

  constructor(
    private fb: FormBuilder,
    private funcionarioService: FuncionarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // inicialização com valores padrão (evita null)
    this.funcionarioForm = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      dataContratacao: ['', Validators.required],
      salario: [0, Validators.required],
      cargo: ['', Validators.required],
      login: ['', Validators.required],
      senha: ['', Validators.required],
      perfil: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.funcionarioId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.funcionarioId) {
      this.modoEdicao = true;
      this.funcionarioService.buscarPorId(this.funcionarioId).subscribe({
        next: (f: Funcionario) => this.funcionarioForm.patchValue(f),
        error: () => alert('Erro ao carregar funcionário')
      });
    }
  }
  voltar() {
  this.router.navigate(['/funcionarios']); // ou ['/dashboard'] se preferir
  }

  onSubmit() {
    if (this.funcionarioForm.valid) {
      // ✅ getRawValue + cast garante compatibilidade com a interface
      const funcionario = this.funcionarioForm.getRawValue() as Funcionario;

      if (this.modoEdicao && this.funcionarioId) {
        this.funcionarioService.atualizar(this.funcionarioId, funcionario).subscribe({
          next: () => {
            alert('Funcionário atualizado com sucesso!');
            this.router.navigate(['/funcionarios']);
          },
          error: () => alert('Erro ao atualizar funcionário')
        });
      } else {
        this.funcionarioService.criar(funcionario).subscribe({
          next: () => {
            alert('Funcionário cadastrado com sucesso!');
            this.router.navigate(['/funcionarios']);
          },
          error: () => alert('Erro ao cadastrar funcionário')
        });
      }
    }
  }
}
