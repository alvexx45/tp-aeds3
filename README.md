# Petcare Manager - Fase IV

Sistema de gerenciamento de petshop desenvolvido em Java com interface gráfica JavaFX, implementando estruturas de dados avançadas (Hash Extensível e Árvore B+) para indexação e relacionamentos.

## 📋 Pré-requisitos

- **Java 11** ou superior
- **Maven 3.6** ou superior
- **JavaFX** (gerenciado automaticamente pelo Maven)

## Instalação do Maven:
- Windows: baixar pelo seguinte link: https://maven.apache.org/download.cgi
- Linux:
  - Debian/Ubuntu: sudo apt install maven
  - Arch: sudo pacman -S maven

## 🚀 Como Executar

Todos os comandos abaixo devem ser executados na pasta src

### **Opção 1: Script Automático (Recomendado)**

Limpa, compila e executa o projeto:

```bash
./run.sh
```

### **Opção 2: Comandos Maven**

**Compilar o projeto:**
```bash
mvn clean compile
```

**Executar o projeto:**
```bash
mvn exec:java
```

### **Opção 3: Gerar JAR Executável**

```bash
mvn package
java -jar target/tp-aeds3-1.0-SNAPSHOT.jar
```

## 💻 Usando o Sistema

### Menu Principal

Ao iniciar, você verá 5 opções:

1. **Gerenciar Clientes** - CRUD completo (CPF, nome, email, telefones)
2. **Gerenciar Pets** - CRUD com relacionamento 1:N via CPF do dono
3. **Gerenciar Serviços** - CRUD de serviços oferecidos
4. **Gerenciar Agendamentos** - CRUD com relacionamento N:N via idPet e idServico
5. **Compressão de Dados** - Compressão e descompressão dos dados utilizando Huffman ou LZW
6. **Executar Testes** - Popula o sistema com dados de exemplo

### Bateria de Testes

A opção **6** insere automaticamente:
- **10 Clientes** com CPF, nome, email e telefones válidos
- **15 Pets** associados aos clientes (demonstra relacionamento 1:N)
- **15 Serviços** diversos (banho, tosa, consultas veterinárias, etc.)
- **20 Agendamentos** relacionando pets e serviços (demonstra relacionamento N:N)

**Útil para:**
- ✅ Testar rapidamente todas as funcionalidades de CRUD e compressão de dados
- ✅ Demonstrar relacionamentos via Hash Extensível (1:N) e Árvore B+ (N:N)
- ✅ Validar integridade dos índices e exclusões em cascata

## 🔧 Funcionalidades Principais

### ✅ CRUD Completo
- **Clientes**: Inclusão, busca (ID/CPF/email), alteração, exclusão
- **Pets**: Inclusão, busca (ID/CPF do dono), alteração, exclusão
- **Serviços**: Inclusão, busca (ID/nome), alteração, exclusão
- **Agendamentos**: Inclusão, busca, alteração, exclusão

### ✅ Relacionamentos
- **1:N (Cliente → Pets)**: Hash Extensível com chave CPF
- **N:N (Pet ↔ Serviço)**: Árvore B+ (ordem 5) via Agendamento

### ✅ Validações
- **CPF**: 11 dígitos obrigatórios
- **Email**: Formato válido (regex)
- **Telefone**: 11 dígitos (DDD + número)
- **Duplicatas**: Impede inserções duplicadas

### ✅ Integridade Referencial
- **Exclusão em Cascata**: Cliente → Pets → Agendamentos
- **Índices Sincronizados**: Atualizações automáticas

## 📊 Complexidade das Operações

| Estrutura | Busca | Inserção | Remoção |
|-----------|-------|----------|---------|
| **Índice Sequencial** (PK) | O(log n) | O(n) | O(n) |
| **Hash Extensível** (1:N) | O(1) médio | O(1) médio | O(1) médio |
| **Árvore B+** (N:N) | O(log n) | O(log n) | O(log n) |


## 👥 Autores

- **Bernardo Bicalho**
- **Bernardo Pires**
- **Eduardo Luttembarck**

**Disciplina:** Algoritmos e Estruturas de Dados III (AEDS-III)  
**Instituição:** PUC Minas  
**Ano:** 2025

**Tópicos cobertos:**
- Estrutura de representação dos registros
- Tratamento de atributos multivalorados
- Implementação de exclusão lógica (lápide)
- Tipos de chaves e índices utilizados
- Persistência de índices em disco
- Relacionamentos 1:N e N:N
- Integridade referencial
- Compressão de Dados

## 📝 Licença

Este projeto foi desenvolvido para fins acadêmicos.
