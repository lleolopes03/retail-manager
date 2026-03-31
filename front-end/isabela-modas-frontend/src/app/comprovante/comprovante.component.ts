import { ChangeDetectionStrategy, Component, Input, NgZone, OnInit } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe, CurrencyPipe, DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import jsPDF from 'jspdf';
import autoTable, { RowInput } from 'jspdf-autotable';
import { EmpresaService } from '../empresa/empresa.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { PagamentoService } from '../pagamento/pagamento.service';
import { FormaPagamentoService } from '../forma-pagamento/forma-pagamento.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-comprovante',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatCardModule, MatButtonModule, MatSnackBarModule, CurrencyPipe, DatePipe, NgFor, NgIf, AsyncPipe, NgClass],
  templateUrl: './comprovante.component.html',
  styleUrls: ['./comprovante.component.scss']
})
export class ComprovanteComponent implements OnInit {
  @Input() venda: any;
  empresa$!: Observable<any>;
  parcelas: any[] = [];

  constructor(
    private empresaService: EmpresaService,
    private pagamentoService: PagamentoService,
    private formaPagamentoService: FormaPagamentoService,
    private router: Router,
    private cdRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private ngZone: NgZone,
    private snackBar: MatSnackBar
  ) {
    this.empresa$ = this.empresaService.buscarEmpresa();
  }

    ngOnInit() {
  if (!this.venda) {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.pagamentoService.buscarVenda(+id).subscribe(v => {
        this.venda = v;








        // Dinheiro - pagamento à vista aprovado
        if (this.venda.tipoPagamento === 'DINHEIRO' || this.venda.formaPagamento?.tipo === 'DINHEIRO') {

          this.venda.linkPagamento = '';
          this.venda.statusPagamento = 'PAGO';
          this.cdRef.markForCheck();
        }

        // Carnê - buscar parcelas
        else if (this.venda.tipoPagamento === 'CARNE' || this.venda.formaPagamento?.tipo === 'CARNE') {

          this.formaPagamentoService.buscarParcelasPorVenda(this.venda.id).subscribe({
            next: (parcelas) => {
              this.parcelas = parcelas;

              this.cdRef.markForCheck();
            },
            error: (err) => {
              console.error('✗ Erro ao buscar parcelas:', err);
              this.cdRef.markForCheck();
            }
          });
        }

        // Crédito / Débito / Pix - apenas consultar status
        else {

          
          // Se já tem link de pagamento na venda, usa ele
          if (this.venda.linkPagamento) {

            this.cdRef.markForCheck();
          } else {

            // Apenas consulta o status sem tentar criar pagamento
            this.pagamentoService.consultarStatus(this.venda.id).subscribe({
              next: (res) => {
                this.venda.statusPagamento = res.statusPagamento || 'PENDENTE';

                this.cdRef.markForCheck();
              },
              error: (err) => {

                this.venda.statusPagamento = 'PENDENTE';
                this.cdRef.markForCheck();
              }
            });
          }
        }

        this.cdRef.markForCheck();
      });
    }
  }
}

  voltar() {
    this.router.navigate(['/dashboard']);
  }

  gerarLinkPagamento() {
    const dto = {
      vendaId: this.venda.id,
      tipo: this.venda.tipoPagamento,
      titulo: `Venda nº ${this.venda.id}`,
      valorTotal: this.venda.valorTotal,
      numeroParcelas: this.venda.numeroParcelas || this.venda.formaPagamento?.numeroParcelas || 1
    };



    this.pagamentoService.criarPagamento(dto).subscribe({
      next: (res) => {

        this.venda.linkPagamento = res.linkPagamento;
        this.venda.statusPagamento = res.statusPagamento || 'PENDENTE';
        this.snackBar.open('Link de pagamento gerado com sucesso!', 'Fechar', { duration: 3000 });
        this.cdRef.markForCheck();
      },
      error: (err) => {
        console.error('Erro ao gerar link:', err);
        this.snackBar.open('Erro ao gerar link de pagamento: ' + (err.error?.erro || err.message), 'Fechar', { duration: 5000 });
      }
    });
  }

  gerarLinkParcela(parcela: any) {

    
    this.formaPagamentoService.gerarLinkParcela(parcela.id).subscribe({
      next: (res) => {

        parcela.linkPagamento = res.linkPagamento;
        this.snackBar.open(`Link da parcela ${parcela.numero} gerado com sucesso!`, 'Fechar', { duration: 3000 });
        this.cdRef.markForCheck();
      },
      error: (err) => {
        console.error('Erro ao gerar link da parcela:', err);
        this.snackBar.open('Erro ao gerar link da parcela: ' + (err.error?.erro || err.message), 'Fechar', { duration: 5000 });
      }
    });
  }


  exportarPDF(empresa: any) {






    
    const doc = new jsPDF();

    // Cabeçalho da empresa
    doc.setFontSize(14);
    doc.setFont('helvetica', 'bold');
    doc.text(empresa.nome, 14, 20);

    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.text(`CNPJ: ${empresa.cnpj}`, 14, 26);
    doc.text(
      `Endereço: ${empresa.endereco.logradouro}, ${empresa.numero} - ${empresa.endereco.bairro}, ${empresa.endereco.localidade}/${empresa.endereco.uf} - CEP: ${empresa.endereco.cep}`,
      14,
      32
    );
    doc.text(`Telefone: ${empresa.telefone} | Email: ${empresa.email}`, 14, 38);

    // Dados do cliente
    doc.text(`Cliente: ${this.venda.cliente?.nome}`, 14, 44);
    doc.text(`CPF: ${this.venda.cliente?.cpf}`, 14, 50);
    doc.text(`Telefone: ${this.venda.cliente?.telefone} | Email: ${this.venda.cliente?.email}`, 14, 56);

    // Dados da venda
    doc.text(`Comprovante Nº: ${this.venda.id}`, 14, 64);
    doc.text(`Data: ${new Date(this.venda.dataVenda).toLocaleDateString()}`, 14, 70);
    doc.text(`Forma de Pagamento: ${this.venda.formaPagamento?.tipo || this.venda.tipoPagamento || 'PIX'}`, 14, 76);

    let currentY = 82;

    // Informações de parcelamento
    if (this.venda.formaPagamento?.tipo === 'CREDITO' && this.venda.formaPagamento?.numeroParcelas > 1) {
      doc.text(`Parcelado em ${this.venda.formaPagamento.numeroParcelas}x no cartão de crédito`, 14, currentY);
      currentY += 6;
    }

    if (this.venda.formaPagamento?.tipo === 'CARNE' && this.parcelas.length > 0) {
      doc.text(`Parcelado em ${this.parcelas.length}x no carnê`, 14, currentY);
      currentY += 6;
    }

    // Tabela de itens
    autoTable(doc, {
      head: [['Produto', 'Qtd', 'Preço Unitário', 'Total']],
      body: this.venda.itens.map((item: any): RowInput => [
        item.produtoNome,
        item.quantidade,
        item.precoUnitario.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }),
        item.subtotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
      ]),
      startY: currentY,
      theme: 'grid',
      headStyles: {
        fillColor: [220, 220, 220],
        textColor: [0, 0, 0],
        fontStyle: 'bold',
        halign: 'center'
      },
      bodyStyles: {
        textColor: [50, 50, 50],
        fontSize: 11
      },
      columnStyles: {
        0: { halign: 'left' },
        1: { halign: 'center' },
        2: { halign: 'right' },
        3: { halign: 'right' }
      }
    });

    let finalY = (doc as any).lastAutoTable?.finalY || currentY;

    // Total
    doc.setFontSize(12);
    doc.setFont('helvetica', 'bold');
    doc.text(
      `Total: ${this.venda.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}`,
      14,
      finalY + 10
    );

    finalY += 20;

    // Tabela de parcelas do carnê
    const isCarne = this.venda.tipoPagamento === 'CARNE' || 
                    this.venda.formaPagamento?.tipo === 'CARNE' ||
                    (this.parcelas && this.parcelas.length > 0);
    





    
    if (isCarne && this.parcelas && this.parcelas.length > 0) {

      
      // Informações do parcelamento
      doc.setFontSize(11);
      doc.setFont('helvetica', 'bold');
      doc.text(`Forma de Pagamento: Carnê`, 14, finalY);
      finalY += 6;
      
      doc.setFont('helvetica', 'normal');
      doc.text(`Número de Parcelas: ${this.parcelas.length}x`, 14, finalY);
      finalY += 6;
      
      doc.text(`Valor de cada parcela: ${this.parcelas[0].valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}`, 14, finalY);
      finalY += 10;
      
      doc.setFontSize(12);
      doc.setFont('helvetica', 'bold');
      doc.text('Datas de Vencimento:', 14, finalY);
      finalY += 6;

      autoTable(doc, {
        head: [['Parcela', 'Valor', 'Vencimento', 'Status']],
        body: this.parcelas.map((parcela: any): RowInput => [
          `${parcela.numero}/${this.parcelas.length}`,
          parcela.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }),
          new Date(parcela.dataVencimento).toLocaleDateString('pt-BR'),
          parcela.status
        ]),
        startY: finalY,
        theme: 'grid',
        headStyles: {
          fillColor: [220, 220, 220],
          textColor: [0, 0, 0],
          fontStyle: 'bold',
          halign: 'center'
        },
        bodyStyles: {
          textColor: [50, 50, 50],
          fontSize: 10
        },
        columnStyles: {
          0: { halign: 'center' },
          1: { halign: 'right' },
          2: { halign: 'center' },
          3: { halign: 'center' }
        }
      });

      finalY = (doc as any).lastAutoTable?.finalY || finalY;
    } else if (this.venda.tipoPagamento === 'CARNE' || this.venda.formaPagamento?.tipo === 'CARNE') {

      
      // Mensagem de aviso se for carnê mas não tem parcelas
      doc.setFontSize(11);
      doc.setFont('helvetica', 'italic');
      doc.setTextColor(255, 0, 0);
      doc.text('⚠️ Parcelas não carregadas. Entre em contato com o suporte.', 14, finalY);
      doc.setTextColor(0, 0, 0);
      finalY += 10;
    }

    // Status do pagamento
    if (this.venda.statusPagamento) {
      doc.setFontSize(11);
      doc.setFont('helvetica', 'bold');
      let statusTexto = '';

      switch (this.venda.statusPagamento) {
        case 'PAGO': statusTexto = 'Pagamento Aprovado'; break;
        case 'PENDENTE': statusTexto = 'Pagamento Pendente'; break;
        case 'REJEITADO': statusTexto = 'Pagamento Rejeitado'; break;
        case 'EM_PROCESSO': statusTexto = 'Pagamento em Processamento'; break;
        case 'CANCELADO': statusTexto = 'Pagamento Cancelado'; break;
      }

      doc.text(`Status do Pagamento: ${statusTexto}`, 14, finalY + 10);
      finalY += 10;
    }

    // Link de pagamento visível no PDF (exceto dinheiro e carnê)
    if (this.venda.linkPagamento && this.venda.tipoPagamento !== 'DINHEIRO' && this.venda.formaPagamento?.tipo !== 'CARNE') {
      doc.setFontSize(11);
      doc.setTextColor(0, 0, 255);
      doc.textWithLink(
        'Clique aqui para pagar',
        14,
        finalY + 10,
        { url: this.venda.linkPagamento }
      );
      doc.setTextColor(0, 0, 0);
      finalY += 10;
    }

    // Rodapé
    doc.setFontSize(10);
    doc.setFont('helvetica', 'normal');
    doc.text('Obrigado pela preferência! Volte sempre à Isabela Modas.', 14, finalY + 10);

    doc.save(`comprovante-${this.venda.id}.pdf`);
  }

  imprimir(empresa: any) {
    const conteudo = document.getElementById('comprovante')?.innerHTML;
    if (conteudo) {
      const janela = window.open('', '', 'width=800,height=600');
      janela?.document.write(`
        <html>
          <head>
            <title>Comprovante</title>
            <style>
              body { font-family: Arial, sans-serif; padding: 20px; }
              h2 { margin-bottom: 10px; text-align: center; }
              .empresa-info, .cliente-info, .venda-info { margin-bottom: 15px; }
              table { width: 100%; border-collapse: collapse; margin-top: 20px; }
              th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }
              th { background-color: #f5f5f5; font-weight: bold; }
              h3 { text-align: right; margin-top: 15px; }
            </style>
          </head>
          <body>
            ${conteudo}
          </body>
        </html>
      `);
      janela?.document.close();
      janela?.print();
    }
  }

  enviarWhatsApp() {
  const cliente = this.venda?.cliente;
  const telefone = cliente?.telefone;

  if (!telefone) {
    alert('Telefone do cliente não encontrado!');
    return;
  }

  let mensagem = `Olá ${cliente?.nome}, segue seu comprovante da compra nº ${this.venda.id} no valor de ${this.venda.valorTotal.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}.`;

  // Se for carnê, inclui informações das parcelas
  if ((this.venda.tipoPagamento === 'CARNE' || this.venda.formaPagamento?.tipo === 'CARNE') && this.parcelas.length > 0) {
    mensagem += `\n\nPagamento parcelado em ${this.parcelas.length}x no carnê:`;
    this.parcelas.forEach((parcela: any) => {
      mensagem += `\nParcela ${parcela.numero}: ${parcela.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} - Vencimento: ${new Date(parcela.dataVencimento).toLocaleDateString('pt-BR')}`;
    });
    mensagem += `\n\nVocê receberá o link de pagamento de cada parcela no dia do vencimento.`;
  }

  // Se for crédito parcelado
  else if (this.venda.formaPagamento?.tipo === 'CREDITO' && this.venda.formaPagamento?.numeroParcelas > 1) {
    mensagem += `\n\nPagamento parcelado em ${this.venda.formaPagamento.numeroParcelas}x no cartão de crédito.`;
  }

  // Se não for dinheiro nem carnê, e tiver link de pagamento
  if (this.venda.linkPagamento && this.venda.tipoPagamento !== 'DINHEIRO' && this.venda.formaPagamento?.tipo !== 'CARNE') {
    mensagem += `\n\nPara concluir o pagamento, acesse: ${this.venda.linkPagamento}`;
  }
  // Se não tiver link mas for pagamento eletrônico
  else if (!this.venda.linkPagamento && this.venda.tipoPagamento !== 'DINHEIRO' && this.venda.formaPagamento?.tipo !== 'CARNE') {
    mensagem += `\n\nO link de pagamento será enviado em breve.`;
  }

  // Sempre inclui status do pagamento
  if (this.venda.statusPagamento) {
    let statusTexto = '';
    switch (this.venda.statusPagamento) {
      case 'PAGO': statusTexto = 'Pagamento Aprovado'; break;
      case 'PENDENTE': statusTexto = 'Pagamento Pendente'; break;
      case 'REJEITADO': statusTexto = 'Pagamento Rejeitado'; break;
      case 'EM_PROCESSO': statusTexto = 'Pagamento em Processamento'; break;
      case 'CANCELADO': statusTexto = 'Pagamento Cancelado'; break;
    }
    mensagem += `\n\nStatus do pagamento: ${statusTexto}`;
  }

  const mensagemCodificada = encodeURIComponent(mensagem);
  window.open(`https://wa.me/${telefone}?text=${mensagemCodificada}`, '_blank');
}
}
