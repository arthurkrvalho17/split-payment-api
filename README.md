# Split Payment API

API de processamento de pagamentos com divisão automática entre múltiplos recebedores. Construída com **arquitetura hexagonal**, **Outbox Pattern**, **mensageria assíncrona via Kafka** e persistência PostgreSQL.

## Índice

- [Visão Geral](#visão-geral)
- [Decisões de Arquitetura](#decisões-de-arquitetura)
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

## Decisões de Arquitetura

### Outbox Pattern — Consistência entre banco e Kafka

O problema clássico de sistemas event-driven é: **como garantir que o evento seja publicado se o banco commitou, mas o Kafka falhou?** (ou vice-versa).

A solução ingênua publica diretamente no Kafka dentro da transação — mas isso cria uma janela de inconsistência: o Kafka pode receber a mensagem antes do banco commitar, e o consumer tenta processar dados que ainda não existem.

Neste projeto, adotamos o **Outbox Pattern**:

```
@Transactional
TransactionService.save()
  ├── INSERT transaction         ┐
  ├── INSERT payment_event (PENDING) ┤ mesmo commit atômico
  └── INSERT split_entries       ┘

OutboxRelayService (@Scheduled a cada 1s)
  ├── SELECT payment_event WHERE status = PENDING
  ├── kafkaTemplate.send(...)
  └── UPDATE payment_event SET status = PUBLISHED (ou FAILED)
```

**Garantias obtidas:**
- Nenhum evento é publicado antes do banco commitar
- Nenhum evento é perdido se o Kafka estiver indisponível (fica `PENDING` até o relay conseguir publicar)
- Rastreabilidade completa: toda mensagem publicada tem registro no banco com status

---

### Arquitetura Hexagonal (Ports & Adapters)

O domínio não conhece Spring, JPA ou Kafka. As interfaces de repositório ficam no domínio; as implementações (JPA, Kafka) ficam na infraestrutura. Isso permite:

- Trocar PostgreSQL por outro banco sem tocar no domínio
- Testar a lógica de negócio sem subir infraestrutura
- Isolar responsabilidades por camada

---

### Kafka + Dead Letter Topic

Eventos inválidos ou que falhem no processamento são encaminhados automaticamente para `payment-events.DLT`, evitando que uma mensagem corrompida bloqueie o consumer indefinidamente. O consumer valida a presença do campo `eventType` antes de processar, com log explícito para facilitar debugging.

---

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
- **Outbox Pattern** — eventos são persistidos atomicamente com a transação e publicados no Kafka por um relay assíncrono
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
│       ├── TransactionService
│       └── OutboxRelayService  # Publica eventos pendentes no Kafka
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
│   │   ├── EventType (enum)
│   │   └── PaymentEventStatus (enum)  # PENDING | PUBLISHED | FAILED
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
3. Salva PaymentEvent TRANSACTION_CREATED no banco (status PENDING)
      │
      ▼
4. Busca SplitRules do Merchant
      │
      ▼
5. Para cada SplitRule:
   ├── Calcula: amount = round((percent * totalAmount) / 100)
   ├── Cria SplitEntry
   └── Salva PaymentEvent SPLIT_EXECUTED no banco (status PENDING)
      │
      ▼
6. Cria SplitEntry para o Merchant (saldo líquido)
   └── percentual = 100% - soma dos percentuais anteriores
      │
      ▼
7. Atualiza Transaction para status COMPLETED
      │
      ▼
8. Salva PaymentEvent TRANSACTION_COMPLETED no banco (status PENDING)
      │
      ▼
9. Commit da transação
      │
      ▼
10. OutboxRelayService (a cada 1s) publica eventos PENDING no Kafka
    └── Atualiza status para PUBLISHED ou FAILED
      │
      ▼
11. Retorna TransactionResponse com todos os splits
```

> **Outbox Pattern:** os eventos nunca são publicados diretamente no Kafka dentro da transação. Eles são persistidos atomicamente no banco com status `PENDING` e publicados por um processo separado (`OutboxRelayService`), garantindo que nenhum evento seja perdido ou publicado antes do commit.

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
│ status          TEXT   (PENDING|PUBLISHED|FAILED)    │
│ created_at      TSTZ                                 │
└──────────────────────────────────────────────────────┘
```

---

## Eventos Kafka

Todos os eventos são publicados no tópico **`payment-events`** pelo `OutboxRelayService` após o commit da transação.

| Evento | Quando é disparado |
|---|---|
| `TRANSACTION_CREATED` | Ao criar uma nova transação (status PENDING) |
| `SPLIT_EXECUTED` | A cada split calculado durante o processamento |
| `TRANSACTION_COMPLETED` | Quando todos os splits são executados com sucesso |
| `TRANSACTION_FAILED` | Em caso de falha no processamento |

**Status do evento (`PaymentEventStatus`):**

| Status | Descrição |
|---|---|
| `PENDING` | Salvo no banco, aguardando publicação |
| `PUBLISHED` | Publicado com sucesso no Kafka |
| `FAILED` | Falha na publicação — requer atenção manual |

**Estrutura do payload:**
```json
{ "eventType": "TRANSACTION_CREATED", "transactionId": "uuid", "amountCents": 10000, "merchantId": "uuid" }
{ "eventType": "SPLIT_EXECUTED", "transactionId": "uuid", "recipientId": "uuid", "amountCents": 1500, "percent": 15 }
{ "eventType": "TRANSACTION_COMPLETED", "transactionId": "uuid", "amountCents": 10000 }
```

Mensagens inválidas ou que falhem no processamento são encaminhadas para o tópico **`payment-events.DLT`** (Dead Letter Topic) e logadas com `WARN`.
