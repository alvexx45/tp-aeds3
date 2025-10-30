# TP AEDS-III - Petcare Manager

## 📋 Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior
- JavaFX (incluído automaticamente via Maven)

## 🚀 Compilação com Maven

### Limpar, compilar e rodar o projeto
```bash
./run.sh
```

### Apenas rodar
```bash
mvn exec:java
```

#### Funcionalidades Principais:
- **Gerenciar Clientes**: CRUD completo com busca por nome, CPF, email
- **Gerenciar Pets**: CRUD completo com relacionamento e busca por dono
- **Gerenciar Serviços**: CRUD completo com busca por faixa de preço
- **Executar Bateria de Testes**: Insere dados de exemplo automaticamente

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
├── src/
│   ├── app/                   (aplicação principal)
│   │   ├── Main.java         (🆕 ponto de entrada JavaFX único)
│   │   └── BateriaTestes.java (testes automáticos)
│   ├── controller/            (🆕 controladores JavaFX)
│   │   ├── MainController.java
│   │   ├── ClienteController.java
│   │   ├── PetController.java
│   │   └── ServicoController.java
│   ├── view/                  (🆕 interfaces FXML)
│   │   ├── MainView.fxml
│   │   ├── ClienteView.fxml
│   │   ├── PetView.fxml
│   │   └── ServicoView.fxml
│   ├── css/                   (🆕 estilos CSS)
│   │   └── Style.css
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

- Bernardo Bicalho
- Bernardo Pires
- Eduardo Luttembarck

## 🎨 Interface Gráfica JavaFX

### Características da Interface
- **Design Moderno**: Interface limpa com layout profissional
- **Navegação Intuitiva**: Organização em abas para cada funcionalidade
- **Validação em Tempo Real**: Verificação de campos obrigatórios e tipos de dados
- **Feedback Visual**: Mensagens de sucesso, erro e confirmação
- **Estilos Personalizados**: CSS customizado com cores harmoniosas

### Funcionalidades por Tela

#### 🏠 **Tela Principal**
- Menu de navegação principal
- Acesso direto a todas as funcionalidades
- Opção de executar testes
- Botão de saída

#### 👥 **Gerenciamento de Clientes**
- **Aba Incluir**: Cadastro com CPF, nome, email e telefones
- **Aba Buscar/Alterar**: Busca por ID, CPF ou email com edição
- **Aba Listar**: Visualização de todos os clientes

#### 🐕 **Gerenciamento de Pets**
- **Aba Incluir**: Cadastro completo com associação ao dono
- **Aba Buscar/Alterar**: Busca por ID com edição
- **Aba Buscar por Dono**: Lista pets por CPF do dono

#### 🛒 **Gerenciamento de Serviços**
- **Aba Incluir**: Cadastro de serviços com nome e valor
- **Aba Buscar/Alterar**: Busca por ID ou nome com edição
- **Aba Listar**: Visualização de todos os serviços

### Tecnologias JavaFX Utilizadas
- **FXML**: Definição declarativa das interfaces
- **CSS**: Estilização customizada
- **Controllers**: Padrão MVC para organização do código
- **Binding**: Ligação entre interface e dados
- **Eventos**: Manipulação de cliques e ações do usuário

## 📝 Notas

- Os arquivos de dados são criados automaticamente na pasta `src/dados/`
- Os arquivos compilados ficam em `target/classes/`
- Para limpar os dados: `rm -rf src/dados/`
