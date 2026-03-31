import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';

// Produtos
import { ProdutoComponent } from './produto/produto.component';
import { ProdutosListaComponent } from './produto/produtos-lista.component';

// Outros módulos
import { FuncionarioComponent } from './funcionario/funcionario.component';
import { FuncionariosListaComponent } from './funcionario/funcionarios-lista.component';
import { ClienteComponent } from './cliente/cliente.component';
import { ClientesListaComponent } from './cliente/clientes-lista.component';
import { FornecedorComponent } from './fornecedor/fornecedor.component';
import { FornecedoresListaComponent } from './fornecedor/fornecedor-lista.component';
import { EmpresaComponent } from './empresa/empresa.component';
import { EmpresaVisualizarComponent } from './empresa/empresa-visualizar.component';


// Compras
import { ComprasListaComponent } from './compra/compra-lista.component';
import { CompraComponent } from './compra/compra.component';

// Vendas
import { VendasListaComponent } from './venda/vendas-lista-component';
import { VendaComponent } from './venda/venda.component';
import { ParcelasVendaComponent } from './venda/parcelas-venda.component';

// Categorias
import { CategoriaComponent } from './categoria/categoria.component';
import { CategoriasListaComponent } from './categoria/categoria-lista.component';

// Movimentações de Estoque
import { MovimentacoesListaComponent } from './movimentacao-estoque/movimentacoes-lista.component';
import { MovimentacaoEstoqueComponent } from './movimentacao-estoque/movimentacao-estoque.component';

// Relatórios
import { RelatoriosComponent } from './relatorios/relatorios.component';
import { RelatorioEstoqueComponent } from './relatorio-estoque/relatorio-estoque.component';

// Outros
import { LembreteComponent } from './lembrete/lembrete.component';
import { ComprovanteComponent } from './comprovante/comprovante.component';



import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },

      // Funcionários
      { path: 'funcionarios', component: FuncionariosListaComponent },
      { path: 'funcionarios/novo', component: FuncionarioComponent },
      { path: 'funcionarios/:id/editar', component: FuncionarioComponent },

      // Clientes
      { path: 'clientes', component: ClientesListaComponent },
      { path: 'clientes/novo', component: ClienteComponent },
      { path: 'clientes/:id/editar', component: ClienteComponent },

      // Fornecedores
      { path: 'fornecedores', component: FornecedoresListaComponent },
      { path: 'fornecedores/novo', component: FornecedorComponent },
      { path: 'fornecedores/:id/editar', component: FornecedorComponent },

      // Produtos
      { path: 'produtos', component: ProdutosListaComponent },
      { path: 'produtos/novo', component: ProdutoComponent },
      { path: 'produtos/:id/editar', component: ProdutoComponent },

      // Categorias
      { path: 'categorias', component: CategoriasListaComponent },
      { path: 'categorias/novo', component: CategoriaComponent },
      { path: 'categorias/:id/editar', component: CategoriaComponent },

      // Compras
      { path: 'compras', component: ComprasListaComponent },
      { path: 'compras/novo', component: CompraComponent },
      { path: 'compras/:id/editar', component: CompraComponent },

      // Vendas
      { path: 'vendas', component: VendasListaComponent },
      { path: 'vendas/novo', component: VendaComponent },
      { path: 'vendas/:id/editar', component: VendaComponent },
      { path: 'vendas/:id/parcelas', component: ParcelasVendaComponent },

      // Movimentações de Estoque
      { path: 'movimentacoes-estoque', component: MovimentacoesListaComponent },
      { path: 'movimentacoes-estoque/nova', component: MovimentacaoEstoqueComponent },

      // Relatórios
      { path: 'relatorios', component: RelatoriosComponent },
      { path: 'relatorio-estoque', component: RelatorioEstoqueComponent },

      // empresa
      { path: 'empresas/novo', component: EmpresaComponent },
      { path: 'empresas/editar', component: EmpresaComponent },
      { path: 'empresa-visualizar', component: EmpresaVisualizarComponent },

      // outros
      { path: 'lembretes', component: LembreteComponent },
      { path: 'comprovante/:id', component: ComprovanteComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
