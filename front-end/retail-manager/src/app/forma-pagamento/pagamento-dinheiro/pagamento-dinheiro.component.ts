import { CommonModule } from "@angular/common";
import { FormaPagamentoService } from "../forma-pagamento.service";
import { Component } from "@angular/core";

@Component({
  selector: 'app-pagamento-dinheiro',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pagamento-dinheiro.component.html',
  styleUrls: ['./pagamento-dinheiro.component.scss']
})
export class PagamentoDinheiroComponent {
  constructor(private formaPagamentoService: FormaPagamentoService) {}

  confirmar(vendaId: number, valorTotal: number) {
    const dto = { tipo: 'DINHEIRO', valorTotal };
    this.formaPagamentoService.criarFormaPagamento(vendaId, dto).subscribe({
      next: () => alert('Pagamento em dinheiro registrado!'),
      error: () => alert('Erro ao registrar pagamento')
    });
  }
}
