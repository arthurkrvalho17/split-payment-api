# Split Payment API

API de processamento de pagamentos com divisão automática entre múltiplos recebedores. Construída com arquitetura hexagonal, eventos assíncronos via Kafka e persistência PostgreSQL.

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [API Reference](#api-reference)
- [Fluxo de Processamento](#fluxo-de-processamento)
- [Schema do Banco de Dados](#schema-do-banco-de-dados)
- [Eventos Kafka](#eventos-kafka)

---

## Visão Geral

A **Split Payment API** é um sistema de PSP (Payment Service Provider) que permite configurar regras de divisão de pagamentos entre merchants e recebedores. Ao processar uma transação, o sistema distribui automaticamente o valor entre os recebedores conforme as regras cadastradas, gerando um trail de auditoria via eventos Kafka.

**Casos de uso principais:**

- Cadastrar merchants e recebedores
- Configurar regras de split (taxa, imposto, comissão, repasse)
- Processar transações com divisão automática
- Consultar detalhes de transações e splits

---

## Tecnologias

| Categoria | Tecnologia |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Linguagem | Java 21 |
| Banco de Dados | PostgreSQL 16 |
| Migrations | Flyway |
| Message Broker | Apache Kafka (Confluent 7.4.0) |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Containerização | Docker / Docker Compose |
| Utilitários | Lombok, Jakarta Validation |

---

## Arquitetura

O projeto segue a **Arquitetura Hexagonal** (Ports & Adapters), com separação clara entre as camadas de domínio, aplicação e infraestrutura.

```
┌─────────────────────────────────────────────────────┐
│                    INFRAESTRUTURA                    │
│                                                     │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │ Controllers │  │  Persistence │  │  Messaging │ │
│  │   (REST)    │  │ (JPA/Adapters│  │  (Kafka)   │ │
│  └──────┬──────┘  └──────┬───────┘  └─────┬──────┘ │
│         │                │                │         │
├─────────┼────────────────┼────────────────┼─────────┤
│         ▼                │                │         │
│     APLICAÇÃO            │                │         │
│                          │                │         │
│  ┌────────────────────┐  │                │         │
│  │      Services      │  │                │         │
│  │ (Regras de Negócio)│  │                │         │
│  └────────┬───────────┘  │                │         │
│           │              │                │         │
├───────────┼──────────────┼────────────────┼─────────┤
│           ▼              ▼                │         │
│                  DOMÍNIO                  │         │
│                                           │         │
│  ┌──────────┐  ┌────────────┐  ┌───────┐ │         │
│  │  Models  │  │Repositories│  │Enums  │ │         │
│  │ (Entities│  │(Interfaces)│  │       │ │         │
│  └──────────┘  └────────────┘  └───────┘ │         │
└─────────────────────────────────────────────────────┘
```

**Padrões utilizados:**
- **Repository Pattern** — abstrações de acesso a dados no domínio
- **Adapter Pattern** — adaptadores de persistência implementam as interfaces do domínio
- **Mapper Pattern** — conversão centralizada entre entidades JPA e modelos de domínio
- **Event-Driven** — eventos publicados em Kafka para auditoria e extensibilidade
- **Service Layer** — lógica de negócio isolada nos serviços de aplicação

---

## Estrutura do Projeto

```
src/main/java/com/psp/split_payment_api/
│
├── application/
│   └── service/               # Serviços de aplicação (regras de negócio)
│       ├── MerchantService
│       ├── RecipientService
│       ├── SplitRuleService
│       └── TransactionService
│
├── domain/
│   ├── exception/             # Exceções de domínio
│   │   ├── NotFoundException (base)
│   │   ├── MerchantNotFoundException
│   │   ├── RecipientNotFoundException
│   │   ├── SplitRuleNotFoundException
│   │   └── TransactionNotFoundException
│   ├── model/                 # Modelos de domínio
│   │   ├── Merchant
│   │   ├── Recipient
│   │   ├── SplitRule
│   │   ├── Transaction
│   │   ├── SplitEntry
│   │   ├── PaymentEvent
│   │   ├── SplitType (enum)
│   │   ├── TransactionStatus (enum)
│   │   └── EventType (enum)
│   └── repository/            # Interfaces de repositório
│
└── infra/
    ├── config/                # Configurações (Kafka)
    ├── controller/            # Controllers REST + GlobalExceptionHandler
    ├── dto/                   # Request e Response DTOs
    ├── mapper/                # Mapeadores domínio <-> entidade JPA
    ├── messaging/             # Producer e Consumer Kafka
    └── persistence/           # Entidades JPA + Adaptadores + JPA Repositories
```

---

## Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21
- Maven 3.8+

### 1. Subir a infraestrutura

```bash
docker-compose up -d
```

Isso irá inicializar:
- **PostgreSQL 16** — `localhost:5432` (user: `split`, password: `split`, db: `psp`)
- **Kafka** — `localhost:9092`
- **Zookeeper** — `localhost:2181`
- **PgAdmin 4** — `localhost:5050` (email: `admin@admin.com`, password: `admin`)

### 2. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação sobe na porta **8080**.

As migrations Flyway são executadas automaticamente ao iniciar, criando todas as tabelas necessárias.

---

## API Reference

### Merchant

#### Criar Merchant

```http
POST /merchant
Content-Type: application/json

{
  "name": "Loja do João",
  "document": "12345678000190"
}
```

**Response `201 Created`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Loja do João",
  "document": "12345678000190",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

---

### Recipient

#### Criar Recebedor

```http
POST /recipient
Content-Type: application/json

{
  "name": "Maria Silva",
  "document": "98765432100",
  "bankAccount": "12345-6"
}
```

**Response `201 Created`:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Maria Silva",
  "document": "98765432100",
  "bankAccount": "12345-6",
  "createdAt": "2024-01-15T10:31:00Z"
}
```

---

### Split Rule

#### Criar Regra de Split

Configura a porcentagem que um recebedor receberá nas transações de um merchant.

```http
POST /splitrule
Content-Type: application/json

{
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "recipientId": "660e8400-e29b-41d4-a716-446655440001",
  "percent": 15,
  "type": "COMMISSION"
}
```

**Tipos disponíveis (`type`):**

| Valor | Descrição |
|---|---|
| `FEE` | Taxa de serviço |
| `TAX` | Imposto |
| `COMMISSION` | Comissão |
| `TRANSFER` | Repasse |

**Response `201 Created`:**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440002",
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "recipientId": "660e8400-e29b-41d4-a716-446655440001",
  "percent": 15,
  "type": "COMMISSION",
  "createdAt": "2024-01-15T10:32:00Z"
}
```

---

### Transaction

#### Processar Transação

Cria e processa uma transação, aplicando automaticamente as regras de split do merchant.

```http
POST /transaction
Content-Type: application/json

{
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "recipientId": "660e8400-e29b-41d4-a716-446655440001",
  "amountCents": 10000
}
```

> Os valores são em **centavos** (ex: `10000` = R$ 100,00)

**Response `201 Created`:**
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "merchantId": "550e8400-e29b-41d4-a716-446655440000",
  "amountCents": 10000,
  "status": "COMPLETED",
  "splits": [
    {
      "recipientId": "660e8400-e29b-41d4-a716-446655440001",
      "percentualApplied": 15,
      "type": "COMMISSION",
      "amountCents": 1500
    },
    {
      "recipientId": "550e8400-e29b-41d4-a716-446655440000",
      "percentualApplied": 85,
      "type": "TRANSFER",
      "amountCents": 8500
    }
  ],
  "createdAt": "2024-01-15T10:33:00Z"
}
```

**Status possíveis:**

| Status | Descrição |
|---|---|
| `PENDING` | Transação criada, aguardando processamento |
| `COMPLETED` | Splits calculados com sucesso |
| `FAILED` | Falha no processamento |

---

#### Consultar Detalhes da Transação

```http
GET /transaction/{id}
```

**Response `200 OK`** (texto formatado):
```
Transação: 880e8400-e29b-41d4-a716-446655440003
Valor Total: R$ 100,00
Status: COMPLETED

Splits:
  - Maria Silva | 15% | COMMISSION | R$ 15,00
  - Loja do João | 85% | TRANSFER  | R$ 85,00
```

---

### Erros

Todas as respostas de erro seguem o formato:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Merchant not found: 550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

| HTTP Status | Situação |
|---|---|
| `400 Bad Request` | Campos obrigatórios ausentes ou inválidos |
| `404 Not Found` | Merchant, Recipient, SplitRule ou Transaction não encontrado |

---

## Fluxo de Processamento

Ao receber uma requisição `POST /transaction`, o seguinte fluxo é executado de forma atômica (`@Transactional`):

```
POST /transaction
      │
      ▼
1. Busca Merchant (lança MerchantNotFoundException se não encontrado)
      │
      ▼
2. Cria Transaction com status PENDING
      │
      ▼
3. Publica evento TRANSACTION_CREATED no Kafka
      │
      ▼
4. Busca SplitRules do Merchant
      │
      ▼
5. Para cada SplitRule:
   ├── Calcula: amount = round((percent * totalAmount) / 100)
   ├── Cria SplitEntry
   └── Publica evento SPLIT_EXECUTED no Kafka
      │
      ▼
6. Cria SplitEntry para o Merchant (saldo líquido)
   └── percentual = 100% - soma dos percentuais anteriores
      │
      ▼
7. Atualiza Transaction para status COMPLETED
      │
      ▼
8. Publica evento TRANSACTION_COMPLETED no Kafka
      │
      ▼
9. Retorna TransactionResponse com todos os splits
```

---

## Schema do Banco de Dados

As tabelas são criadas automaticamente via **Flyway** ao iniciar a aplicação.

```
┌───────────────────┐        ┌───────────────────┐
│     merchant      │        │     recipient     │
├───────────────────┤        ├───────────────────┤
│ id          UUID  │        │ id          UUID  │
│ name        TEXT  │        │ name        TEXT  │
│ document    TEXT  │        │ document    TEXT  │
│ created_at  TSTZ  │        │ bank_account TEXT  │
└────────┬──────────┘        │ created_at  TSTZ  │
         │                   └────────┬──────────┘
         │                            │
         ▼                            ▼
┌───────────────────────────────────────────────────┐
│                    split_rule                     │
├───────────────────────────────────────────────────┤
│ id           UUID                                 │
│ merchant_id  UUID  FK → merchant.id               │
│ recipient_id UUID  FK → recipient.id              │
│ percent      INT                                  │
│ type         TEXT  (FEE|TAX|COMMISSION|TRANSFER)  │
│ created_at   TSTZ                                 │
└───────────────────────────────────────────────────┘

┌───────────────────┐
│    transaction    │
├───────────────────┤
│ id           UUID │
│ merchant_id  UUID │  FK → merchant.id
│ amount_cents BIGINT│
│ status       TEXT │  (PENDING|COMPLETED|FAILED)
│ created_at   TSTZ │
└────────┬──────────┘
         │
         ▼
┌─────────────────────────────────────────────────────┐
│                    split_entry                      │
├─────────────────────────────────────────────────────┤
│ id              UUID                                │
│ transaction_id  UUID  FK → transaction.id           │
│ recipient_id    UUID  FK → recipient.id             │
│ amount_cents    BIGINT                              │
│ percent_applied INT                                 │
│ type            TEXT  (FEE|TAX|COMMISSION|TRANSFER) │
│ created_at      TSTZ                               │
└─────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│                    payment_event                     │
├──────────────────────────────────────────────────────┤
│ id              UUID                                 │
│ transaction_id  UUID   FK → transaction.id           │
│ event_type      TEXT   (TRANSACTION_CREATED|         │
│                         SPLIT_EXECUTED|              │
│                         TRANSACTION_COMPLETED|       │
│                         TRANSACTION_FAILED)          │
│ payload         JSONB                                │
│ created_at      TSTZ                                 │
└──────────────────────────────────────────────────────┘
```

---

## Eventos Kafka

Todos os eventos são publicados no tópico **`payment-events`**.

| Evento | Quando é disparado |
|---|---|
| `TRANSACTION_CREATED` | Ao criar uma nova transação (status PENDING) |
| `SPLIT_EXECUTED` | A cada split calculado durante o processamento |
| `TRANSACTION_COMPLETED` | Quando todos os splits são executados com sucesso |
| `TRANSACTION_FAILED` | Em caso de falha no processamento |

**Estrutura do evento:**
```json
{
  "id": "uuid-do-evento",
  "transactionId": "uuid-da-transacao",
  "eventType": "TRANSACTION_COMPLETED",
  "payload": "{ ... }",
  "createdAt": "2024-01-15T10:33:00Z"
}
```

O consumer padrão (`psp-group`) processa e loga os eventos. O sistema está preparado para extensão com múltiplos consumers para notificações, relatórios e integrações.
