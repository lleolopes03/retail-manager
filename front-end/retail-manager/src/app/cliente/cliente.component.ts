import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ClienteService, Cliente } from './cliente.service';

@Component({
  selector: 'app-cliente',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './cliente.component.html',
  styleUrls: ['./cliente.component.scss']
})
export class ClienteComponent implements OnInit {
  clienteForm;
  clienteId?: number;
  modoEdicao = false;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.clienteForm = this.fb.group({
      nome: ['', Validators.required],
      dataNascimento: ['', Validators.required],
      cpf: ['', Validators.required],
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
    this.clienteId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.clienteId) {
      this.modoEdicao = true;
      this.clienteService.buscarPorId(this.clienteId).subscribe({
        next: (c: Cliente) => this.clienteForm.patchValue(c),
        error: () => alert('Erro ao carregar cliente')
      });
    }
  }
  voltar() {
  this.router.navigate(['/clientes']); // ou ['/dashboard'] se preferir
  }

  buscarCep() {
  const cep = this.clienteForm.get('endereco.cep')?.value;
  if (cep) {
    this.clienteService['http'].get(`http://localhost:8080/cep?cep=${cep}`).subscribe({
      next: (dados: any) => {
        this.clienteForm.patchValue({
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
    if (this.clienteForm.valid) {
      const cliente = this.clienteForm.getRawValue() as Cliente;

      if (this.modoEdicao && this.clienteId) {
        this.clienteService.atualizar(this.clienteId, cliente).subscribe({
          next: () => {
            alert('Cliente atualizado com sucesso!');
            this.router.navigate(['/clientes']);
          },
          error: () => alert('Erro ao atualizar cliente')
        });
      } else {
        this.clienteService.criar(cliente).subscribe({
          next: () => {
            alert('Cliente cadastrado com sucesso!');
            this.router.navigate(['/clientes']);
          },
          error: () => alert('Erro ao cadastrar cliente')
        });
      }
    }
  }
}
