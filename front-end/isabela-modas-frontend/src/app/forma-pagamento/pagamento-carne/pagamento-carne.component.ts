import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { FormaPagamentoService, ParcelaDto, RelatorioParcelasAtrasadasDto } from "../forma-pagamento.service";
import { Component } from "@angular/core";

@Component({
  selector: 'app-pagamento-carne',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento-carne.component.html',
  styleUrls: ['./pagamento-carne.component.scss']
})
export class PagamentoCarneComponent {
  numeroParcelas = 6;
  primeiraParcela = new Date().toISOString().split('T')[0];
  intervaloDias = 30;

  parcelas: ParcelaDto[] = [];
  parcelasAtrasadas: RelatorioParcelasAtrasadasDto[] = [];

  constructor(private formaPagamentoService: FormaPagamentoService) {}

 gerarCarne(vendaId: number, valorTotal: number) {
  const dto = {
    tipo: 'CARNE',
    numeroParcelas: this.numeroParcelas,
    primeiraParcela: this.primeiraParcela, // yyyy-MM-dd
    intervaloDias: this.intervaloDias,
    valorTotal: valorTotal
  };

  this.formaPagamentoService.criarFormaPagamento(vendaId, dto).subscribe({
    next: () => alert('Carnê gerado com sucesso!'),
    error: (err) => {
      console.error(err);
      alert('Erro ao gerar carnê');
    }
  });
}

  listarParcelas(vendaId: number) {
    this.formaPagamentoService.buscarParcelasPorVenda(vendaId).subscribe({
      next: (data) => this.parcelas = data,
      error: () => alert('Erro ao buscar parcelas')
    });
  }

  pagarParcela(parcelaId: number) {
    this.formaPagamentoService.gerarLinkParcela(parcelaId).subscribe({
      next: (res) => window.open(res.linkPagamento, '_blank'),
      error: () => alert('Erro ao gerar link de pagamento')
    });
  }

  listarParcelasAtrasadas() {
    this.formaPagamentoService.buscarParcelasAtrasadas().subscribe({
      next: (data) => this.parcelasAtrasadas = data,
      error: () => alert('Erro ao buscar parcelas atrasadas')
    });
  }

  cobrarParcela(parcelaId: number, numeroCliente: string) {
    this.formaPagamentoService.enviarCobrancaWhatsApp(parcelaId, numeroCliente).subscribe({
      next: () => alert('Cobrança enviada pelo WhatsApp!'),
      error: () => alert('Erro ao enviar cobrança')
    });
  }
}
