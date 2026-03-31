# Retail Manager — Frontend

Interface web para gestão de varejo, desenvolvida com Angular 21 e Angular Material.

## Funcionalidades

- Tela de login com autenticação JWT
- Dashboard com resumo de vendas e gráficos (Chart.js)
- Cadastro e listagem de clientes, fornecedores e funcionários
- Cadastro e listagem de produtos com controle de categorias
- Registro de vendas com múltiplos itens e formas de pagamento
- Registro de compras de fornecedores
- Movimentação de estoque
- Parcelamento de vendas — geração de carnê
- Pagamento via Mercado Pago (cartão e PIX)
- Comprovante de venda em PDF (jsPDF)
- Exportação de relatórios para Excel (xlsx)
- Consulta automática de endereço por CEP
- Gestão de dados da empresa
- Rotas protegidas com AuthGuard + interceptor JWT

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Angular | 21 |
| Angular Material + CDK | 21 |
| TypeScript | 5.9 |
| RxJS | 7.8 |
| Chart.js | 4 |
| jsPDF + jspdf-autotable | 4 + 5 |
| xlsx | 0.18 |
| jwt-decode | 4 |

## Pré-requisitos

- Node.js 20+
- npm 10+
- Angular CLI 21+
- Backend rodando em `http://localhost:8080`

## Como rodar

### 1. Instale as dependências

```bash
cd front-end/isabela-modas-frontend
npm install
```

### 2. Execute em modo desenvolvimento

```bash
npm start
```

A aplicação estará disponível em: `http://localhost:4200`

> O `npm start` usa o proxy configurado em `proxy.conf.json` para redirecionar chamadas ao backend em `http://localhost:8080`.

### 3. Build para produção

```bash
npm run build
```

Os arquivos compilados estarão em `dist/retail-manager-frontend/`.

## Estrutura do projeto

```
src/app/
├── auth/                  # Login, AuthGuard, interceptor JWT
├── dashboard/             # Tela inicial com gráficos e resumos
├── cliente/               # Cadastro e listagem de clientes
├── produto/               # Cadastro e listagem de produtos
├── categoria/             # Categorias de produtos
├── fornecedor/            # Fornecedores
├── funcionario/           # Funcionários
├── venda/                 # Registro de vendas
├── compra/                # Registro de compras
├── movimentacao-estoque/  # Movimentações de estoque
├── forma-pagamento/       # Formas de pagamento (cartão, carnê)
├── parcela/               # Controle de parcelas
├── comprovante/           # Geração de comprovante em PDF
├── empresa/               # Dados da empresa
└── relatorio/             # Relatórios e exportação Excel
```

## Decisões técnicas

- **AuthGuard + Interceptor**: o interceptor injeta o token JWT automaticamente em todas as requisições autenticadas; o guard protege rotas que exigem login
- **Proxy de desenvolvimento**: evita problemas de CORS durante o desenvolvimento local sem alterar o build de produção
- **jsPDF**: geração de comprovantes em PDF diretamente no browser, sem dependência de servidor
- **Chart.js**: gráficos leves e responsivos para o dashboard, com dados vindos da API
- **xlsx**: exportação de relatórios para Excel sem precisar de servidor, tudo client-side
