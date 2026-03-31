import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { FormaPagamentoService } from "../forma-pagamento.service";

@Component({
  selector: 'app-pagamento-pix',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pagamento-pix.component.html',
  styleUrls: ['./pagamento-pix.component.scss']
})
export class PagamentoPixComponent {
  linkPagamento: string | null | undefined = null;


  constructor(private formaPagamentoService: FormaPagamentoService) {}

  gerarPix(vendaId: number, valorTotal: number) {
    const dto = { tipo: 'PIX', numeroParcelas: 1, valorTotal };
    this.formaPagamentoService.criarFormaPagamento(vendaId, dto).subscribe({
      next: (res) => this.linkPagamento = res.linkPagamento,
      error: () => alert('Erro ao gerar PIX')
    });
  }

  copiar(link: string) {
    navigator.clipboard.writeText(link);
    alert('Link copiado!');
  }
}
