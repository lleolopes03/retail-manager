import { Component, OnInit, LOCALE_ID, ChangeDetectorRef } from '@angular/core';
import { RelatoriosService } from './relatorios.service';
import { Chart } from 'chart.js/auto';
import { DateAdapter } from '@angular/material/core';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

// Angular Common
import { CurrencyPipe, DatePipe, NgFor, NgIf, AsyncPipe, registerLocaleData } from '@angular/common';
import localePt from '@angular/common/locales/pt';
import { FormsModule } from '@angular/forms';

// bibliotecas para exportação
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Router } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

registerLocaleData(localePt);

@Component({
  selector: 'app-relatorios',
  standalone: true,
  providers: [{ provide: LOCALE_ID, useValue: 'pt' }],
  imports: [
    MatCardModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    FormsModule,
    CurrencyPipe,
    DatePipe,
    NgIf,
    AsyncPipe
  ],
  templateUrl: './relatorios.component.html',
  styleUrls: ['./relatorios.component.scss']
})
export class RelatoriosComponent implements OnInit {
  totalDiario$!: Observable<number>;
  totalMensal$!: Observable<number>;
  relatoriosClientes$!: Observable<any[]>;
  relatoriosFormaPagamento$!: Observable<any[]>;
  valoresAReceber$!: Observable<any[]>;
  inadimplenciaGeral$!: Observable<any[]>; // 🔹 UNIFICADO
  parcelasAtrasadas$!: Observable<any[]>;

  dataInicio: Date | null = null;
  dataFim: Date | null = null;
  
  isLoading = false;
  
  // 🔹 Dados em memória para exportação
  private dadosClientes: any[] = [];
  private dadosFormaPagamento: any[] = [];
  private dadosValoresAReceber: any[] = [];
  private dadosInadimplenciaGeral: any[] = []; // 🔹 UNIFICADO
  private dadosParcelasAtrasadas: any[] = [];

  private graficoClientes?: Chart;
  private graficoFormaPagamento?: Chart;

  constructor(
    private relatoriosService: RelatoriosService,
    private router: Router,
    private dateAdapter: DateAdapter<Date>,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {
    this.dateAdapter.setLocale('pt-BR');
  }

  ngOnInit(): void {
    setTimeout(() => {
      this.carregarRelatoriosIniciais();
    }, 0);
  }

  carregarRelatoriosIniciais(): void {
    const hoje = new Date().toISOString().split('T')[0];

    this.totalDiario$ = this.relatoriosService.relatorioDiario(hoje);
    this.totalMensal$ = this.relatoriosService.relatorioMensal('2026-01-01', '2026-01-31');

    this.relatoriosClientes$ = this.relatoriosService.relatorioPorCliente('2026-01-01', '2026-01-31');
    this.relatoriosClientes$.subscribe(dados => {
      this.dadosClientes = dados;
      this.montarGraficoClientes(dados);
    });

    this.relatoriosFormaPagamento$ = this.relatoriosService.relatorioPorFormaPagamento('2026-01-01', '2026-01-31');
    this.relatoriosFormaPagamento$.subscribe(dados => {
      this.dadosFormaPagamento = dados;
      this.montarGraficoFormaPagamento(dados);
    });

    this.valoresAReceber$ = this.relatoriosService.relatorioValoresAReceber();
    this.valoresAReceber$.subscribe(dados => this.dadosValoresAReceber = dados);

    // 🔹 Inadimplência Geral (UNIFICADO: vendas + parcelas)
    this.inadimplenciaGeral$ = this.relatoriosService.relatorioInadimplenciaGeral();
    this.inadimplenciaGeral$.subscribe(dados => this.dadosInadimplenciaGeral = dados);

    // 🔹 Parcelas atrasadas (pode falhar se não houver parcelas)
    this.parcelasAtrasadas$ = this.relatoriosService.relatorioParcelasAtrasadas();
    this.parcelasAtrasadas$.subscribe({
      next: (dados) => {
        this.dadosParcelasAtrasadas = dados || [];

        setTimeout(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }, 0);
      },
      error: (err) => {
        console.error('❌ Erro HTTP ao carregar parcelas atrasadas:');
        console.error('Status:', err.status);
        console.error('Mensagem:', err.message);
        console.error('Detalhes:', err.error);
        console.warn('⚠️ Continuando sem parcelas atrasadas...');
        this.dadosParcelasAtrasadas = [];
        setTimeout(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }, 0);
      }
    });
  }

  atualizarRelatorios() {
    if (!this.dataInicio || !this.dataFim) {
      this.snackBar.open('⚠️ Selecione o período de início e fim', 'Fechar', { duration: 3000 });
      return;
    }

    this.isLoading = true;
    const inicioFormatado = this.dataInicio.toISOString().split('T')[0];
    const fimFormatado = this.dataFim.toISOString().split('T')[0];



    this.totalMensal$ = this.relatoriosService.relatorioMensal(inicioFormatado, fimFormatado);

    this.relatoriosClientes$ = this.relatoriosService.relatorioPorCliente(inicioFormatado, fimFormatado);
    this.relatoriosClientes$.subscribe(dados => {

      this.dadosClientes = dados;
      this.montarGraficoClientes(dados);
    });


    this.relatoriosFormaPagamento$ = this.relatoriosService.relatorioPorFormaPagamento(inicioFormatado, fimFormatado);
    this.relatoriosFormaPagamento$.subscribe({
      next: (dados) => {

        this.dadosFormaPagamento = dados;
        this.montarGraficoFormaPagamento(dados);
      },
      error: (err) => {
        console.error('❌ Erro ao buscar forma pagamento:', err);
      }
    });

    this.valoresAReceber$ = this.relatoriosService.relatorioValoresAReceber();
    this.valoresAReceber$.subscribe(dados => this.dadosValoresAReceber = dados);

    // 🔹 Inadimplência Geral (UNIFICADO: vendas + parcelas)
    this.inadimplenciaGeral$ = this.relatoriosService.relatorioInadimplenciaGeral();
    this.inadimplenciaGeral$.subscribe(dados => this.dadosInadimplenciaGeral = dados);

    this.parcelasAtrasadas$ = this.relatoriosService.relatorioParcelasAtrasadas();
    this.parcelasAtrasadas$.subscribe({
      next: (dados) => {
        this.dadosParcelasAtrasadas = dados || [];
        setTimeout(() => {
          this.isLoading = false;
          this.snackBar.open('✅ Relatórios atualizados', 'Fechar', { duration: 2000 });
          this.cdr.detectChanges();
        }, 0);
      },
      error: (err) => {
        console.error('⚠️ Erro ao carregar parcelas atrasadas:', err);
        this.dadosParcelasAtrasadas = [];
        setTimeout(() => {
          this.isLoading = false;
          this.snackBar.open('⚠️ Relatórios atualizados (parcelas atrasadas indisponíveis)', 'Fechar', { duration: 3000 });
          this.cdr.detectChanges();
        }, 0);
      }
    });
  }

  montarGraficoClientes(dados: any[]) {
    if (this.graficoClientes) this.graficoClientes.destroy();
    const labels = dados.map(c => c.nomeCliente);
    const valores = dados.map(c => c.totalComprado);

    this.graficoClientes = new Chart('graficoClientes', {
      type: 'bar',
      data: {
        labels,
        datasets: [{ label: 'Total Comprado por Cliente', data: valores, backgroundColor: '#3f51b5' }]
      },
      options: { responsive: true, plugins: { legend: { display: false } } }
    });
  }

  montarGraficoFormaPagamento(dados: any[]) {

    
    if (this.graficoFormaPagamento) {
      this.graficoFormaPagamento.destroy();
    }
    
    if (!dados || dados.length === 0) {
      console.warn('⚠️ Nenhum dado de forma de pagamento para exibir no gráfico');
      return;
    }
    
    const labels = dados.map(fp => fp.tipo);
    const valores = dados.map(fp => fp.total);
    



    this.graficoFormaPagamento = new Chart('graficoFormaPagamento', {
      type: 'pie',
      data: {
        labels,
        datasets: [{ 
          label: 'Vendas por Forma de Pagamento', 
          data: valores, 
          backgroundColor: ['#3f51b5', '#ff9800', '#4caf50', '#f44336', '#9c27b0', '#673ab7', '#00bcd4'] 
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true, position: 'bottom' }
        }
      }
    });
  }

  exportarExcel() {
    const wb = XLSX.utils.book_new();

    // 🔹 Aba 1: Clientes
    if (this.dadosClientes.length > 0) {
      const wsClientes = XLSX.utils.json_to_sheet(
        this.dadosClientes.map(c => ({
          'Cliente': c.nomeCliente,
          'CPF': c.cpf,
          'Total Comprado': c.totalComprado
        }))
      );
      XLSX.utils.book_append_sheet(wb, wsClientes, 'Clientes');
    }

    // 🔹 Aba 2: Forma de Pagamento
    if (this.dadosFormaPagamento.length > 0) {
      const wsFormas = XLSX.utils.json_to_sheet(
        this.dadosFormaPagamento.map(f => ({
          'Forma de Pagamento': f.tipo,
          'Total': f.total
        }))
      );
      XLSX.utils.book_append_sheet(wb, wsFormas, 'Formas Pagamento');
    }

    // 🔹 Aba 3: Valores a Receber
    if (this.dadosValoresAReceber.length > 0) {
      const wsReceber = XLSX.utils.json_to_sheet(
        this.dadosValoresAReceber.map(v => ({
          'Cliente': v.clienteNome,
          'CPF': v.clienteCpf,
          'Valor': v.valor,
          'Vencimento': v.dataVencimento
        }))
      );
      XLSX.utils.book_append_sheet(wb, wsReceber, 'Valores a Receber');
    }

    // 🔹 Aba 4: Inadimplência Geral (UNIFICADO)
    if (this.dadosInadimplenciaGeral.length > 0) {
      const wsInadim = XLSX.utils.json_to_sheet(
        this.dadosInadimplenciaGeral.map(i => ({
          'Cliente': i.clienteNome,
          'CPF': i.clienteCpf,
          'Tipo': i.tipo,
          'Descrição': i.descricao,
          'Valor': i.valor,
          'Vencimento': i.dataVencimento,
          'Dias Atraso': i.diasAtraso
        }))
      );
      XLSX.utils.book_append_sheet(wb, wsInadim, 'Inadimplência');
    }

    const dataAtual = new Date().toISOString().split('T')[0];
    XLSX.writeFile(wb, `relatorio-completo-${dataAtual}.xlsx`);
    this.snackBar.open('✅ Relatório Excel gerado', 'Fechar', { duration: 2000 });
  }

  voltar() {
    this.router.navigate(['/dashboard']);
  }

  exportarPDF() {
    const doc = new jsPDF();
    let yPos = 20;

    // 🔹 Título
    doc.setFontSize(16);
    doc.text('Relatório Completo de Vendas', 14, yPos);
    yPos += 10;

    doc.setFontSize(10);
    const dataAtual = new Date().toLocaleDateString('pt-BR');
    doc.text(`Data de emissão: ${dataAtual}`, 14, yPos);
    yPos += 10;

    // 🔹 Tabela 1: Clientes
    if (this.dadosClientes.length > 0) {
      doc.setFontSize(12);
      doc.text('Vendas por Cliente', 14, yPos);
      yPos += 5;

      autoTable(doc, {
        head: [['Cliente', 'CPF', 'Total Comprado']],
        body: this.dadosClientes.map(c => [
          c.nomeCliente,
          c.cpf,
          c.totalComprado.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
        ]),
        startY: yPos,
        theme: 'grid',
        headStyles: { fillColor: [63, 81, 181] }
      });
      yPos = (doc as any).lastAutoTable.finalY + 10;
    }

    // 🔹 Tabela 2: Forma de Pagamento
    if (this.dadosFormaPagamento.length > 0) {
      doc.setFontSize(12);
      doc.text('Vendas por Forma de Pagamento', 14, yPos);
      yPos += 5;

      autoTable(doc, {
        head: [['Forma de Pagamento', 'Total']],
        body: this.dadosFormaPagamento.map(f => [
          f.tipo,
          f.total.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
        ]),
        startY: yPos,
        theme: 'grid',
        headStyles: { fillColor: [63, 81, 181] }
      });
      yPos = (doc as any).lastAutoTable.finalY + 10;
    }

    // 🔹 Nova página se necessário
    if (yPos > 250) {
      doc.addPage();
      yPos = 20;
    }

    // 🔹 Tabela 3: Valores a Receber
    if (this.dadosValoresAReceber.length > 0) {
      doc.setFontSize(12);
      doc.text('Valores a Receber', 14, yPos);
      yPos += 5;

      autoTable(doc, {
        head: [['Cliente', 'CPF', 'Valor', 'Vencimento']],
        body: this.dadosValoresAReceber.map(v => [
          v.clienteNome,
          v.clienteCpf,
          v.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }),
          new Date(v.dataVencimento).toLocaleDateString('pt-BR')
        ]),
        startY: yPos,
        theme: 'grid',
        headStyles: { fillColor: [76, 175, 80] }
      });
      yPos = (doc as any).lastAutoTable.finalY + 10;
    }

    // 🔹 Nova página se necessário
    if (yPos > 250) {
      doc.addPage();
      yPos = 20;
    }

    // 🔹 Tabela 4: Inadimplência Geral (UNIFICADO)
    if (this.dadosInadimplenciaGeral.length > 0) {
      doc.setFontSize(12);
      doc.text('Inadimplência Geral (Vendas + Parcelas)', 14, yPos);
      yPos += 5;

      autoTable(doc, {
        head: [['Cliente', 'CPF', 'Tipo', 'Descrição', 'Valor', 'Vencimento', 'Dias']],
        body: this.dadosInadimplenciaGeral.map(i => [
          i.clienteNome,
          i.clienteCpf,
          i.tipo,
          i.descricao,
          i.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }),
          new Date(i.dataVencimento).toLocaleDateString('pt-BR'),
          i.diasAtraso
        ]),
        startY: yPos,
        theme: 'grid',
        headStyles: { fillColor: [244, 67, 54] },
        styles: { fontSize: 8 }
      });
      yPos = (doc as any).lastAutoTable.finalY + 10;
    }

    // 🔹 Nova página se necessário
    if (yPos > 250) {
      doc.addPage();
      yPos = 20;
    }

    // 🔹 Tabela 5: Parcelas Atrasadas (detalhado)
    if (this.dadosParcelasAtrasadas.length > 0) {
      doc.setFontSize(12);
      doc.text('Detalhamento de Parcelas Atrasadas', 14, yPos);
      yPos += 5;

      autoTable(doc, {
        head: [['Cliente', 'CPF', 'Parcela', 'Valor', 'Vencimento', 'Dias Atraso']],
        body: this.dadosParcelasAtrasadas.map(p => [
          p.clienteNome,
          p.clienteCpf,
          p.numero || p.numeroParcela,
          p.valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }),
          new Date(p.dataVencimento).toLocaleDateString('pt-BR'),
          p.diasAtraso
        ]),
        startY: yPos,
        theme: 'grid',
        headStyles: { fillColor: [255, 152, 0] },
        styles: { fontSize: 8 }
      });
    }

    const dataArquivo = new Date().toISOString().split('T')[0];
    doc.save(`relatorio-completo-${dataArquivo}.pdf`);
    this.snackBar.open('✅ Relatório PDF gerado', 'Fechar', { duration: 2000 });
  }
}
