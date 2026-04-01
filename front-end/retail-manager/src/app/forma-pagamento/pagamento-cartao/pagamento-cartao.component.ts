import { FormsModule } from "@angular/forms";
import { FormaPagamentoService } from "../forma-pagamento.service";
import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";

@Component({
  selector: 'app-pagamento-cartao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento-cartao.component.html',
  styleUrls: ['./pagamento-cartao.component.scss']
})
export class PagamentoCartaoComponent {
  numeroParcelas = 1;

  constructor(private formaPagamentoService: FormaPagamentoService) {}

  pagar(vendaId: number, valorTotal: number) {
    const dto = { tipo: 'CREDITO', numeroParcelas: this.numeroParcelas, valorTotal };
    this.formaPagamentoService.criarFormaPagamento(vendaId, dto).subscribe({
      next: () => alert('Pagamento com cartão registrado!'),
      error: () => alert('Erro ao registrar pagamento')
    });
  }
}
