# Retail Manager — Backend

API REST completa para gestão de varejo, desenvolvida com Java 21 e Spring Boot 3.2.

## Funcionalidades

- Autenticação e autorização via JWT (Spring Security)
- Cadastro e gestão de clientes, fornecedores e funcionários
- Cadastro de produtos com controle de estoque
- Registro de vendas com múltiplos itens e formas de pagamento
- Registro de compras de fornecedores com atualização automática de estoque
- Movimentação de estoque (entrada e saída)
- Parcelamento de vendas (carnê)
- Integração com Mercado Pago (geração de pagamento e webhook)
- Integração com API de CEP para preenchimento automático de endereço
- Envio de notificações via WhatsApp
- Relatórios de inadimplência, vendas por cliente e formas de pagamento
- Gestão de dados da empresa
- CORS configurado para integração com frontend Angular

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.2.1 |
| Spring Security + JWT (JJWT) | 0.11.5 |
| Spring Data JPA + Hibernate | — |
| PostgreSQL | 15+ |
| MapStruct | 1.5.5 |
| Lombok | — |
| Mercado Pago SDK | 2.1.24 |
| WebFlux (RestClient) | — |
| dotenv-java | 3.0.0 |
| Spring Boot Actuator | — |
| Spring Boot Mail | — |

## Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ rodando na porta 5432

## Como rodar

### 1. Configure o banco de dados

```sql
CREATE DATABASE retail_manager;
```

### 2. Crie o arquivo `.env` na raiz do módulo `back-end/`

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

> Veja `.env.example` para referência completa.

### 3. Execute a aplicação

```bash
cd back-end
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## Endpoints principais

### Autenticação
| Método | URL | Descrição |
|---|---|---|
| POST | `/auth/login` | Login e geração de token JWT |

### Clientes
| Método | URL | Descrição |
|---|---|---|
| POST | `/clientes` | Cadastrar cliente |
| GET | `/clientes` | Listar clientes |
| GET | `/clientes/{id}` | Buscar por ID |
| PUT | `/clientes/{id}` | Atualizar |
| DELETE | `/clientes/{id}` | Remover |

### Produtos
| Método | URL | Descrição |
|---|---|---|
| POST | `/produtos` | Cadastrar produto |
| GET | `/produtos` | Listar produtos |
| GET | `/produtos/{id}` | Buscar por ID |
| PUT | `/produtos/{id}` | Atualizar |
| DELETE | `/produtos/{id}` | Remover |

### Vendas
| Método | URL | Descrição |
|---|---|---|
| POST | `/vendas` | Registrar venda |
| GET | `/vendas` | Listar vendas |
| GET | `/vendas/{id}` | Buscar por ID |

### Compras
| Método | URL | Descrição |
|---|---|---|
| POST | `/compras` | Registrar compra |
| GET | `/compras` | Listar compras |

### Estoque
| Método | URL | Descrição |
|---|---|---|
| GET | `/movimentacoes-estoque` | Listar movimentações |
| POST | `/movimentacoes-estoque` | Registrar movimentação manual |

### Pagamentos
| Método | URL | Descrição |
|---|---|---|
| POST | `/pagamentos` | Gerar pagamento (Mercado Pago) |
| POST | `/pagamentos/webhook` | Receber notificação do Mercado Pago |

### Parcelas
| Método | URL | Descrição |
|---|---|---|
| GET | `/parcelas` | Listar parcelas |
| PUT | `/parcelas/{id}/pagar` | Registrar pagamento de parcela |

### Utilitários
| Método | URL | Descrição |
|---|---|---|
| GET | `/cep/{cep}` | Consultar endereço por CEP |
| POST | `/whatsapp/enviar` | Enviar mensagem via WhatsApp |

## Estrutura do projeto

```
back-end/
├── src/main/java/com/br/isabelaModas/
│   ├── config/          # Configurações: CORS, JWT, Security, PasswordEncoder
│   ├── controller/      # Controllers REST
│   ├── dtos/            # DTOs de request e response
│   ├── entity/          # Entidades JPA
│   ├── exception/       # Tratamento global de exceções
│   ├── mapper/          # Mappers MapStruct
│   ├── repository/      # Interfaces JPA Repository
│   └── service/         # Regras de negócio
└── src/main/resources/
    ├── application.properties
    └── .env.example
```

## Decisões técnicas

- **JWT stateless**: autenticação sem sessão no servidor, escalável horizontalmente
- **MapStruct**: mapeamento entre DTOs e entidades em tempo de compilação — sem reflexão, type-safe
- **dotenv-java**: carrega variáveis de ambiente do `.env` em desenvolvimento sem expor segredos no repositório
- **WebFlux RestClient**: comunicação não-bloqueante com APIs externas (CEP, WhatsApp)
- **Mercado Pago SDK**: integração oficial com suporte a webhook para confirmação assíncrona de pagamentos
