import { Component, OnInit } from '@angular/core';
import { LembreteService, Lembrete } from './lembrete.service';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgClass, DatePipe } from '@angular/common';

@Component({
  selector: 'app-lembretes',
  standalone: true,
  imports: [
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormsModule,
    NgFor,
    NgIf,
    NgClass,
    DatePipe
  ],
  templateUrl: './lembrete.component.html',
  styleUrls: ['./lembrete.component.scss']
})
export class LembreteComponent implements OnInit {
  lembretes: Lembrete[] = [];

  // campos do formulário manual
  novoTitulo: string = '';
  novaDescricao: string = '';
  novaData!: Date;

  constructor(private lembreteService: LembreteService) {}

  ngOnInit(): void {
    this.lembreteService.listarFormasPagamento().subscribe(formas => {
      const vencimentos = this.lembreteService.gerarLembretesDeVencimento(formas);
      this.lembretes = [...this.lembretes, ...vencimentos];
    });
  }

  concluir(lembrete: Lembrete) {
    lembrete.concluido = true;
  }

  adicionarManual() {
    if (this.novoTitulo && this.novaData) {
      const novo: Lembrete = {
        id: Date.now(),
        titulo: this.novoTitulo,
        descricao: this.novaDescricao,
        dataHora: this.novaData,
        tipo: 'manual',
        concluido: false
      };
      this.lembretes.push(novo);

      // limpa formulário
      this.novoTitulo = '';
      this.novaDescricao = '';
      this.novaData = undefined!;
    }
  }
}
