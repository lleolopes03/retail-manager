import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CategoriaService, Categoria } from './categoria.service';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-categoria',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './categoria.component.html',
  styleUrls: ['./categoria.component.scss']
})
export class CategoriaComponent implements OnInit {
  categoriaForm;
  categoriaId?: number;
  modoEdicao = false;

  constructor(
    private fb: FormBuilder,
    private categoriaService: CategoriaService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.categoriaForm = this.fb.nonNullable.group({
      nome: ['', Validators.required],
      descricao: ['']
    });
  }

  ngOnInit(): void {
    this.categoriaId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.categoriaId) {
      this.modoEdicao = true;
      this.categoriaService.buscarPorId(this.categoriaId).subscribe({
        next: (c: Categoria) => this.categoriaForm.patchValue(c),
        error: () => alert('Erro ao carregar categoria')
      });
    }
  }

  onSubmit() {
    if (this.categoriaForm.valid) {
      const categoria: Categoria = this.categoriaForm.getRawValue();

      if (this.modoEdicao && this.categoriaId) {
        this.categoriaService.atualizar(this.categoriaId, categoria).subscribe({
          next: () => {
            alert('Categoria atualizada com sucesso!');
            this.router.navigate(['/categorias']);
          },
          error: () => alert('Erro ao atualizar categoria')
        });
      } else {
        this.categoriaService.criar(categoria).subscribe({
          next: () => {
            alert('Categoria cadastrada com sucesso!');
            this.router.navigate(['/categorias']);
          },
          error: () => alert('Erro ao cadastrar categoria')
        });
      }
    }
  }
  voltar() {
  this.router.navigate(['/categorias']); // ou ['/dashboard'] se preferir
}
}
