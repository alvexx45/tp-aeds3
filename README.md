# TP AEDS-3 - Gerenciador de Petshop

## 📋 Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

## 🚀 Compilação com Maven

### Limpar e compilar o projeto
```bash
mvn clean compile
```

### Apenas compilar
```bash
mvn compile
```

### Criar JAR executável
```bash
mvn package
```

## ▶️ Execução

### 1. Executar o programa principal (Menu interativo)
```bash
mvn exec:java
```

#### Opções do Menu:
- **1** - Gerenciar Clientes (CRUD completo)
- **2** - Gerenciar Pets (CRUD completo com relacionamento)
- **3** - Gerenciar Serviços (CRUD completo)
- **4** - Executar Bateria de Testes (insere dados de exemplo)
- **0** - Sair

### 2. Bateria de Testes

A opção **4** do menu executa uma bateria de testes automática que insere:
- **10 Clientes** com dados completos (CPF, nome, email, telefones)
- **15 Serviços** diversos (banho, tosa, consultas, etc.)
- **15 Pets** associados aos clientes (demonstra relacionamento 1:N)

Esta bateria é útil para:
- ✅ Testar rapidamente todas as funcionalidades
- ✅ Demonstrar o relacionamento 1:N via Hash Extensível
- ✅ Validar a integridade dos índices
- ✅ Popular o sistema com dados realistas

## 📁 Estrutura do Projeto

```
tp-aeds3/
├── pom.xml                    (configuração Maven)
├── README.md                  (este arquivo)
├── docs/                      (documentação)
│   ├── dcu.png
│   ├── der.png
│   └── Fase II - TP.pdf
└── src/                       (código-fonte)
    ├── app/                   (aplicação e menus)
    │   ├── Main.java
    │   ├── MenuCliente.java
    │   ├── MenuPet.java
    │   ├── MenuServico.java
    │   └── BateriaTestes.java
    ├── dao/                   (Data Access Objects)
    │   ├── Arquivo.java
    │   ├── ClienteDAO.java
    │   ├── PetDAO.java
    │   ├── ServicoDAO.java
    │   ├── AgendarDAO.java
    │   ├── IndiceSequencial.java
    │   ├── IndiceHashExtensivel.java
    │   ├── HashExtensivel.java
    │   ├── RegistroHashExtensivel.java
    │   ├── RelacionamentoPetDono.java
    │   └── Registro.java
    ├── model/                 (modelos de dados)
    │   ├── Cliente.java
    │   ├── Pet.java
    │   ├── Servico.java
    │   └── Agendar.java
    ├── bin/                   (arquivos compilados - gerado automaticamente)
    ├── dados/                 (arquivos de dados - gerado automaticamente)
        ├── clientes/
        │   ├── clientes.db    (dados dos clientes)
        │   └── clientes.idx   (índice sequencial - PK)
        ├── pets/
        │   ├── pets.db        (dados dos pets)
        │   ├── pets.idx       (índice sequencial - PK)
        │   ├── pets_hash.dir  (diretório da hash extensível)
        │   └── pets_hash.db   (cestos da hash extensível)
        └── servicos/
            ├── servicos.db    (dados dos serviços)
            └── servicos.idx   (índice sequencial - PK)
```

## 🔧 Funcionalidades Implementadas

### Fase II - Índices

#### ✅ Índice Primário (PK)
- **Índice Sequencial** implementado em `IndiceSequencial.java`
- Busca binária O(log n)
- Mantido ordenado por ID
- Usado nas tabelas: `clientes` e `pets`

#### ✅ Relacionamento 1:N com Hash Extensível
- **Hash Extensível** implementada em `HashExtensivel.java`
- Gerenciador em `IndiceHashExtensivel.java`
- Relacionamento Pet-Dono (CPF do Cliente → IDs dos Pets)
- Busca O(1) em média
- Expansão dinâmica de cestos

### Classes Principais

#### DAO (Data Access Objects)
- `Arquivo<T>`: Gerenciamento genérico de arquivos com índice sequencial
- `ClienteDAO`: Operações CRUD para clientes
- `PetDAO`: Operações CRUD para pets (com hash extensível)
- `ServicoDAO`: Operações CRUD para serviços
- `AgendarDAO`: Operações CRUD para agendamentos

#### Índices
- `IndiceSequencial`: Índice primário baseado em PK
- `IndiceHashExtensivel`: Gerenciador do índice hash para relacionamento 1:N
- `HashExtensivel<T>`: Implementação da estrutura hash extensível
- `RelacionamentoPetDono`: Registro do relacionamento na hash

#### Modelos
- `Cliente`: CPF, nome, email, telefones
- `Pet`: nome, espécie, raça, peso, dono (Cliente)
- `Servico`: descrição, preço
- `Agendar`: data, horário, pet, serviço

## 🧪 Testes Disponíveis

A **Bateria de Testes** (opção 4 do menu) insere dados de exemplo e demonstra:
- Relacionamento 1:N entre Cliente e Pet via Hash Extensível
- Funcionamento dos índices sequenciais
- CRUD completo de todas as entidades
- Busca por CPF com performance O(1)

## 📊 Complexidade

| Operação | Índice Sequencial | Hash Extensível |
|----------|-------------------|-----------------|
| Busca    | O(log n)          | O(1) médio      |
| Inserção | O(n)              | O(1) médio      |
| Remoção  | O(n)              | O(1) médio      |

## 👥 Autores

Trabalho Prático - AEDS III
PUC Minas

## 📝 Notas

- Os arquivos de dados são criados automaticamente na pasta `src/dados/`
- Os arquivos compilados ficam em `src/bin/`
- Para limpar os dados: `rm -rf src/dados/`
- Para limpar compilação: `mvn clean` ou `rm -rf src/bin/`
