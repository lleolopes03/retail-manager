import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormaPagamentoService } from '../forma-pagamento/forma-pagamento.service';

@Component({
  selector: 'app-parcelas-venda',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatSnackBarModule
  ],
  template: `
    <mat-card>
      <mat-card-header>
        <mat-card-title>Parcelas do Carnê - Venda #{{ vendaId }}</mat-card-title>
      </mat-card-header>
      
      <mat-card-content>
        <table mat-table [dataSource]="parcelas" class="mat-elevation-z8">
          
          <ng-container matColumnDef="numero">
            <th mat-header-cell *matHeaderCellDef> Parcela </th>
            <td mat-cell *matCellDef="let p"> {{ p.numero }} </td>
          </ng-container>

          <ng-container matColumnDef="valor">
            <th mat-header-cell *matHeaderCellDef> Valor </th>
            <td mat-cell *matCellDef="let p"> {{ p.valor | currency:'BRL' }} </td>
          </ng-container>

          <ng-container matColumnDef="vencimento">
            <th mat-header-cell *matHeaderCellDef> Vencimento </th>
            <td mat-cell *matCellDef="let p"> {{ p.dataVencimento | date:'dd/MM/yyyy' }} </td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef> Status </th>
            <td mat-cell *matCellDef="let p">
              <span [ngClass]="{
                'status-pago': p.status === 'PAGO',
                'status-pendente': p.status === 'PENDENTE',
                'status-atrasado': p.status === 'ATRASADO'
              }">
                {{ p.status }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="acoes">
            <th mat-header-cell *matHeaderCellDef> Ações </th>
            <td mat-cell *matCellDef="let p">
              <button 
                mat-raised-button 
                color="primary" 
                *ngIf="p.status === 'PENDENTE'"
                (click)="darBaixa(p.id)">
                Dar Baixa
              </button>
              <button 
                mat-raised-button 
                color="accent" 
                *ngIf="p.status === 'PENDENTE'"
                (click)="gerarLinkParcela(p.id)">
                Gerar Link
              </button>
              <mat-icon *ngIf="p.status === 'PAGO'" color="primary">check_circle</mat-icon>
            </td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
          <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
        </table>
      </mat-card-content>

      <mat-card-actions>
        <button mat-raised-button routerLink="/vendas">Voltar para Vendas</button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: [`
    mat-card {
      margin: 20px;
    }
    table {
      width: 100%;
      margin-top: 20px;
    }
    .status-pago {
      color: green;
      font-weight: bold;
    }
    .status-pendente {
      color: orange;
      font-weight: bold;
    }
    .status-atrasado {
      color: red;
      font-weight: bold;
    }
    button {
      margin-right: 10px;
    }
  `]
})
export class ParcelasVendaComponent implements OnInit {
  vendaId!: number;
  parcelas: any[] = [];
  displayedColumns = ['numero', 'valor', 'vencimento', 'status', 'acoes'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formaPagamentoService: FormaPagamentoService,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.vendaId = +id;
      setTimeout(() => {
        this.carregarParcelas();
      }, 0);
    }
  }

  carregarParcelas(): void {
    this.formaPagamentoService.buscarParcelasPorVenda(this.vendaId).subscribe({
      next: (parcelas) => {
        this.parcelas = parcelas;

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar parcelas:', err);
        this.snackBar.open('Erro ao carregar parcelas', 'Fechar', { duration: 3000 });
        this.cdr.detectChanges();
      }
    });
  }

  darBaixa(parcelaId: number): void {
    if (confirm('Confirma dar baixa nesta parcela? (Cliente pagou em dinheiro/pix direto)')) {
      this.formaPagamentoService.darBaixaParcela(parcelaId).subscribe({
        next: () => {
          this.snackBar.open('Baixa realizada com sucesso!', 'Fechar', { duration: 3000 });
          setTimeout(() => {
            this.carregarParcelas();
          }, 0);
        },
        error: (err) => {
          console.error('Erro ao dar baixa:', err);
          this.snackBar.open('Erro ao dar baixa na parcela', 'Fechar', { duration: 3000 });
        }
      });
    }
  }

  gerarLinkParcela(parcelaId: number): void {
    this.formaPagamentoService.gerarLinkParcela(parcelaId).subscribe({
      next: (res) => {
        const link = res.linkPagamento;
        
        // Copiar link para área de transferência
        navigator.clipboard.writeText(link).then(() => {
          // Mostrar dialog com o link
          const mensagem = `🔗 Link de Pagamento Gerado!\n\n${link}\n\n✅ Link copiado para área de transferência!\n\n💡 Envie pelo WhatsApp ou email para o cliente.`;
          alert(mensagem);
          
          this.snackBar.open('✅ Link copiado!', 'Fechar', { duration: 3000 });

        }).catch(() => {
          // Fallback se clipboard falhar
          alert(`🔗 Link de Pagamento:\n\n${link}\n\n📋 Copie manualmente e envie para o cliente.`);
        });
      },
      error: (err) => {
        console.error('Erro ao gerar link:', err);
        const mensagemErro = err.error?.message || 'Erro desconhecido';
        this.snackBar.open(`❌ Erro ao gerar link: ${mensagemErro}`, 'Fechar', { duration: 5000 });
      }
    });
  }
}
