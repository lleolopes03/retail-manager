# Retail Manager

Sistema fullstack completo para gestão de varejo, com back-end em Java/Spring Boot e front-end em Angular. Cobre desde o cadastro de clientes e produtos até vendas com parcelamento, integração com Mercado Pago (PIX e cartão) e geração de comprovantes em PDF.

---

## Arquitetura

```
[Angular 21 :4200]  →  [Spring Boot API :8080]  →  PostgreSQL
                              ↓
                    Mercado Pago / WhatsApp / API CEP
```

| Camada     | Tecnologia principal              | Pasta                     |
|------------|-----------------------------------|---------------------------|
| Front-end  | Angular 21 + Angular Material     | `front-end/retail-manager` |
| Back-end   | Java 21 + Spring Boot 3.2         | `back-end/`               |
| Banco      | PostgreSQL 15+                    | —                         |

---

## Funcionalidades

- Autenticação JWT com rotas protegidas (AuthGuard + interceptor automático)
- Dashboard com resumo de vendas e gráficos (Chart.js)
- Cadastro e gestão de clientes, fornecedores e funcionários
- Cadastro de produtos com categorias e controle de estoque
- Registro de vendas com múltiplos itens e formas de pagamento
- Parcelamento de vendas — geração de carnê
- Pagamento via Mercado Pago (PIX e cartão) com confirmação por webhook
- Registro de compras de fornecedores com atualização automática de estoque
- Movimentação de estoque (entrada e saída manual)
- Geração de comprovante de venda em PDF (jsPDF — client-side)
- Exportação de relatórios para Excel (xlsx — client-side)
- Consulta automática de endereço por CEP
- Envio de notificações via WhatsApp
- Relatórios de inadimplência, vendas por cliente e formas de pagamento
- Gestão de dados da empresa

---

## Tecnologias

### Back-end

| Tecnologia                    | Versão    |
|-------------------------------|-----------|
| Java                          | 21        |
| Spring Boot                   | 3.2.1     |
| Spring Security + JWT (JJWT)  | 0.11.5    |
| Spring Data JPA + Hibernate   | —         |
| PostgreSQL                    | 15+       |
| MapStruct                     | 1.5.5     |
| Mercado Pago SDK              | 2.1.24    |
| WebFlux RestClient            | —         |
| dotenv-java                   | 3.0.0     |
| Spring Boot Actuator          | —         |

### Front-end

| Tecnologia                | Versão |
|---------------------------|--------|
| Angular                   | 21     |
| Angular Material + CDK    | 21     |
| TypeScript                | 5.9    |
| RxJS                      | 7.8    |
| Chart.js                  | 4      |
| jsPDF + jspdf-autotable   | 4 + 5  |
| xlsx                      | 0.18   |
| jwt-decode                | 4      |

---

## Pré-requisitos

- Java 21+
- Maven 3.9+
- Node.js 20+ e npm 10+
- Angular CLI 21+
- PostgreSQL 15+ rodando na porta `5432`

---

## Como rodar

### 1. Clone o repositório

```bash
git clone https://github.com/lleolopes03/retail-manager.git
cd retail-manager
```

### 2. Configure e suba o back-end

```bash
# Crie o banco de dados no PostgreSQL
CREATE DATABASE retail_manager;

# Crie o arquivo .env na pasta back-end/ (veja .env.example)
cd back-end
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

> Veja o [README do back-end](./back-end/README.md) para detalhes das variáveis de ambiente e endpoints.

### 3. Configure e suba o front-end

```bash
cd front-end/retail-manager
npm install
npm start
```

A aplicação estará disponível em: `http://localhost:4200`

> O `npm start` usa proxy configurado em `proxy.conf.json` para redirecionar chamadas ao back-end em `http://localhost:8080`, evitando problemas de CORS em desenvolvimento.

---

## Variáveis de ambiente (back-end)

Crie um arquivo `.env` dentro de `back-end/` com o seguinte conteúdo:

```env
DB_URL=jdbc:postgresql://localhost:5432/retail_manager
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_longa_aqui
JWT_EXPIRATION=86400000

MERCADOPAGO_ACCESS_TOKEN=seu_token_mp
MERCADOPAGO_WEBHOOK_SECRET=seu_secret_webhook

WHATSAPP_API_URL=https://api.whatsapp.com
WHATSAPP_TOKEN=seu_token_whatsapp
```

> Veja `back-end/.env.example` para referência completa.

---

## Estrutura do projeto

```
retail-manager/
├── back-end/
│   ├── src/main/java/com/br/isabelaModas/
│   │   ├── config/          # CORS, JWT, Security, PasswordEncoder
│   │   ├── controller/      # Controllers REST
│   │   ├── dtos/            # DTOs de request e response
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Tratamento global de exceções
│   │   ├── mapper/          # Mappers MapStruct
│   │   ├── repository/      # Interfaces JPA Repository
│   │   └── service/         # Regras de negócio
│   └── README.md
│
└── front-end/retail-manager/
    ├── src/app/
    │   ├── auth/                  # Login, AuthGuard, interceptor JWT
    │   ├── dashboard/             # Tela inicial com gráficos e resumos
    │   ├── cliente/               # Cadastro e listagem de clientes
    │   ├── produto/               # Cadastro e listagem de produtos
    │   ├── categoria/             # Categorias de produtos
    │   ├── fornecedor/            # Fornecedores
    │   ├── funcionario/           # Funcionários
    │   ├── venda/                 # Registro de vendas
    │   ├── compra/                # Registro de compras
    │   ├── movimentacao-estoque/  # Movimentações de estoque
    │   ├── forma-pagamento/       # Formas de pagamento
    │   ├── parcela/               # Controle de parcelas
    │   ├── comprovante/           # Geração de comprovante em PDF
    │   ├── empresa/               # Dados da empresa
    │   └── relatorio/             # Relatórios e exportação Excel
    └── README.md
```

---

## Decisões técnicas

| Decisão | Motivo |
|---|---|
| JWT stateless | Autenticação sem sessão no servidor — escalável horizontalmente |
| MapStruct | Mapeamento type-safe entre DTOs e entidades em tempo de compilação, sem reflexão |
| dotenv-java | Carrega variáveis do `.env` em desenvolvimento sem expor segredos no repositório |
| WebFlux RestClient | Comunicação não-bloqueante com APIs externas (CEP, WhatsApp) |
| Mercado Pago SDK oficial | Integração com suporte a webhook para confirmação assíncrona de pagamentos |
| jsPDF + xlsx client-side | Geração de PDF e Excel no navegador — sem dependência de servidor para relatórios |
| Proxy Angular | Evita problemas de CORS em desenvolvimento sem alterar o build de produção |

---

## Documentação detalhada

- [README — Back-end](./back-end/README.md)
- [README — Front-end](./front-end/retail-manager/README.md)

---

## Autor

Desenvolvido por [Leonardo Lopes](https://github.com/lleolopes03).
